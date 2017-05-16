package com.chd.yunpan.net;

import java.io.IOException;
import java.util.List;

//import org.apache.http.NameValuePair;

public class HttpUtils {

	/**
	 * 网络访问
	 * */
	public static String GetStringForHttpPost(CookieUtil cookieUtil,
			List</*NameValuePair*/Object> list, String url, int size)
			throws IOException {
		if(url.startsWith("https://")){
			return HttpsUtils.GetStringForHttpsPost(cookieUtil, list, url, size);
		}
		/*HttpURLConnection conn= null;
		DataOutputStream dos =null;
		try {
			URL url2 = new URL(url);
			conn = (HttpURLConnection) url2.openConnection();
			conn.setReadTimeout(5000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Cookie", "fuid=" + cookieUtil.getId()
					+ ";token=" + cookieUtil.getToken());
			conn.connect();
		dos = new DataOutputStream(conn.getOutputStream());
			StringBuffer buf = new StringBuffer();
			if (list != null) {
				for (int i = 0; i < list.size(); i++) {
					//System.out.println(list.get(i).getValue());
					if(i==list.size()-1){
						buf.append(list.get(i).getName()+"="+URLEncoder.encode(list.get(i).getValue(),"utf-8"));
					}else {
						buf.append(list.get(i).getName()+"="+URLEncoder.encode(list.get(i).getValue(),"utf-8")+"&");
					}
				}
			}
			
			dos.write(buf.toString().getBytes("utf-8"));
			dos.flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			return UTF2GBK.Unicode2GBK(sb.toString());
		} catch (Exception e) {
			
		}finally{
			if(dos!=null){
				dos.close();
			}
			if(conn!=null){
				
				conn.disconnect();
			}
			
		}*/
		return null;
	}



	/**
	 * 网络访问
	 * */
	public static String GetStringForHttpGet(CookieUtil cookieUtil,
			List</*NameValuePair*/Object> list, String ul, int size)
			throws IOException {
		System.out.println(ul);
		if(ul.startsWith("https://")){
			return HttpsUtils.GetStringForHttpsGet(cookieUtil, list, ul, size);
		}

	/*	if (list != null) {
			ul += "?";
			for (int j = 0; j < list.size(); j++) {
				NameValuePair pair = list.get(j);
				ul = ul + pair.getName() + "=" + pair.getValue();
				if (j < list.size() - 1) {
					ul += "&";
				}
			}

		}
		URL url = new URL(ul);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setReadTimeout(5000);
		if(cookieUtil!=null)
		urlConnection.setRequestProperty("Cookie", "fuid=" + cookieUtil.getId()
				+ ";token=" + cookieUtil.getToken());
		try {
			urlConnection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				sb.append(temp);
			}
			return UTF2GBK.Unicode2GBK(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			urlConnection.disconnect();
		}
*/
		return null;
	}

	
}
