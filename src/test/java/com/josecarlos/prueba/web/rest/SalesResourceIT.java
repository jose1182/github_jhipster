package com.josecarlos.prueba.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.josecarlos.prueba.IntegrationTest;
import com.josecarlos.prueba.domain.Sales;
import com.josecarlos.prueba.repository.SalesRepository;
import com.josecarlos.prueba.service.criteria.SalesCriteria;
import com.josecarlos.prueba.service.dto.SalesDTO;
import com.josecarlos.prueba.service.mapper.SalesMapper;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SalesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SalesResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sales";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SalesRepository salesRepository;

    @Autowired
    private SalesMapper salesMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSalesMockMvc;

    private Sales sales;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sales createEntity(EntityManager em) {
        Sales sales = new Sales().title(DEFAULT_TITLE);
        return sales;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sales createUpdatedEntity(EntityManager em) {
        Sales sales = new Sales().title(UPDATED_TITLE);
        return sales;
    }

    @BeforeEach
    public void initTest() {
        sales = createEntity(em);
    }

    @Test
    @Transactional
    void createSales() throws Exception {
        int databaseSizeBeforeCreate = salesRepository.findAll().size();
        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);
        restSalesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesDTO)))
            .andExpect(status().isCreated());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeCreate + 1);
        Sales testSales = salesList.get(salesList.size() - 1);
        assertThat(testSales.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void createSalesWithExistingId() throws Exception {
        // Create the Sales with an existing ID
        sales.setId(1L);
        SalesDTO salesDTO = salesMapper.toDto(sales);

        int databaseSizeBeforeCreate = salesRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSalesMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sales.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));
    }

    @Test
    @Transactional
    void getSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get the sales
        restSalesMockMvc
            .perform(get(ENTITY_API_URL_ID, sales.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sales.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE));
    }

    @Test
    @Transactional
    void getSalesByIdFiltering() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        Long id = sales.getId();

        defaultSalesShouldBeFound("id.equals=" + id);
        defaultSalesShouldNotBeFound("id.notEquals=" + id);

        defaultSalesShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultSalesShouldNotBeFound("id.greaterThan=" + id);

        defaultSalesShouldBeFound("id.lessThanOrEqual=" + id);
        defaultSalesShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllSalesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title equals to DEFAULT_TITLE
        defaultSalesShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the salesList where title equals to UPDATED_TITLE
        defaultSalesShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title not equals to DEFAULT_TITLE
        defaultSalesShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the salesList where title not equals to UPDATED_TITLE
        defaultSalesShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultSalesShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the salesList where title equals to UPDATED_TITLE
        defaultSalesShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title is not null
        defaultSalesShouldBeFound("title.specified=true");

        // Get all the salesList where title is null
        defaultSalesShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllSalesByTitleContainsSomething() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title contains DEFAULT_TITLE
        defaultSalesShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the salesList where title contains UPDATED_TITLE
        defaultSalesShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllSalesByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        // Get all the salesList where title does not contain DEFAULT_TITLE
        defaultSalesShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the salesList where title does not contain UPDATED_TITLE
        defaultSalesShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultSalesShouldBeFound(String filter) throws Exception {
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sales.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)));

        // Check, that the count call also returns 1
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultSalesShouldNotBeFound(String filter) throws Exception {
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restSalesMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingSales() throws Exception {
        // Get the sales
        restSalesMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        int databaseSizeBeforeUpdate = salesRepository.findAll().size();

        // Update the sales
        Sales updatedSales = salesRepository.findById(sales.getId()).get();
        // Disconnect from session so that the updates on updatedSales are not directly saved in db
        em.detach(updatedSales);
        updatedSales.title(UPDATED_TITLE);
        SalesDTO salesDTO = salesMapper.toDto(updatedSales);

        restSalesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesDTO))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
        Sales testSales = salesList.get(salesList.size() - 1);
        assertThat(testSales.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void putNonExistingSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, salesDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(salesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(salesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSalesWithPatch() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        int databaseSizeBeforeUpdate = salesRepository.findAll().size();

        // Update the sales using partial update
        Sales partialUpdatedSales = new Sales();
        partialUpdatedSales.setId(sales.getId());

        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSales.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSales))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
        Sales testSales = salesList.get(salesList.size() - 1);
        assertThat(testSales.getTitle()).isEqualTo(DEFAULT_TITLE);
    }

    @Test
    @Transactional
    void fullUpdateSalesWithPatch() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        int databaseSizeBeforeUpdate = salesRepository.findAll().size();

        // Update the sales using partial update
        Sales partialUpdatedSales = new Sales();
        partialUpdatedSales.setId(sales.getId());

        partialUpdatedSales.title(UPDATED_TITLE);

        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSales.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSales))
            )
            .andExpect(status().isOk());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
        Sales testSales = salesList.get(salesList.size() - 1);
        assertThat(testSales.getTitle()).isEqualTo(UPDATED_TITLE);
    }

    @Test
    @Transactional
    void patchNonExistingSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, salesDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(salesDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSales() throws Exception {
        int databaseSizeBeforeUpdate = salesRepository.findAll().size();
        sales.setId(count.incrementAndGet());

        // Create the Sales
        SalesDTO salesDTO = salesMapper.toDto(sales);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSalesMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(salesDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Sales in the database
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSales() throws Exception {
        // Initialize the database
        salesRepository.saveAndFlush(sales);

        int databaseSizeBeforeDelete = salesRepository.findAll().size();

        // Delete the sales
        restSalesMockMvc
            .perform(delete(ENTITY_API_URL_ID, sales.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Sales> salesList = salesRepository.findAll();
        assertThat(salesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
