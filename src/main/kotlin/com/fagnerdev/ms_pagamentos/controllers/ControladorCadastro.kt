package com.fagnerdev.ms_pagamentos.controllers



import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarCliente
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimento
import com.fagnerdev.ms_pagamentos.dtos.RespostaCliente
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimento
import com.fagnerdev.ms_pagamentos.entidades.Clientes
import com.fagnerdev.ms_pagamentos.entidades.Estabelecimento
import com.fagnerdev.ms_pagamentos.exceptions.ExcecaoRegraNegocio
import com.fagnerdev.ms_pagamentos.repositories.RepositorioCliente
import com.fagnerdev.ms_pagamentos.repositories.RepositorioEstabelecimento
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@RestController
@RequestMapping("/api/cadastros")
class ControladorCadastro(
    private val repositorioCliente: RepositorioCliente,
    private val repositorioEstabelecimento: RepositorioEstabelecimento
) {

    @PostMapping("/clientes")
    fun criarCliente(
        @Valid @RequestBody req: RequisicaoCriarCliente,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaCliente> {
        if (repositorioCliente.existsByEmail(req.email)) {
            throw ExcecaoRegraNegocio("Email já cadastrado")
        }

        val salvo = repositorioCliente.save(Clientes(nome = req.nome, email = req.email))

        val location = uriBuilder
            .path("/api/cadastros/clientes/{id}")
            .buildAndExpand(salvo.id)
            .toUri()

        val resposta = RespostaCliente(
            id = salvo.id!!,
            nome = salvo.nome,
            email = salvo.email
        )

        return ResponseEntity.created(location).body(resposta)
    }

    @PostMapping("/estabelecimentos")
    fun criarEstabelecimento(
        @Valid @RequestBody req: RequisicaoCriarEstabelecimento,
        uriBuilder: UriComponentsBuilder
    ): ResponseEntity<RespostaEstabelecimento> {
        if (repositorioEstabelecimento.existsByDocumento(req.documento)) {
            throw ExcecaoRegraNegocio("Documento já cadastrado")
        }

        val salvo = repositorioEstabelecimento.save(
            Estabelecimento(razaoSocial = req.razaoSocial, documento = req.documento)
        )

        val location = uriBuilder
            .path("/api/cadastros/estabelecimentos/{id}")
            .buildAndExpand(salvo.id)
            .toUri()

        val resposta = RespostaEstabelecimento(
            id = salvo.id!!,
            razaoSocial = salvo.razaoSocial,
            documento = salvo.documento
        )

        return ResponseEntity.created(location).body(resposta)
    }
}
