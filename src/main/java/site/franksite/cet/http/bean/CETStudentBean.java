package site.franksite.cet.http.bean;

public class CETStudentBean {
	private String examNumber; // 准考证号
	private String name; // 姓名
	private String imgCode; //验证码
	/**
	 * @return the examNumber
	 */
	public String getExamNumber() {
		return examNumber;
	}
	/**
	 * @param examNumber the examNumber to set
	 */
	public void setExamNumber(String examNumber) {
		this.examNumber = examNumber;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the imgCode
	 */
	public String getImgCode() {
		return imgCode;
	}
	/**
	 * @param imgCode the imgCode to set
	 */
	public void setImgCode(String imgCode) {
		this.imgCode = imgCode;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CETStudentBean [examNumber=" + examNumber + ", name=" + name
				+ ", imgCode=" + imgCode + "]";
	}
	
}
