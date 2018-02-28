/**
 * 
 */
package site.franksite.cet.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author Frank
 *
 */
public class CETHttpParser {

	private static final String END_STR = ");";
	private static final String START_STR = "parent.result.callback(";

	public JSONObject parseResult(String response) {
		int start = response.indexOf(START_STR);
		int end = response.indexOf(END_STR);
		String json = response.substring(start + START_STR.length() , end).replace("\"", "");
		json = json.replace('\'', '"');
		return (JSONObject) JSON.parse(json);
	}
	
}
