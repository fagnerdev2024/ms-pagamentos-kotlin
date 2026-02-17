package com.fagnerdev.ms_pagamentos.exceptions


import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.core.convert.ConversionFailedException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import tools.jackson.databind.exc.InvalidFormatException
import java.time.OffsetDateTime

@RestControllerAdvice
class ApiExceptionHandler {

    private val log = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    @ExceptionHandler(ExcecaoNaoEncontrado::class)
    fun naoEncontrado(ex: ExcecaoNaoEncontrado, request: HttpServletRequest): ProblemDetail {
        val pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Recurso não encontrado")
        pd.title = "Não encontrado"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    @ExceptionHandler(ExcecaoRegraNegocio::class)
    fun regraNegocio(ex: ExcecaoRegraNegocio, request: HttpServletRequest): ProblemDetail {
        val pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Violação de regra de negócio")
        pd.title = "Regra de negócio"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacao(ex: MethodArgumentNotValidException, request: HttpServletRequest): ProblemDetail {
        val pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        pd.title = "Erro de validação"
        pd.detail = "Um ou mais campos estão inválidos"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())

        val erros = ex.bindingResult.allErrors.map { err ->
            val fe = err as? FieldError
            mapOf(
                "campo" to (fe?.field ?: err.objectName),
                "mensagem" to (err.defaultMessage ?: "inválido")
            )
        }
        pd.setProperty("erros", erros)
        return pd
    }

    /**
     * Banco/constraint (ex: UNIQUE, PK duplicada, FK, etc).
     * Importante: não vaza SQL/stacktrace pro cliente.
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun integridade(ex: DataIntegrityViolationException, request: HttpServletRequest): ProblemDetail {
        // Log completo fica no servidor
        log.warn("Violação de integridade em {}: {}", request.requestURI, ex.message, ex)

        val pd = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        pd.title = "Conflito de dados"
        pd.detail = "Não foi possível concluir a operação por conflito de dados (duplicidade ou restrição do banco)."
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    /**
     * Fallback: evita 500 com stacktrace no response.
     */
    @ExceptionHandler(Exception::class)
    fun generica(ex: Exception, request: HttpServletRequest): ProblemDetail {
        log.error("Erro inesperado em {}: {}", request.requestURI, ex.message, ex)

        val pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        pd.title = "Erro interno"
        pd.detail = "Ocorreu um erro inesperado. Tente novamente mais tarde."
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun jsonInvalido(ex: HttpMessageNotReadableException, request: HttpServletRequest): ProblemDetail {
        val detalhe = extrairDetalheEnumInvalido(ex) ?: "JSON inválido ou campos com formato incorreto."

        val pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhe)
        pd.title = "Requisição inválida"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    private fun extrairDetalheEnumInvalido(ex: HttpMessageNotReadableException): String? {
        val cause = ex.cause
        if (cause is InvalidFormatException) {
            val alvo = cause.targetType
            if (alvo != null && alvo.isEnum) {
                val valor = cause.value?.toString() ?: "valor inválido"
                val permitidos = alvo.enumConstants?.joinToString(", ") { it.toString() } ?: ""
                return "Valor inválido: '$valor'. Valores permitidos: $permitidos."
            }
        }
        return null
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun parametroInvalido(ex: MethodArgumentTypeMismatchException, request: HttpServletRequest): ProblemDetail {
        val nome = ex.name // "id"
        val valor = ex.value?.toString() ?: "null"
        val esperado = ex.requiredType?.simpleName ?: "tipo esperado"

        val pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Parâmetro '$nome' inválido: '$valor'. Esperado: $esperado."
        )
        pd.title = "Parâmetro inválido"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    @ExceptionHandler(ConversionFailedException::class)
    fun conversaoInvalida(ex: ConversionFailedException, request: HttpServletRequest): ProblemDetail {
        val valor = ex.value?.toString() ?: "null"
        val alvo = ex.targetType?.type?.simpleName ?: "tipo esperado"

        val pd = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Valor inválido: '$valor'. Esperado: $alvo."
        )
        pd.title = "Parâmetro inválido"
        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }
}
