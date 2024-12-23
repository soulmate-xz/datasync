CREATE TABLE `material_data_sync_error_record`
(
    `id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `created_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_on` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `monitor_id`  varchar(255) DEFAULT NULL COMMENT '数据同步监控ID',
    `exception`  TEXT         DEFAULT NULL COMMENT '异常信息',
    `sql`        LONGTEXT         DEFAULT NULL COMMENT '执行的sql',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='执行异常的信息'