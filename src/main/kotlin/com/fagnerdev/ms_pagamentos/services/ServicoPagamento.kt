package com.fagnerdev.ms_pagamentos.services




import com.fagnerdev.ms_pagamentos.dtos.RequisicaoAlterarStatusPagamento
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaEventoPagamento
import com.fagnerdev.ms_pagamentos.dtos.RespostaPagamento
import java.util.UUID

interface ServicoPagamento {
    fun criar(requisicao: RequisicaoCriarPagamento): RespostaPagamento
    fun buscarPorId(id: UUID): RespostaPagamento
    fun alterarStatus(id: UUID, requisicao: RequisicaoAlterarStatusPagamento): RespostaPagamento
    fun linhaDoTempo(id: UUID): List<RespostaEventoPagamento>
}
