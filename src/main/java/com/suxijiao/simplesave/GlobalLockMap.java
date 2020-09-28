package com.suxijiao.simplesave;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GlobalLockMap {

	// 全局对象锁
	private static final ReadWriteLock globalLock = new ReentrantReadWriteLock();
	private static final Lock globalReadLock = globalLock.readLock();
	private static final Lock globalWriteLock = globalLock.writeLock();

	private static final Map<String, ReadWriteLock> keyMap = new HashMap<>();
	
	protected static Set<String> initKeyMap() {
		try {
			@SuppressWarnings("unchecked")
			Set<String> keys = (Set<String>) SimpleSaveUtils.readFileToObject("globaldata.data.g");
			if(keys != null && keys.size() > 0) {
				for(String str: keys) {
					keyMap.put(str, new ReentrantReadWriteLock());
				}
			}
			return keys;
		} catch (Exception e) {
			throw new RuntimeException("init error");
		}
	}
	
	protected static void getGlobalWriteLock() {
		globalWriteLock.lock();
	}
	
	protected static void releaseGlobalWriteLock() {
		globalWriteLock.unlock();
	}

	protected static void getWriteLock(String key) {
		globalWriteLock.lock();
		try {
			if (!keyMap.containsKey(key)) {
				keyMap.put(key, new ReentrantReadWriteLock());
				try {
					SimpleSaveUtils.writeFile("globaldata.data.g", keyMap.keySet());
				} catch (Exception e) {
					throw new RuntimeException("write file error");
				}
			}
		} finally {
			globalWriteLock.unlock();
		}
		keyMap.get(key).writeLock().lock();
	}
	
	protected static void releaseWriteLock(String key) {
		globalReadLock.lock();
		try {
			if (!keyMap.containsKey(key)) {
				throw new RuntimeException("key is not exists");
			}
			keyMap.get(key).writeLock().unlock();
		} finally {
			globalReadLock.unlock();
		}
	}
	
	protected static void getReadLock(String key) {
		globalReadLock.lock();
		try {
			if (!keyMap.containsKey(key)) {
				throw new RuntimeException("key is not exists");
			}
			keyMap.get(key).readLock().lock();
		} finally {
			globalReadLock.unlock();
		}
	}
	
	protected static void releaseReadLock(String key) {
		globalReadLock.lock();
		try {
			if (!keyMap.containsKey(key)) {
				throw new RuntimeException("key is not exists");
			}
			keyMap.get(key).readLock().unlock();
		} finally {
			globalReadLock.unlock();
		}
	}
}
