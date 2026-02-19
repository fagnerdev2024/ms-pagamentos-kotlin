package com.fagnerdev.ms_pagamentos.controllers




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimentoDto
import com.fagnerdev.ms_pagamentos.services.CadastroService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/cadastros")
class CadastroController(
    private val cadastroService: CadastroService
) {

    @PostMapping("/clientes")
    fun criarCliente(
        @Valid @RequestBody req: RequisicaoCriarClienteDto,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaClienteDto> {

        val resposta = cadastroService.criarCliente(req)

        val location = uriBuilder
            .path("/api/cadastros/clientes/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }

    @PostMapping("/estabelecimentos")
    fun criarEstabelecimento(
        @Valid @RequestBody req: RequisicaoCriarEstabelecimentoDto,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaEstabelecimentoDto> {

        val resposta = cadastroService.criarEstabelecimento(req)

        val location = uriBuilder
            .path("/api/cadastros/estabelecimentos/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }
}
