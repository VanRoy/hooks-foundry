create type subscription_status as enum ('enabled', 'disabled', 'paused');
create cast (varchar as subscription_status) with inout as implicit;

create table subscription (
    id uuid primary key,
    tenant_id varchar(255),
    name varchar(255),
    url varchar(2048) not null,
    status subscription_status not null default 'enabled',
    event_types text[] not null,
    signatures jsonb not null,
    created_at timestamp(6) without time zone not null default current_timestamp,
    updated_at timestamp(6) without time zone not null default current_timestamp
);
