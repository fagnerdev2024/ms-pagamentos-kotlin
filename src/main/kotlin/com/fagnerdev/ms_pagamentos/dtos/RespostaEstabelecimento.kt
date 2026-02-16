package com.fagnerdev.ms_pagamentos.dtos



data class RespostaEstabelecimento(
    val id: Long,
    val razaoSocial: String,
    val documento: String
)

