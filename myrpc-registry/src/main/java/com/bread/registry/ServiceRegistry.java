package com.bread.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhiwj
 * @date 2019/6/12
 */
public class ServiceRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);
    private String zookeeperAddress;

    private CountDownLatch cdl = new CountDownLatch(1);

    public ServiceRegistry(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public void register(String data) {
        ZooKeeper zk = connectServer();
        LOGGER.debug("zooKeeper连接上 。。。");
        if (zk != null) {
            createNode(zk, data);
        }
    }

    private ZooKeeper connectServer() {
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, watchedEvent -> {
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    cdl.countDown();
                }
            });
            cdl.await();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return zooKeeper;
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
            String path = zk.create(Constant.ZK_DATA_PATH, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.info("zookeeper node create [{} ==> {}]", path, data);
        } catch (KeeperException | InterruptedException e) {
            LOGGER.warn("", e);
        }
    }
}
