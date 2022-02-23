package com.josecarlos.prueba.service.mapper;

import com.josecarlos.prueba.domain.*;
import com.josecarlos.prueba.service.dto.JobDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Job} and its DTO {@link JobDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JobMapper extends EntityMapper<JobDTO, Job> {}
