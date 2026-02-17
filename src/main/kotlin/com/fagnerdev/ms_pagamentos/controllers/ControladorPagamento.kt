package com.fagnerdev.ms_pagamentos.controllers




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamento
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamento
import com.fagnerdev.ms_pagamentos.services.ServicoPagamento
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/pagamentos")
class ControladorPagamento(
    private val servicoPagamento: ServicoPagamento
) {

    @PostMapping
    fun criar(@Valid @RequestBody req: RequisicaoCriarPagamento, uriBuilder: UriComponentsBuilder): ResponseEntity<RespostaPagamento> {
        val resposta = servicoPagamento.criar(req)

        val location = uriBuilder
            .path("/api/pagamentos/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }


    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: UUID): ResponseEntity<RespostaPagamento> = ResponseEntity.ok(servicoPagamento.buscarPorId(id))


    @PatchMapping("/{id}/status")
    fun alterarStatus(@PathVariable id: UUID, @Valid @RequestBody req: RequisicaoAlterarStatusPagamento): ResponseEntity<RespostaPagamento> = ResponseEntity.ok(servicoPagamento.alterarStatus(id, req))


    @GetMapping("/{id}/linha-do-tempo")
    fun linhaDoTempo(@PathVariable id: UUID): ResponseEntity<List<RespostaEventoPagamento>> = ResponseEntity.ok(servicoPagamento.linhaDoTempo(id))
}
