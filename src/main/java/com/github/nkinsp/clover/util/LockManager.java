package com.github.nkinsp.clover.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;



public class LockManager {

	
	private final Map<Object, LockObject>  lockMap = new ConcurrentHashMap<>();
	
	private Timer timer;
	
	private LockManager() {}
	
	private static final LockManager manager = new LockManager();
	
	
	
	
	private synchronized LockObject getLock(Object key) {
		
		LockObject lock = lockMap.computeIfAbsent(key, s->new LockObject());
		
		startLockTimer();
		
		return lock;
		
	}
	
	private synchronized void startLockTimer() {
		if(timer != null) {
			return;
		}
		
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				System.out.println("check timer");
				checkTimerTask();
				
			}
		}, 10,1000*10);
		
	}
	
	private  void checkTimerTask() {
	
		long time5min = 1000*10;
		List<Object> removeKeys = lockMap.keySet().stream().filter(key->{
			
			
			LockObject value =  lockMap.get(key);
			
			if(value == null) {
				return false;
			}
			
			if(value.time == 0) {
				return false;
			}
			
			long t =  System.currentTimeMillis() - value.time;
			if(!value.run && t > time5min) {
				return true;
			}
			
			return false;
			
			
		}).collect(Collectors.toList());
		
		System.out.println("删除锁SIZE=>"+removeKeys.size());
		
		for (Object object : removeKeys) {
			
			lockMap.remove(object);
			System.out.println("删除锁=>"+object);
			
		}
		
		
	}
	
	public class LockObject {
		
		
		private volatile long time = 0L;
		
		private final Lock lock = new ReentrantLock();

		private volatile boolean run = false;
		
		public  void lock() {
			
			lock.lock();
			
			this.time = System.currentTimeMillis();
			
			this.run = true;
		}
		
		public  void unLock() {
			
			lock.unlock();
			this.run = false;
		}
		
	}
	
	
	
	public  static void lock(Object value) {

		
		LockObject lock = manager.getLock(value);
		lock.lock();
		

	} 
	
	
	public static void unLock(Object value) {

		LockObject lock = manager.getLock(value);		
		lock.unLock();
		

	}
	
	
	
	
	public static void main(String[] args) {
	
		
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				System.out.println("check timer");
////				checkTimerTask();
//				
//			}
//		}, 1000,1000*5);
		
		for (int i = 0; i <20; i++) {
//			final String v = i+"";
			new Thread(()->{
				String key = "10000";
				
				String v = Thread.currentThread().getName();
				try {
					LockManager.lock(key);
					System.out.println("开始加锁-->  "+v);
					Thread.sleep(1000);
					System.out.println("任务结束-->  "+v);
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					System.out.println("取消加锁-->  "+v);
					System.out.println("================");
					LockManager.unLock(key);
					
				}
		
				
			}).start();
		}
		
	}
}
