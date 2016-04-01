CREATE TABLE users (
  username varchar_ignorecase(50) NOT NULL PRIMARY KEY,
  password varchar_ignorecase(500) NOT NULL,
  enabled boolean NOT NULL
);

CREATE TABLE authorities (
  username varchar_ignorecase(50) NOT NULL,
  authority varchar_ignorecase(50) NOT NULL,
  CONSTRAINT fk_authorities_users FOREIGN KEY(username) REFERENCES users(username)
);

CREATE UNIQUE INDEX ix_auth_username ON authorities (username,authority);


-- Default Admin User
INSERT INTO PUBLIC.USERS (USERNAME, PASSWORD, ENABLED)
VALUES
	('${admin.name}', '${admin.password}', true),
	('${user.name}', '${user.password}', true);

INSERT INTO PUBLIC.AUTHORITIES (USERNAME, AUTHORITY)
VALUES
	('${admin.name}', 'ROLE_USER'),
	('${admin.name}', 'ROLE_ADMIN'),
	('${user.name}', 'ROLE_USER');

