package com.fagnerdev.ms_pagamentos.controllers




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarCliente
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimento
import com.fagnerdev.ms_pagamentos.dtos.RespostaCliente
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimento
import com.fagnerdev.ms_pagamentos.services.ServicoCadastro
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/cadastros")
class ControladorCadastro(
    private val servicoCadastro: ServicoCadastro
) {

    @PostMapping("/clientes")
    fun criarCliente(
        @Valid @RequestBody req: RequisicaoCriarCliente,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaCliente> {

        val resposta = servicoCadastro.criarCliente(req)

        val location = uriBuilder
            .path("/api/cadastros/clientes/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }

    @PostMapping("/estabelecimentos")
    fun criarEstabelecimento(
        @Valid @RequestBody req: RequisicaoCriarEstabelecimento,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaEstabelecimento> {

        val resposta = servicoCadastro.criarEstabelecimento(req)

        val location = uriBuilder
            .path("/api/cadastros/estabelecimentos/{id}")
            .buildAndExpand(resposta.id)
            .toUri()

        return ResponseEntity.created(location).body(resposta)
    }
}
