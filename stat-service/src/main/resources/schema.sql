DROP TABLE IF EXISTS ENDPOINT_HIT;

CREATE TABLE IF NOT EXISTS ENDPOINT_HIT
    (
        id        serial
            constraint hit_pk
                primary key,
        app       varchar(50),
        uri       varchar(50),
        ip        varchar(50),
        timestamp timestamp WITH TIME ZONE
);




