package com.fagnerdev.ms_pagamentos.services





import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarCliente
import com.fagnerdev.ms_pagamentos.dtos.RequisicaoCriarEstabelecimento
import com.fagnerdev.ms_pagamentos.dtos.RespostaCliente
import com.fagnerdev.ms_pagamentos.dtos.RespostaEstabelecimento

interface ServicoCadastro {

    fun criarCliente(req: RequisicaoCriarCliente): RespostaCliente

    fun criarEstabelecimento(req: RequisicaoCriarEstabelecimento): RespostaEstabelecimento
}

