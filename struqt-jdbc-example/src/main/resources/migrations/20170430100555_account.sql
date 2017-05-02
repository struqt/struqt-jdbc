--------------------------------------------------------------------------------
--                     dP                                dP
--          .d8888b. d8888P 88d888b. dP    dP .d8888b. d8888P
--          Y8ooooo.   88   88'  `88 88    88 88'  `88   88
--                88   88   88       88.  .88 88.  .88   88
--          `88888P'   dP   dP       `88888P' `8888P88   dP
--                                                  dP
--------------------------------------------------------------------------------

-- MySQL 5.6.33

create table `account` (
  `id`              bigint      not null comment 'ID of account' auto_increment,
  `uuid`            char(36)    not null comment 'UUID of account',
  `create_time`     datetime    not null comment 'When this record been created' default current_timestamp,
  unique key `uk_uuid` (`uuid`),
  primary key (`id`)
)
engine = InnoDB default charset = utf8
comment 'Account Table';
alter table `account` auto_increment=5000000;


create table `account_security` (
  `account_id`      bigint      not null comment 'ID of account' auto_increment,
  `username`        varchar(64) default null comment 'Name of account',
  `password`        varchar(64) default null comment 'Password',
  `email`           varchar(64) default null comment 'Email address',
  `mobile`          varchar(64) default null comment 'Cell phone number',
  `real_name`       varchar(64) default null comment 'Real name',
  `use_2fa`         tinyint     default 0 not null comment 'Use Two-Factor Authentication (0:No,1:Yes)',
  `no_login`        tinyint     default 0 not null comment 'Disable Login (0:No,1:Yes)',
  `email_ok`        tinyint     default 0 not null comment 'Email verified (0:No,1:Yes)',
  `mobile_ok`       tinyint     default 0 not null comment 'Mobile verified (0:No,1:Yes)',
  `real_name_ok`    tinyint     default 0 not null comment 'Real name verified (0:No,1:Yes)',
  `update_time`     datetime    not null comment 'When this record been modified' default current_timestamp on update current_timestamp,
  `create_time`     datetime    not null comment 'When this record been created' default current_timestamp,
  unique key `uk_username` (`username`),
  unique key `uk_email_ok` (`email`,`email_ok`),
  unique key `uk_mobile_ok` (`mobile`,`mobile_ok`),
  constraint `fk_account_security` foreign key (`account_id`) references `account`(`id`),
  primary key (`account_id`)
)
engine = InnoDB default charset = utf8
comment 'Account Security Data Table';

