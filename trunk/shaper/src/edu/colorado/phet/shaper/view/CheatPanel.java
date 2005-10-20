/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.ShaperConstants;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * CheatPanel
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CheatPanel extends GraphicLayerSet implements SimpleObserver {
    
    private static final DecimalFormat CHEAT_FORMAT = new DecimalFormat( "0.00" );
    private static final Font TITLE_FONT = new Font( ShaperConstants.FONT_NAME, Font.BOLD, 14 );
    private static final Font VALUES_FONT = new Font( ShaperConstants.FONT_NAME, Font.PLAIN, 12 );
    
    private FourierSeries _outputFourierSeries;
    private ArrayList _valueGraphics;
    
    public CheatPanel( Component component, FourierSeries outputFourierSeries ) {
        super( component );
        
        _outputFourierSeries = outputFourierSeries;
        _outputFourierSeries.addObserver( this );
        
        PhetShapeGraphic background = new PhetShapeGraphic( component );
        background.setIgnoreMouse( true );
        background.setColor( Color.LIGHT_GRAY );
        background.setBorderColor( Color.DARK_GRAY );
        background.setStroke( new BasicStroke( 1f ) );
        background.setShape( new Rectangle( 0, 0, 275, 80 ) );
        addGraphic( background );
        
        HTMLGraphic titleGraphic = new HTMLGraphic( component );
        titleGraphic.setIgnoreMouse( true );
        titleGraphic.setColor( Color.BLACK );
        titleGraphic.setFont( TITLE_FONT );
        titleGraphic.setHTML( SimStrings.get( "CheatDialog.label" ) );
        titleGraphic.setLocation( 35, 5 );
        addGraphic( titleGraphic );
        
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
        PhetImageGraphic closeButton = new PhetImageGraphic( component, ShaperConstants.CLOSE_BUTTON_IMAGE );
        closeButton.setLocation( 5, 5 );
        addGraphic( closeButton );
        closeButton.addMouseInputListener( new MouseInputAdapter() {
            public void mouseReleased( MouseEvent e ) {
                setVisible( false );
            }
        } );
        
        // Interactivity
        background.setIgnoreMouse( true );
        
        update();
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }
    
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
