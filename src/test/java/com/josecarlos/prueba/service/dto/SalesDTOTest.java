package com.josecarlos.prueba.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.josecarlos.prueba.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SalesDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SalesDTO.class);
        SalesDTO salesDTO1 = new SalesDTO();
        salesDTO1.setId(1L);
        SalesDTO salesDTO2 = new SalesDTO();
        assertThat(salesDTO1).isNotEqualTo(salesDTO2);
        salesDTO2.setId(salesDTO1.getId());
        assertThat(salesDTO1).isEqualTo(salesDTO2);
        salesDTO2.setId(2L);
        assertThat(salesDTO1).isNotEqualTo(salesDTO2);
        salesDTO1.setId(null);
        assertThat(salesDTO1).isNotEqualTo(salesDTO2);
    }
}
