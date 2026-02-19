package com.fagnerdev.ms_pagamentos.exceptions



import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum

class ExceptionTransicaoPagamentoInvalido(
    val statusAtual: StatusPagamentoEnum,
    val statusSolicitado: StatusPagamentoEnum,
    val proximosPermitidos: Set<StatusPagamentoEnum>
) : RuntimeException("Transição inválida: $statusAtual -> $statusSolicitado")
