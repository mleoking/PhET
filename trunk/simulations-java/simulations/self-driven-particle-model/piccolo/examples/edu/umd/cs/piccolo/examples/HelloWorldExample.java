package edu.umd.cs.piccolo.examples;

import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;

public class HelloWorldExample extends PFrame {
	
	public void initialize() {	
		PText text = new PText("Hello World");
		getCanvas().getLayer().addChild(text);
	}

	public static void main(String[] args) {
		new HelloWorldExample();
	}
}