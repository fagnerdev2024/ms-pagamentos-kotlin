package com.fagnerdev.ms_pagamentos.dtos



import com.fagnerdev.ms_pagamentos.entidades.MeioPagamento
import com.fagnerdev.ms_pagamentos.entidades.StatusPagamento
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class RespostaPagamento(
    val id: UUID,
    val clienteId: Long,
    val estabelecimentoId: Long,
    val valor: BigDecimal,
    val moeda: String,
    val meio: MeioPagamento,
    val status: StatusPagamento,
    val criadoEm: OffsetDateTime,
    val atualizadoEm: OffsetDateTime
)
