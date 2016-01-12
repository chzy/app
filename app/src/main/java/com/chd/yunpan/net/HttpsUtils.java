package com.chd.yunpan.net;




import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.List;

public class HttpsUtils {
	/**
	 * 网络访问
	 * */
	public static String GetStringForHttpsGet(CookieUtil cookieUtil,
			List</*NameValuePair*/Object> list, String url, int size)
			throws IOException {
		if (url.startsWith("http://")) {
			return /*HttpUtils.GetStringForHttpGet(cookieUtil, list, url, size)*/null;
		}
		/*HttpGet request;
		HttpClient client;
		HttpResponse response;
		client = getNewHttpClient();
		HttpParams httpParams = client.getParams();
		// 连接 超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		// Socket 超时时间
		 HttpConnectionParams.setSoTimeout(httpParams, 7000);
		if (list != null) {
			url += "?";
			for (int j = 0; j < list.size(); j++) {
				NameValuePair pair = list.get(j);
				url = url + pair.getName() + "=" + pair.getValue();
				if (j < list.size() - 1) {
					url += "&";
				}
			}

		}
		request = new HttpGet(url);
		if (cookieUtil == null) {
			request.addHeader("Cookie", "fuid=305806304"
					+ ";token=SSHPRfJDKhaUMV8JP1Jlx3zAEgF77G");
		} else {
			request.addHeader("Cookie", "fuid=" + cookieUtil.getId()
					+ ";token=" + cookieUtil.getToken());
		}

		response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(response.getEntity()).trim();

			return UTF2GBK.Unicode2GBK(result);
		}*/
		if (size > 0) {
			return GetStringForHttpsGet(cookieUtil, list, url, size - 1);
		}
		return null;
	}

	/**
	 * 网络访问
	 * */
	public static String GetStringForHttpsPost(CookieUtil cookieUtil,
			List</*NameValuePair*/Object> list, String url, int size)
			throws IOException {
		if (url.startsWith("http://")) {
			return /*HttpUtils.GetStringForHttpPost(cookieUtil, list, url, size)*/null;
		}
		/*HttpPost request;
		HttpClient client;
		HttpResponse response;
		client = getNewHttpClient();
		HttpParams httpParams = client.getParams();
		// 连接 超时时间
		HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
		// Socket 超时时间
		// HttpConnectionParams.setSoTimeout(httpParams, 7000);

		request = new HttpPost(url);
		if (cookieUtil == null) {
			request.addHeader("Cookie", "fuid=305806304"
					+ ";token=SSHPRfJDKhaUMV8JP1Jlx3zAEgF77G");
		} else {
			request.addHeader("Cookie", "fuid=" + cookieUtil.getId()
					+ ";token=" + cookieUtil.getToken());
		}
		if (list != null) {
			request.setEntity(new UrlEncodedFormEntity(list, "utf-8"));
		}

		response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == 200) {
			String result = EntityUtils.toString(response.getEntity()).trim();

			return UTF2GBK.Unicode2GBK(result);
		}*/
		if (size > 0) {
			return GetStringForHttpsPost(cookieUtil, list, url, size - 1);
		}
		return null;
	}

	/**
	 * 获取HttpClient 能够访问HTTPS和HTTP
	 * 
	 * @return
	 */
	private static CloseableHttpClient getNewHttpClient() {
		try {
			/*KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactoryEx sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);*/
		} catch (Exception e) {
			return /*new DefaultHttpClient()*/null;
		}
			return null;
	}

}
