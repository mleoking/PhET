// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * MinimizedView is the minimized version of a "view".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MinimizedView extends GraphicLayerSet {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CONTROLS_LAYER = 3;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 30 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new PhetFont( Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetImageGraphic _maximizeButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MinimizedView( Component component, String title ) {
        super( component );
        
        // Enable antialiasing for all children.
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

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
        titleGraphic.setLocation( 45, 37 ); // right of button
        
        // Maximize button
        _maximizeButton = new PhetImageGraphic( component, FourierConstants.MAXIMIZE_BUTTON_IMAGE );
        addGraphic( _maximizeButton, CONTROLS_LAYER );
        _maximizeButton.centerRegistrationPoint();
        _maximizeButton.setLocation( (_maximizeButton.getWidth()/2) + 10, _maximizeButton.getHeight()/2 + 5 );
        
        // Interactivity
        _maximizeButton.setCursorHand();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public PhetImageGraphic getMaximizeButton() {
        return _maximizeButton;
    }
}