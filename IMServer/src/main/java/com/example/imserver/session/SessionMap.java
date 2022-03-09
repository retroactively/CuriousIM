package com.example.imserver.session;

import com.example.common.bean.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class SessionMap {

	private SessionMap() {

	}

	private static SessionMap singleInstance = new SessionMap();

	public static SessionMap getInstance() {
		return singleInstance;
	}

	// session map
	private ConcurrentHashMap<String, ServerSession> map = new ConcurrentHashMap<>();

	public void addSession(String sessionId, ServerSession session) {
		map.put(sessionId, session);
		log.info("User login: id = " + session.getUser().getUserId() + " , online sums: " + map.size());
	}

	public void removeSession(String sessionId) {
		if (!map.containsKey(sessionId)) {
			return;
		}
		ServerSession session = map.get(sessionId);
		map.remove(sessionId);
		log.info("User logout: id = " + session.getUser().getUserId() + " , online sums: " + map.size());
	}

	public ServerSession getSession(String sessionId) {
		return map.getOrDefault(sessionId, null);
	}

	public List<ServerSession> getSessionsByUser(String userId) {
		return map.values().stream().filter(s -> s.getUser().getUserId().equals(userId)).collect(Collectors.toList());
	}

	public boolean isLogin(User user) {
		Iterator<Map.Entry<String, ServerSession>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ServerSession> next = iterator.next();
			User person = next.getValue().getUser();
			if (person.getUserId().equals(user.getUserId()) && person.getPlatform().equals(user.getPlatform())) {
				return true;
			}
		}
		return false;
	}
}
