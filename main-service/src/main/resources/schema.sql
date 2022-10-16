DROP TABLE IF EXISTS COMPILATIONS_EVENTS, EVENTS, USERS, CATEGORIES, COMPILATIONS, LOCATIONS, REQUESTS;

CREATE TABLE IF NOT EXISTS USERS
(
    id    serial
        constraint users_pk
            primary key,
    name  varchar(50)        not null,
    email varchar(50) unique not null
);

CREATE TABLE IF NOT EXISTS CATEGORIES
(
    id   serial
        constraint categories_pk
            primary key,
    name varchar(50) not null
);

CREATE TABLE IF NOT EXISTS COMPILATIONS
(
    id     serial
        constraint compilations_pk
            primary key not null,
    title  varchar(200) not null,
    pinned boolean      not null
);

CREATE TABLE IF NOT EXISTS LOCATIONS
(
    id        serial
        constraint locations_pk
            primary key,
    latitude  integer,
    longitude integer
);

CREATE TABLE IF NOT EXISTS EVENTS
(
    id                 serial
        constraint events_pk
            primary key,
    annotation         varchar(500)                not null,
    category_id        integer                     not null
        constraint events_categories_id_fk
            references categories,
    created            timestamp without time zone,
    description        varchar(1000),
    event_date         timestamp without time zone not null,
    initiator_id       integer                     not null
        constraint events_users_id_fk
            references users,
    location_id        integer                     not null
        constraint events_locations_id_fk
            references locations,
    paid               boolean                     not null,
    participant_limit  integer,
    published          timestamp without time zone,
    state              varchar(500),
    title              varchar(500)                not null,
    request_moderation boolean
);

CREATE TABLE IF NOT EXISTS REQUESTS
(
    id           serial
        constraint requests_pk
            primary key not null,
    created      timestamp,
    event_id     integer
        constraint requests_events_id_fk
            references events,
    requester_id integer
        constraint requests_users_id_fk
            references users,
    status       varchar(50)
);

CREATE TABLE IF NOT EXISTS COMPILATIONS_EVENTS
(
    id             serial
        constraint compilations_events_pk
            primary key,
    compilation_id integer not null
        constraint compilations_events_compilations_id_fk
            references compilations,
    event_id       integer not null
        constraint compilations_events_events_id_fk
            references events
);