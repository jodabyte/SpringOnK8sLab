module domain {
    exports de.jodabyte.springonk8slab.productservice.domain.command;
    exports de.jodabyte.springonk8slab.productservice.domain.event;
    exports de.jodabyte.springonk8slab.productservice.domain.exception;
    exports de.jodabyte.springonk8slab.productservice.domain.service;
    exports de.jodabyte.springonk8slab.productservice.domain.view;

    opens de.jodabyte.springonk8slab.productservice.domain.mapper to org.mapstruct, spring.beans;
    opens de.jodabyte.springonk8slab.productservice.domain.model to org.hibernate.orm.core, spring.core;
    opens de.jodabyte.springonk8slab.productservice.domain.repository to spring.beans, spring.core;
    opens de.jodabyte.springonk8slab.productservice.domain.service to spring.core;

    requires jakarta.persistence;
    requires java.compiler;
    requires org.hibernate.orm.core;
    requires org.slf4j;
    requires spring.aop;
    requires spring.beans;
    requires spring.context;
    requires spring.core;
    requires spring.data.commons;
    requires spring.tx;

    requires static lombok;
    requires static org.mapstruct;
}
