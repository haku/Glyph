package net.sparktank.glyph.model;

import java.util.Collection;
import java.util.Collections;

public class Question {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	final private String question;
	final private Collection<String> answers;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -	
	
	public Question (String question, Collection<String> answers) {
		this.question = question;
		this.answers = answers;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -	
	
	public String getQuestion() {
		return this.question;
	}
	
	public Collection<String> getAnswers() {
		return Collections.unmodifiableCollection(this.answers);
	}
	
	public boolean answerIsCorrect (String answer) {
		return this.answers.contains(answer);
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -	
}
