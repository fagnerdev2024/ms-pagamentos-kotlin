package com.fagnerdev.ms_pagamentos.entidades

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "clientes")
class Clientes(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val nome: String = "",

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    val criadoEm: OffsetDateTime = OffsetDateTime.now()
)