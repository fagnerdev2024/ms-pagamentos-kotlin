package com.fagnerdev.ms_pagamentos.dtos



import com.fagnerdev.ms_pagamentos.entidades.StatusPagamento
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class RequisicaoAlterarStatusPagamento(
    @field:NotNull(message = "status é obrigatório")
    val status: StatusPagamento,

    @field:NotBlank(message = "mensagem é obrigatória")
    val mensagem: String
)

