package com.fagnerdev.ms_pagamentos.dtos



import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RequisicaoCriarCliente(
    @field:NotBlank(message = "Nome é obrigatório")
    val nome: String,

    @field:NotBlank(message = "Email é obrigatório")
    @field:Email(message = "Email inválido")
    val email: String
)

