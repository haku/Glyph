package net.sparktank.glyph.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class QuestionGroup {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	final private String symbolicName;
	final private String humanName;
	final private Collection<Question> questions;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public QuestionGroup (String symbolicName, String humanName) {
		this.symbolicName = symbolicName;
		this.humanName = humanName;
		this.questions = new LinkedList<Question>();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public String getSymbolicName() {
		return this.symbolicName;
	}
	
	public String getHumanName() {
		return this.humanName;
	}
	
	public Collection<Question> getQuestions() {
		return Collections.unmodifiableCollection(this.questions);
	}
	
	public void addQuestion (Question question) {
		this.questions.add(question);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
