package com.example.imserver.util;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

@Slf4j
public class ZkUtil {

	private static final String ZK_PATH = "/IM";

	private static final String BIZ_PATH = "/Server";

	private static CuratorFramework zkClient;


	public void connect(String host, String port) {
		zkClient = CuratorFrameworkFactory.
				builder().connectString(host + ":" + port).
				sessionTimeoutMs(4000).retryPolicy(new
						ExponentialBackoffRetry(1000, 3)).
				namespace("").build();
		zkClient.start();
	}

	public void disconnect() {
		if (zkClient != null) {
			zkClient.close();
		}
		log.info("zookeeper disconnect!");
	}

	public void registerNode(String host, String port) throws Exception {
		String path = ZK_PATH + BIZ_PATH + "/" + host + ":" + port;
		zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.EPHEMERAL)
				.forPath(path, (host + ":" + port).getBytes());
	}

	public void deleteNode(String host, String port) throws Exception {
		String path = ZK_PATH + BIZ_PATH + "/" + host + ":" + port;
		zkClient.delete().forPath(path);
	}

	public GenericFutureListener<Future<? super Void>> getZKFutureListener(String host, String port) {
		return future -> {
			if (future.isSuccess()) {
				connect(host, port);
				registerNode(host, port);
				log.info("server register at zookeeper successfully.");
			}
		};
	}

}
