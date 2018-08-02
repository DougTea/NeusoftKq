/**
 * 
 */
package com.neusoft.datainsight.checkwork.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;

import com.neusoft.datainsight.checkwork.util.RedirectIfNotError;

/**
 * @author kuangjq
 *
 */
public class HttpService {
	private HttpClient client;
	private HttpClientContext context;
	
	
	public HttpService() {
		super();
		RedirectStrategy redirectStrategy = new RedirectIfNotError();
		client=HttpClients.custom()
		        .setRedirectStrategy(redirectStrategy)
		        .build();
		context=HttpClientContext.create();
	}


	public String getKqHtmlStr() {
		HttpGet get = new HttpGet("http://kq.neusoft.com/index.jsp");
		try {
	        BasicCookieStore cookieStore = new BasicCookieStore();
	        context.setCookieStore(cookieStore);
			HttpResponse response = client.execute(get,context);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	public String getAttendanceHtmlStr(Map<String, String> formData) {
		RequestBuilder rb = RequestBuilder.post().setUri("http://kq.neusoft.com/login.jsp");
		formData.forEach((k, v) -> {
			rb.addParameter(k, v);
		});
		HttpUriRequest post = rb.build();
		try {
			HttpResponse response = client.execute(post,context);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int addRecord(String formData) {
		HttpPost post = new HttpPost("http://kq.neusoft.com/record.jsp");
		try {
			List<BasicNameValuePair> parameters = new ArrayList<>(1);
			parameters.add(new BasicNameValuePair("currentempoid", formData));
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(parameters);
			post.setEntity(postEntity);
			HttpResponse response = client.execute(post,context);
			int status = response.getStatusLine().getStatusCode();
			if (status==200) {
				HttpEntity entity = response.getEntity();
				String attendanceJSP = EntityUtils.toString(entity);
				if(attendanceJSP!=null) {
					return Jsoup.parse(attendanceJSP).select("#kq_part > div.right-kq-part.ml30.fl > table > tbody").first().childNodeSize();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public BufferedImage getIdentifyCodeImage() {
		HttpGet get =new HttpGet("http://kq.neusoft.com/imageRandeCode");
		try {
			HttpResponse response = client.execute(get,context);
			int status = response.getStatusLine().getStatusCode();
			if (status == 200) {
				HttpEntity entity = response.getEntity();
			    return ImageIO.read(entity.getContent());	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
