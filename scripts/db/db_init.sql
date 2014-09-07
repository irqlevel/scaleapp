@args Integer vsid
CREATE SEQUENCE db_id_seq;
CREATE FUNCTION db_next_id(OUT result bigint) AS $$
DECLARE
    our_epoch bigint := 1407922045080;
    seq_id bigint;
    now_millis bigint;
    shard_id int := @vsid;
BEGIN
    SELECT nextval('db_id_seq') % 1024 INTO seq_id;
    SELECT FLOOR(EXTRACT(EPOCH FROM clock_timestamp()) * 1000) INTO now_millis;
    result := (now_millis - our_epoch) << 23;
    result := result | (shard_id << 10);
    result := result | (seq_id);
END;
$$ LANGUAGE PLPGSQL;

CREATE TABLE Users (
	"id" bigint NOT NULL DEFAULT db_next_id(),
	"username" varchar(255) UNIQUE NOT NULL,
	"hashp"	varchar(255) NOT NULL
);

CREATE TABLE DHT_USERNAME (
	"id" bigint NOT NULL DEFAULT db_next_id(),
	"key" varchar(255) UNIQUE NOT NULL,
	"value" varchar(255) NOT NULL,
	"hash"	varchar(255) NOT NULL
);

CREATE TABLE DHT_SESSION (
	"id" bigint NOT NULL DEFAULT db_next_id(),
	"key" varchar(255) UNIQUE NOT NULL,
	"value" varchar(255) NOT NULL,
	"hash"	varchar(255) NOT NULL
);
