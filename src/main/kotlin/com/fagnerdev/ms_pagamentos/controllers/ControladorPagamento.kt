package com.fagnerdev.ms_pagamentos.controllers

import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamento
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamento
import com.fagnerdev.ms_pagamentos.entidades.EventoPagamento
import com.fagnerdev.ms_pagamentos.services.ServicoPagamento
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/pagamentos")
class ControladorPagamento(
    private val servicoPagamento: ServicoPagamento
) {

    @PostMapping
    fun criar(@Valid @RequestBody requisicao: RequisicaoCriarPagamento): ResponseEntity<RespostaPagamento> {
        val resposta = servicoPagamento.criar(requisicao)
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: UUID): ResponseEntity<RespostaPagamento> {
        val resposta = servicoPagamento.buscarPorId(id)
        return ResponseEntity.ok(resposta)
    }

    @PatchMapping("/{id}/status")
    fun alterarStatus(
        @PathVariable id: UUID,
        @Valid @RequestBody requisicao: RequisicaoAlterarStatusPagamento
    ): ResponseEntity<RespostaPagamento> {
        val resposta = servicoPagamento.alterarStatus(id, requisicao)
        return ResponseEntity.ok(resposta)
    }

    @GetMapping("/{id}/linha-do-tempo")
    fun linhaDoTempo(@PathVariable id: UUID): ResponseEntity<List<EventoPagamento>> {
        val eventos = servicoPagamento.linhaDoTempo(id)
        return ResponseEntity.ok(eventos)
    }
}
