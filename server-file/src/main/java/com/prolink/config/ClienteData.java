package com.prolink.config;

import com.prolink.dao.ClienteDAO;
import com.prolink.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class ClienteData {
    Logger logger = LoggerFactory.getLogger(getClass());
    private static Properties properties;
    private static ClienteData instance;
    private Path path = Paths.get("clientes.properties");
    private static Set<Cliente> clientes = new HashSet<>();

    public static ClienteData getInstance(){
        if(instance==null) instance = new ClienteData();
        return instance;
    }
    private ClienteData(){
        properties = new Properties();
        try{
            if(Files.notExists(path)) Files.createFile(path);
            refreshDatabase();
            load();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void refreshDatabase(){
            ClienteDAO dao = new ClienteDAO();
            List<Cliente> clientes = dao.refreshClientes();
            save(clientes);
    }

    private void load(){
        try {
            InputStream stream = new FileInputStream(path.toFile());
            properties = new Properties();
            properties.load(stream);
            for (Object key : properties.keySet()) {
                Cliente cliente = new Cliente();
                cliente.getFileProperties(key.toString(), properties.getProperty(key.toString()));
                cliente.setNomeFormatado(novoNome(cliente.getNome()));
                cliente.setIdFormatado(novoId(cliente.getId()));
                clientes.add(cliente);
            }
            stream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void save(List<Cliente> list){
        if(list==null) return;
        try {
            Properties properties = new Properties();
            OutputStream outputStream = new FileOutputStream(path.toFile());
            for (Cliente c : list)
                properties.setProperty(String.valueOf(c.getId()), c.toFile());
            properties.store(outputStream, "Properties");
            outputStream.close();
            load();
        }catch (IOException e){ e.printStackTrace();}
    }

    public Set<Cliente> getClientes() {
        return clientes;
    }
    private String novoId(int id){
        int size = String.valueOf(id).length();
        return size==1?"000"+id:
                (size==2?"00"+id:
                        (size==3?"0"+id:String.valueOf(id))
                );
    }
    private String novoNome(String nome) {
        String novoNome = replace(nome);
        String[] valor = novoNome.toUpperCase().split(" ");
        String v1 = "";
        int limite = 20;
        for(int i=0; i<valor.length;i++){
            int size = v1.length();
            if(size+valor[i].length()>=limite) break;
            if(contains(valor[i]) && valor[i+1]!=null){
                if(!contains(valor[i+1]))
                    v1 += size+valor[i].length()+valor[i+1].length()<limite? valor[i]+" "+valor[i+1]+" ": "";
            }
            else v1 += size+valor[i].length()<limite? valor[i]+" ": "";
        }
        return v1.trim();
    }
    private String replace(String valor){
        String newValor = valor;
        //String chars = "\"!@#$%¨&*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";
        String chars = "\"!@#$%¨*()_+{}|<>:?'-=[]\\,.;/£¢¬§ªº°";
        for(char b : chars.toCharArray())
            newValor = newValor.replace(String.valueOf(b), " ");
        newValor = newValor.replace("   "," ");
        newValor = newValor.replace("  "," ");
        return newValor;
    }
    private boolean contains(String value){
        String[] array = new String[]{"DE","EM","A","E"};
        for(String s : array){
            return value.trim().equals(s);
        }
        return false;
    }
}
