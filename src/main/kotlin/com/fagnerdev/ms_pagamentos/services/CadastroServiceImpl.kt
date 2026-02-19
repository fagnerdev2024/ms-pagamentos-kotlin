package com.fagnerdev.ms_pagamentos.services



import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimentoDto
import com.fagnerdev.ms_pagamentos.entities.ClienteEntity
import com.fagnerdev.ms_pagamentos.entities.EstabelecimentoEntity
import com.fagnerdev.ms_pagamentos.exceptions.ExceptionRegraNegocio
import com.fagnerdev.ms_pagamentos.repositories.ClienteRepository
import com.fagnerdev.ms_pagamentos.repositories.EstabelecimentoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CadastroServiceImpl(
    private val clienteRepository: ClienteRepository,
    private val estabelecimentoRepository: EstabelecimentoRepository
) : CadastroService {

    @Transactional
    override fun criarCliente(req: RequisicaoCriarClienteDto): RespostaClienteDto {

        if (clienteRepository.existsByEmail(req.email)) {
            throw ExceptionRegraNegocio("Email já cadastrado")
        }

        val salvo = clienteRepository.save(
            ClienteEntity(
                nome = req.nome,
                email = req.email
            )
        )

        return RespostaClienteDto(
            id = requireNotNull(salvo.id) { "ID do cliente não gerado" },
            nome = salvo.nome,
            email = salvo.email
        )
    }

    @Transactional
    override fun criarEstabelecimento(req: RequisicaoCriarEstabelecimentoDto): RespostaEstabelecimentoDto {

        if (estabelecimentoRepository.existsByDocumento(req.documento)) {
            throw ExceptionRegraNegocio("Documento já cadastrado")
        }

        val salvo = estabelecimentoRepository.save(
            EstabelecimentoEntity(
                razaoSocial = req.razaoSocial,
                documento = req.documento
            )
        )

        return RespostaEstabelecimentoDto(
            id = requireNotNull(salvo.id) { "ID do estabelecimento não gerado" },
            razaoSocial = salvo.razaoSocial,
            documento = salvo.documento
        )
    }
}
