/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D.Double;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents creates a graphical display that is meant to look
 * like a bicycle pump.  It allows the user to interact with it to move the
 * handle up and down.
 *
 * @author John Blanco
 */
public class BicyclePumpNode extends PNode {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // The follow constants define the size and positions of the various
    // components of the pump as proportions of the overall width and height
    // of the node.
    private static final double PUMP_HORIZ_POSITION_PROPORTION = 0.75;
    private static final double PUMP_BASE_WIDTH_PROPORTION = 0.3;
    private static final double PUMP_BASE_HEIGHT_PROPORTION = 0.02;
    private static final Color PUMP_BASE_COLOR = new Color (0xCC9966);
    private static final Color PUMP_BODY_COLOR = Color.RED;
    private static final double PUMP_BODY_HEIGHT_PROPORTION = 0.75;
    private static final double PUMP_BODY_WIDTH_PROPORTION = 0.075;
    private static final double PUMP_SHAFT_WIDTH_PROPORTION = PUMP_BODY_WIDTH_PROPORTION * 0.25;
    private static final double PUMP_SHAFT_HEIGHT_PROPORTION = PUMP_BODY_HEIGHT_PROPORTION;
    private static final Color PUMP_SHAFT_COLOR = Color.LIGHT_GRAY;
    private static final double PUMP_HANDLE_WIDTH_PROPORTION = 0.25;
    private static final double PUMP_HANDLE_HEIGHT_PROPORTION = 0.02;
    private static final double PUMP_HANDLE_INIT_VERT_POS_PROPORTION = PUMP_BODY_HEIGHT_PROPORTION * 1.1;
    private static final Color PUMP_HANDLE_COLOR = new Color (0xCC9966);
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    public BicyclePumpNode(double width, double height){
        
        // JPB TBD - Create a box to show where the overall pump will go.
        PPath outline = new PPath(new Rectangle2D.Double(0, 0, width, height));
        outline.setStrokePaint( Color.WHITE );
        outline.setStroke( new BasicStroke(25) );
        addChild(outline);
        
        // Add the base of the pump.
        double pumpBaseWidth = width * PUMP_BASE_WIDTH_PROPORTION;
        double pumpBaseHeight = height * PUMP_BASE_HEIGHT_PROPORTION;
        PPath pumpBase = new PPath(
                new Rectangle2D.Double( 0, 0, pumpBaseWidth, pumpBaseHeight ));
        pumpBase.setPaint( PUMP_BASE_COLOR );
        pumpBase.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - (pumpBaseWidth / 2), height - pumpBaseHeight );
        addChild( pumpBase );
        
        // Add the handle of the pump.
        double pumpHandleWidth = width * PUMP_HANDLE_WIDTH_PROPORTION;
        double pumpHandleHeight = height * PUMP_HANDLE_HEIGHT_PROPORTION;
        PPath pumpHandle = new PPath( new Rectangle2D.Double( 0, 0, pumpHandleWidth, pumpHandleHeight ));
        pumpHandle.setPaint( PUMP_HANDLE_COLOR );
        pumpHandle.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - (pumpHandleWidth / 2), 
                height - (height * PUMP_HANDLE_INIT_VERT_POS_PROPORTION) - pumpHandleHeight );
        addChild( pumpHandle );
        
        // Add the shaft for the pump.
        double pumpShaftWidth = width * PUMP_SHAFT_WIDTH_PROPORTION;
        double pumpShaftHeight = height * PUMP_SHAFT_HEIGHT_PROPORTION;
        PPath pumpShaft = new PPath( new Rectangle2D.Double( 0, 0, pumpShaftWidth, pumpShaftHeight ));
        pumpShaft.setPaint( PUMP_SHAFT_COLOR );
        pumpShaft.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - (pumpShaftWidth / 2), 
                height - (height * PUMP_HANDLE_INIT_VERT_POS_PROPORTION) );
        addChild( pumpShaft );
        
        
        // Add the body of the pump
        double pumpBodyWidth = width * PUMP_BODY_WIDTH_PROPORTION;
        double pumpBodyHeight = height * PUMP_BODY_HEIGHT_PROPORTION;
        PPath pumpBody = new PPath( new Rectangle2D.Double( 0, 0, pumpBodyWidth, pumpBodyHeight ));
        pumpBody.setPaint( PUMP_BODY_COLOR );
        pumpBody.setOffset( width * PUMP_HORIZ_POSITION_PROPORTION - (pumpBodyWidth / 2), 
                height - pumpBodyHeight - pumpBaseHeight );
        addChild( pumpBody );
        
        
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------

    
    

}
