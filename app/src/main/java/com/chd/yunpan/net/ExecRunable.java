package com.chd.yunpan.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecRunable {
	private static ExecutorService service = Executors.newFixedThreadPool(10);
	private static ExecutorService down = Executors.newFixedThreadPool(1);
	public static void execRun(Runnable runnable){
		service.execute(runnable);
	}
	
	public static void execDwon(Runnable runnable){
		down.execute(runnable);
	}
}
