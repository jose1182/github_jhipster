package com.josecarlos.prueba.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesMapperTest {

    private SalesMapper salesMapper;

    @BeforeEach
    public void setUp() {
        salesMapper = new SalesMapperImpl();
    }
}
