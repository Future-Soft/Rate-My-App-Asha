package com.future.ratemyapp.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.rms.RecordStore;

public class StorageHelper
{
	private static String RS_NAME = "ApplicationSettings";
	private static StorageHelper instance;
	static {
		instance = new StorageHelper(RS_NAME);
	}
	private Hashtable m_cache;
	
	private StorageHelper(String name)
	{
		m_cache = new Hashtable();
		RS_NAME = name;
		init();
	}
	
	private void init()
	{
		RecordStore rs = null;
		try {
		rs = RecordStore.openRecordStore(RS_NAME, true);
		if (rs.getNumRecords() > 0)
		{
			load(rs.getRecord(1),0);
		}
		else
		{
			byte[] buffer = new byte[4];
			writeInt(buffer, 0, 0);
			rs.addRecord(buffer, 0, buffer.length);
		}
		}
		catch (Exception e)
		{ 
			System.out.println(e);
		}
		finally {
			if (rs != null)
				try
				{
					rs.closeRecordStore();
				} catch (Exception e)
				{}
		}
	}
	
	private static void writeInt(byte[] buffer, int offset, int value) {
		buffer[offset] = (byte) (0x000000FF & value);
		buffer[offset + 1] = (byte) ((0x0000FF00 & value) >> 8);
		buffer[offset + 2] = (byte) ((0x00FF0000 & value) >> 16);
		buffer[offset + 3] = (byte) ((0xFF000000 & value) >> 24);
	}
	
	private void load(byte[] buffer,int offset)
	{
		m_cache.clear();
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(RS_NAME, false);
			byte[] temp = rs.getRecord(1);
			DataInputStream din = new DataInputStream(new ByteArrayInputStream(temp));
			int numVals = din.readInt();
			for (int i=0;i<numVals;++i)
			{
				String key = din.readUTF();
				String val = din.readUTF();
				m_cache.put(key, val);
			}
		}
		catch (Exception e)
		{ 
			System.out.println(e);
		}
		finally {
			if (rs != null)
				try
				{
					rs.closeRecordStore();
				} catch (Exception e)
				{}
		}
	}
	
	public static void storeSetting(String key, String value, boolean flush)
	{
		instance._put(key, value, flush);
	}
	
	public static void storeSetting(String key, String value)
	{
		instance._put(key, value);
	}
	
	private void _put(String key, String value, boolean save)
	{ 
		m_cache.put(key, value);
		if (save)
			_save();
	}
	
	private void _put(String key, String value)
	{ 
		_put(key,value,true);
	}
	
	public static String getSetting(String key)
	{
		return instance._get(key);
	}
	
	public static String getSetting(String key, String def)
	{
		return instance._get(key, def);
	}
	
	private String _get(String key)
	{
		return _get(key, null);
	}
	
	private String _get(String key, String def)
	{
		String val = (String) m_cache.get(key);
		if (val == null) return def;
		return val;
	}
	
	public static void flush()
	{
		instance._save();
	}
	
	private void _save()
	{
		RecordStore rs = null;
		try {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.writeInt(m_cache.size());
		Enumeration en = m_cache.keys();
		while (en.hasMoreElements())
		{
			String key = (String) en.nextElement();
			String val = (String) m_cache.get(key);
			dos.writeUTF(key);
			dos.writeUTF(val);
		}
		rs = RecordStore.openRecordStore(RS_NAME, false);
		byte[] temp = bos.toByteArray();
		rs.setRecord(1, temp, 0, temp.length);
		
		}
		catch (Exception e)
		{ 
			System.out.println(e);
		}
		finally {
			if (rs != null)
				try
				{
					rs.closeRecordStore();
				} catch (Exception e)
				{}
		}
		
	}
}
