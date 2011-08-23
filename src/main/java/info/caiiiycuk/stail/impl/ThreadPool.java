package info.caiiiycuk.stail.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
	
	private static ExecutorService executor = Executors.newCachedThreadPool();

	static void execute(Runnable command) {
		executor.execute(command);
	}
}
