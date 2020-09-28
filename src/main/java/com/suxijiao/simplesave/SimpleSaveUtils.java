package com.suxijiao.simplesave;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleSaveUtils {
	private static final Logger logger = LoggerFactory.getLogger(SimpleSaveUtils.class);
	/** 深度复制对象 
	 * @throws Exception */
	protected static Object deepCopyObject(Object o) {
		Object copy = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		ByteArrayInputStream bais = null;
		ObjectInputStream ois = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);
            copy = ois.readObject();
        } catch (Exception e) {
        	logger.error("复制对象失败", e);	
        } finally {
        	try {
	        	if(oos != null) {
	        		oos.close();
	        	}
	        	if(baos != null) {
	        		baos.close();
	        	}
	        	if(ois != null) {
	        		ois.close();
	        	}
	        	if(bais != null) {
	        		bais.close();
	        	}
        	} catch (Exception ex) {
        		logger.error("关闭流失败", ex);
        	}
        }
        return copy;
	}
	
	/** 备份文件*/
	protected static void bakFile(String fileName) throws Exception {
		copyFile(fileName, fileName + ".temp");
	}
	
	/** 复制文件*/
	protected static void copyFile(String fileName, String targetFileName) throws Exception {
		File source = new File(getPath() + fileName);
		if(!source.exists()) {
			source.createNewFile();
		}
		File dest = new File(getPath() + targetFileName);
		if(!dest.exists()) {
			dest.createNewFile();
		}
	    InputStream in = null;
	    OutputStream out = null;
	    try {
	        in = new FileInputStream(source);
	        out = new FileOutputStream(dest);
	
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = in.read(buffer)) > 0) {
	            out.write(buffer, 0, len);
	        }
	    } finally {
	    	if(out != null) {
	    		out.close();
	    	}
	    	if(in != null) {
	    		in.close();
	    	}
	    }
	} 
	
	/** 恢复文件*/
	protected static void reStoreFile(String fileName) throws Exception {
		copyFile(fileName + ".temp", fileName);
	}
	
	/** 序列化对象到文件中*/
	protected static void writeFile(String fileName, Object o) throws Exception {
		logger.info("开始序列化文件-" + getPath() + fileName);
		File file = new File(getPath() + fileName);
		if(!file.exists()) {
			file.createNewFile();
		}
        FileOutputStream fos = null;
        ObjectOutputStream objectOutputStream = null;
        try {
        	fos = new FileOutputStream(file);
        	objectOutputStream=new ObjectOutputStream(fos);
        	objectOutputStream.writeObject(o);
        	logger.info("序列化文件成功-" + getPath() + fileName);
        } finally {
        	if(objectOutputStream != null) {
        		objectOutputStream.close();
        	}
        	if(fos != null) {
        		fos.close();
        	}
        }
	}
	
	/** 反序列化文件到对象*/
	protected static Object readFileToObject(String fileName) throws Exception {
		File file = new File(getPath() + fileName);
		if(!file.exists()) {
			logger.info(getPath() + fileName + "--文件不存在");
			return null;
		}
		FileInputStream fileInputStream = null;
		ObjectInputStream objectInputStream = null;
		Object result = null;
		try {
			fileInputStream = new FileInputStream(file);
			objectInputStream = new ObjectInputStream(fileInputStream);
			result = objectInputStream.readObject();
		} finally {
			if(objectInputStream != null) {
				objectInputStream.close();
			}
			if(fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return result;
	}
	
	/** 获取数据路径*/
	protected static String getPath(){
		String result = null;
		String path = SimpleSaveUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		if(System.getProperty("os.name").contains("dows"))
		{
			path = path.substring(1,path.length());
		}
		if(path.contains("jar"))
		{
			path = path.substring(0,path.lastIndexOf("."));
			result = path.substring(0,path.lastIndexOf("/"));
		}
		if(result == null) {
			result = path.replace("/target/classes/", "");
		}
		if(result.startsWith("file:")) {
			result = result.substring(5);
		}
		return result + "/data/";
	}
	
	/** 创建数据文件夹*/
	protected static void createDataDir() {
		File dir = new File(getPath());
		if(!dir.exists()) {
			dir.mkdirs();
		}
	}
	
}
