package com.fagnerdev.ms_pagamentos.repositories



import com.fagnerdev.ms_pagamentos.entidades.EventoPagamento
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RepositorioEventoPagamento : JpaRepository<EventoPagamento, Long> {

    fun findAllByPagamentoIdOrderByCriadoEmAsc(pagamentoId: UUID): List<EventoPagamento>
}
