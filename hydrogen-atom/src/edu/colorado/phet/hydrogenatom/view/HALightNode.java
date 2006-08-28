/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * HALightNode
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HALightNode extends PNode {
    
    private static double TOP_MARGIN = 10;
    private static double LEFT_MARGIN = 10;
    private static double Y_SPACING = 5;
    private static double PANEL_Y_OFFSET = 230;
    private static Dimension PANEL_SIZE = new Dimension( 300, 200 );
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public HALightNode( 
            PNode onOffControl,
            PNode sourceControl,
            PNode intensityControl ) 
    {
        super();
       
        // Title
        PText titleNode = new PText( SimStrings.get( "title.lightControls" ) );
        titleNode.setFont( HAConstants.TITLE_FONT );
        titleNode.setTextPaint( Color.BLACK );
        
        // Cable
        BufferedImage cableImage = null;
        try {
            cableImage = ImageLoader.loadBufferedImage( HAConstants.IMAGE_GUN_CONTROL_CABLE );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        PImage cableNode = new PImage( cableImage );
        
        // Panel background
        PPath panelNode = new PPath();
        Shape shape = new Rectangle2D.Double( 0, 0, PANEL_SIZE.getWidth(), PANEL_SIZE.getHeight() );
        panelNode.setPathTo( shape );
        panelNode.setStrokePaint( Color.BLACK );
        panelNode.setPaint( Color.GRAY );
        
        // Add nodes in background-to-foreground order
        addChild( cableNode );
        addChild( panelNode );
        addChild( titleNode );
        addChild( onOffControl );
        addChild( sourceControl );
        addChild( intensityControl );
        
        // Layout
        onOffControl.setOffset( 0, 0 );
        cableNode.setOffset( 13, 175 );
        panelNode.setOffset( 0, PANEL_Y_OFFSET );
        titleNode.setOffset( LEFT_MARGIN, panelNode.getFullBounds().getY() + TOP_MARGIN ); 
        sourceControl.setOffset( LEFT_MARGIN, titleNode.getFullBounds().getY() + titleNode.getFullBounds().getHeight() + Y_SPACING );
        intensityControl.setOffset( LEFT_MARGIN, sourceControl.getFullBounds().getY() + sourceControl.getFullBounds().getHeight() + Y_SPACING );
    }
}
