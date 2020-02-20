package com.alex.edisoft.repositories;

import com.alex.edisoft.model.FileEntity;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Репозиторий, предназначенный для работы с БД
 * @author gusev.aleksandr
 * @since 19.02.20
 */
public interface FileRepository extends CrudRepository<FileEntity, Long>, JpaSpecificationExecutor<FileEntity> {

    /**
     * Сохранить в БД файл
     * @param entity файл
     * @return сохраненный файл
     */
    @Override
    FileEntity save(FileEntity entity);

    /**
     * Получить сохраненные в БД файлы
     * @return список файлов
     */
    @Override
    Iterable<FileEntity> findAll();

    /**
     * Получить файл из БД по id
     * @return полученный файл
     */
    @Override
    Optional<FileEntity> findById(Long aLong);

    /**
     * Получить сохраненные в БД файлы без содержимого файлов
     * @return список файлов
     */
    @Query(value = "select id, file_name, null as file_body, null as source_file_body from file_entity", nativeQuery = true)
    Iterable<FileEntity> getAllWithoutBody();
}
