package com.wesdm.threads;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadLambdaExample {

	Integer x = 1;

	public static void main(String[] args) {
		ThreadLambdaExample e = new ThreadLambdaExample();
		// new Thread(() -> e.foo()).start();
		// new Thread(() -> e.foo()).start();

		ExecutorService executor = Executors.newSingleThreadExecutor();

		executor.submit(() -> {
			String threadName = Thread.currentThread().getName();
			System.out.println("Hello " + threadName);
		});

		softShutdown(executor);

		Callable<Integer> task = () -> {
			try {
				TimeUnit.SECONDS.sleep(1);
				return 123;
			} catch (InterruptedException ex) {
				throw new IllegalStateException("task interrupted", ex);
			}
		};

		executor = Executors.newFixedThreadPool(1);
		Future<Integer> future = executor.submit(task);

		System.out.println("future done? " + future.isDone());

		Integer result = null;
		try {
			result = future.get();
		} catch (InterruptedException | ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		System.out.println("future done? " + future.isDone());
		System.out.print("result: " + result);

		softShutdown(executor);

		executor = Executors.newFixedThreadPool(1);

		future = executor.submit(() -> {
			try {
				TimeUnit.SECONDS.sleep(2);
				return 123;
			} catch (InterruptedException ex) {
				throw new IllegalStateException("task interrupted", ex);
			}
		});

		try {
			future.get(1, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		softShutdown(executor);

		executor = Executors.newWorkStealingPool();

		List<Callable<String>> callables = Arrays.asList(() -> "task1", () -> "task2", () -> "task3");

		try {
			executor.invokeAll(callables).stream().map(future2 -> {
				try {
					return future2.get();
				} catch (Exception ex) {
					throw new IllegalStateException(ex);
				}
			}).forEach(System.out::println);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		softShutdown(executor);

	}

	private static void softShutdown(ExecutorService executor) {
		try {
			System.out.println("attempt to shutdown executor");
			executor.shutdown();
			executor.awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.err.println("tasks interrupted");
		} finally {
			if (!executor.isTerminated()) {
				System.err.println("cancel non-finished tasks");
			}
			executor.shutdownNow();
			System.out.println("shutdown finished");
		}
	}

	private void foo() {
		while (x < 100) {
			synchronized (x) {
				System.out.println(x);
				x++;
			}
		}
	}
}
