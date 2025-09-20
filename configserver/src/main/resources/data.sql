INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, "KEY", "VALUE") VALUES
('Product Service', 'k8s', 'master', 'spring.datasource.url', 'jdbc:postgresql://postgres:5432/odins_oddities'),
('Product Service', 'k8s', 'master', 'spring.datasource.username', '${POSTGRES_USER}'),
('Product Service', 'k8s', 'master', 'spring.datasource.password', '${POSTGRES_PASSWORD}'),
('Product Service', 'k8s', 'master', 'spring.jpa.hibernate.ddl-auto', 'update'),
('Product Service', 'local', 'master', 'spring.datasource.url', 'jdbc:postgresql://localhost:5432/odins_oddities'),
('Product Service', 'local', 'master', 'spring.datasource.username', '${POSTGRES_USER}'),
('Product Service', 'local', 'master', 'spring.datasource.password', '${POSTGRES_PASSWORD}'),
('Product Service', 'local', 'master', 'spring.jpa.hibernate.ddl-auto', 'create');