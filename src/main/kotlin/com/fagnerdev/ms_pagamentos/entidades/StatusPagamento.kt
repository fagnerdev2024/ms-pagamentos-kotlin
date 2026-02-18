package com.fagnerdev.ms_pagamentos.entidades

enum class StatusPagamento {
    CRIADO,
    AUTORIZADO,
    CAPTURADO,
    CANCELADO,
    FALHOU,
    ESTORNADO
}