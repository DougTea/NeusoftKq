/**
 * 
 */
package com.neusoft.datainsight.checkwork;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.neusoft.datainsight.checkwork.service.HttpService;

import net.sourceforge.tess4j.util.LoadLibs;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * @author kuangjq
 *
 */
public class Attendance {

	private HttpService service;
	ITesseract tesseract;

	public Attendance() {
		super();
		service = new HttpService();
		tesseract=new Tesseract();
		File tessDataFolder = LoadLibs.extractTessResources("tessdata");
		tesseract.setDatapath(tessDataFolder.getParent());
	}

	public boolean tryToSignIn(String user,String psw,int reTryTime) {
		String htmlStr = service.getKqHtmlStr();
		int i = 0;
		for (; i < reTryTime && htmlStr != null; i++) {
			Document html = Jsoup.parse(htmlStr);
			Map<String, String> formData = new LinkedHashMap<>(10);

			formData.put("login", Boolean.TRUE.toString());
			formData.put("neusoft_attendance_online", "");
			formData.put("neusoft_key",
					html.select("#logon > form > div:nth-child(2) > input[type=\"hidden\"]:nth-child(3)").attr("value"));

			formData.put(
					html.select("#tbLogonPanel > div > div > div:nth-child(2) > div:nth-child(2) > input").attr("name"),
					user);
			formData.put(
					html.select("#tbLogonPanel > div > div > div:nth-child(2) > div:nth-child(3) > input").attr("name"),
					psw);

			String code = null;
			int j=0;
			for (;j<10;j++) {
				try {
					code = tesseract.doOCR(service.getIdentifyCodeImage()).trim().substring(0, 4);
					if (code.matches("\\d{4}")) {
						break;
					}
				} catch (TesseractException e) {
					e.printStackTrace();
				}
			}
			if(j==10) {
				continue;
			}
			formData.put(
					html.select("#tbLogonPanel > div > div > div:nth-child(2) > div:nth-child(5) > input").attr("name"),
					code);

			String attendanceJSPStr = service.getAttendanceHtmlStr(formData);
			if (attendanceJSPStr != null&&attendanceJSPStr.contains("严禁一切不正当的在线打卡考勤行为")) {
				Document attendanceJSP = Jsoup.parse(attendanceJSPStr);
				String attr = attendanceJSP.select("body > form > input[type=\"hidden\"]").attr("value");
				int recordNum = attendanceJSP.select("#kq_part > div.right-kq-part.ml30.fl > table > tbody").first()
						.childNodeSize();

				int result = service.addRecord(attr);
				if (result == recordNum + 2) {
					break;
				}
			} else {
				continue;
			}
		}
		if (i == 100) {
			return false;
		} else {
			return true;
		}
	}

	public static void main(String[] args) {
		Attendance at = new Attendance();
		System.out.println( at.tryToSignIn("kuangjq","Aa123456",50));
	}
}
