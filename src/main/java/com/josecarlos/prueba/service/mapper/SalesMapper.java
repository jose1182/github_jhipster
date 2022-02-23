package com.josecarlos.prueba.service.mapper;

import com.josecarlos.prueba.domain.*;
import com.josecarlos.prueba.service.dto.SalesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Sales} and its DTO {@link SalesDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface SalesMapper extends EntityMapper<SalesDTO, Sales> {}
