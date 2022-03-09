package com.example.imclient;

import com.example.common.bean.User;
import com.example.common.concurrent.FutureTaskScheduler;
import com.example.imclient.clientcommand.BaseCommand;
import com.example.imclient.clientcommand.ClientMenuCommand;
import com.example.imclient.clientcommand.ConsoleChatCommand;
import com.example.imclient.clientcommand.ConsoleLoginCommand;
import com.example.imclient.sender.ChatMsgSender;
import com.example.imclient.sender.LoginSender;
import com.example.imclient.session.ClientSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CommandController {

	@Autowired
	private ClientMenuCommand clientMenuCommand;

	@Autowired
	private ConsoleLoginCommand loginCommand;

	@Autowired
	private ConsoleChatCommand chatCommand;

	@Autowired
	private LoginSender loginSender;

	@Autowired
	private ChatMsgSender chatMsgSender;

	@Autowired
	private ChatClient chatClient;

	private Map<String, BaseCommand> commandMap;

	private Scanner scanner;

	private Channel channel;

	private ClientSession session;

	private int reconnectCount = 0;

	private boolean connectFlag = false;

	private String menuStr;

	private User user;


	GenericFutureListener<ChannelFuture> closeListener = (ChannelFuture future) -> {
		log.info(new Date() + " : connection disconnected ...");
		channel = future.channel();
		ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
		session.close();
		// 唤醒用户线程
		notifyCommandThread();
	};


	GenericFutureListener<ChannelFuture> connectedListener = (ChannelFuture future) -> {
		final EventLoop eventLoop = future.channel().eventLoop();
		if (!future.isSuccess() && (++reconnectCount < 3)) {
			log.info("connect failed, try to reconnect in 10 seconds, {} times", reconnectCount);
			eventLoop.schedule(() -> chatClient.connect(), 10, TimeUnit.SECONDS);
			connectFlag = false;
		} else if (future.isSuccess()) {
			connectFlag = true;
			log.info("connect successfully!");
			channel = future.channel();

			session = new ClientSession(channel);
			session.setConnectState(true);
			// 连接上之后添加通道关闭监听
			channel.closeFuture().addListener(closeListener);
			// 唤醒用户线程
			notifyCommandThread();
		} else {
			log.info("IM server connect failed many times");
			connectFlag = false;
			// 唤醒用户线程
			notifyCommandThread();
		}
	};


	public synchronized void notifyCommandThread() {
		this.notify();
	}

	public synchronized void waitCommandThread() {
		try {
			this.wait();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void initCommandMap() {
		commandMap = new HashMap<>();
		commandMap.put(loginCommand.getKey(), loginCommand);
		commandMap.put(clientMenuCommand.getKey(), clientMenuCommand);
		commandMap.put(chatCommand.getKey(), chatCommand);

		Set<Map.Entry<String, BaseCommand>> entries = commandMap.entrySet();
		Iterator<Map.Entry<String, BaseCommand>> iterator = entries.iterator();

		StringBuilder menus =new StringBuilder();
		menus.append("[menu] ");
		while (iterator.hasNext()) {
			BaseCommand next = iterator.next().getValue();
			menus.append(next.getKey()).append("->").append(next.getTip()).append(" | ");
		}
		menuStr = menus.toString();

		clientMenuCommand.setCommandsShow(menuStr);
	}

	public void startConnectServer() {
		FutureTaskScheduler.add(() -> {
			chatClient.setConnectedListener(connectedListener);
			chatClient.connect();
		});
	}

	public void startCommandThread() throws InterruptedException{
		Thread.currentThread().setName("Command Thread");
		while (true) {
			// 建立连接
			while (!connectFlag) {
				startConnectServer();
				waitCommandThread();
			}
			// 处理命令
			while (null != session) {
				scanner = new Scanner(System.in);
				clientMenuCommand.execute(scanner);
				String key = clientMenuCommand.getCommandInput();
				BaseCommand command = commandMap.get(key);
				if (null == command) {
					System.err.println("can't tell the inst [" + key + "], please try again.");
					continue;
				}

				switch (key) {
					case ConsoleLoginCommand.KEY:
						command.execute(scanner);
						startLogin((ConsoleLoginCommand) command);
						break;
					case ConsoleChatCommand.KEY:
						command.execute(scanner);
						startSingleChat((ConsoleChatCommand) command);
						break;
				}
			}
		}
	}

	private void startLogin(ConsoleLoginCommand command) {
		if (!connectFlag) {
			log.info("connect is wrong, please try again.");
			return;
		}
		user = User.builder()
				.userId(command.getUserName())
				.token(command.getPassWord())
						.devId("123456").build();

		session.setUser(user);
		loginSender.setUser(user);
		loginSender.setClientSession(session);
		loginSender.sendLoginMsg();
	}

	private void startSingleChat(ConsoleChatCommand command) {
		if (!isLogin()) {
			log.warn("not login, please login first");
			return;
		}

		chatMsgSender.setClientSession(session);
		chatMsgSender.setUser(user);
		chatMsgSender.sendChatMessage(command.getToId(), command.getMessage());
	}

	private boolean isLogin() {
		if (null == session) {
			log.warn("session in null!");
			return false;
		}
		return session.isLoginState();
	}




}
