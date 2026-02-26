package com.fagnerdev.ms_pagamentos.exceptions

import com.fagnerdev.ms_pagamentos.entities.StatusPagamentoEnum
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
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ExceptionNaoEncontrado::class)
    fun naoEncontrado(ex: ExceptionNaoEncontrado, httpServletRequest: HttpServletRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.message ?: "Recurso não encontrado")
        problemDetail.title = "Não encontrado"
        problemDetail.setProperty("path", httpServletRequest.requestURI)
        problemDetail.setProperty("timestamp", OffsetDateTime.now())
        return problemDetail
    }

    @ExceptionHandler(ExceptionRegraNegocio::class)
    fun regraNegocio(ex: ExceptionRegraNegocio, httpServletRequest: HttpServletRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message ?: "Violação de regra de negócio")
        problemDetail.title = "Regra de negócio"
        problemDetail.setProperty("path", httpServletRequest.requestURI)
        problemDetail.setProperty("timestamp", OffsetDateTime.now())
        return problemDetail
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun validacao(ex: MethodArgumentNotValidException, httpServletRequest: HttpServletRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = "Erro de validação"
        problemDetail.detail = "Um ou mais campos estão inválidos"
        problemDetail.setProperty("path", httpServletRequest.requestURI)
        problemDetail.setProperty("timestamp", OffsetDateTime.now())

        val erros = ex.bindingResult.allErrors.map { err ->
            val fe = err as? FieldError
            mapOf(
                "campo" to (fe?.field ?: err.objectName),
                "mensagem" to (err.defaultMessage ?: "inválido")
            )
        }
        problemDetail.setProperty("erros", erros)
        return problemDetail
    }

    /**
     * Banco/constraint (ex: UNIQUE, PK duplicada, FK, etc).
     * Importante: não vaza SQL/stacktrace pro cliente.
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun integridade(ex: DataIntegrityViolationException, httpServletRequest: HttpServletRequest): ProblemDetail {
        log.warn("Violação de integridade em {}: {}", httpServletRequest.requestURI, ex.message, ex)

        val problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        problemDetail.title = "Conflito de dados"
        problemDetail.detail = "Não foi possível concluir a operação por conflito de dados (duplicidade ou restrição do banco)."
        problemDetail.setProperty("path", httpServletRequest.requestURI)
        problemDetail.setProperty("timestamp", OffsetDateTime.now())
        return problemDetail
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



    @ExceptionHandler(ExceptionTransicaoPagamentoInvalido::class)
    fun transicaoInvalida(ex: ExceptionTransicaoPagamentoInvalido, request: HttpServletRequest): ProblemDetail {
        val pd = ProblemDetail.forStatus(HttpStatus.CONFLICT)
        pd.title = "Transição de status inválida"

        val detail = montarMensagemTransicao(ex.statusAtual, ex.statusSolicitado, ex.proximosPermitidos)
        pd.detail = detail

        // Campos estruturados (excelente pra frontend e testes)
        pd.setProperty("currentStatus", ex.statusAtual.name)
        pd.setProperty("requestedStatus", ex.statusSolicitado.name)
        pd.setProperty("allowedNextStatuses", ex.proximosPermitidos.map { it.name })
        pd.setProperty("expectedFlow", fluxoEsperado())

        pd.setProperty("path", request.requestURI)
        pd.setProperty("timestamp", OffsetDateTime.now())
        return pd
    }

    private fun montarMensagemTransicao(
        atual: StatusPagamentoEnum,
        solicitado: StatusPagamentoEnum,
        permitidos: Set<StatusPagamentoEnum>
    ): String {
        val pode = if (permitidos.isEmpty()) "nenhuma (estado final)" else permitidos.joinToString(", ")

        val explicacao = when (atual) {
            StatusPagamentoEnum.CAPTURADO -> when (solicitado) {
                StatusPagamentoEnum.CANCELADO ->
                    "O pagamento já foi CAPTURADO (cobrança efetivada). Cancelamento só é permitido antes da captura. " +
                            "Depois de capturado, o fluxo correto é ESTORNAR."

                else ->
                    "O pagamento já foi CAPTURADO (cobrança efetivada). A partir daqui, o próximo passo permitido é ESTORNAR."
            }

            StatusPagamentoEnum.ESTORNADO ->
                "O pagamento já foi ESTORNADO (refund realizado). Após estorno, o pagamento é encerrado e não permite novas transições."

            StatusPagamentoEnum.CANCELADO ->
                "O pagamento já foi CANCELADO antes da captura. Cancelamento é estado final e não permite novas transições."

            StatusPagamentoEnum.FALHOU ->
                "O pagamento está como FALHOU. Estado final: não permite continuar o fluxo."

            StatusPagamentoEnum.CRIADO -> when (solicitado) {
                StatusPagamentoEnum.CAPTURADO ->
                    "Não é possível CAPTURAR um pagamento em CRIADO. Fluxo correto: CRIADO -> AUTORIZADO -> CAPTURADO."

                StatusPagamentoEnum.ESTORNADO ->
                    "Não é possível ESTORNAR um pagamento em CRIADO. Estorno só acontece após CAPTURA."

                else ->
                    "Em CRIADO você pode AUTORIZAR, CANCELAR ou marcar como FALHOU."
            }

            StatusPagamentoEnum.AUTORIZADO -> when (solicitado) {
                StatusPagamentoEnum.ESTORNADO ->
                    "Não é possível ESTORNAR um pagamento apenas AUTORIZADO. Estorno só acontece após CAPTURA. " +
                            "Se não vai capturar, o correto é CANCELAR."

                StatusPagamentoEnum.CRIADO ->
                    "Não é possível voltar de AUTORIZADO para CRIADO. O fluxo é progressivo."

                else ->
                    "Em AUTORIZADO você pode CAPTURAR, CANCELAR ou marcar como FALHOU."
            }
        }

        return buildString {
            appendLine("Transição inválida.")
            appendLine("Status atual: $atual")
            appendLine("Status solicitado: $solicitado")
            appendLine()
            appendLine("Motivo: $explicacao")
            appendLine()
            appendLine("Próximos status permitidos a partir de $atual: $pode")
            appendLine()
            appendLine("Fluxo esperado:")
            append(fluxoEsperado().joinToString("\n") { "- $it" })
        }
    }

    private fun fluxoEsperado(): List<String> =
        listOf(
            "CRIADO -> AUTORIZADO -> CAPTURADO -> ESTORNADO",
            "AUTORIZADO -> CANCELADO (cancelamento antes da captura)",
            "CRIADO -> CANCELADO (cancelamento antes da autorização)",
            "CRIADO/AUTORIZADO -> FALHOU (erro no processo)"
        )
}
