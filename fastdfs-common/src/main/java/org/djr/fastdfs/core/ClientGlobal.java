/**
* Copyright (C) 2008 Happy Fish / YuQing
*
* FastDFS Java Client may be copied only under the terms of the GNU Lesser
* General Public License (LGPL).
* Please visit the FastDFS Home Page http://www.csource.org/ for more detail.
**/

package org.djr.fastdfs.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.djr.fastdfs.common.IniFileReader;
import org.djr.fastdfs.common.MyException;
import org.djr.fastdfs.pool.PoolBean;
import org.djr.fastdfs.utils.StringUtils;

/**
* Global variables
* @author Happy Fish / YuQing
* @version Version 1.11
*/
public class ClientGlobal
{
	public static int g_connect_timeout; //millisecond
	public static int g_network_timeout; //millisecond
	public static String g_charset;
	public static int g_tracker_http_port;
	public static boolean g_anti_steal_token;  //if anti-steal token
	public static String g_secret_key;   //generage token secret key
	public static TrackerGroup g_tracker_group;
	
	public static final int DEFAULT_CONNECT_TIMEOUT = 5;  //second
	public static final int DEFAULT_NETWORK_TIMEOUT = 30; //second
  
	public static String conf_filename = null;
	public static PoolBean bean = null;
  
  public static String[] groups = null;  //应用指定的组
	
	private ClientGlobal()
	{
	}
	
/**
* load global variables
* @param conf_filename config filename
*/
	public static void init(String _conf_filename) throws FileNotFoundException, IOException, MyException
	{
  		IniFileReader iniReader;
  		String[] szTrackerServers;
			String[] parts;
			
			conf_filename = _conf_filename;
  		iniReader = new IniFileReader(_conf_filename);

			g_connect_timeout = iniReader.getIntValue("connect_timeout", DEFAULT_CONNECT_TIMEOUT);
  		if (g_connect_timeout < 0)
  		{
  			g_connect_timeout = DEFAULT_CONNECT_TIMEOUT;
  		}
  		g_connect_timeout *= 1000; //millisecond
  		
  		g_network_timeout = iniReader.getIntValue("network_timeout", DEFAULT_NETWORK_TIMEOUT);
  		if (g_network_timeout < 0)
  		{
  			g_network_timeout = DEFAULT_NETWORK_TIMEOUT;
  		}
  		g_network_timeout *= 1000; //millisecond

  		g_charset = iniReader.getStrValue("charset");
  		if (g_charset == null || g_charset.length() == 0)
  		{
  			g_charset = "ISO8859-1";
  		}
  		
  		szTrackerServers = iniReader.getValues("tracker_server");
  		if (szTrackerServers == null)
  		{
  			throw new MyException("item \"tracker_server\" in " + _conf_filename + " not found");
  		}
  		
  		InetSocketAddress[] tracker_servers = new InetSocketAddress[szTrackerServers.length];
  		for (int i=0; i<szTrackerServers.length; i++)
  		{
  			parts = szTrackerServers[i].split("\\:", 2);
  			if (parts.length != 2)
  			{
  				throw new MyException("the value of item \"tracker_server\" is invalid, the correct format is host:port");
  			}
  			
  			tracker_servers[i] = new InetSocketAddress(parts[0].trim(), Integer.parseInt(parts[1].trim()));
  		}
  		g_tracker_group = new TrackerGroup(tracker_servers);
  		
  		g_tracker_http_port = iniReader.getIntValue("http.tracker_http_port", 80);
  		g_anti_steal_token = iniReader.getBoolValue("http.anti_steal_token", false);
  		if (g_anti_steal_token)
  		{
  			g_secret_key = iniReader.getStrValue("http.secret_key");
  		}
  		
  		/*根据配置文件，初始化组信息，如果组信息为空，返回错误*/
  		groups = iniReader.getValues("storage_group");
  		if( groups == null){
  		  throw new MyException("item \"storage_group\" in " + _conf_filename + " not found");
  		}
  		
  		initPoolBean(iniReader, _conf_filename);
	}
	
