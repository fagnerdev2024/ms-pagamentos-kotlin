package com.fagnerdev.ms_pagamentos.dtos

import jakarta.validation.constraints.NotBlank

data class RequisicaoCriarEstabelecimento(
    @field:NotBlank(message = "Razão social é obrigatória")
    val razaoSocial: String,

    @field:NotBlank(message = "Documento é obrigatório")
    val documento: String
)
