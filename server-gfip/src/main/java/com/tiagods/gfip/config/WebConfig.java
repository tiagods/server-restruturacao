package com.tiagods.gfip.config;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Tesseract1;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class WebConfig {

    //@Bean
    public ITesseract tesserac1t(){
        Tesseract instance = new Tesseract();
        instance.setLanguage("por");//por,eng
        return instance;
    }
}
