package com.fagnerdev.ms_pagamentos.dtos



import com.fagnerdev.ms_pagamentos.entities.MeioPagamentoEnum
import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

data class RespostaPagamentoDto(
    val id: UUID,
    val clienteId: Long,
    val estabelecimentoId: Long,
    val valor: BigDecimal,
    val moeda: String,
    val meio: MeioPagamentoEnum,
    val status: StatusPagamentoEnum,
    val criadoEm: OffsetDateTime,
    val atualizadoEm: OffsetDateTime
)

