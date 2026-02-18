package com.fagnerdev.ms_pagamentos.exceptions



import com.fagnerdev.ms_pagamentos.entidades.StatusPagamento

class TransicaoPagamentoInvalidaException(
    val statusAtual: StatusPagamento,
    val statusSolicitado: StatusPagamento,
    val proximosPermitidos: Set<StatusPagamento>
) : RuntimeException("Transição inválida: $statusAtual -> $statusSolicitado")
