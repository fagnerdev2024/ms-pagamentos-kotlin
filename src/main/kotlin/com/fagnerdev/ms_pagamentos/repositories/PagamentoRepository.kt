package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entities.PagamentoEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PagamentoRepository : JpaRepository<PagamentoEntity, UUID>
