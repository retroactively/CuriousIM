package com.example.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ThreadUtil {

	/**
	 * CPU核心数
	 */
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

	/**
	 * 空闲保活时限（second）
	 */
	private static final int KEEP_ALIVE_SECONDS = 30;

	/**
	 * 有界队列size
	 */
	private static final int QUEUE_SIZE = 1000;

	/**
	 * CPU核心线程数
	 */
	private static final int CORE_POOL_SIZE = 0;

	/**
	 * CPU最大线程数
	 */
	private static final int MAX_POOL_SIZE = CPU_COUNT;

	/**
	 * IO线程池核心线程数
	 */
	private static final int IO_CORE_SIZE = 0;

	/**
	 * IO线程池最大线程数
	 */
	private static final int IO_MAX_SIZE = Math.max(2, 2 * CPU_COUNT);

	/**
	 * 混合线程池核心线程数
	 */
	private static final int MIXED_CORE_SIZE = 0;

	/**
	 * 混合线程池最大线程数
	 */
	private static final int MIXED_MAX_SIZE = 128;

	/**
	 * 自制线程工厂
	 */
	private static class CustomedThreadFactory implements ThreadFactory {

		private static final AtomicInteger poolNumber = new AtomicInteger(1);
		private final ThreadGroup group;

		private final AtomicInteger threadNumber = new AtomicInteger(1);
		private final String threadTag;

		CustomedThreadFactory(String threadTag) {
			SecurityManager manager = System.getSecurityManager();
			group = (manager != null) ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();
			this.threadTag = "pool - " + poolNumber.getAndIncrement() + "-" + threadTag + "-";
		}

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(group, runnable, threadTag + threadNumber.getAndIncrement(), 0);
			if (thread.isDaemon()) {
				thread.setDaemon(false);
			}
			if (thread.getPriority() != Thread.NORM_PRIORITY) {
				thread.setPriority(Thread.NORM_PRIORITY);
			}
			return thread;
		}
	}

	static class ShutdownHookThread extends Thread {
		private volatile boolean hasShutdown = false;
		private static AtomicInteger shutdownTimes = new AtomicInteger(0);
		private final Callable callback;

		public ShutdownHookThread(String name, Callable callback) {
			super("JVM exithook(" + name + ")");
			this.callback = callback;
		}

		@Override
		public void run() {
			synchronized (this) {
				log.info(getName() + " starting ...");
				if (!this.hasShutdown) {
					this.hasShutdown = true;
					long beginTime = System.currentTimeMillis();
					try {
						this.callback.call();
					} catch (Exception e) {
						log.error(getName() + " error: " + e.getMessage());
					}
					long consumingTimeTotal = System.currentTimeMillis() - beginTime;
					log.info(getName() + " spend time(ms) : " + consumingTimeTotal);
				}
			}
		}
	}

	/**
	 * 关闭线程池
	 * @param threadPool ExecutorService
	 */
	public static void shutdownThreadPoolGracefully(ExecutorService threadPool) {
		if (threadPool == null || threadPool.isTerminated()) {
			return;
		}

		// 1. 拒绝接受新任务
		try {
			threadPool.shutdown();
		} catch (SecurityException e) {
			return;
		} catch (NullPointerException e) {
			return;
		}
		// 2. 等待60s，等待线程池中的任务完成执行
		try {
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
				if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
					log.error("thread pool not be terminated normally");
				}
			}
		} catch (InterruptedException e) {
			threadPool.shutdownNow();
		}
		// 3. 任务仍未关闭，循环关闭100次，每次等待10ms
		if (!threadPool.isTerminated()) {
			try {
				for (int i = 0; i < 100; i++) {
					if (threadPool.awaitTermination(10, TimeUnit.MILLISECONDS)) {
						break;
					}
					threadPool.shutdownNow();
				}
			} catch (InterruptedException e) {
				log.error(e.getMessage());
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}


	}

	/**
	 *  懒汉式单例模式创建线程池： CPU密集型任务
	 */
	private static class CPUIntenseTaskThreadPoolLazyLoader {
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				MAX_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE), new CustomedThreadFactory("CPU"));

		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("CPU intense task thread pool", new Callable() {
						@Override
						public Object call() throws Exception {
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}

	/**
	 * 懒汉式单例模式创建线程池： IO密集型任务
	 */
	private static class IOIntenseTaskThreadPoolLazyLoader {
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				IO_CORE_SIZE, IO_MAX_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE), new CustomedThreadFactory("IO"));

		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("IO intense task thread pool", new Callable() {
						@Override
						public Object call() throws Exception {
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}

	/**
	 * 懒汉式单例模式创建线程池： 混合型密集型任务
	 */
	private static class MixedTaskThreadPoolLazyLoader {
		private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
				MIXED_MAX_SIZE, MIXED_MAX_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(QUEUE_SIZE), new CustomedThreadFactory("MIX"));

		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("MIX intense task thread pool", new Callable() {
						@Override
						public Object call() throws Exception {
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}


	/**
	 * 懒汉式单例模式创建线程池： 周期型、顺序型任务（时间）
	 */
	private static class TimeTaskThreadPoolLazyLoader {
		private static final ThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(
				1, new CustomedThreadFactory("TIME"));

		static {
			EXECUTOR.allowCoreThreadTimeOut(true);
			Runtime.getRuntime().addShutdownHook(
					new ShutdownHookThread("TIME task thread pool", new Callable() {
						@Override
						public Object call() throws Exception {
							shutdownThreadPoolGracefully(EXECUTOR);
							return null;
						}
					})
			);
		}
	}


	/**
	 *  获取执行CPU任务的线程池
	 * @return ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getCPUIntenseTaskThreadPool() {
		return CPUIntenseTaskThreadPoolLazyLoader.EXECUTOR;
	}

	/**
	 * 获取执行IO型任务的线程池
	 * @return ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getIOIntenseTaskThreadPool() {
		return IOIntenseTaskThreadPoolLazyLoader.EXECUTOR;
	}

	/**
	 * 获取执行混合型任务的线程池
	 * @return ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getMixedTaskThreadPool() {
		return MixedTaskThreadPoolLazyLoader.EXECUTOR;
	}

	/**
	 *  获取执行时间型任务的线程池
	 * @return ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getTimeTaskThreadPool() {
		return TimeTaskThreadPoolLazyLoader.EXECUTOR;
	}

	/**
	 * 顺序执行
	 * @param command
	 */
	public static void seqExecute(Runnable command) {
		getTimeTaskThreadPool().execute(command);
	}

	/**
	 * 延迟执行
	 * @param command
	 */
	public static void delayExecute(Runnable command, int time, TimeUnit unit) {
		((ScheduledThreadPoolExecutor)getTimeTaskThreadPool()).schedule(command, time, unit);
	}

	/**
	 * 固定频率执行
	 * @param command
	 */
	public static void fixedRateExecute(Runnable command, int time, TimeUnit unit) {
		((ScheduledThreadPoolExecutor)getTimeTaskThreadPool()).scheduleAtFixedRate(command, time, time, unit);
	}


}
