create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

create table oauth_client_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication BLOB,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token BLOB,
  authentication BLOB
);

create table oauth_code (
  code VARCHAR(256), authentication BLOB
);

create table oauth_approvals (
	userId VARCHAR(256),
	clientId VARCHAR(256),
	scope VARCHAR(256),
	status VARCHAR(10),
	expiresAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	lastModifiedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- customized oauth_client_details table
create table ClientDetails (
  appId VARCHAR(256) PRIMARY KEY,
  resourceIds VARCHAR(256),
  appSecret VARCHAR(256),
  scope VARCHAR(256),
  grantTypes VARCHAR(256),
  redirectUrl VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additionalInformation VARCHAR(4096),
  autoApproveScopes VARCHAR(256)
);

INSERT INTO oauth_client_details(client_id, resource_ids, client_secret, scope, authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove)
    VALUES ('trust-web', 'all', '{bcrypt}$2a$10$Oi6TUjsIUZX2yqnhJ5Iisep3af3vdEzsSmt6ztNiNccMjYAKN01J2', 'read,write', 'password,refresh_token', '', 'ROLE_OPTION', 43200, 302400, null, true);

-- user schema
CREATE TABLE users (
  id VARCHAR(40) NOT NULL,
  username varchar(255) DEFAULT NULL,
  password varchar(255) DEFAULT NULL,
  display_name varchar(255) DEFAULT NULL,
  enabled bit(1) NOT NULL,
  created_date datetime(3) NOT NULL ,
  last_modified_date datetime(3) NOT NULL ,
  last_modified_by VARCHAR(50) NOT NULL ,
  created_by VARCHAR(50) NOT NULL ,
  version INT DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE (username)
);

CREATE TABLE roles(
  id VARCHAR(40) not null,
  name VARCHAR(50) not null,
  description VARCHAR(50) not null,
  created_date datetime(3) NOT NULL ,
  last_modified_date datetime(3) NOT NULL ,
  last_modified_by VARCHAR(50) not NULL,
  created_by VARCHAR(50) not NULL,
  version INT DEFAULT 0,
  PRIMARY KEY (id),
  UNIQUE (name)
);


create table users_roles(
  users_id VARCHAR(40) not null,
  roles_id VARCHAR(40) not null
);

ALTER TABLE users_roles
  ADD FOREIGN KEY (roles_id)
REFERENCES roles (id);

ALTER TABLE users_roles
  ADD FOREIGN KEY (users_id)
REFERENCES users (id);


INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20000','张三', 1, 'user', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');

INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('94cc5822179e4458a01b3f8346a25f4d', '李四:新致软件', 1, 'org:user', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');

INSERT INTO users (id,  display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebff0aa0001', '系统管理员', 1, 'admin', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', 'system', 'system', '2018-07-31 15:49:56.985','2018-07-31 15:49:56.985');

INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20011','test-user-1', 1, 'test-user-1', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');

INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20012','test-user-2', 1, 'test-user-2', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20013','test-user-3', 1, 'test-user-3', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20014','test-user-4', 1, 'test-user-4', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20015','test-user-5', 1, 'test-user-5', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20016','test-user-6', 1, 'test-user-6', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20017','test-user-7', 1, 'test-user-7', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20018','test-user-8', 1, 'test-user-8', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20019','test-user-9', 1, 'test-user-9', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20020','test-user-a', 1, 'test-user-a', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20021','test-user-b', 1, 'test-user-b', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20022','test-user-c', 1, 'test-user-c', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20023','test-user-d', 1, 'test-user-d', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20024','test-user-e', 1, 'test-user-e', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20025','test-user-f', 1, 'test-user-f', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20026','test-user-g', 1, 'test-user-26', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20027','test-user-h', 1, 'test-user-h', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20028','test-user-i', 1, 'test-user-i', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20029','test-user-j', 1, 'test-user-j', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');
INSERT INTO users (id, display_name, enabled, username, password, created_by, last_modified_by, created_date, last_modified_date)
VALUES ('297eaf7d508ebfe001508ebfefd20030','test-user-k', 1, 'test-user-k', '{bcrypt}$2a$10$YJaafCPydYF.TVCpm92ciuV3.d2p9KDI0B33KHpfCNyS9T9UvEReu', '297eaf7d508ebfe001508ebff0aa0001', '297eaf7d508ebfe001508ebff0aa0001', '2018-07-31 15:49:56.985', '2018-07-31 15:49:56.985');

INSERT INTO roles (ID, NAME, description, created_by, last_modified_by, created_date, last_modified_date) VALUES ('2d2994219a14476eba13c5036ecda147', 'ROLE_USER', '普通用户', 'system', 'system', '2018-07-31 15:49:56.985','2018-07-31 15:49:56.985');
INSERT INTO roles (ID, NAME, description, created_by, last_modified_by, created_date, last_modified_date) VALUES ('f2a26d2090624570b6bb630ab546c98f', 'ROLE_ADMIN', '系统管理员', 'system', 'system', '2018-07-31 15:49:56.985','2018-07-31 15:49:56.985');
INSERT INTO roles (ID, NAME, description, created_by, last_modified_by, created_date, last_modified_date) VALUES ('f2a26d2090624570b6bb630ab546c99f', 'ROLE_SALES', '销售', 'system', 'system', '2018-07-31 15:49:56.985','2018-07-31 15:49:56.985');

INSERT into users_roles(roles_id, users_id)
VALUES ('2d2994219a14476eba13c5036ecda147', '297eaf7d508ebfe001508ebfefd20000');
INSERT into users_roles(roles_id, users_id)
VALUES ('2d2994219a14476eba13c5036ecda147', '94cc5822179e4458a01b3f8346a25f4d');
INSERT into users_roles(roles_id, users_id)
VALUES ('f2a26d2090624570b6bb630ab546c98f', '297eaf7d508ebfe001508ebff0aa0001');
INSERT into users_roles(roles_id, users_id)
VALUES ('f2a26d2090624570b6bb630ab546c99f', '297eaf7d508ebfe001508ebfefd20000');
