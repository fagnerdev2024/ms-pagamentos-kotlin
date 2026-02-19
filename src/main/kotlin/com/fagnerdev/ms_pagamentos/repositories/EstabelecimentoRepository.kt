package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entities.EstabelecimentoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface EstabelecimentoRepository : JpaRepository<EstabelecimentoEntity, Long> {

    fun existsByDocumento(documento: String): Boolean
}
