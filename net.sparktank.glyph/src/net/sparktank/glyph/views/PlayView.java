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

import net.sparktank.glyph.helpers.SwtHelper;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class PlayView extends ViewPart {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static final String ID = "net.sparktank.glyph.views.PlayView";
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	ViewPart methods.
	
	@Override
	public void createPartControl(Composite parent) {
		createLayout(parent);
	}
	
	@Override
	public void setFocus() {
		this.txtAnswer.setFocus();
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
//	GUI stuff.
	
	protected static final int SEP = 3;
	
	private Label lblQuestion;
	private Text txtAnswer;
	
	private void createLayout (Composite parent) {
		FormData formData;
		
		parent.setLayout(new FormLayout());
		
		this.lblQuestion = new Label(parent, SWT.CENTER);
		this.txtAnswer = new Text(parent, SWT.CENTER);
		
		SwtHelper.increaseFontSize(this.lblQuestion, 2);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.right = new FormAttachment(100, -SEP);
		formData.top = new FormAttachment(0, SEP);
		formData.bottom = new FormAttachment(this.txtAnswer, -SEP);
		this.lblQuestion.setLayoutData(formData);
		
		formData = new FormData();
		formData.left = new FormAttachment(0, SEP);
		formData.right = new FormAttachment(100, -SEP);
		formData.bottom = new FormAttachment(100, -SEP);
		this.txtAnswer.setLayoutData(formData);
		
		
		// Test data.
		this.lblQuestion.setText("Question?");
		this.txtAnswer.setText("Answer!");
		
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}