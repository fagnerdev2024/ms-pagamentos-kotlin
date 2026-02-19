package com.fagnerdev.ms_pagamentos.services





import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimentoDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaClienteDto
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimentoDto

interface CadastroService {

    fun criarCliente(req: RequisicaoCriarClienteDto): RespostaClienteDto

    fun criarEstabelecimento(req: RequisicaoCriarEstabelecimentoDto): RespostaEstabelecimentoDto
}

