/**
 * 
 */
package site.franksite.cet.http;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import site.franksite.cet.http.bean.CETStudentBean;

/**
 * @author Frank
 *
 */
public class CETHttpBusiness {

	private static final String KEY_DATA = "data";
	private static final String KEY_IMG_CODE = "v";
	private HttpHelper helper;
	
	public CETHttpBusiness() {
		helper = HttpHelper.build();
	}
	
	private String getImageRef(String examNumber) {
		String getHtml = helper.get("http://cache.neea.edu.cn/Imgs.do?c=CET&ik=" + examNumber + "&t=" + Math.random());
		String imgRef = getHtml.substring(getHtml.indexOf("(") + 1, getHtml.indexOf(")")).replace("\"", "");
		return imgRef;
	}
	
	public File getImageCode(String examNumber) {
		String filePath = helper.get(getImageRef(examNumber));
		return new File(filePath);
	}
	
	public String getGrade(CETStudentBean student) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(KEY_DATA, new CETDataSerialize().serialize(student) + "," + student.getExamNumber() + "," + student.getName());
		params.put(KEY_IMG_CODE, student.getImgCode());
		return helper.post("http://cache.neea.edu.cn/cet/query", params);
	}
	
}
