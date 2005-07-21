/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.*;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * GraphClosed
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GraphClosed extends GraphicLayerSet {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CONTROLS_LAYER = 3;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 50 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton _openButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GraphClosed( Component component, String title ) {
        super( component );
        
        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( 0, 0 );
      
        // Title
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        addGraphic( titleGraphic, TITLE_LAYER ); 
        titleGraphic.setRegistrationPoint( 0, titleGraphic.getHeight() / 2 ); // left center
        titleGraphic.setLocation( 50, 50 ); // right of button
        
        // Open button
        ImageIcon openIcon = null;
        try {
            openIcon = new ImageIcon( ImageLoader.loadBufferedImage( FourierConstants.OPEN_BUTTON_IMAGE ) );
        }
        catch ( IOException ioe ) {
            throw new RuntimeException( "missing image resource: " + FourierConstants.OPEN_BUTTON_IMAGE );
        }
        _openButton = new JButton( openIcon );
        _openButton.setBackground( new Color( 0, 0, 0, 0 ) ); // transparent
        PhetGraphic openButtonGraphic = PhetJComponent.newInstance( component, _openButton );
        addGraphic( openButtonGraphic, CONTROLS_LAYER );
        openButtonGraphic.setLocation( 10, 10 );
        
        // Interactivity
        openButtonGraphic.setCursorHand();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public JButton getOpenButton() {
        return _openButton;
    }
}