package com.fagnerdev.ms_pagamentos.entidades

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Table(name = "pagamentos")
class Pagamento(
    @Id
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    val clientes: Clientes,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estabelecimento_id", nullable = false)
    val estabelecimento: Estabelecimento,

    @Column(nullable = false, precision = 19, scale = 2)
    val valor: BigDecimal,

    @Column(nullable = false, length = 3)
    val moeda: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val meio: MeioPagamento,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: StatusPagamento = StatusPagamento.CRIADO,

    @Column(nullable = false)
    val criadoEm: OffsetDateTime = OffsetDateTime.now(),

    @Column(nullable = false)
    var atualizadoEm: OffsetDateTime = OffsetDateTime.now()
) {
    fun tocarAtualizacao() {
        atualizadoEm = OffsetDateTime.now()
    }
}