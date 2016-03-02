package com.chd.photo.entity;

public class ThumUtil 
{
	public static String getThumid(String objid)
	{
		return "trpc://" + objid;
	}
	
	public static boolean isStartWithTrpc(String url)
	{
		return url.startsWith("trpc://");
	}
	
	public static String splitFileName(String filepath)
	{
		if (!filepath.startsWith("file://"))
		{
			return filepath;
		}
		
		return filepath.substring(6);
	}
	
}
