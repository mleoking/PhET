/* Copyright 2009, University of Colorado */


package edu.colorado.phet.genetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A class that represents a simple model element in the view.
 * 
 * @author John Blanco
 */
public class SimpleModelElementNode extends PPath {
	
	private static final boolean SHOW_CENTER_DOT = false;
	
	private final SimpleModelElement modelElement;
	
	private PhetPPath centerDot = new PhetPPath(Color.RED, new BasicStroke(2), Color.RED);
	
	public SimpleModelElementNode(SimpleModelElement modelElement, ModelViewTransform2D mvt){
	
		this.modelElement = modelElement;
		
		// Set up this node to look like it should.
		setPathTo(mvt.createTransformedShape(modelElement.getShape()));
		setPaint(modelElement.getPaint());
		
		// Register for important event notifications from the model.
		modelElement.addListener(new SimpleModelElement.Listener() {
			
			public void positionChanged() {
				setOffset(SimpleModelElementNode.this.modelElement.getPosition());
				
			}
		});
		
		// Put a center dot on the node (for debug purposes).
		if (SHOW_CENTER_DOT){
			centerDot.setPathTo(mvt.createTransformedShape(new Ellipse2D.Double(-1, -1, 2, 2)));
			addChild(centerDot);
		}
	}
}
