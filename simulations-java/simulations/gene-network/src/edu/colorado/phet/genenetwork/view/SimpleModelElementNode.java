/* Copyright 2009, University of Colorado */


package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.Cap;
import edu.colorado.phet.genenetwork.model.CapBindingRegion;
import edu.colorado.phet.genenetwork.model.LacI;
import edu.colorado.phet.genenetwork.model.LacOperator;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * A class that represents a simple model element in the view.
 * 
 * @author John Blanco
 */
public class SimpleModelElementNode extends PPath {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final boolean SHOW_CENTER_DOT = false;
	private static final boolean SHOW_ATTACHMENT_POINTS = false;
	private static final Font LABEL_FONT = new PhetFont(16, true );
	private static final Stroke NORMAL_STROKE = new BasicStroke(1);
	private static final Stroke GHOST_MODE_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			BasicStroke.JOIN_BEVEL, 0, new float[] {4, 2}, 0);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	private final SimpleModelElement modelElement;
	private final ModelViewTransform2D mvt;
	
	private PhetPPath centerDot = new PhetPPath(Color.RED, new BasicStroke(2), Color.RED);
	
	private boolean ghostModeEnabled = false;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public SimpleModelElementNode(final SimpleModelElement modelElement, final ModelViewTransform2D mvt, 
			boolean showLabel){
	
		this.modelElement = modelElement;
		this.mvt = mvt;
		setStroke(NORMAL_STROKE);
		
		// Register for important event notifications from the model.
		modelElement.addListener(new ModelElementListenerAdapter() {
			
			public void positionChanged() {
				updateOffset();
			}
			public void shapeChanged() {
				updateShape();
			}
			
			public void existenceStrengthChanged() {
				updatePaintAndStroke(false);
			}
		});
		
		// Set the initial appearance.
		updateShape();
		updatePaintAndStroke(false);
		
		// If there is a label text value, and showing it is enabled, show it.
		if (showLabel == true && modelElement.getLabel() != null){
			PText labelNode = new PText(modelElement.getLabel());
			labelNode.setFont(LABEL_FONT);
			labelNode.setOffset(getFullBoundsReference().getCenterX() - labelNode.getFullBoundsReference().width / 2, 
					getFullBoundsReference().getCenterY() - labelNode.getFullBoundsReference().height / 2);
			addChild(labelNode);
		}
		
		// Put a center dot on the node (for debug purposes).
		if (SHOW_CENTER_DOT){
			centerDot.setPathTo(new Ellipse2D.Double(-2, -2, 4, 4));
			addChild(centerDot);
		}
		
		// Show the attachment points (for debug purposes).
		if (SHOW_ATTACHMENT_POINTS){
			// NOTE: This is probably not complete in that it won't show the
			// binding points for all elements, basically because I'm adding
			// them on an as-needed basis.
			Dimension2D unscaledOffset = null;
			if (modelElement instanceof LacI){
				unscaledOffset = LacI.getLacOperatorAttachementPointOffset();
			}
			else if (modelElement instanceof LacOperator){
				unscaledOffset = LacOperator.getLacIAttachementPointOffset();
			}
			else if (modelElement instanceof Cap){
				unscaledOffset = Cap.getCapBindingRegionAttachmentOffset();
			}
			else if (modelElement instanceof CapBindingRegion){
				unscaledOffset = CapBindingRegion.getCapAttachmentPointOffset();
			}
			
			if (unscaledOffset != null){
				Dimension2D scaledOffset = new PDimension(mvt.getAffineTransform().getScaleX() * unscaledOffset.getWidth(),
						mvt.getAffineTransform().getScaleY() * unscaledOffset.getHeight());
				PhetPPath attachementPointDot = new PhetPPath(Color.MAGENTA);
				attachementPointDot.setPathTo(new Ellipse2D.Double(-2, -2, 4, 4));
				attachementPointDot.setOffset(scaledOffset.getWidth(), scaledOffset.getHeight());
				addChild(attachementPointDot);
			}
		}
		
		// Set initial offset.
		updateOffset();

        addInputEventListener(new CursorHandler());
        addInputEventListener(new PBasicInputEventHandler(){
            public void mouseDragged(PInputEvent event) {
                Point2D out =mvt.viewToModelDifferential(new Point2D.Double(event.getDeltaRelativeTo(getParent()).width,event.getDeltaRelativeTo(getParent()).height));
                modelElement.setPosition(modelElement.getPositionRef().getX()+out.getX(),modelElement.getPositionRef().getY()+out.getY());
                modelElement.setDragging(true);
            }

            @Override
            public void mouseReleased(PInputEvent event) {
                modelElement.setDragging(false);
            }
        });
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/**
	 * This method forces the node to become visible regardless of the state
	 * of the model element, i.e. whether or not the model element is
	 * indicating that it should be transparent.  Note that this does NOT
	 * forestall subsequent updates to the visibility state from the model.
	 */
	public void forceVisible(){
		updatePaintAndStroke(true);
	}
	
	/**
	 * Set "ghost mode", which is a mode in which the node is presented as a
	 * non-filled shape with a dotted outline.  This is generally intended to
	 * convey the idea that the corresponding shape can or should go at a
	 * given location, but is not currently AT that location.
	 */
	public void setGhostMode(boolean ghostModeEnabled){
		this.ghostModeEnabled = ghostModeEnabled;
		updatePaintAndStroke(false);
	}
	
    private void updateOffset() {
        setOffset( mvt.modelToView( modelElement.getPositionRef() ));
    }
    
    private void updateShape() {

    	// We only want the shape, and not any translation associated with the
    	// shape, so we create our own transform that only does the scaling
    	// that is indicated in the model-view transform.
    	
    	// Create transform that only scales, and does no translation.
    	AffineTransform scalingOnlyTransform = AffineTransform.getScaleInstance(mvt.getAffineTransform().getScaleX(),
    			mvt.getAffineTransform().getScaleY());
    	
    	// Create the transformed shape.
		Shape transformedShape = scalingOnlyTransform.createTransformedShape(modelElement.getShape());
		
		// Set the node to this shape.
		setPathTo(transformedShape);
    }
    
    private void updatePaintAndStroke(boolean forceVisible){
    	int alpha = 255;
    	if (!forceVisible){
    		alpha = (int)Math.round(modelElement.getExistenceStrength() * 255);
    	}
    	setStrokePaint(new Color(0, 0, 0, alpha));
    	if (!ghostModeEnabled){
    		setStroke(NORMAL_STROKE);
    		Paint currentPaint = modelElement.getPaint();
    		Paint newPaint = currentPaint;
    		if (currentPaint instanceof GradientPaint){
    			GradientPaint gp = (GradientPaint)currentPaint;
    			alpha = (int)Math.round(modelElement.getExistenceStrength() * 255);
    			Color color1 = new Color(gp.getColor1().getRed(), gp.getColor1().getGreen(),
    					gp.getColor1().getBlue(), alpha);
    			Color color2 = new Color(gp.getColor2().getRed(), gp.getColor2().getGreen(),
    					gp.getColor2().getBlue(), alpha);
    			
    			newPaint = new GradientPaint(gp.getPoint1(), color1, gp.getPoint2(), color2);
    		}
    		else if (currentPaint instanceof Color){
    			Color oldColor = (Color)currentPaint;
    			alpha = (int)Math.round(modelElement.getExistenceStrength() * 255);
    			newPaint = new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), alpha);
    		}
    		setPaint(newPaint);
    	}
    	else{
    		setStroke(GHOST_MODE_STROKE);
    		setPaint(null);
    	}
    }
}
