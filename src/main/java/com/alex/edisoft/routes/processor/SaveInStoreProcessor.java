package com.alex.edisoft.routes.processor;

import com.alex.edisoft.model.FileEntity;
import com.alex.edisoft.repositories.FileRepository;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFileMessage;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Обработчик полученного из директории файла
 * Сохраняет файл в БД
 * @author gusev.aleksandr
 * @since 19.02.20
 */
@Component
public class SaveInStoreProcessor implements Processor {

    private FileRepository fileRepository;

    public SaveInStoreProcessor(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        //получаем изначальный файл
        File sourceFile = (File) ((GenericFileMessage) exchange.getIn()).getGenericFile().getFile();

        if (sourceFile.exists()) {
            try (FileInputStream fis = new FileInputStream(sourceFile)) {
                //получаем название файла
                String originalFileName = (String) exchange.getIn().getHeader(Exchange.FILE_NAME);

                //получаем содержимое изначального файла
                byte[] originalFileBody = new byte[fis.available()];
                IOUtils.read(fis, originalFileBody);

                //получаем содержимое измененного (xslt) файла
                byte[] transformedFileBody = ((String) exchange.getIn().getBody()).getBytes(StandardCharsets.UTF_8);

                FileEntity fileEntity = new FileEntity();
                fileEntity.setFileName(originalFileName);
                fileEntity.setSourceFileBody(originalFileBody);
                fileEntity.setFileBody(transformedFileBody);
                fileEntity.setDate(new Date());

                //сохраняем все в БД
                fileRepository.save(fileEntity);
            }
        }
    }
}
