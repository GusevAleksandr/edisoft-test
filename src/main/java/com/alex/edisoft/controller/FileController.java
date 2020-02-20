package com.alex.edisoft.controller;

import com.alex.edisoft.model.FileEntity;
import com.alex.edisoft.repositories.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Рест-контроллер для получения информации о загруженных файлах
 * @author gusev.aleksandr
 * @since 19.02.20
 */
@RequestMapping("/files")
@Controller
public class FileController {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);

    private FileRepository fileRepository;

    public FileController(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Iterable<FileEntity>> getFiles() {
        return ResponseEntity.ok(fileRepository.getAllWithoutBody());
    }

    @RequestMapping(value = "/xml/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getFileById(@PathVariable("id") String id) {
        try {
            return fileRepository.findById(new Long(id))
                    .map(this::createResponseWithFile)
                    .orElse(ResponseEntity.ok().build());
        } catch (NumberFormatException ex) {
            log.error("Ошибка приведения значения {} в методе getFileById", id, ex);
            return ResponseEntity.ok().build();
        }
    }

    private ResponseEntity<byte[]> createResponseWithFile(FileEntity fileEntity) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_XML);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + fileEntity.getFileName());
        header.setContentLength(fileEntity.getFileBody().length);

        return new ResponseEntity<byte[]>(fileEntity.getFileBody(), header, HttpStatus.OK);
    }
}
