package net.sparktank.glyph.helpers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;

/*
 * Based on a sample from
 * http://www.eclipse.org/articles/article.php?file=Article-Understanding-Layouts/index.html
 * but modified to ignore invisible widgets.
 */
public class ColumnLayout extends Layout {
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	public static final int MARGIN = 4;
	public static final int SPACING = 2;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	Point[] sizes;
	int maxWidth, totalHeight;
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	@Override
	protected Point computeSize (Composite composite, int wHint, int hHint, boolean flushCache) {
		Control children[] = composite.getChildren();
		
		if (flushCache || this.sizes == null || this.sizes.length != children.length) {
			initialize(children);
		}
		
		int width = wHint, height = hHint;
		if (wHint == SWT.DEFAULT) width = this.maxWidth;
		if (hHint == SWT.DEFAULT) height = this.totalHeight;
		
		return new Point(width + 2 * MARGIN, height + 2 * MARGIN);
	}
	
	@Override
	protected void layout (Composite composite, boolean flushCache) {
		Control children[] = composite.getChildren();
		
		if (flushCache || this.sizes == null || this.sizes.length != children.length) {
			initialize(children);
		}
		
		Rectangle rect = composite.getClientArea();
		int x = MARGIN, y = MARGIN;
		int width = Math.max(rect.width - 2 * MARGIN, this.maxWidth);
		
		for (int i = 0; i < children.length; i++) {
			if (children[i].getVisible()) {
    			int height = this.sizes[i].y;
    			children[i].setBounds(x, y, width, height);
    			y += height + SPACING;
			}
		}
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
	
	private void initialize (Control children[]) {
		this.maxWidth = 0;
		this.totalHeight = 0;
		this.sizes = new Point[children.length];
		
		int visibleCount = 0;
		for (int i = 0; i < children.length; i++) {
			if (children[i].getVisible()) {
    			this.sizes[i] = children[i].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    			this.maxWidth = Math.max(this.maxWidth, this.sizes[i].x);
    			this.totalHeight += this.sizes[i].y;
    			
    			visibleCount++;
			}
			else {
				this.sizes[i] = new Point(0, 0);
			}
		}
		
		this.totalHeight += (visibleCount - 1) * SPACING;
	}
	
//	- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
}
