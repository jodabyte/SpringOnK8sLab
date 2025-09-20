package de.jodabyte.springonk8slab.productservice.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.event.Deallocated;
import de.jodabyte.springonk8slab.productservice.domain.model.OrderLine;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderLineMapper {

    OrderLine from(Allocate allocate);

    Deallocated to(OrderLine orderLine);
}
