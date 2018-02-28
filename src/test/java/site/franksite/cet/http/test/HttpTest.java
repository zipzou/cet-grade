/**
 * 
 */
package site.franksite.cet.http.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import site.franksite.cet.http.CETHttpBusiness;
import site.franksite.cet.http.CETHttpParser;
import site.franksite.cet.http.HttpHelper;
import site.franksite.cet.http.bean.CETStudentBean;

/**
 * @author Frank
 *
 */
public class HttpTest {

	private static final Logger LOG = Logger.getLogger(HttpTest.class);
	
	@Test
	public void testPost() throws UnsupportedEncodingException {
		HttpHelper helper = HttpHelper.build();
		String getHtml = helper.get("http://cache.neea.edu.cn/Imgs.do?c=CET&ik=360032172105103&t=" + Math.random());
		if (null != getHtml) {
			// 获取验证码地址
			String imgRef = getHtml.substring(getHtml.indexOf("(") + 1, getHtml.indexOf(")")).replace("\"", "");
			helper.get(imgRef);
		}
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();
//		LOG.debug(getHtml);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("data", "CET4_172_DANGCI,360032172103127,陈帆");
		params.put("v", code);
		String dataStr = helper.post("http://cache.neea.edu.cn/cet/query", params);
		LOG.debug(dataStr);
		scanner.close();
	}
	
	@Test
	public void testWithStudent() {
		CETHttpBusiness business = new CETHttpBusiness();
		CETStudentBean stu = new CETStudentBean();
		stu.setName("徐博宇");
		stu.setExamNumber("360032172105103");
		File codeFile = business.getImageCode(stu.getExamNumber());
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();
		scanner.close();
		stu.setImgCode(code);
		String grade = business.getGrade(stu);
		JSONObject result = new CETHttpParser().parseResult(grade);
		LOG.debug(result);
	}
}
