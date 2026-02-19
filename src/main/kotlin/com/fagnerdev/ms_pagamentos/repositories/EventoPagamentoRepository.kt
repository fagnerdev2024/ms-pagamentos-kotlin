package com.fagnerdev.ms_pagamentos.repositories



import com.fagnerdev.ms_pagamentos.entities.EventoPagamentoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*


interface EventoPagamentoRepository : JpaRepository<EventoPagamentoEntity, Long> {

    fun findAllByPagamentoIdOrderByCriadoEmAsc(pagamentoId: UUID): List<EventoPagamentoEntity>
}

