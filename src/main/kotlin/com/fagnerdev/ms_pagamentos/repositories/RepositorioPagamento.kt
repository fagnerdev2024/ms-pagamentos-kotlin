package com.fagnerdev.ms_pagamentos.repositories


import com.fagnerdev.ms_pagamentos.entidades.Pagamento
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface RepositorioPagamento : JpaRepository<Pagamento, UUID>
