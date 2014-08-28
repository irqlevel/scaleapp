CREATE DATABASE {{db}}_{{vsid}};
CREATE USER {{usr}}_{{vsid}} WITH password '{{usr_pass}}';
GRANT ALL privileges ON DATABASE {{db}}_{{vsid}} TO {{usr}}_{{vsid}};

