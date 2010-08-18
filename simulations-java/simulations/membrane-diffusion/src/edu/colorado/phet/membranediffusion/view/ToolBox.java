package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents the "tool box" for membrane channels that can be
 * grabbed by the user and placed on the membrane.
 * 
 * @author John Blanco
 */
public abstract class ToolBox extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Background color used to fill the box.
	private static final Color BACKGROUND_COLOR = Color.WHITE; 
	
	// Outline stroke.
	protected static final float OUTLINE_STROKE_WIDTH = 2f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Main outline of the box.
	protected PhetPPath boxNode;

	// The various grabbable things in the box.
	private ArrayList<ToolBoxItem> grabbableItems = new ArrayList<ToolBoxItem>();
	
	// The caption, which may or may not be set.
	private PText captionNode = null;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public ToolBox( PDimension size ) {
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getHeight(), 15, 15), BACKGROUND_COLOR,
				OUTLINE_STROKE, Color.BLACK);
		addChild(boxNode);
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	protected void addToolBoxItem(ToolBoxItem toolBoxItem){
	    grabbableItems.add( toolBoxItem );
	    addChild(toolBoxItem);
	    // Note: Layout is not automatically adjusted, subclasses should do
	    // so when needed.
	}
	
	protected void setCaption(String caption){
	    
	    // Remove old caption if there was one.
	    if (captionNode != null){
	        removeChild(captionNode);
	    }
	    
	    // Create the caption node and scale it so that it is not too large.
        this.captionNode = new PText( caption );
        captionNode.setFont( new PhetFont() );
        PDimension captionMaxSize = new PDimension( boxNode.getFullBoundsReference().width * 0.8,
                boxNode.getFullBoundsReference().height * 0.2 );
        double captionScalingFactor = Math.min( captionMaxSize.width / captionNode.getFullBoundsReference().width,
                captionMaxSize.height / captionNode.getFullBoundsReference().height );
        captionNode.setScale( captionScalingFactor );
        addChild( captionNode );
        // Note: Layout is not automatically adjusted, subclasses should do
        // so when needed.
	}
	
	protected void updateLayout(){
	    // Position the grabbable nodes.
	    if (grabbableItems.size() > 0){
	        double spacingX = boxNode.getFullBoundsReference().width / (grabbableItems.size() + 1);
	        double offsetY = boxNode.getFullBoundsReference().height * 0.3;
	        for (int i = 0; i < grabbableItems.size(); i++){
	            grabbableItems.get( i ).setOffset( spacingX * (i + 1), offsetY );
	        }
	    }
	    
	    // Position the caption.
	    if (captionNode != null){
	        captionNode.setOffset( 
	                boxNode.getFullBoundsReference().width / 2 - captionNode.getFullBoundsReference().width / 2,
	                boxNode.getFullBoundsReference().height * 0.7 );
	    }
	}
}
