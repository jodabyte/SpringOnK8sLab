
module productservice {
    opens de.jodabyte.springonk8slab.productservice to spring.beans;
    opens de.jodabyte.springonk8slab.productservice.controller to spring.beans, spring.core, spring.web;

    requires domain;
    requires io.swagger.v3.oas.annotations;
    requires java.instrument;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.core;
    requires spring.tx;
    requires spring.web;
}
