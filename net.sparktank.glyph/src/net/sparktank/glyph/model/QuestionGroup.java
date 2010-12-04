/*
 * Copyright 2010 Fae Hutter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

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
	
	@Override
	public String toString() {
		return getHumanName() + "(" + this.questions.size() + ")";
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
