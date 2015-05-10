package sk.upjs.ics.novotnyr.mlt.gui;

import sun.security.krb5.Config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class Configuration {
	private static final Configuration INSTANCE = new Configuration();

	public static final String PROPERTY_EXPORT_FOLDER = "exportFolder";

	public static final String PROPERTY_MOST_RECENTLY_USED_OPEN_FOLDER = "mostRecentlyUsedOpenFolder";

	public static final String PROPERTY_MOST_RECENTLY_USED_FILES = "mostRecentlyUsedFiles";

	private final Properties properties;

	private String PROPERTY_FILENAME = ".mltsettings"; ;


	private final File userHome;
	private final File propertiesFile;

	private Configuration() {
		userHome = new File(System.getProperty("user.home"));
		propertiesFile = new File(userHome, PROPERTY_FILENAME);

		properties = initProperties(propertiesFile);
	}


	private Properties initProperties(File propertiesFile) {
		Properties properties = new Properties();
		if(!propertiesFile.canRead()) {
			properties.setProperty(PROPERTY_EXPORT_FOLDER, userHome.toString());
			properties.setProperty(PROPERTY_MOST_RECENTLY_USED_OPEN_FOLDER, userHome.toString());
		}
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(propertiesFile);
			properties.load(fileReader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return properties;
	}

	public void save() {
		FileWriter writer = null;
		try {
			writer = new FileWriter(this.propertiesFile);
			properties.store(writer, "");
		} catch (IOException e) {
			throw new ConfigurationException("Unable to save configuration: " + e.getMessage(), e);
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	public static Configuration getInstance() {
		return INSTANCE;
	}

	public File getExportFolder() {
		return new File(properties.getProperty(PROPERTY_EXPORT_FOLDER));
	}

	public void setExportFolder(File exportFolder) {
		properties.setProperty(PROPERTY_EXPORT_FOLDER, exportFolder.toString());
	}

	public File getMostRecentlyUsedOpenFolder() {
		return new File(properties.getProperty(PROPERTY_MOST_RECENTLY_USED_OPEN_FOLDER));
	}

	public void setMostRecentlyUsedOpenFolder(File mostRecentlyUsedOpenFolder) {
		properties.setProperty(PROPERTY_MOST_RECENTLY_USED_OPEN_FOLDER, mostRecentlyUsedOpenFolder.toString());
	}

	public void addMostRecentlyUsedFile(File file) {
		List<File> mruFiles = getMostRecentlyUsedFiles();
		mruFiles.add(file);

		StringBuilder sb = new StringBuilder();
		for (File mruFile : mruFiles) {
			sb.append(mruFile).append(File.pathSeparator);
		}
		properties.setProperty(PROPERTY_MOST_RECENTLY_USED_FILES, sb.toString());
	}

	public List<File> getMostRecentlyUsedFiles() {
		List<File> mruFiles = new LinkedList<File>();

		String mruString = properties.getProperty(PROPERTY_MOST_RECENTLY_USED_FILES);
		if(mruString == null) {
			return mruFiles;
		}

		StringTokenizer tokenizer = new StringTokenizer(mruString, File.pathSeparator, false);
		while(tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			mruFiles.add(new File(token));
		}
		return mruFiles;
	}
}
