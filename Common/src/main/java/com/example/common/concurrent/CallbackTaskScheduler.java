package com.example.common.concurrent;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class CallbackTaskScheduler extends Thread {

	// 任务队列
	private ConcurrentLinkedQueue<CallbackTask> executeTaskQueue = new ConcurrentLinkedQueue<>();

	private long sleepTime = 200;

	// 用来从队列中获取需要执行的CallbackTask
	private ExecutorService fakerPool = Executors.newFixedThreadPool(12);

	// 实际执行任务的线程池
	ListeningExecutorService realPool = MoreExecutors.listeningDecorator(fakerPool);

	private static CallbackTaskScheduler instance = new CallbackTaskScheduler();

	// 私有化构造函数，直接启动线程
	private CallbackTaskScheduler() {
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			handleTask();
			threadSleep(sleepTime);
		}
	}


	public static <R> void add(CallbackTask<R> executeTask) {
		instance.executeTaskQueue.add(executeTask);
	}

	private void threadSleep(long time) {
		try {
			sleep(time);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage());
		}
	}

	/**
	 * 检查任务队列是否有任务，
	 * 如果有任务则交给ListeningExecutorService执行
	 */
	private void handleTask() {
		try {
			CallbackTask executeTask = null;
			while (executeTaskQueue.peek() != null) {
				executeTask = executeTaskQueue.poll();
				handleTask(executeTask);
			}
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
	}

	private <R> void handleTask(CallbackTask<R> executeTask) {
		// 提交任务
		ListenableFuture<R> future = realPool.submit(() -> executeTask.execute());
		Futures.addCallback(future, new FutureCallback<R>() {
			@Override
			public void onSuccess(R r) {
				executeTask.onSuccess(r);
			}

			@Override
			public void onFailure(Throwable throwable) {
				executeTask.onFailure(throwable);
			}
		}, realPool);
	}
}
