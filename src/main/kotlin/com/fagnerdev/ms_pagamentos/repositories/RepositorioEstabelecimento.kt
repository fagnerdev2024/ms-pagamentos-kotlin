package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entidades.Estabelecimento
import org.springframework.data.jpa.repository.JpaRepository

interface RepositorioEstabelecimento : JpaRepository<Estabelecimento, Long> {

    fun existsByDocumento(documento: String): Boolean
}
