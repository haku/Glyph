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

package net.sparktank.glyph.views;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import net.sparktank.glyph.helpers.SwtHelper;
import net.sparktank.glyph.model.Question;
import net.sparktank.glyph.model.QuestionGroup;
import net.sparktank.glyph.model.QuestionHelper;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class PlayView extends ViewPart {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static final String ID = "net.sparktank.glyph.views.PlayView";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	Collection<QuestionGroup> allQuestionGroups = null;
	
	private BlockingQueue<Question> currentGameRemainingQuestions = null;
	private Question currentGameQuestion = null;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	ViewPart methods.
	
	@Override
	public void createPartControl(Composite parent) {
		try {
			createContent();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		createLayout(parent);
	}
	
	@Override
	public void setFocus() {
		this.txtAnswer.setFocus();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private void createContent() throws IOException {
		Collection<QuestionGroup> qs = QuestionHelper.getAllQuestionGroups();
		System.err.println(qs.size() + " question groups found and loaded.");
		this.allQuestionGroups = qs;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	GUI stuff.
	
	protected static final int SEP = 3;
	
	CheckboxTableViewer tblQuestionSets;
	private Button btnAddQuestions;
	private Label lblQuestion;
	Text txtAnswer;
	
	private void createLayout (Composite parent) {
		FormData formData;
		
		parent.setLayout(new FormLayout());
		
		this.tblQuestionSets = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.btnAddQuestions = new Button(parent, SWT.NONE);
		this.lblQuestion = new Label(parent, SWT.CENTER);
		this.txtAnswer = new Text(parent, SWT.CENTER);
		
		this.tblQuestionSets.setContentProvider(this.contentProvider);
		this.tblQuestionSets.setInput(getViewSite()); // use content provider.
		
		this.btnAddQuestions.setText("Add questions");
		this.lblQuestion.setText("Question?");
		this.txtAnswer.setText("Answer!");
		
		SwtHelper.increaseFontSize(this.lblQuestion, 3);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.top = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(this.btnAddQuestions, -SEP);
		this.tblQuestionSets.getTable().setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(100, -SEP);
		this.btnAddQuestions.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(this.tblQuestionSets.getTable(), SEP);
		formData.right = new FormAttachment(100, -SEP);
		formData.top = new FormAttachment(0, SEP);
		this.lblQuestion.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(this.tblQuestionSets.getTable(), SEP);
		formData.right = new FormAttachment(100, -SEP);
		formData.top = new FormAttachment(this.lblQuestion, SEP);
		this.txtAnswer.setLayoutData(formData);
		
		this.lblQuestion.setEnabled(false);
		this.txtAnswer.setEnabled(false);
		
		this.btnAddQuestions.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				try {
					Object[] checkedElements = PlayView.this.tblQuestionSets.getCheckedElements();
					Collection<QuestionGroup> qgs = new LinkedList<QuestionGroup>();
					for (Object object : checkedElements) {
						QuestionGroup qg = (QuestionGroup) object;
						qgs.add(qg);
					}
					addQuestionsToGame(qgs);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/* UNUSED */}
		});
		
		this.txtAnswer.addListener (SWT.DefaultSelection, new Listener () {
			@Override
			public void handleEvent (Event e) {
				procAnswer();
			}
		});
		
		this.txtAnswer.addListener (SWT.FOCUSED, new Listener() {
			@Override
			public void handleEvent(Event event) {
				PlayView.this.txtAnswer.setSelection(0, PlayView.this.txtAnswer.getText().length());
			}
		});
		
	}
	
	private IStructuredContentProvider contentProvider = new IStructuredContentProvider() {
		
		@Override
		public Object[] getElements(Object inputElement) {
			if (PlayView.this.allQuestionGroups != null) {
				return PlayView.this.allQuestionGroups.toArray();
			}
			return new String[]{};
		}
		
		@Override
		public void dispose() {/* UNUSED */}
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {/* UNUSED */}
		
	};
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	Events.
	
	void addQuestionsToGame (Collection<QuestionGroup> questionGroups) {
		List<Question> questions = new LinkedList<Question>();
		for (QuestionGroup qg : questionGroups) {
			questions.addAll(qg.getQuestions());
		}
		if (questions.size() < 1) {
			System.err.println("No questions to add.");
			return;
		}
		
		Collections.shuffle(questions);
		
		this.currentGameRemainingQuestions = new LinkedBlockingQueue<Question>(questions);
		
		presentNextQuestion();
	}
	
	protected void presentNextQuestion () {
		this.currentGameQuestion = this.currentGameRemainingQuestions.poll();
		
		if (this.currentGameQuestion != null) {
			this.lblQuestion.setText(this.currentGameQuestion.getQuestion());
			this.txtAnswer.setText("");
			
			this.lblQuestion.setEnabled(true);
			this.txtAnswer.setEnabled(true);
			
			this.txtAnswer.setFocus();
		}
		else { // End of questions.
			this.lblQuestion.setText("");
			this.txtAnswer.setText("");
			
			this.lblQuestion.setEnabled(false);
			this.txtAnswer.setEnabled(false);
		}
	}
	
	protected void procAnswer () {
		if (this.currentGameQuestion != null) {
			String answer = this.txtAnswer.getText();
			if (answer != null && answer.length() > 0) {
				if (this.currentGameQuestion.answerIsCorrect(answer)) {
					System.err.println("Correct.");
				}
				else {
					System.err.print("Wrong: ");
					System.err.print(answer);
					System.err.print(" != ");
					for (String correctAnswer : this.currentGameQuestion.getAnswers()) {
						System.err.print(correctAnswer);
						System.err.print(", ");
					}
					System.err.println();
				}
				presentNextQuestion();
			}
			else {
				System.err.println("No answer entered.");
			}
		}
		else {
			System.err.println("No question asked.");
		}
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}