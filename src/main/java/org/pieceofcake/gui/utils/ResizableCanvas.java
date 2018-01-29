package org.pieceofcake.gui.utils;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
	
	@Override
	public boolean isResizable() {
		return true;
	}
	
	@Override
	public double minWidth(double height) {
		return 20;
	}
	
	@Override
	public double maxWidth(double height) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public double prefWidth(double height) {
		return 900;
	}
	
	@Override
	public double minHeight(double width) {
		return 20;
	}
	
	@Override
	public double maxHeight(double width) {
		return Double.MAX_VALUE;
	}
	
	@Override
	public double prefHeight(double width) {
		return 900;
	}	

}
