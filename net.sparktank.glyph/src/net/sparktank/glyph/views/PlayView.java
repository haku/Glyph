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

import net.sparktank.glyph.helpers.ColumnLayout;
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
	private Collection<Question> currentGameAnswersCorrect = null;
	private Collection<Question> currentGameAnswersIncorrect = null;
	
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
	private Button btnResetGame;
	
	Composite playArea;
	private Label lblQuestion;
	private Label lblCorrectAnswer;
	Text txtAnswer;
	private Label lblStatus;
	
	private void createLayout (Composite parent) {
		FormData formData;
		
		parent.setLayout(new FormLayout());
		
		this.tblQuestionSets = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.btnAddQuestions = new Button(parent, SWT.NONE);
		this.btnResetGame = new Button(parent, SWT.NONE);
		
		this.playArea = new Composite(parent, SWT.NONE);
		this.lblQuestion = new Label(this.playArea, SWT.CENTER);
		this.lblCorrectAnswer = new Label(this.playArea, SWT.CENTER);
		this.txtAnswer = new Text(this.playArea, SWT.CENTER);
		this.lblStatus = new Label(this.playArea, SWT.CENTER);
		
		this.tblQuestionSets.setContentProvider(this.contentProvider);
		this.tblQuestionSets.setInput(getViewSite()); // use content provider.
		
		this.btnResetGame.setText("Reset");
		this.btnAddQuestions.setText("Add questions");
		
		SwtHelper.increaseFontSize(this.lblQuestion, 3);
		SwtHelper.increaseFontSize(this.lblCorrectAnswer, 3);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.top = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(this.btnAddQuestions, -SEP);
		this.tblQuestionSets.getTable().setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(100, -SEP);
		this.btnResetGame.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(this.btnResetGame, SEP);
		formData.bottom = new FormAttachment(100, -SEP);
		this.btnAddQuestions.setLayoutData(formData);
		
		this.playArea.setLayout(new ColumnLayout());
		formData = new FormData();
		formData.left = new FormAttachment(this.tblQuestionSets.getTable(), SEP);
		formData.right = new FormAttachment(100, -SEP);
		formData.top = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(100, SEP);
		this.playArea.setLayoutData(formData);
		
		this.btnResetGame.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				resetGame();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {/* UNUSED */}
		});
		
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
		
		resetGame();
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
	
	public void resetGame () {
		this.currentGameRemainingQuestions = null;
		this.currentGameQuestion = null;
		this.currentGameAnswersCorrect = null;
		this.currentGameAnswersIncorrect = null;
		
		this.lblQuestion.setText("Question?");
		this.lblCorrectAnswer.setVisible(false);
		this.txtAnswer.setText("Answer.");
		
		this.lblQuestion.setEnabled(false);
		this.txtAnswer.setEnabled(false);
		this.lblStatus.setEnabled(false);
		
		updateStatus();
		this.playArea.layout();
	}
	
	protected void updateStatus () {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		
		if (this.currentGameAnswersCorrect != null) {
			sb.append(this.currentGameAnswersCorrect.size());
			sb.append(" correct.");
			first = false;
		}
		
		if (this.currentGameAnswersIncorrect != null) {
			if (!first) sb.append("  ");
			sb.append(this.currentGameAnswersIncorrect.size());
			sb.append(" incorrect.");
			first = false;
		}
		
		if (this.currentGameRemainingQuestions != null) {
			if (!first) sb.append("  ");
			sb.append(this.currentGameRemainingQuestions.size());
			sb.append(" questions remaining.");
		}
		
		this.lblStatus.setText(sb.toString());
	}
	
	void addQuestionsToGame (Collection<QuestionGroup> questionGroups) {
		List<Question> questions = new LinkedList<Question>();
		for (QuestionGroup qg : questionGroups) {
			questions.addAll(qg.getQuestions());
		}
		if (questions.size() < 1) {
			System.err.println("No questions to add.");
			return;
		}
		
		// Shuffle into existing questions.
		if (this.currentGameRemainingQuestions != null) questions.addAll(this.currentGameRemainingQuestions);
		Collections.shuffle(questions);
		this.currentGameRemainingQuestions = new LinkedBlockingQueue<Question>();
		
		if (this.currentGameAnswersCorrect == null) this.currentGameAnswersCorrect = new LinkedList<Question>();
		if (this.currentGameAnswersIncorrect == null) this.currentGameAnswersIncorrect = new LinkedList<Question>();
		
		this.currentGameRemainingQuestions.addAll(questions);
		updateStatus();
		
		if (this.currentGameQuestion == null) presentNextQuestion();
	}
	
	protected void presentNextQuestion () {
		this.currentGameQuestion = this.currentGameRemainingQuestions.poll();
		
		if (this.currentGameQuestion != null) {
			this.lblQuestion.setText(this.currentGameQuestion.getQuestion());
			this.lblCorrectAnswer.setVisible(false);
			this.txtAnswer.setText("");
			
			updateStatus();
			
			this.lblQuestion.setEnabled(true);
			this.txtAnswer.setEnabled(true);
			this.lblStatus.setEnabled(true);
			
			this.playArea.layout();
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
					this.currentGameAnswersCorrect.add(this.currentGameQuestion);
					presentNextQuestion();
				}
				else {
					this.currentGameAnswersIncorrect.add(this.currentGameQuestion);
					
					StringBuilder sb = new StringBuilder();
					boolean first = true;
					for (String correctAnswer : this.currentGameQuestion.getAnswers()) {
						if (!first) sb.append("\n"); else first = false;;
						sb.append(correctAnswer);
					}
					this.lblCorrectAnswer.setText(sb.toString());
					this.lblCorrectAnswer.setVisible(true);
					this.txtAnswer.setEnabled(false);
					getSite().getWorkbenchWindow().getShell().getDisplay().timerExec(2000, new Runnable() {
						@Override
						public void run() {
							presentNextQuestion();
						}
					});
					this.playArea.layout();
				}
			}
		}
		else {
			System.err.println("No question asked."); // Should never happen.
		}
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}