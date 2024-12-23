package com.qiyan;

import com.qiyan.config.CanalConfig;
import com.qiyan.config.DBConfig;
import com.qiyan.config.MonitorConfig;
import com.qiyan.manager.DBMonitorManager;
import com.qiyan.utils.ParseConfigUtils;

import java.util.ArrayList;
import java.util.List;

public class Starter {

    public void run() {
        List<MonitorConfig> configs = ParseConfigUtils.getMonitorConfigs();
        List<DBMonitorManager> managers = new ArrayList<>();

        for (MonitorConfig config : configs) {
            managers.add(new DBMonitorManager(config));
        }

        for (DBMonitorManager monitorManager1 : managers) {
            for (DBMonitorManager monitorManager2 : managers) {
                if (!monitorManager1.equals(monitorManager2)) {
                    monitorManager1.addOtherMonitorMessageQueue(monitorManager2.getMessageQueue());
                }
            }
        }

        for (DBMonitorManager monitorManager : managers) {
            monitorManager.run();
        }

    }

    private List<MonitorConfig> buildMonitorConfigs() {
        List<MonitorConfig> configs = new ArrayList<>();

        CanalConfig canalConfig1 = new CanalConfig("dev_datasync1", "datasync1.*");
        DBConfig dbConfig1 = new DBConfig();
        dbConfig1.setHostname("192.168.5.146");
        dbConfig1.setDatabase("datasync1");
        dbConfig1.setUsername("root");
        dbConfig1.setPassword("123456");
        MonitorConfig monitorConfig1 = new MonitorConfig(canalConfig1, dbConfig1);
        configs.add(monitorConfig1);

//        CanalConfig canalConfig2 = new CanalConfig("docker_datasync1", "datasync1.*");
//        DBConfig dbConfig2 = new DBConfig();
//        dbConfig2.setHostname("192.168.5.132");
//        dbConfig2.setDatabase("datasync1");
//        dbConfig2.setUsername("root");
//        dbConfig2.setPassword("123456");
//        MonitorConfig monitorConfig2 = new MonitorConfig(canalConfig2, dbConfig2);
//        configs.add(monitorConfig2);

        return configs;
    }
}
