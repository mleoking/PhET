// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.help;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;


/**
 * ExplanationDialog is the dialog accessed via the Help>Explanation menu item.
 * It shows an annotated picture of a real experiment.
 * The annotation is fully localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ExplanationDialog extends PaintImmediateDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int X_MARGIN = 20;
    private static final int Y_MARGIN = 10;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BUBBLE_COLOR = new Color( 250, 250, 170 ); // opaque pale yellow
    private static final Color BUBBLE_ARROW_COLOR = BUBBLE_COLOR;
    private static final Color BUBBLE_TEXT_COLOR = Color.BLACK;
    private static final Color TEXT_COLOR = Color.BLACK;
    
    /* Space for the "Java Application Window" label that Web Start puts on the bottom of dialogs. */
    private static final int JAVA_APP_WINDOW_HEIGHT = 50;
        
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param owner
     */
    public ExplanationDialog( Frame owner ) {
        super( owner, false /* nonmodal */ );
        setTitle( OQCResources.EXPLANATION_TITLE );
        setResizable( false );
        
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        apparatusPanel.setBackground( BACKGROUND_COLOR );
        
        // Picture with annotated "bubbles"
        CompositePhetGraphic picture = new CompositePhetGraphic( apparatusPanel );
        {
            // The image
            PhetImageGraphic image = new PhetImageGraphic( apparatusPanel, OQCResources.EXPLANATION_IMAGE );
            image.setLocation( 0, 0 );
            picture.addGraphic( image );

            // Mask bubble
            HelpBubble maskBubble = new HelpBubble( apparatusPanel, OQCResources.EXPLANATION_MASK );
            maskBubble.setColors( BUBBLE_TEXT_COLOR, BUBBLE_COLOR, BUBBLE_ARROW_COLOR );
            maskBubble.pointAt( new Point( 100, 175 ), HelpBubble.LEFT_CENTER, 40 );
            picture.addGraphic( maskBubble );

            // Mirror bubble
            HelpBubble mirrorBubble = new HelpBubble( apparatusPanel, OQCResources.EXPLANATION_MIRROR );
            mirrorBubble.setColors( BUBBLE_TEXT_COLOR, BUBBLE_COLOR, BUBBLE_ARROW_COLOR );
            mirrorBubble.pointAt( new Point( 605, 205 ), HelpBubble.RIGHT_CENTER, 50 );
            picture.addGraphic( mirrorBubble );

            // Diffraction Grating bubble
            HelpBubble gratingBubble = new HelpBubble( apparatusPanel, OQCResources.EXPLANATION_GRATING );
            gratingBubble.setColors( BUBBLE_TEXT_COLOR, BUBBLE_COLOR, BUBBLE_ARROW_COLOR );
            gratingBubble.pointAt( new Point( 320, 420 ), HelpBubble.RIGHT_BOTTOM, 40 );
            picture.addGraphic( gratingBubble );
        }
        
        // Text explanation
        int fontSize = OQCResources.EXPLANATION_FONT_SIZE;
        HTMLGraphic text = new HTMLGraphic( apparatusPanel );
        text.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        text.setFont( new PhetFont( Font.PLAIN, fontSize ) );
        text.setColor( TEXT_COLOR );
        text.setHTML( OQCResources.EXPLANATION_TEXT );
        
        // Layout
        picture.setRegistrationPoint( picture.getWidth()/2, 0 ); // top center
        picture.setLocation( Math.max( picture.getWidth()/2, text.getWidth()/2 ) + X_MARGIN, Y_MARGIN ); // centered above text
        apparatusPanel.addGraphic( picture );
        text.setLocation( X_MARGIN, picture.getHeight() + ( 2 * Y_MARGIN ) ); // below the graphic
        apparatusPanel.addGraphic( text );
        
        // Add to the dialog
        getContentPane().add( apparatusPanel );
        int width = Math.max( picture.getWidth(), text.getWidth() ) + ( 2 * X_MARGIN );
        int height = picture.getHeight() + text.getHeight() + ( 4 * Y_MARGIN ) + JAVA_APP_WINDOW_HEIGHT;
        setSize( width, height );
        center();
    }
    
    /*
     * Centers the dialog on the screen.
     */
    private void center() {
        // Center the dialog on the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getSize().width;
        int h = getSize().height;
        int x = ( dim.width - w ) / 2;
        int y = ( dim.height - h ) / 2;
        setLocation( x, y );
    }
}
