CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY UNIQUE,
    external_id_user VARCHAR(45) NOT NULL UNIQUE,
    user_name VARCHAR(100) NOT NULL,
	password VARCHAR(100) NOT NULL,
    login VARCHAR(100) UNIQUE,
    age INTEGER,
	user_creator VARCHAR(45),
    active BOOLEAN NOT NULL,
	role VARCHAR(255) NOT NULL,
    read_terms BOOLEAN,
    created_date TIMESTAMP NOT NULL,
    updated_date TIMESTAMP,
	token VARCHAR(100),
	expiry_date_token TIMESTAMP
);

CREATE TABLE task(
  	task_id BIGSERIAL PRIMARY KEY UNIQUE,
  	external_id_task VARCHAR(45) NOT NULL UNIQUE,
	task_name VARCHAR(100) NOT NULL,
	description TEXT CHECK (LENGTH(description) <= 300),
	reward NUMERIC(10, 2) NOT NULL,
	performed BOOLEAN,
	external_id_user_sponsor VARCHAR(45) NOT NULL,
	external_id_user_child VARCHAR(45) NOT NULL,
	created_date TIMESTAMP NOT NULL,
	updated_date TIMESTAMP
);