package com.fagnerdev.ms_pagamentos.dtos




import com.fagnerdev.ms_pagamentos.entidades.StatusPagamento
import java.time.OffsetDateTime

data class RespostaEventoPagamento(
    val id: Long,
    val status: StatusPagamento,
    val mensagem: String,
    val criadoEm: OffsetDateTime
)


