package de.jodabyte.springonk8slab.productservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import de.jodabyte.springonk8slab.productservice.domain.command.CreateBatch;
import de.jodabyte.springonk8slab.productservice.domain.model.Batch;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BatchMapper {

    @Mapping(target = "purchasedQuantity", source = "qty")
    Batch from(CreateBatch createBatch);

}
