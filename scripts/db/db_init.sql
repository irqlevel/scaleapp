CREATE SEQUENCE {{db}}_ids_seq_{{vsid}};
CREATE FUNCTION {{db}}_next_id_{{vsid}}(OUT result bigint) AS $$
DECLARE
    our_epoch bigint := 1407922045080;
    seq_id bigint;
    now_millis bigint;
    shard_id int := {{vsid}};
BEGIN
    SELECT nextval('{{db}}_ids_seq_{{vsid}}') % 1024 INTO seq_id;
    SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
    result := (now_millis - our_epoch) << 23;
    result := result | (shard_id << 10);
    result := result | (seq_id);
END;
$$ LANGUAGE PLPGSQL;

CREATE TABLE Users (
	"id" bigint NOT NULL DEFAULT {{db}}_next_id_{{vsid}}(),
	"username" varchar(255)
);


CREATE TABLE DhtKeys (
	"id" bigint NOT NULL DEFAULT {{db}}_next_id_{{vsid}}(),
	"key" varchar(255),
	"value" varchar(255),
	"hash"	varchar(255)
);

