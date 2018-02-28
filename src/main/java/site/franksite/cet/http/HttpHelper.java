/**
 * 
 */
package site.franksite.cet.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;

/**
 * HTTP请求辅助类
 * @author Frank
 *
 */
public class HttpHelper { // 该类为有状态的

	private HttpClient client; // 请求客户端
	private CookieStore cookie; // cookie
	
	private HttpHelper() {
		cookie = new BasicCookieStore();
		client = HttpClients
		.custom()
		.setConnectionTimeToLive(30, TimeUnit.SECONDS)
		.setDefaultCookieStore(cookie)
		.build();
	}
	
	public static HttpHelper build() {
		return new HttpHelper();
	}
	/**
	 * 请求POST到服务器
	 * @param url 请求的URL服务器地址
	 * @param params 请求的参数
	 * @return 服务器响应
	 */
	public String post(String url, Map<String, Object> params) {
		HttpPost post = new HttpPost(url); // 新建POST
		setCETHeader(post);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		List<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		for (String key : params.keySet()) {
			pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
		}
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		InputStream responseInput = null;
		try {
			HttpResponse response = client.execute(post);
			responseInput = response.getEntity().getContent();
			return IOUtils.toString(responseInput, "UTF-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				responseInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	/**
	 * 发送GET请求到服务器
	 * @param url 服务器的URL地址
	 * @return 服务器响应
	 */
	public String get(String url) {
		HttpGet get = new HttpGet(url);
		InputStream responseInput = null;
		setCETHeader(get);
		try {
			HttpResponse response = client.execute(get);
			String contentType = response.getEntity().getContentType().getValue();
			responseInput = response.getEntity().getContent();
			if (contentType.contains("text/html")) {
				// 返回的为HTML字符串
				return IOUtils.toString(responseInput, "UTF-8");
			} else if (contentType.matches("image\\/(jpeg|png|gif|jpg)")) {
				long time = new Date().getTime();
				File imgFile = new File(time + "." + contentType.replace("image/", ""));
				FileOutputStream imgOutput = new FileOutputStream(imgFile);
				IOUtils.write(IOUtils.toByteArray(responseInput), imgOutput);
				imgOutput.close();
				return imgFile.getAbsolutePath();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				responseInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void setCETHeader(AbstractHttpMessage httpMsg) {
		httpMsg.setHeader("Referer", "http://cet.neea.edu.cn/cet/");
//		httpMsg.setHeader("Origin", "http://cet.neea.edu.cn");
//		httpMsg.setHeader("Host", "cache.neea.edu.cn");
	}
}
