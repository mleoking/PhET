// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticalquantumcontrol.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.opticalquantumcontrol.OQCResources;
import edu.colorado.phet.opticalquantumcontrol.model.FourierSeries;


/**
 * CheatPanel is a panel that show the amplitudes of the output pulse
 * that we are trying to match.  If you are looking at this panel,
 * then you are cheating.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CheatPanel extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final DecimalFormat CHEAT_FORMAT = new DecimalFormat( "0.00" );
    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 14 );
    private static final Font VALUES_FONT = new PhetFont( Font.PLAIN, 12 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _outputFourierSeries;
    private ArrayList _valueGraphics;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor
     *
     * @param component
     * @param outputFourierSeries
     */
    public CheatPanel( Component component, FourierSeries outputFourierSeries ) {
        super( component );

        _outputFourierSeries = outputFourierSeries;
        _outputFourierSeries.addObserver( this );

        HTMLGraphic titleGraphic = new HTMLGraphic( component );
        titleGraphic.setIgnoreMouse( true );
        titleGraphic.setColor( Color.BLACK );
        titleGraphic.setFont( TITLE_FONT );
        titleGraphic.setHTML( OQCResources.CHEAT_DIALOG_LABEL );
        titleGraphic.setLocation( 35, 5 );

        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setIgnoreMouse( true );
        background.setColor( Color.LIGHT_GRAY );
        background.setBorderColor( Color.DARK_GRAY );
        background.setStroke( new BasicStroke( 1f ) );
        final int width = (int) Math.max( 275, titleGraphic.getLocation().getX() + titleGraphic.getWidth() + 10 );
        background.setShape( new Rectangle( 0, 0, width, 80 ) );

        addGraphic( background );
        addGraphic( titleGraphic );

        // Label and value for each harmonic
        _valueGraphics = new ArrayList();
        int x = 30;
        int numberOfHarmonics = _outputFourierSeries.getNumberOfHarmonics();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {

            double dAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
            String sAmplitude = CHEAT_FORMAT.format( dAmplitude );

            HTMLGraphic label = new HTMLGraphic( component );
            label.setIgnoreMouse( true );
            label.setFont( VALUES_FONT );
            label.setColor( Color.BLACK );
            label.setHTML( "<html>A<sub>" + ( i + 1 ) + "</sub></html>" );
            label.centerRegistrationPoint();
            label.setLocation( x, 40 );
            addGraphic( label );

            HTMLGraphic value = new HTMLGraphic( component );
            value.setIgnoreMouse( true );
            value.setFont( VALUES_FONT );
            value.setColor( Color.BLACK );
            value.setHTML( "-0.00" );
            value.centerRegistrationPoint();
            value.setLocation( x, 60 );
            addGraphic( value );

            x += 37;

            _valueGraphics.add( value );
        }

        // Close button
        PhetImageGraphic closeButton = new PhetImageGraphic( component, OQCResources.CLOSE_BUTTON_IMAGE );
        closeButton.setLocation( 5, 5 );
        addGraphic( closeButton );
        closeButton.addMouseInputListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                setVisible( false );
            }
        } );

        update();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Changes the visibility of this graphic.
     * When the graphic becomes visible, it is updated.
     *
     * @param visible true or false
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the graphic to match the model that it is observing.
     */
    public void update() {
        if ( isVisible() ) {
            int numberOfHarmonics = _outputFourierSeries.getNumberOfHarmonics();
            for ( int i = 0; i < numberOfHarmonics; i++ ) {
                double dAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
                String sAmplitude = CHEAT_FORMAT.format( dAmplitude );
                ( (HTMLGraphic) _valueGraphics.get( i ) ).setHTML( sAmplitude );
            }
        }
    }
}
