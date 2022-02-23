package com.josecarlos.prueba.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.josecarlos.prueba.domain.Sales} entity.
 */
public class SalesDTO implements Serializable {

    private Long id;

    private String title;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SalesDTO)) {
            return false;
        }

        SalesDTO salesDTO = (SalesDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, salesDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SalesDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            "}";
    }
}
