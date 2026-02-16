package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entidades.Clientes
import org.springframework.data.jpa.repository.JpaRepository

interface RepositorioCliente : JpaRepository<Clientes, Long> {

    fun existsByEmail(email: String): Boolean
}
