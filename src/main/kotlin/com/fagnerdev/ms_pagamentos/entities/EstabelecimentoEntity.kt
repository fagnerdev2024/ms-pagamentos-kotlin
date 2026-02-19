package com.fagnerdev.ms_pagamentos.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.OffsetDateTime

@Entity
@Table(name = "estabelecimentos")
class EstabelecimentoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val razaoSocial: String = "",

    @Column(nullable = false, unique = true)
    val documento: String = "",

    @Column(nullable = false)
    val criadoEm: OffsetDateTime = OffsetDateTime.now()
)