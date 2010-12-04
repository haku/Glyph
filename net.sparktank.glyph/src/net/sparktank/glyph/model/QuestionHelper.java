package net.sparktank.glyph.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

import net.sparktank.glyph.Activator;
import net.sparktank.glyph.helpers.ClassPathHelper;
import net.sparktank.glyph.helpers.ClassPathHelper.FileProcessor;

import org.osgi.framework.Bundle;

public class QuestionHelper {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private static final String GLYPH_EXT = ".glyph";
	
	private static final String GROUP = "group=";
	private static final String COMMENT = "#";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	/**
	 * Search for .glyph files and parse them.
	 * Search this bundle and the system classpath.
	 */
	static public Collection<QuestionGroup> getAllQuestionGroups () throws ZipException, IOException {
		final Collection<QuestionGroup> ret = new LinkedList<QuestionGroup>();
		
		Bundle bundle = Activator.getDefault().getBundle();
		@SuppressWarnings("unchecked")
		Enumeration<URL> entries = bundle.findEntries("/", "*" + GLYPH_EXT, true);
		while (entries.hasMoreElements()) {
			URL element = entries.nextElement();
			InputStream is = element.openStream();
			ret.addAll(loadData(new BufferedReader(new InputStreamReader(is))));
		}
		
		ClassPathHelper.searchAndProcessFiles(new FileProcessor() {
			
			@Override
			public boolean acceptFile(File file) {
				return file.getName().toLowerCase().endsWith(GLYPH_EXT);
			}
			
			@Override
			public boolean acceptFile(String filename) {
				return filename.toLowerCase().endsWith(GLYPH_EXT);
			}
			
			@Override
			public void procFile(File file) {
				try {
					ret.addAll(loadData(file));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
			@Override
			public void procStream(InputStream is) {
				try {
					ret.addAll(loadData(new BufferedReader(new InputStreamReader(is))));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			
		});
		
		return Collections.unmodifiableCollection(ret);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	static Collection<QuestionGroup> loadData (File dataFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(dataFile));
		return loadData(reader);
	}
	
	/**
	 * This method will close the stream when it is finished with it.
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	static Collection<QuestionGroup> loadData (BufferedReader reader) throws IOException {
		List<String> groupLines = new LinkedList<String>();
		List<String> dataLines = new LinkedList<String>();
		
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				if (line.length() > 0) {
    				if (line.startsWith(GROUP)) {
    					groupLines.add(line);
    				}
    				else if (!line.startsWith(COMMENT) && line.contains("=")) {
    					dataLines.add(line);
    				}
				}
			}
		}
		finally {
			reader.close();
		}
		
		Map<String, QuestionGroup> questionGroups = new HashMap<String, QuestionGroup>();
		int groupFlagLength = GROUP.length();
		for (String groupLine : groupLines) {
			String groupData = groupLine.substring(groupFlagLength);
			String[] parts = groupData.split("\\|");
			if (parts.length < 2) throw new IllegalArgumentException("Data line is manformed: '"+groupData+"'.");
			
			QuestionGroup qg = new QuestionGroup(parts[0], parts[1]);
			questionGroups.put(qg.getSymbolicName(), qg);
		}
		
		for (String dataLine : dataLines) {
			int x = dataLine.indexOf("=");
			if (x > 0) {
				String questionData = dataLine.substring(x+1);
				String[] parts = questionData.split("\\|");
				if (parts.length >= 2) {
					String questionGroupSymbol = dataLine.substring(0, x);
					QuestionGroup questionGroup = questionGroups.get(questionGroupSymbol);
					if (questionGroup != null) {
						Collection<String> answers = new LinkedList<String>();
						for (int i = 1; i < parts.length; i++) {
							answers.add(parts[i]);
						}
						Question question = new Question(parts[0], answers);
						questionGroup.addQuestion(question);
					}
				}
			}
		}
		
		return questionGroups.values();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
