package site.franksite.cet.ui.config;

public class WindowConfig {
	
	private PersonalConfig personal;
	private BatchConfig batch;
	private WindowConfig() {
		super();
		personal = new PersonalConfig();
		batch = new BatchConfig();
	}
	
	private static class WindowConfigHolder {
		private static WindowConfig INSTANCE = new WindowConfig();
	}
	
	public static WindowConfig instance() {
		return WindowConfigHolder.INSTANCE;
	}

	/**
	 * @return the personal
	 */
	public PersonalConfig getPersonal() {
		return personal;
	}

	/**
	 * @return the batch
	 */
	public BatchConfig getBatch() {
		return batch;
	}
	
}
