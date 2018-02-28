/**
 * 
 */
package site.franksite.cet.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import site.franksite.cet.http.bean.CETStudentBean;

/**
 * @author Frank
 *
 */
public class CETDataSerialize {

	private static final String[] Z_RULE = {"","CET4-D","CET6-D","CJT4-D","CJT6-D","PHS4-D","PHS6-D","CRT4-D","CRT6-D","TFU4-D"};
	
	public String serialize(CETStudentBean student) {
		String type = Z_RULE[findIndex(student.getExamNumber())];
		InputStream jsonInput = getClass().getResourceAsStream("/map.json");
		try {
			String mapString = IOUtils.toString(jsonInput, "UTF-8");
			Object obj = JSON.parse(mapString);
			JSONObject jsonObj = null;
			if (obj instanceof JSONObject) {
				jsonObj = (JSONObject) obj;
			}
			if (null != jsonObj) {
				Object arr = jsonObj.get("rdsub");
				if (arr instanceof JSONArray) {
					for (Object json : (JSONArray) arr) {
						if (((JSONObject) json).get("code").equals(type)) {
							return ((JSONObject) json).getString("tab");
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				jsonInput.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private int findIndex(String examNumber) {
		int index = -1;
		char firCh = examNumber.charAt(0);
		if (firCh == 'F') {
			index = 1;
		} else if (firCh == 'S') {
			index = 2;
		} else {
			firCh = examNumber.charAt(9);
			if (firCh >= '0' && firCh <= '9') {
				index = firCh - '0';
			}
		}
		return index;
	}
	
}
