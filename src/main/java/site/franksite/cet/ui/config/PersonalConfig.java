/**
 * 
 */
package site.franksite.cet.ui.config;

/**
 * @author Frank
 *
 */
public class PersonalConfig {

	private String name;
	private String examNumber;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersonalConfig [name=" + name + ", examNumber=" + examNumber
				+ "]";
	}
	
}
