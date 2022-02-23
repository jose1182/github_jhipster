package com.josecarlos.prueba.service;

import com.josecarlos.prueba.domain.*; // for static metamodels
import com.josecarlos.prueba.domain.Sales;
import com.josecarlos.prueba.repository.SalesRepository;
import com.josecarlos.prueba.service.criteria.SalesCriteria;
import com.josecarlos.prueba.service.dto.SalesDTO;
import com.josecarlos.prueba.service.mapper.SalesMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Sales} entities in the database.
 * The main input is a {@link SalesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link SalesDTO} or a {@link Page} of {@link SalesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class SalesQueryService extends QueryService<Sales> {

    private final Logger log = LoggerFactory.getLogger(SalesQueryService.class);

    private final SalesRepository salesRepository;

    private final SalesMapper salesMapper;

    public SalesQueryService(SalesRepository salesRepository, SalesMapper salesMapper) {
        this.salesRepository = salesRepository;
        this.salesMapper = salesMapper;
    }

    /**
     * Return a {@link List} of {@link SalesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SalesDTO> findByCriteria(SalesCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Sales> specification = createSpecification(criteria);
        return salesMapper.toDto(salesRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link SalesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SalesDTO> findByCriteria(SalesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Sales> specification = createSpecification(criteria);
        return salesRepository.findAll(specification, page).map(salesMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(SalesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Sales> specification = createSpecification(criteria);
        return salesRepository.count(specification);
    }

    /**
     * Function to convert {@link SalesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Sales> createSpecification(SalesCriteria criteria) {
        Specification<Sales> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Sales_.id));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Sales_.title));
            }
        }
        return specification;
    }
}
