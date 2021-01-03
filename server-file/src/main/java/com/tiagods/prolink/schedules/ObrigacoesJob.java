package com.tiagods.prolink.schedules;

import com.tiagods.prolink.config.ObrigacaoConfig;
import com.tiagods.prolink.service.ObrigacaoPreparedService;
import com.tiagods.prolink.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class ObrigacoesJob {

    @Autowired
    private ObrigacaoPreparedService obrigacaoPreparedService;
    @Autowired
    private ObrigacaoConfig obrigacaoConfig;

    @Scheduled(cron = "0 0 1,23 1 * ?")
    //@Scheduled(cron = "0 0 3 ? * MON-FRI")
    public void executar() {
        String cid = UUID.randomUUID().toString();
        log.info("Correlation: [{}]. Iniciando processo - Movendo por agendamento", cid);

        Set<LocalDate> datas = DateUtils.gerarPeriodosProcessamento(null);

        obrigacaoConfig.getObrigacoes().forEach((key, value)->{
            obrigacaoPreparedService.iniciarMovimentacaoPorObrigacaoGeral(cid, key, value, datas);
        });
    }
}
