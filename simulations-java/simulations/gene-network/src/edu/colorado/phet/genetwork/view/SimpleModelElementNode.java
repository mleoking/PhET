/* Copyright 2009, University of Colorado */


package edu.colorado.phet.genetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A class that represents a simple model element in the view.
 * 
 * @author John Blanco
 */
public class SimpleModelElementNode extends PPath {
	
	private static final boolean SHOW_CENTER_DOT = false;
	private static final Font LABEL_FONT = new PhetFont(16, true );
	
	private final SimpleModelElement modelElement;
	private final ModelViewTransform2D mvt;
	
	private PhetPPath centerDot = new PhetPPath(Color.RED, new BasicStroke(2), Color.RED);
	
	public SimpleModelElementNode(SimpleModelElement modelElement, ModelViewTransform2D mvt){
	
		this.modelElement = modelElement;
		this.mvt = mvt;
		
		// Set up this node to look like it should.
		Shape transformedShape = mvt.createTransformedShape(modelElement.getShape());
		
		// We only want the shape, and not any translation associated with the
		// shape, so that we can move it in a reasonable way later.  For this,
		// we have to subtract off the translation.
		transformedShape.getBounds2D().getCenterX();
		transformedShape = AffineTransform.getTranslateInstance(-transformedShape.getBounds2D().getCenterX(), 
				-transformedShape.getBounds2D().getCenterY()).createTransformedShape(transformedShape);
		
		// Set the shape and color.
		setPathTo(transformedShape);
		setPaint(modelElement.getPaint());
		
		// Register for important event notifications from the model.
		modelElement.addListener(new SimpleModelElement.Listener() {
			public void positionChanged() {
				updateOffset();
			}
		});
		
		// If there is a label text value, show it.
		if (modelElement.getLabel() != null){
			PText labelNode = new PText(modelElement.getLabel());
			labelNode.setFont(LABEL_FONT);
			labelNode.setOffset(getFullBoundsReference().getCenterX() - labelNode.getFullBoundsReference().width / 2, 
					getFullBoundsReference().getCenterY() - labelNode.getFullBoundsReference().height / 2);
			addChild(labelNode);
		}
		
		// Put a center dot on the node (for debug purposes).
		if (SHOW_CENTER_DOT){
			centerDot.setPathTo(mvt.createTransformedShape(new Ellipse2D.Double(-1, -1, 2, 2)));
			addChild(centerDot);
		}
		
		// Set initial offset.
		updateOffset();
	}
	
    private void updateOffset() {
        setOffset( mvt.modelToView( modelElement.getPositionRef() ));
    }
}
