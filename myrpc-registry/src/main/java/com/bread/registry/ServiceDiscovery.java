package com.bread.registry;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhiwj
 * @date 2019/6/17
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private String zookeeperAddress;

    private volatile List<String> dataList = new ArrayList<>();

    public ServiceDiscovery(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
//        ZooKeeper zk = connectServer(zookeeperAddress);
        ZkClient zk = new ZkClient(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        zk.setZkSerializer(new StringZkSerializer());
        watchNode(zk);

    }

    private void watchNode(ZkClient zk) {
        List<String> children = zk.subscribeChildChanges(Constant.ZK_REGISTRY_PATH, (parentPath, currentChilds) -> watchNode(zk));
        List<String> dataList = new ArrayList<>(children.size());
        for (String child : children) {
            String data = zk.readData(Constant.ZK_REGISTRY_PATH + "/" + child);
            dataList.add(data);
        }
        this.dataList = dataList;
    }


    public String discover() {
        if (dataList.size() > 0) {
            if (dataList.size() == 1) {
                return dataList.get(0);
            } else {
                return dataList.get(ThreadLocalRandom.current().nextInt(dataList.size()));
            }
        }
        return null;
    }


}
