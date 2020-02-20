package com.alex.edisoft.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.*;
import java.util.Date;

/**
 * Сущность файла для хранения в БД
 * @author gusev.aleksandr
 * @since 19.02.20
 */
@Entity
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    private String fileName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Lob
    private byte[] sourceFileBody;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Lob
    private byte[] fileBody;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getSourceFileBody() {
        return sourceFileBody;
    }

    public void setSourceFileBody(byte[] sourceFileBody) {
        this.sourceFileBody = sourceFileBody;
    }

    public byte[] getFileBody() {
        return fileBody;
    }

    public void setFileBody(byte[] fileBody) {
        this.fileBody = fileBody;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
