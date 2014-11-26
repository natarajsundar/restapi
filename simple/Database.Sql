CREATE TABLE property
(
    id    serial primary key,
    propertyName        VARCHAR not null,
    propertyvalue       VARCHAR not null
);

INSERT INTO property (propertyName, propertyvalue)
VALUES ('propertyName','propertyValue');