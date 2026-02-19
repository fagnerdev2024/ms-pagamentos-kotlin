package com.fagnerdev.ms_pagamentos.dtos



data class RespostaEstabelecimentoDto(
    val id: Long,
    val razaoSocial: String,
    val documento: String
)

