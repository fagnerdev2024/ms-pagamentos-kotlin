package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entities.ClienteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ClienteRepository : JpaRepository<ClienteEntity, Long> {

    fun existsByEmail(email: String): Boolean
}
