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

package net.sparktank.glyph.helpers;

import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;

public class SwtHelper {
	
	static public void increaseFontSize (Control control, float sizeMultiplier) {
		FontData[] fontData = control.getFont().getFontData();
		for(int i = 0; i < fontData.length; ++i) {
			int size = (int) (fontData[i].getHeight() * sizeMultiplier);
			fontData[i].setHeight(size);
		}
		
		final Font bigLabelFont = new Font(control.getDisplay(), fontData);
		control.setFont(bigLabelFont);
		
		control.addDisposeListener(new DisposeListener() {
		    @Override
			public void widgetDisposed(DisposeEvent e) {
		        bigLabelFont.dispose();
		    }
		});
	}
	
}
