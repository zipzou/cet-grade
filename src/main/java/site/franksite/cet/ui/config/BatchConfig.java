/**
 * 
 */
package site.franksite.cet.ui.config;

/**
 * @author Frank
 *
 */
public class BatchConfig {

	private String filePath; // 文件路径

	/**
	 * @return the filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * @param filePath the filePath to set
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchConfig [filePath=" + filePath + "]";
	}
}
