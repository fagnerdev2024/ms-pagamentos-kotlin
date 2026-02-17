package com.fagnerdev.ms_pagamentos.services



import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarCliente
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimento
import com.fagnerdev.ms_pagamentos.dtos.RespostaCliente
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimento
import com.fagnerdev.ms_pagamentos.entidades.Clientes
import com.fagnerdev.ms_pagamentos.entidades.Estabelecimento
import com.fagnerdev.ms_pagamentos.exceptions.ExcecaoRegraNegocio
import com.fagnerdev.ms_pagamentos.repositories.RepositorioCliente
import com.fagnerdev.ms_pagamentos.repositories.RepositorioEstabelecimento
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ServicoCadastroImpl(
    private val repositorioCliente: RepositorioCliente,
    private val repositorioEstabelecimento: RepositorioEstabelecimento
) : ServicoCadastro {

    @Transactional
    override fun criarCliente(req: RequisicaoCriarCliente): RespostaCliente {

        if (repositorioCliente.existsByEmail(req.email)) {
            throw ExcecaoRegraNegocio("Email já cadastrado")
        }

        val salvo = repositorioCliente.save(
            Clientes(
                nome = req.nome,
                email = req.email
            )
        )

        return RespostaCliente(
            id = requireNotNull(salvo.id) { "ID do cliente não gerado" },
            nome = salvo.nome,
            email = salvo.email
        )
    }

    @Transactional
    override fun criarEstabelecimento(req: RequisicaoCriarEstabelecimento): RespostaEstabelecimento {

        if (repositorioEstabelecimento.existsByDocumento(req.documento)) {
            throw ExcecaoRegraNegocio("Documento já cadastrado")
        }

        val salvo = repositorioEstabelecimento.save(
            Estabelecimento(
                razaoSocial = req.razaoSocial,
                documento = req.documento
            )
        )

        return RespostaEstabelecimento(
            id = requireNotNull(salvo.id) { "ID do estabelecimento não gerado" },
            razaoSocial = salvo.razaoSocial,
            documento = salvo.documento
        )
    }
}
