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
