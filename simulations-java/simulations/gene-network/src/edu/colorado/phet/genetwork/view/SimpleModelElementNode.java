/* Copyright 2009, University of Colorado */


package edu.colorado.phet.genetwork.view;

import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A class that represents a simple model element in the view.
 * 
 * @author John Blanco
 */
public class SimpleModelElementNode extends PPath {
	
	private final SimpleModelElement modelElement;
	
	public SimpleModelElementNode(SimpleModelElement modelElement){
	
		this.modelElement = modelElement;
		
		// Set up this node to look like it should.
		setPathTo(modelElement.getShape());
		setPaint(modelElement.getPaint());
		
		// Register for important event notifications from the model.
		modelElement.addListener(new SimpleModelElement.Listener() {
			
			public void positionChanged() {
				setOffset(SimpleModelElementNode.this.modelElement.getPosition());
				
			}
		});
	}
}
