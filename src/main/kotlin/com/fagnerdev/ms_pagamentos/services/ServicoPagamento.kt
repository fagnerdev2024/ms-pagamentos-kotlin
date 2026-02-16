package com.fagnerdev.ms_pagamentos.services

import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamento
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamento
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
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class ServicoPagamento(
    private val repositorioCliente: RepositorioCliente,
    private val repositorioEstabelecimento: RepositorioEstabelecimento,
    private val repositorioPagamento: RepositorioPagamento,
    private val repositorioEventoPagamento: RepositorioEventoPagamento
) {

    @Transactional
    fun criar(requisicao: RequisicaoCriarPagamento): RespostaPagamento {
        val cliente = repositorioCliente.findById(requisicao.clienteId!!)
            .orElseThrow { ExcecaoNaoEncontrado("Cliente ${requisicao.clienteId} não encontrado") }

        val estabelecimento = repositorioEstabelecimento.findById(requisicao.estabelecimentoId!!)
            .orElseThrow { ExcecaoNaoEncontrado("Estabelecimento ${requisicao.estabelecimentoId} não encontrado") }

        val pagamento = Pagamento(
            clientes = cliente,
            estabelecimento = estabelecimento,
            valor = requisicao.valor!!,
            moeda = requisicao.moeda!!.uppercase(),
            meio = requisicao.meio!!,
            status = StatusPagamento.CRIADO
        )

        val salvo = repositorioPagamento.save(pagamento)
        repositorioEventoPagamento.save(
            EventoPagamento(pagamentoId = salvo.id, status = salvo.status, mensagem = "Pagamento criado")
        )
        return salvo.paraResposta()
    }

    //@Transactional(readOnly = true)
    fun buscarPorId(id: UUID): RespostaPagamento =
        repositorioPagamento.findById(id)
            .orElseThrow { ExcecaoNaoEncontrado("Pagamento $id não encontrado") }
            .paraResposta()

    @Transactional
    fun alterarStatus(id: UUID, requisicao: RequisicaoAlterarStatusPagamento): RespostaPagamento {
        val pagamento = repositorioPagamento.findById(id)
            .orElseThrow { ExcecaoNaoEncontrado("Pagamento $id não encontrado") }

        val proximo = requisicao.status!!
        validarTransicao(pagamento.status, proximo)

        pagamento.status = proximo
        pagamento.tocarAtualizacao()

        val salvo = repositorioPagamento.save(pagamento)
        repositorioEventoPagamento.save(
            EventoPagamento(pagamentoId = salvo.id, status = salvo.status, mensagem = requisicao.mensagem!!)
        )
        return salvo.paraResposta()
    }

    //@Transactional(readOnly = true)
    fun linhaDoTempo(id: UUID): List<EventoPagamento> {
        if (!repositorioPagamento.existsById(id)) throw ExcecaoNaoEncontrado("Pagamento $id não encontrado")
        return repositorioEventoPagamento.findAllByPagamentoIdOrderByCriadoEmAsc(id)
    }

    private fun validarTransicao(atual: StatusPagamento, proximo: StatusPagamento) {
        val permitidos = when (atual) {
            StatusPagamento.CRIADO -> setOf(StatusPagamento.AUTORIZADO, StatusPagamento.CANCELADO, StatusPagamento.FALHOU)
            StatusPagamento.AUTORIZADO -> setOf(StatusPagamento.CAPTURADO, StatusPagamento.CANCELADO, StatusPagamento.FALHOU)
            StatusPagamento.CAPTURADO -> emptySet()
            StatusPagamento.CANCELADO -> emptySet()
            StatusPagamento.FALHOU -> emptySet()
        }

        if (proximo !in permitidos) {
            throw ExcecaoRegraNegocio("Transição inválida: $atual -> $proximo")
        }
    }

    private fun Pagamento.paraResposta(): RespostaPagamento =
        RespostaPagamento(
            id = this.id,
            clienteId = this.clientes.id!!,
            estabelecimentoId = this.estabelecimento.id!!,
            valor = this.valor,
            moeda = this.moeda,
            meio = this.meio,
            status = this.status,
            criadoEm = this.criadoEm,
            atualizadoEm = this.atualizadoEm
        )
}