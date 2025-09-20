package de.jodabyte.springonk8slab.productservice.domain.mapper;

import org.mapstruct.Mapper;

import de.jodabyte.springonk8slab.productservice.domain.command.Allocate;
import de.jodabyte.springonk8slab.productservice.domain.event.Deallocated;

@Mapper
public interface EventAndCommandMapper {

    Allocate toCommand(Deallocated deallocated);
}
