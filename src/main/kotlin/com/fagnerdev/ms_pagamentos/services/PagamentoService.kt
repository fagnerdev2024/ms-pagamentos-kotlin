package com.fagnerdev.ms_pagamentos.services




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamentoDto
import java.util.UUID

interface PagamentoService {
    fun criar(requisicao: RequisicaoCriarPagamentoDto): RespostaPagamentoDto
    fun buscarPorId(id: UUID): RespostaPagamentoDto
    fun alterarStatus(id: UUID, requisicao: RequisicaoAlterarStatusPagamentoDto): RespostaPagamentoDto
    fun linhaDoTempo(id: UUID): List<RespostaEventoPagamentoDto>
}
