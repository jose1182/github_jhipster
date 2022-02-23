package com.josecarlos.prueba.web.rest;

import com.josecarlos.prueba.repository.SalesRepository;
import com.josecarlos.prueba.service.SalesQueryService;
import com.josecarlos.prueba.service.SalesService;
import com.josecarlos.prueba.service.criteria.SalesCriteria;
import com.josecarlos.prueba.service.dto.SalesDTO;
import com.josecarlos.prueba.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.josecarlos.prueba.domain.Sales}.
 */
@RestController
@RequestMapping("/api")
public class SalesResource {

    private final Logger log = LoggerFactory.getLogger(SalesResource.class);

    private static final String ENTITY_NAME = "sales";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SalesService salesService;

    private final SalesRepository salesRepository;

    private final SalesQueryService salesQueryService;

    public SalesResource(SalesService salesService, SalesRepository salesRepository, SalesQueryService salesQueryService) {
        this.salesService = salesService;
        this.salesRepository = salesRepository;
        this.salesQueryService = salesQueryService;
    }

    /**
     * {@code POST  /sales} : Create a new sales.
     *
     * @param salesDTO the salesDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new salesDTO, or with status {@code 400 (Bad Request)} if the sales has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/sales")
    public ResponseEntity<SalesDTO> createSales(@RequestBody SalesDTO salesDTO) throws URISyntaxException {
        log.debug("REST request to save Sales : {}", salesDTO);
        if (salesDTO.getId() != null) {
            throw new BadRequestAlertException("A new sales cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SalesDTO result = salesService.save(salesDTO);
        return ResponseEntity
            .created(new URI("/api/sales/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /sales/:id} : Updates an existing sales.
     *
     * @param id the id of the salesDTO to save.
     * @param salesDTO the salesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesDTO,
     * or with status {@code 400 (Bad Request)} if the salesDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the salesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/sales/{id}")
    public ResponseEntity<SalesDTO> updateSales(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SalesDTO salesDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Sales : {}, {}", id, salesDTO);
        if (salesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SalesDTO result = salesService.save(salesDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /sales/:id} : Partial updates given fields of an existing sales, field will ignore if it is null
     *
     * @param id the id of the salesDTO to save.
     * @param salesDTO the salesDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated salesDTO,
     * or with status {@code 400 (Bad Request)} if the salesDTO is not valid,
     * or with status {@code 404 (Not Found)} if the salesDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the salesDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/sales/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<SalesDTO> partialUpdateSales(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody SalesDTO salesDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Sales partially : {}, {}", id, salesDTO);
        if (salesDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, salesDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!salesRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SalesDTO> result = salesService.partialUpdate(salesDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, salesDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /sales} : get all the sales.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sales in body.
     */
    @GetMapping("/sales")
    public ResponseEntity<List<SalesDTO>> getAllSales(SalesCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Sales by criteria: {}", criteria);
        Page<SalesDTO> page = salesQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /sales/count} : count all the sales.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/sales/count")
    public ResponseEntity<Long> countSales(SalesCriteria criteria) {
        log.debug("REST request to count Sales by criteria: {}", criteria);
        return ResponseEntity.ok().body(salesQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /sales/:id} : get the "id" sales.
     *
     * @param id the id of the salesDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the salesDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/sales/{id}")
    public ResponseEntity<SalesDTO> getSales(@PathVariable Long id) {
        log.debug("REST request to get Sales : {}", id);
        Optional<SalesDTO> salesDTO = salesService.findOne(id);
        return ResponseUtil.wrapOrNotFound(salesDTO);
    }

    /**
     * {@code DELETE  /sales/:id} : delete the "id" sales.
     *
     * @param id the id of the salesDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/sales/{id}")
    public ResponseEntity<Void> deleteSales(@PathVariable Long id) {
        log.debug("REST request to delete Sales : {}", id);
        salesService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
