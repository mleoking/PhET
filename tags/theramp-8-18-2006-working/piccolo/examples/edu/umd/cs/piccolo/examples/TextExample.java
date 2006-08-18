package edu.umd.cs.piccolo.examples;

import java.awt.GraphicsDevice;

import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.event.PStyledTextEventHandler;

/**
 * @author Lance Good
 */
public class TextExample extends PFrame {

	/**
	 * Constructor for TextExample.
	 */
	public TextExample() {
		super();
	}

	/**
	 * Constructor for TextExample.
	 * @param fullScreenMode
	 * @param aCanvas
	 */
	public TextExample(boolean fullScreenMode, PCanvas aCanvas) {
		super(fullScreenMode, aCanvas);
	}

	/**
	 * Constructor for TextExample.
	 * @param aDevice
	 * @param fullScreenMode
	 * @param aCanvas
	 */
	public TextExample(
		GraphicsDevice aDevice,
		boolean fullScreenMode,
		PCanvas aCanvas) {
		super(aDevice, fullScreenMode, aCanvas);
	}

	
	public void initialize() {
		getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
		
		PStyledTextEventHandler textHandler = new PStyledTextEventHandler(getCanvas());
		getCanvas().addInputEventListener(textHandler);
	}

	public static void main(String[] args) {
		new TextExample();
	}
}
