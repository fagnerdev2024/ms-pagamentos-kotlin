package com.fagnerdev.ms_pagamentos.controllers

import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarCliente
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimento
import com.fagnerdev.ms_pagamentos.entidades.Clientes
import com.fagnerdev.ms_pagamentos.entidades.Estabelecimento
import com.fagnerdev.ms_pagamentos.exceptions.ExcecaoRegraNegocio
import com.fagnerdev.ms_pagamentos.repositories.RepositorioCliente
import com.fagnerdev.ms_pagamentos.repositories.RepositorioEstabelecimento
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cadastros")
class ControladorCadastro(
    private val repositorioCliente: RepositorioCliente,
    private val repositorioEstabelecimento: RepositorioEstabelecimento
) {

    @PostMapping("/clientes")
    fun criarCliente(@Valid @RequestBody req: RequisicaoCriarCliente): ResponseEntity<Clientes> {
        if (repositorioCliente.existsByEmail(req.email)) {
            throw ExcecaoRegraNegocio("Email já cadastrado")
        }

        val salvo = repositorioCliente.save(Clientes(nome = req.nome, email = req.email))
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }

    @PostMapping("/estabelecimentos")
    fun criarEstabelecimento(@Valid @RequestBody req: RequisicaoCriarEstabelecimento): ResponseEntity<Estabelecimento> {
        if (repositorioEstabelecimento.existsByDocumento(req.documento)) {
            throw ExcecaoRegraNegocio("Documento já cadastrado")
        }

        val salvo = repositorioEstabelecimento.save(
            Estabelecimento(razaoSocial = req.razaoSocial, documento = req.documento)
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo)
    }
}
