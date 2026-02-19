package com.fagnerdev.ms_pagamentos.services


import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamentoDto
import com.fagnerdev.ms_pagamentos.entities.EventoPagamentoEntity
import com.fagnerdev.ms_pagamentos.entities.PagamentoEntity
import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum
import com.fagnerdev.ms_pagamentos.exceptions.ExceptionNaoEncontrado
import com.fagnerdev.ms_pagamentos.exceptions.ExceptionTransicaoPagamentoInvalido
import com.fagnerdev.ms_pagamentos.repositories.ClienteRepository
import com.fagnerdev.ms_pagamentos.repositories.EstabelecimentoRepository
import com.fagnerdev.ms_pagamentos.repositories.EventoPagamentoRepository
import com.fagnerdev.ms_pagamentos.repositories.PagamentoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PagamentoServiceImpl(
    private val clienteRepository: ClienteRepository,
    private val estabelecimentoRepository: EstabelecimentoRepository,
    private val pagamentoRepository: PagamentoRepository,
    private val eventoPagamentoRepository: EventoPagamentoRepository
) : PagamentoService {

    @Transactional
    override fun criar(requisicao: RequisicaoCriarPagamentoDto): RespostaPagamentoDto {
        val cliente = clienteRepository.findById(requisicao.clienteId)
            .orElseThrow { ExceptionNaoEncontrado("Cliente ${requisicao.clienteId} não encontrado") }

        val estabelecimento = estabelecimentoRepository.findById(requisicao.estabelecimentoId)
            .orElseThrow { ExceptionNaoEncontrado("Estabelecimento ${requisicao.estabelecimentoId} não encontrado") }

        val pagamentoEntity = PagamentoEntity(
            clienteEntity = cliente,
            estabelecimentoEntity = estabelecimento,
            valor = requisicao.valor,
            moeda = requisicao.moeda.uppercase(),
            meio = requisicao.meio,
            status = StatusPagamentoEnum.CRIADO
        )

        val salvo = pagamentoRepository.save(pagamentoEntity)

        registrarEvento(
            pagamentoId = salvo.id,
            status = salvo.status,
            mensagem = "Pagamento criado"
        )

        return salvo.paraResposta()
    }

    @Transactional(readOnly = true)
    override fun buscarPorId(id: UUID): RespostaPagamentoDto =
        pagamentoRepository.findById(id)
            .orElseThrow { ExceptionNaoEncontrado("Pagamento $id não encontrado") }
            .paraResposta()

    @Transactional
    override fun alterarStatus(id: UUID, requisicao: RequisicaoAlterarStatusPagamentoDto): RespostaPagamentoDto {
        val pagamento = pagamentoRepository.findById(id)
            .orElseThrow { ExceptionNaoEncontrado("Pagamento $id não encontrado") }

        validarTransicao(pagamento.status, requisicao.status)

        pagamento.status = requisicao.status
        pagamento.tocarAtualizacao()

        val salvo = pagamentoRepository.save(pagamento)

        registrarEvento(
            pagamentoId = salvo.id,
            status = salvo.status,
            mensagem = requisicao.mensagem
        )

        return salvo.paraResposta()
    }

    @Transactional(readOnly = true)
    override fun linhaDoTempo(id: UUID): List<RespostaEventoPagamentoDto> {
        if (!pagamentoRepository.existsById(id)) {
            throw ExceptionNaoEncontrado("Pagamento $id não encontrado")
        }

        return eventoPagamentoRepository.findAllByPagamentoIdOrderByCriadoEmAsc(id)
            .map { it.paraRespostaEvento() }
    }

    private fun registrarEvento(pagamentoId: UUID, status: StatusPagamentoEnum, mensagem: String) {
        eventoPagamentoRepository.save(
            EventoPagamentoEntity(
                pagamentoId = pagamentoId,
                status = status,
                mensagem = mensagem
            )
        )
    }

    private fun validarTransicao(atual: StatusPagamentoEnum, proximo: StatusPagamentoEnum) {
        val permitidos = proximosPermitidos(atual)
        if (proximo !in permitidos) {
            throw ExceptionTransicaoPagamentoInvalido(atual, proximo, permitidos)
        }
    }

    private fun proximosPermitidos(atual: StatusPagamentoEnum): Set<StatusPagamentoEnum> =
        when (atual) {
            StatusPagamentoEnum.CRIADO ->
                setOf(StatusPagamentoEnum.AUTORIZADO, StatusPagamentoEnum.CANCELADO, StatusPagamentoEnum.FALHOU)

            StatusPagamentoEnum.AUTORIZADO ->
                setOf(StatusPagamentoEnum.CAPTURADO, StatusPagamentoEnum.CANCELADO, StatusPagamentoEnum.FALHOU)

            StatusPagamentoEnum.CAPTURADO ->
                setOf(StatusPagamentoEnum.ESTORNADO)

            StatusPagamentoEnum.CANCELADO,
            StatusPagamentoEnum.FALHOU,
            StatusPagamentoEnum.ESTORNADO ->
                emptySet()
        }


    private fun PagamentoEntity.paraResposta(): RespostaPagamentoDto =
        RespostaPagamentoDto(
            id = this.id,
            clienteId = requireNotNull(this.clienteEntity.id) { "Cliente sem id" },
            estabelecimentoId = requireNotNull(this.estabelecimentoEntity.id) { "Estabelecimento sem id" },
            valor = this.valor,
            moeda = this.moeda,
            meio = this.meio,
            status = this.status,
            criadoEm = this.criadoEm,
            atualizadoEm = this.atualizadoEm
        )

    private fun EventoPagamentoEntity.paraRespostaEvento(): RespostaEventoPagamentoDto =
        RespostaEventoPagamentoDto(
            id = requireNotNull(this.id) { "Evento sem id" },
            status = this.status,
            mensagem = this.mensagem,
            criadoEm = this.criadoEm
        )
}
