package com.tiagods.obrigacoes.services;

import com.tiagods.obrigacoes.exception.ParametroIncorretoException;
import com.tiagods.obrigacoes.exception.ParametroNotFoundException;
import com.tiagods.obrigacoes.exception.PathInvalidException;
import com.tiagods.obrigacoes.model.Obrigacao;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class ValidacaoObrigacaoTest extends ObrigacaoPreparedServiceTest {

    @Test
    public void testarComAnoObrigatorio(){
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.IRPF);
        obrigacao.setAno(null);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroIncorretoException | PathInvalidException e) {
            Assert.fail();
        } catch (ParametroNotFoundException ex){
            Assert.assertEquals("O parametro ano é obrigatório para essa obrigação", ex.getMessage());
        }
    }

    @Test
    public void testarComMesNaoNecesario() {
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.IRPF);
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | PathInvalidException e) {
            Assert.fail();
        } catch (ParametroIncorretoException ex){
            Assert.assertEquals("Obrigação "+obrigacao.getTipo().getDescricao()+" é anual e o mês não deve ser informado", ex.getMessage());
        }
    }

    @Test
    public void testarComPastaIncorreta(){
        Obrigacao obrigacao = montarObrigacaoFakeAnoMesCliente(Obrigacao.Tipo.PROLINKDIGITAL);
        obrigacao.setDirForJob("c:/Temp/asdfjalsdj");
        try {
            String cid = "cid-test";
            moverPastaClientesEValidar(cid, obrigacao);
        } catch (IOException | ParametroNotFoundException | ParametroIncorretoException e) {
            Assert.fail();
        } catch (PathInvalidException ex) {
            Assert.assertEquals("O diretorio informado é invalido para essa obrigação", ex.getMessage());
        }
    }
}
