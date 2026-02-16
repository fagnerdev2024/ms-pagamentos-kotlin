package com.fagnerdev.ms_pagamentos.entidades

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "eventos_pagamento")
class EventoPagamento(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val pagamentoId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: StatusPagamento,

    @Column(nullable = false)
    val mensagem: String = "",

    @Column(nullable = false)
    val criadoEm: OffsetDateTime = OffsetDateTime.now()
)