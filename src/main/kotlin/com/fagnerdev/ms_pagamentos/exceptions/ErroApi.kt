package com.fagnerdev.ms_pagamentos.exceptions



import java.time.OffsetDateTime

data class ErroApi(
    val timestamp: OffsetDateTime = OffsetDateTime.now(),
    val status: Int,
    val erro: String,
    val mensagem: String,
    val caminho: String,
    val detalhes: Map<String, Any?> = emptyMap()
)
