package com.fagnerdev.ms_pagamentos.controllers




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamentoDto
import com.fagnerdev.ms_pagamentos.services.PagamentoService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder
import java.util.UUID

@RestController
@RequestMapping("/api/pagamentos")
class PagamentoController(
    private val pagamentoService: PagamentoService
) {

    @PostMapping
    fun criar(@Valid @RequestBody req: RequisicaoCriarPagamentoDto, uriBuilder: UriComponentsBuilder): ResponseEntity<RespostaPagamentoDto> {
        val resposta = pagamentoService.criar(req)

        val location = uriBuilder
            .path("/api/pagamentos/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }


    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: UUID): ResponseEntity<RespostaPagamentoDto> = ResponseEntity.ok(pagamentoService.buscarPorId(id))


    @PatchMapping("/{id}/status")
    fun alterarStatus(@PathVariable id: UUID, @Valid @RequestBody req: RequisicaoAlterarStatusPagamentoDto): ResponseEntity<RespostaPagamentoDto> = ResponseEntity.ok(pagamentoService.alterarStatus(id, req))


    @GetMapping("/{id}/linha-do-tempo")
    fun linhaDoTempo(@PathVariable id: UUID): ResponseEntity<List<RespostaEventoPagamentoDto>> = ResponseEntity.ok(pagamentoService.linhaDoTempo(id))
}
