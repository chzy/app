package com.chd.yunpan.net;

import com.chd.yunpan.parse.Parse;

import org.apache.http.NameValuePair;

import java.util.List;

public class RequestParam00 {
	public String url ;
	
	public List<NameValuePair> pairs;
	
	
	public static final int GET = 0;
	
	public static final int POST = 1;
	
	public int method = GET;
	
	public Parse parse;
}
