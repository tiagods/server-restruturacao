package com.tiagods.clientesender.model;

import lombok.Data;

import java.nio.file.Path;
import java.util.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EmailDto {
    @NotNull (message = "From email address cannot be null")
    @Email (message = "From email address is not valid")
    private String de;

    @NotEmpty (message = "To email address cannot be empty")
    @Email (message = "To email address is not valid")
    private String [] para;

    @Email (message = "Cc email address is not valid")
    private String [] cc = new String[]{};

    @Email (message = "Bcc email address is not valid")
    private String [] bcc = new String[]{};

    @NotNull (message = "Email subject cannot be null")
    private String assunto;

    @NotNull (message = "Email message cannot be null")
    private String message = "";

    private boolean isTemplate = true;

    private String pathToAttachment;

    private String attachmentName;

    private Map<String, Path> attachs = new HashMap<>();

    private String templateName = "email-inlineimage.html";

    //private String templateLocation="email-inlineimage.html";

    private Map<String, Object> parameterMap = new HashMap<>();

    private Map<String, Object> staticResourceMap=new HashMap<>();

    private String emailedMessage;

    private String outrosAnexos;

    public EmailDto() { }

    public EmailDto(String de, String toList, String assunto, String message) {
        this();
        this.de = de;
        this.assunto = assunto;
        this.message = message;
        this.para = splitByComma(toList);
    }

    public EmailDto(String de, String toList, String ccList) {
        this();
        this.de = de;
        this.para = splitByComma(toList);
        this.cc = splitByComma(ccList);
    }

    public EmailDto(String de, String toList) {
        this();
        this.de = de;
        this.para = splitByComma(toList);
    }

    public EmailDto(String de, String toList, String ccList, String assunto, String message) {
        this();
        this.de = de;
        this.assunto = assunto;
        this.message = message;
        this.para = splitByComma(toList);
        this.cc = splitByComma(ccList);
    }

    // getters and setters not mentioned for brevity
    private String[] splitByComma(String toMultiple) {

        String[] split = toMultiple.split(",");

        Set<String> toSplit = new TreeSet<>();
        for(String s : split) {
            String[] stp = s.split(";");
            for(String p : stp) {
                toSplit.add(p.trim());
            }
        }
        String[] newArray = new String[toSplit.size()];
        int i = 0;
        for(String value : toSplit){
            newArray[i] = value;
            i++;
        }

        return newArray;
    }

    public boolean isHasAttachment() {
        return !attachs.isEmpty();
    }

    @Override
    public String toString() {
        return "EmailDto [" + (de != null ? "from=" + de + ", " : "")
                + (para != null ? "to=" + Arrays.toString(para) + ", " : "")
                + (cc != null ? "cc=" + Arrays.toString(cc) + ", " : "")
                + (bcc != null ? "bcc=" + Arrays.toString(bcc) + ", " : "")
                + (assunto != null ? "assunto=" + assunto + ", " : "")
                + (message != null ? "message=" + message + ", " : "")
                + isTemplate + ", hasAttachment=" + isHasAttachment() + ", "
                + (pathToAttachment != null ? "pathToAttachment=" + pathToAttachment + ", " : "")
                + (attachmentName != null ? "attachmentName=" + attachmentName + ", " : "")
                + (templateName != null ? "templateName=" + templateName + ", " : "")
                + (parameterMap != null ? "parameterMap=" + parameterMap + ", " : "")
                + (emailedMessage != null ? "emailedMessage=" + emailedMessage : "") + "]";
    }
}
