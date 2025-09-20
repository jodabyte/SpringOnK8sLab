package de.jodabyte.springonk8slab.productservice.domain.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import de.jodabyte.springonk8slab.productservice.domain.event.Allocated;
import de.jodabyte.springonk8slab.productservice.domain.model.Allocation;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AllocationMapper {

    Allocation from(Allocated allocated);

    List<de.jodabyte.springonk8slab.productservice.domain.view.Allocation> to(List<Allocation> allocations);

    de.jodabyte.springonk8slab.productservice.domain.view.Allocation to(Allocation allocation);
}
