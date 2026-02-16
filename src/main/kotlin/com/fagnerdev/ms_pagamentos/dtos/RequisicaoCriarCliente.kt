package com.fagnerdev.ms_pagamentos.dtos


import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class RequisicaoCriarCliente(
    @field:NotBlank(message = "nome é obrigatório")
    val nome: String,

    @field:NotBlank(message = "email é obrigatório")
    @field:Email(message = "email inválido")
    val email: String
)


