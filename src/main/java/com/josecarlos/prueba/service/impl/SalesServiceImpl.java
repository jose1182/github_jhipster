package com.josecarlos.prueba.service.impl;

import com.josecarlos.prueba.domain.Sales;
import com.josecarlos.prueba.repository.SalesRepository;
import com.josecarlos.prueba.service.SalesService;
import com.josecarlos.prueba.service.dto.SalesDTO;
import com.josecarlos.prueba.service.mapper.SalesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Sales}.
 */
@Service
@Transactional
public class SalesServiceImpl implements SalesService {

    private final Logger log = LoggerFactory.getLogger(SalesServiceImpl.class);

    private final SalesRepository salesRepository;

    private final SalesMapper salesMapper;

    public SalesServiceImpl(SalesRepository salesRepository, SalesMapper salesMapper) {
        this.salesRepository = salesRepository;
        this.salesMapper = salesMapper;
    }

    @Override
    public SalesDTO save(SalesDTO salesDTO) {
        log.debug("Request to save Sales : {}", salesDTO);
        Sales sales = salesMapper.toEntity(salesDTO);
        sales = salesRepository.save(sales);
        return salesMapper.toDto(sales);
    }

    @Override
    public Optional<SalesDTO> partialUpdate(SalesDTO salesDTO) {
        log.debug("Request to partially update Sales : {}", salesDTO);

        return salesRepository
            .findById(salesDTO.getId())
            .map(existingSales -> {
                salesMapper.partialUpdate(existingSales, salesDTO);

                return existingSales;
            })
            .map(salesRepository::save)
            .map(salesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SalesDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sales");
        return salesRepository.findAll(pageable).map(salesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SalesDTO> findOne(Long id) {
        log.debug("Request to get Sales : {}", id);
        return salesRepository.findById(id).map(salesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Sales : {}", id);
        salesRepository.deleteById(id);
    }
}
