/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.IMagnet;


/**
 * FieldProbeGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FieldProbeGraphic extends CompositePhetGraphic implements SimpleObserver {

    private IMagnet _magnetModel;
    private PhetTextGraphic _bText, _bxText, _byText, _angleText;
    NumberFormat _formatter;
    
    public FieldProbeGraphic( Component component, IMagnet magnetModel ) {
        super( component );
        _magnetModel = magnetModel;
        _formatter = new DecimalFormat( "###0.00" );
        
        // Probe body with registration point at top center.
        PhetImageGraphic body = new PhetImageGraphic( component, FaradayConfig.FIELD_PROBE_IMAGE );
        int rx = body.getImage().getWidth() / 2;
        int ry = 0;
        body.setRegistrationPoint( rx, ry );
        addGraphic( body );
        
        // Common text attributes.
        Font font = new Font( "SansSerif", Font.BOLD, 12 );
        Color color = Color.WHITE;
        int xText = -20;
        int yText = 42;
        int ySpacing = 19;
        
        // B text
        _bText = new PhetTextGraphic( component, font, "1234567890-E1", color, xText, yText );
        addGraphic( _bText );
        
        // Bx text
        _bxText = new PhetTextGraphic( component, font, "1234567890-E1", color, xText, yText + ySpacing );
        addGraphic( _bxText );
        
        // By text
        _byText = new PhetTextGraphic( component, font, "1234567890-E1", color, xText, yText + (2*ySpacing) );
        addGraphic( _byText );
        
        // Angle text
        _angleText = new PhetTextGraphic( component, font, "1234567890-E1", color, xText, yText + (3*ySpacing) );
        addGraphic( _angleText );
        
        // Setup interactivity.
        super.setCursorHand();
        super.addTranslationListener( new TranslationListener() {
            public void translationOccurred( TranslationEvent e ) {
                int x = getX() + e.getDx();
                int y = getY() + e.getDy();
                setLocation( x, y );
                update();
            }
        } );
        
        // Synchronize view with model.
        update();
    }
    
    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        if ( isVisible() ) {
            AbstractVector2D B = _magnetModel.getStrength( getLocation() );
            
            String b = _formatter.format( new Double( B.getMagnitude() ) );
            String bx = _formatter.format( new Double( B.getX() ) );
            String by = _formatter.format( new Double( B.getY() ) );
            String angle = _formatter.format( new Double( Math.toDegrees( B.getAngle() ) ) );
            
            _bText.setText( b );
            _bxText.setText( bx );
            _byText.setText( by );
            _angleText.setText( angle );
        }
    }

}
