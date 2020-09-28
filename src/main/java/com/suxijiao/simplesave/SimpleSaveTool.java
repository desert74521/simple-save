package com.suxijiao.simplesave;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleSaveTool {
	
	// 是否初始化
	private static boolean isInitData = false; 
	
	// 数据
	private static Map<String,Object> dbData = new HashMap<>();

	/**
	 * @desc init data
	 * @param forceInit true -> if init fail try to delete all data and rebuild empty data
	 * */
	private static void initData(boolean forceInit) {
		GlobalLockMap.getGlobalWriteLock();
		if(isInitData) {
			return;
		}
		Set<String> keys = GlobalLockMap.initKeyMap();
		if(keys != null && keys.size() > 0) {
			for(String str: keys) {
				Object data;
				try {
					data = SimpleSaveUtils.readFileToObject(str + ".data");
					dbData.put(str, data);
				} catch (Exception e) {
					throw new RuntimeException("init data error");
				}
			}
		}
		GlobalLockMap.releaseGlobalWriteLock();
		
	}
	
	/**
	 * @desc init data if init fail throw exception
	 * */
	private static void initData() {
		initData(false);
	}
	
	/**
	 * @desc update data by key
	 * @param data data
	 * @param key
	 * */
	public static <T> void saveOrUpdateData(String key, T data) {
		if(!isInitData) {
			initData();
		}
		GlobalLockMap.getWriteLock(key);
		try {
			SimpleSaveUtils.writeFile(key+".data", data);
		} catch (Exception e) {
			throw new RuntimeException("file save error");
		} finally {
			GlobalLockMap.releaseWriteLock(key);
		}
	}
	
	/**
	 * @desc update data by key
	 * @param key key
	 * @param cls data
	 * */
	public static <T> T getDataByKey(String key) {
		if(!isInitData) {
			initData();
		}
		GlobalLockMap.getReadLock(key);
		try {
			@SuppressWarnings("unchecked")
			T data = (T)dbData.get(key);
			return data;
		} catch (Exception e) {
			throw new RuntimeException("file read error");
		} finally {
			GlobalLockMap.getReadLock(key);
		}
	}
}
