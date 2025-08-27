INSERT INTO PROPERTIES (APPLICATION, PROFILE, LABEL, "KEY", "VALUE") VALUES
('Product Service', 'default', 'master', 'spring.datasource.url', 'jdbc:postgresql://postgres:5432/odins_oddities'),
('Product Service', 'default', 'master', 'spring.datasource.username', '${POSTGRES_USER}'),
('Product Service', 'default', 'master', 'spring.datasource.password', '${POSTGRES_PASSWORD}'),
('Product Service', 'default', 'master', 'spring.jpa.hibernate.ddl-auto', 'update');