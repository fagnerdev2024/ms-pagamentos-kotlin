package com.fagnerdev.ms_pagamentos.dtos


import jakarta.validation.constraints.NotBlank

data class RequisicaoCriarEstabelecimentoDto(
    @field:NotBlank(message = "razaoSocial é obrigatória")
    val razaoSocial: String,

    @field:NotBlank(message = "documento é obrigatório")
    val documento: String
)
