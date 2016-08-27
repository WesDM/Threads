package com.wesdm.threads.atomicity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class StopThread {
	public static void main(String... strings) throws InterruptedException {
		// StopThreadBroken stb = new StopThreadBroken();
		// stb.main();

		// StopThreadSynchronized sts = new StopThreadSynchronized();
		// sts.main();

		IncrementOperatorBroken sts = new IncrementOperatorBroken();
		sts.main();
	}
}

/*
 * This class is broken. It will not terminate because the most recent values of
 * stopRequested won't be seen by the backgroundThread
 */
class StopThreadBroken {
	private static boolean stopRequested;

	public void main() throws InterruptedException {
		Thread backgroundThread = new Thread(() -> {
			int i = 0;
			while (!stopRequested)
				i++;
		});
		backgroundThread.start();
		TimeUnit.SECONDS.sleep(1);
		stopRequested = true;
	}
}

// Properly synchronized cooperative thread termination
class StopThreadSynchronized {
	private static boolean stopRequested;

	private static synchronized void requestStop() {
		stopRequested = true;
	}

	private static synchronized boolean stopRequested() {
		return stopRequested;
	}

	public void main() throws InterruptedException {
		Thread backgroundThread = new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while (!stopRequested())
					i++;
			}
		});
		backgroundThread.start();
		TimeUnit.SECONDS.sleep(1);
		requestStop();
	}
}

// Cooperative thread termination with a volatile field
class StopThreadVolatile {
	private static volatile boolean stopRequested;

	public void main() throws InterruptedException {
		Thread backgroundThread = new Thread(new Runnable() {
			public void run() {
				int i = 0;
				while (!stopRequested)
					i++;
			}
		});
		backgroundThread.start();
		TimeUnit.SECONDS.sleep(1);
		stopRequested = true;
	}
}

class IncrementOperatorBroken {
	// Broken - requires synchronization!
	private volatile int counter = 0;

	public void main() throws InterruptedException {
		Thread backgroundThread = new Thread(new Runnable() {
			public void run() {
				while (true) {
					updateCounter();
				}
			}
		}, "BGThread");
		backgroundThread.start();
		// TimeUnit.SECONDS.sleep(1);
		while (true) {
			updateCounter();
		}
	}

	public void updateCounter() {
		counter++;
		synchronized (this) {
			System.out.println(Thread.currentThread().getName() + ":" + counter);
		}
	}
}
