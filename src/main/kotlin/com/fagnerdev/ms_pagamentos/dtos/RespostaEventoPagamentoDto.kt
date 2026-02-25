package com.fagnerdev.ms_pagamentos.dtos




import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum
import java.time.OffsetDateTime

data class RespostaEventoPagamentoDto(
    val id: Long,
    val status: StatusPagamentoEnum,
    val mensagem: String,
    val criadoEm: OffsetDateTime
)


