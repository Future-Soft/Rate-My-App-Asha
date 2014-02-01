package com.future.ratemyapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;


public class Properties
{
	private Hashtable m_hastable;

	/**
	 * Creates an empty property list with no default values.
	 */
	public Properties()
	{
		m_hastable = new Hashtable();
	}

	private static String readLine(InputStream in,String enc) throws IOException
	{
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		int c;
		while (true)
		{
			c = in.read();
			if (c == -1)
			{
				return bs.toByteArray().length > 0 ? new String(bs.toByteArray(),enc) : null;
			}
			if (c != '\n' && c != -1)
			{
				if (c != '\r')
				{
					bs.write(c);
				}
			}
			else
			{
				break;
			}
		}
		return (bs.toByteArray().length == 0 && c == -1) ? null : new String(bs.toByteArray(),enc);
	}
	
	private static String _readLine(InputStream in) throws IOException
	{
		StringBuffer bs = new StringBuffer();
		int c;
		while (true)
		{
			c = in.read();
			if (c == -1)
			{
				return bs.length() > 0 ? bs.toString() : null;
			}
			if (c != '\n' && c != -1)
			{
				if (c != '\r')
				{
					bs.append((char) c);
				}
			}
			else
			{
				break;
			}
		}
		return (bs.length() == 0 && c == -1) ? null : bs.toString();
	}
	/**
	 * uses default encoding "ISO-8859-8" to load the properties 
	 */
	public void load(InputStream is) throws Exception
	{
		//load(is,"ISO-8859-8");
		load(is,"UTF-8");
	}
	
	/**
	 * loads properties from {@link InputStream} into this properties object
	 * 
	 * @param is
	 */
	public void load(InputStream is,String enc) throws Exception
	{
		String line = null;
		int seperatorInd;

		line = readLine(is,enc);
		//Logger.log(line);
		while (line != null)
		{
			if ((seperatorInd = line.indexOf('=')) != -1 && line.charAt(0) != '#')
			{
				m_hastable.put(line.substring(0, seperatorInd), line.substring(seperatorInd + 1));
			}
			line = readLine(is,enc);
			//Logger.log(line);
		}
	}

	/**
	 * 
	 * @param key
	 * @return return the value associated with this key
	 */
	public String getProperty(String key)
	{
		String res = (String) m_hastable.get(key);
		if (res == null)
			return res;
		return res.trim();

	}
	
	/**
	 * sets a value to a property
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value)
	{
		m_hastable.put(key, value);
	}

	/**
	 * 
	 * @return enumarion on the sets of keys for this properties object
	 */
	public Enumeration propertyNames()
	{
		return m_hastable.keys();
	}

	/**
	 * 
	 * @return the number of keys in this properties object
	 */
	public int size()
	{
		return m_hastable.size();
	}
}