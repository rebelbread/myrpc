package com.bread.registry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author zhiwj
 * @date 2019/6/17
 */
public class ServiceDiscovery {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

    private String zookeeperAddress;

    private volatile List<String> dataList = new ArrayList<>();

    private CountDownLatch cdl = new CountDownLatch(1);

    public ServiceDiscovery(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
        ZooKeeper zk = connectServer(zookeeperAddress);
        if (zk != null) {
            watchNode(zk);
        }

    }

    private ZooKeeper connectServer(String zookeeperAddress) {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(zookeeperAddress, Constant.ZK_SESSION_TIMEOUT, watchedEvent -> {
                if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
                    cdl.countDown();
                }
            });
            cdl.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.warn("", e);
        }
        return zk;
    }

    private void watchNode(ZooKeeper zk) {
        try {
            List<String> children = zk.getChildren(Constant.ZK_REGISTRY_PATH, watchedEvent -> watchNode(zk));
            List<String> dataList = new ArrayList<>(children.size());
            for (String child : children) {
                byte[] data = zk.getData(Constant.ZK_REGISTRY_PATH + "/" + child, false, null);
                dataList.add(new String(data));
            }
            this.dataList = dataList;
        } catch (KeeperException | InterruptedException e) {
            LOGGER.warn("", e);
        }
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