	/**
	 * 增加方法，初始化连接池配置
	 * @param iniReader
	 * @throws MyException 
	 */
	private static void initPoolBean(IniFileReader iniReader, String conf_filename) throws MyException{
	  bean = new PoolBean();
	  bean.setFastDFSConfName(conf_filename);
	  
	  String poolName = iniReader.getStrValue("poolName");
    if(poolName != null && StringUtils.isNotBlank(poolName))
      bean.setPoolName(poolName);
    
	  int initConnections = iniReader.getIntValue("initConnections", 2);
    if(initConnections < groups.length){
      throw new MyException("item \"initConnections \" in " + conf_filename + " must be greater than length of storage_group");
    }
	  bean.setInitConnections(initConnections);
	  
	  int minConnections =iniReader.getIntValue("minConnections", 1);
    if(minConnections < groups.length){
      throw new MyException("item \"minConnections \" in " + conf_filename + " must be greater than length of storage_group");
    }
	  bean.setMinConnections(minConnections);
	  
	  int maxConnections = iniReader.getIntValue("maxConnections", 10);
    if(maxConnections < groups.length){
      throw new MyException("item \"maxConnections \" in " + conf_filename + " must be greater than length of storage_group");
    }
	  bean.setMaxConnections(maxConnections);
	  
	  if(minConnections >= maxConnections){
	    throw new MyException("item \"maxConnections\" in " + conf_filename + " must be greater than minConnections");
	  }else if(initConnections >= maxConnections)
      throw new MyException("item \"maxConnections error\" in " + conf_filename + " must be greater than initConnections");
	  else if(minConnections > initConnections)
      throw new MyException("item \"initConnections error\" in " + conf_filename + " must be greater than minConnections");
	  
	  int activeMaxConnections = iniReader.getIntValue("activeMaxConnections", 100);
	  bean.setActiveMaxConnections(activeMaxConnections);
	  
	  int connectionTimeOut = iniReader.getIntValue("connectionTimeOut", 10 * 1000);
	  bean.setConnectionTimeOut(connectionTimeOut);
	  
	  boolean isCheakPool = iniReader.getBoolValue("isCheakPool", false);
	  bean.setCheakPool(isCheakPool);
	  
	  int lazyCheck = iniReader.getIntValue("lazyCheck", 1000 * 60 * 60);
	  bean.setLazyCheck(lazyCheck);
	  
	  int periodCheck = iniReader.getIntValue("periodCheck", 1000 * 60 * 60);
	  bean.setPeriodCheck(periodCheck);
	  
	}
	
/**
* construct Socket object
* @param ip_addr ip address or hostname
* @param port port number
* @return connected Socket object
*/
	public static Socket getSocket(String ip_addr, int port) throws IOException
	{
		Socket sock = new Socket();
		sock.setSoTimeout(ClientGlobal.g_network_timeout);
		sock.connect(new InetSocketAddress(ip_addr, port), ClientGlobal.g_connect_timeout);
		return sock;
	}
	
/**
* construct Socket object
* @param addr InetSocketAddress object, including ip address and port
* @return connected Socket object
*/
	public static Socket getSocket(InetSocketAddress addr) throws IOException
	{
		Socket sock = new Socket();
		sock.setSoTimeout(ClientGlobal.g_network_timeout);
		sock.connect(addr, ClientGlobal.g_connect_timeout);
		return sock;
	}
	
	public static int getG_connect_timeout()
	{
		return g_connect_timeout;
	}
	
	public static void setG_connect_timeout(int connect_timeout)
	{
		ClientGlobal.g_connect_timeout = connect_timeout;
	}
	
	public static int getG_network_timeout()
	{
		return g_network_timeout;
	}
	
	public static void setG_network_timeout(int network_timeout)
	{
		ClientGlobal.g_network_timeout = network_timeout;
	}
	
	public static String getG_charset()
	{
		return g_charset;
	}
	
	public static void setG_charset(String charset)
	{
		ClientGlobal.g_charset = charset;
	}
	
	public static int getG_tracker_http_port()
	{
		return g_tracker_http_port;
	}
	
	public static void setG_tracker_http_port(int tracker_http_port)
	{
		ClientGlobal.g_tracker_http_port = tracker_http_port;
	}
	
	public static boolean getG_anti_steal_token()
	{
		return g_anti_steal_token;
	}
	
	public static boolean isG_anti_steal_token()
	{
		return g_anti_steal_token;
	}
	
	public static void setG_anti_steal_token(boolean anti_steal_token)
	{
		ClientGlobal.g_anti_steal_token = anti_steal_token;
	}
	
	public static String getG_secret_key()
	{
		return g_secret_key;
	}
	
	public static void setG_secret_key(String secret_key)
	{
		ClientGlobal.g_secret_key = secret_key;
	}
	
	public static TrackerGroup getG_tracker_group()
	{
		return g_tracker_group;
	}
	
	public static void setG_tracker_group(TrackerGroup tracker_group)
	{
		ClientGlobal.g_tracker_group = tracker_group;
	}
}
