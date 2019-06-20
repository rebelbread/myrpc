package com.bread.registry;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private String zookeeperAddress;

    public ServiceRegistry(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public void register(String data) {
//        ZooKeeper zk = connectServer();
        ZkClient zk = new ZkClient(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        zk.setZkSerializer(new StringZkSerializer());
        LOGGER.debug("zooKeeper连接上 。。。");
        createNode(zk, data);
    }

    private void createNode(ZkClient zk, String data) {
        String path = zk.create(Constant.ZK_DATA_PATH, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
//        String path = zk.createEphemeralSequential(Constant.ZK_DATA_PATH, data);
        LOGGER.info("zookeeper node create [{} ==> {}]", path, data);
    }
}
