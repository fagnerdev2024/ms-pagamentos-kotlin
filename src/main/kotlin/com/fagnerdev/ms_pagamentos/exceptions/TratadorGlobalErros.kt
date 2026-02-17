package com.fagnerdev.ms_pagamentos.exceptions


import org.springframework.web.HttpRequestMethodNotSupportedException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class TratadorGlobalErros {

    @ExceptionHandler(ExcecaoNaoEncontrado::class)
    fun tratarNaoEncontrado(ex: ExcecaoNaoEncontrado, req: HttpServletRequest): ResponseEntity<ErroApi> =
        construir(HttpStatus.NOT_FOUND, ex.message ?: "Recurso não encontrado", req.requestURI)

    @ExceptionHandler(ExcecaoRegraNegocio::class)
    fun tratarRegraNegocio(ex: ExcecaoRegraNegocio, req: HttpServletRequest): ResponseEntity<ErroApi> =
        construir(HttpStatus.UNPROCESSABLE_ENTITY, ex.message ?: "Regra de negócio violada", req.requestURI)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun tratarValidacao(ex: MethodArgumentNotValidException, req: HttpServletRequest): ResponseEntity<ErroApi> {
        val campos = ex.bindingResult.allErrors
            .filterIsInstance<FieldError>()
            .associate { it.field to (it.defaultMessage ?: "inválido") }

        val body = ErroApi(
            status = HttpStatus.BAD_REQUEST.value(),
            erro = HttpStatus.BAD_REQUEST.reasonPhrase,
            mensagem = "Erro de validação",
            caminho = req.requestURI,
            detalhes = mapOf("campos" to campos)
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun tratarGenerico(ex: Exception, req: HttpServletRequest): ResponseEntity<ErroApi> =
        construir(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Erro inesperado",
            req.requestURI,
            mapOf("tipo" to ex::class.simpleName)
        )

    private fun construir(
        status: HttpStatus,
        mensagem: String,
        caminho: String,
        detalhes: Map<String, Any?> = emptyMap()
    ): ResponseEntity<ErroApi> {
        val body = ErroApi(
            status = status.value(),
            erro = status.reasonPhrase,
            mensagem = mensagem,
            caminho = caminho,
            detalhes = detalhes
        )
        return ResponseEntity.status(status).body(body)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun tratarMetodoNaoSuportado(
        ex: HttpRequestMethodNotSupportedException,
        req: jakarta.servlet.http.HttpServletRequest
    ): org.springframework.http.ResponseEntity<ErroApi> =
        construir(
            status = org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED,
            mensagem = "Método HTTP não suportado para este endpoint",
            caminho = req.requestURI,
            detalhes = mapOf(
                "metodoRecebido" to ex.method,
                "metodosPermitidos" to (ex.supportedHttpMethods?.map(Any::toString) ?: emptyList<String>())
            )
        )


}
