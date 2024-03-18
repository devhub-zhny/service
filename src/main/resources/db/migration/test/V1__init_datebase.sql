CREATE TABLE `device`
(
    `device_id`            bigint(20) NOT NULL,
    `device_physical_id`   bigint(20) DEFAULT NULL,
    `parent_device_id`     bigint(20) DEFAULT NULL,
    `offline_time`         datetime     DEFAULT NULL,
    `device_status`        tinyint(1) DEFAULT '1',
    `device_address`       varchar(255) DEFAULT NULL,
    `device_state`         varchar(255) DEFAULT '正常',
    `device_name`          varchar(255) DEFAULT '未设置名称',
    `device_category_name` varchar(255) DEFAULT NULL,
    `is_binding`           tinyint(1) DEFAULT '0',
    PRIMARY KEY (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `device_data`
(
    `data_id`         bigint(20) NOT NULL,
    `data_time`       datetime     DEFAULT NULL,
    `device_id`       bigint(20) DEFAULT NULL,
    `property_name`   varchar(255) DEFAULT NULL,
    `property_value`  float        DEFAULT NULL,
    `property_unit`   varchar(255) DEFAULT NULL,
    `alarm_threshold` float        DEFAULT NULL,
    `is_valid`        tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `device_property`
(
    `property_id`   bigint(20) NOT NULL,
    `device_id`     bigint(20) DEFAULT NULL,
    `property_name` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`property_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
