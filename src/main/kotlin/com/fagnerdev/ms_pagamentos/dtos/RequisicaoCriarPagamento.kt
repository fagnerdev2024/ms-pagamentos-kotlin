package com.fagnerdev.ms_pagamentos.dtos



import com.fagnerdev.ms_pagamentos.entidades.MeioPagamento
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class RequisicaoCriarPagamento(
    @field:NotNull(message = "clienteId é obrigatório")
    val clienteId: Long,

    @field:NotNull(message = "estabelecimentoId é obrigatório")
    val estabelecimentoId: Long,

    @field:NotNull(message = "valor é obrigatório")
    @field:DecimalMin(value = "0.01", message = "valor deve ser maior que zero")
    val valor: BigDecimal,

    @field:NotBlank(message = "moeda é obrigatória")
    @field:Size(min = 3, max = 3, message = "moeda deve ter 3 letras (ex: BRL)")
    val moeda: String,

    @field:NotNull(message = "meio é obrigatório")
    val meio: MeioPagamento
)
