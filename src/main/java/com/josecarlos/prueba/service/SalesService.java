package com.josecarlos.prueba.service;

import com.josecarlos.prueba.service.dto.SalesDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.josecarlos.prueba.domain.Sales}.
 */
public interface SalesService {
    /**
     * Save a sales.
     *
     * @param salesDTO the entity to save.
     * @return the persisted entity.
     */
    SalesDTO save(SalesDTO salesDTO);

    /**
     * Partially updates a sales.
     *
     * @param salesDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<SalesDTO> partialUpdate(SalesDTO salesDTO);

    /**
     * Get all the sales.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<SalesDTO> findAll(Pageable pageable);

    /**
     * Get the "id" sales.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<SalesDTO> findOne(Long id);

    /**
     * Delete the "id" sales.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
