package com.fagnerdev.ms_pagamentos.services


import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamento
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamento
import com.fagnerdev.ms_pagamentos.entidades.EventoPagamento
import com.fagnerdev.ms_pagamentos.entidades.Pagamento
import com.fagnerdev.ms_pagamentos.entidades.StatusPagamento
import com.fagnerdev.ms_pagamentos.exceptions.ExcecaoNaoEncontrado
import com.fagnerdev.ms_pagamentos.exceptions.ExcecaoRegraNegocio
import com.fagnerdev.ms_pagamentos.repositories.RepositorioCliente
import com.fagnerdev.ms_pagamentos.repositories.RepositorioEstabelecimento
import com.fagnerdev.ms_pagamentos.repositories.RepositorioEventoPagamento
import com.fagnerdev.ms_pagamentos.repositories.RepositorioPagamento
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ServicoPagamentoImpl(
    private val repositorioCliente: RepositorioCliente,
    private val repositorioEstabelecimento: RepositorioEstabelecimento,
    private val repositorioPagamento: RepositorioPagamento,
    private val repositorioEventoPagamento: RepositorioEventoPagamento
) : ServicoPagamento {

    @Transactional
    override fun criar(requisicao: RequisicaoCriarPagamento): RespostaPagamento {
        val cliente = repositorioCliente.findById(requisicao.clienteId)
            .orElseThrow { ExcecaoNaoEncontrado("Cliente ${requisicao.clienteId} não encontrado") }

        val estabelecimento = repositorioEstabelecimento.findById(requisicao.estabelecimentoId)
            .orElseThrow { ExcecaoNaoEncontrado("Estabelecimento ${requisicao.estabelecimentoId} não encontrado") }

        val pagamento = Pagamento(
            clientes = cliente,
            estabelecimento = estabelecimento,
            valor = requisicao.valor,
            moeda = requisicao.moeda.uppercase(),
            meio = requisicao.meio,
            status = StatusPagamento.CRIADO
        )

        val salvo = repositorioPagamento.save(pagamento)

        registrarEvento(
            pagamentoId = salvo.id,
            status = salvo.status,
            mensagem = "Pagamento criado"
        )

        return salvo.paraResposta()
    }

    @Transactional(readOnly = true)
    override fun buscarPorId(id: UUID): RespostaPagamento =
        repositorioPagamento.findById(id)
            .orElseThrow { ExcecaoNaoEncontrado("Pagamento $id não encontrado") }
            .paraResposta()

    @Transactional
    override fun alterarStatus(id: UUID, requisicao: RequisicaoAlterarStatusPagamento): RespostaPagamento {
        val pagamento = repositorioPagamento.findById(id)
            .orElseThrow { ExcecaoNaoEncontrado("Pagamento $id não encontrado") }

        validarTransicao(pagamento.status, requisicao.status)

        pagamento.status = requisicao.status
        pagamento.tocarAtualizacao()

        val salvo = repositorioPagamento.save(pagamento)

        registrarEvento(
            pagamentoId = salvo.id,
            status = salvo.status,
            mensagem = requisicao.mensagem
        )

        return salvo.paraResposta()
    }

    @Transactional(readOnly = true)
    override fun linhaDoTempo(id: UUID): List<RespostaEventoPagamento> {
        if (!repositorioPagamento.existsById(id)) {
            throw ExcecaoNaoEncontrado("Pagamento $id não encontrado")
        }

        return repositorioEventoPagamento.findAllByPagamentoIdOrderByCriadoEmAsc(id)
            .map { it.paraRespostaEvento() }
    }

    private fun registrarEvento(pagamentoId: UUID, status: StatusPagamento, mensagem: String) {
        repositorioEventoPagamento.save(
            EventoPagamento(
                pagamentoId = pagamentoId,
                status = status,
                mensagem = mensagem
            )
        )
    }

    private fun validarTransicao(atual: StatusPagamento, proximo: StatusPagamento) {
        val permitidos = when (atual) {
            StatusPagamento.CRIADO ->
                setOf(StatusPagamento.AUTORIZADO, StatusPagamento.CANCELADO, StatusPagamento.FALHOU)

            StatusPagamento.AUTORIZADO ->
                setOf(StatusPagamento.CAPTURADO, StatusPagamento.CANCELADO, StatusPagamento.FALHOU)

            StatusPagamento.CAPTURADO,
            StatusPagamento.CANCELADO,
            StatusPagamento.FALHOU -> emptySet()
        }

        if (proximo !in permitidos) {
            // aqui é regra de negócio (não é 404)
            throw ExcecaoRegraNegocio("Transição inválida: $atual -> $proximo")
        }
    }

    private fun Pagamento.paraResposta(): RespostaPagamento =
        RespostaPagamento(
            id = this.id,
            clienteId = requireNotNull(this.clientes.id) { "Cliente sem id" },
            estabelecimentoId = requireNotNull(this.estabelecimento.id) { "Estabelecimento sem id" },
            valor = this.valor,
            moeda = this.moeda,
            meio = this.meio,
            status = this.status,
            criadoEm = this.criadoEm,
            atualizadoEm = this.atualizadoEm
        )

    private fun EventoPagamento.paraRespostaEvento(): RespostaEventoPagamento =
        RespostaEventoPagamento(
            id = requireNotNull(this.id) { "Evento sem id" },
            status = this.status,
            mensagem = this.mensagem,
            criadoEm = this.criadoEm
        )
}
