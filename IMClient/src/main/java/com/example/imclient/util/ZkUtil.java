package com.example.imclient.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.List;
import java.util.Random;

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
		log.info("zookeeper disconnect.");
	}

	public String[] getRandomServer() throws Exception {
		List<String> childrens = zkClient.getChildren().forPath(ZK_PATH + BIZ_PATH);
		if (childrens.isEmpty()) {
			throw new Exception("获取 Chat Server 信息失败");
		}
		int size = childrens.size();
		// 随机获取server连接，后期可扩展负载均衡算法
		String child = childrens.get(new Random().nextInt(size));
		System.out.println("get server is : " + child);
		//ip:port格式
		return child.split(":");
	}


}
