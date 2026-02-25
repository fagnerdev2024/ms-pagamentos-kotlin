package com.fagnerdev.ms_pagamentos.dtos



import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RequisicaoAlterarStatusPagamentoDto(
    @field:NotNull(message = "status é obrigatório")
    val status: StatusPagamentoEnum,

    @field:NotBlank(message = "mensagem é obrigatória")
    val mensagem: String
)

