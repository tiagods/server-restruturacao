package com.prolink.olders.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class DBConfig {
    Logger logger = LoggerFactory.getLogger(getClass());
    private static Properties properties;
    private static DBConfig instance;
    String URL = "";
    String USER = "";
    String PASSWORD = "";
    String DBDRIVER = "";

    public DBConfig(){
        properties = new Properties();
        try{
            InputStream stream = getClass()
                    .getClassLoader()
                    .getResource ("database.properties")
                    .openStream();

            load(stream);
            URL = properties.getProperty("url");
            USER = properties.getProperty("user");
            PASSWORD = properties.getProperty("password");
            DBDRIVER = properties.getProperty("driver");
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void load(InputStream stream) throws IOException{
        properties.load(stream);
        stream.close();
    }

    public String getURL() {
        return URL;
    }

    public String getUSER() {
        return USER;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String getDBDRIVER() {
        return DBDRIVER;
    }
}
