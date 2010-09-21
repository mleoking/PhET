/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * ELAValueNode displays the ELA value for debugging purposes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ELAValueNode extends PText {

    private static final Font FONT = new PhetFont( 14 );
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final NumberFormat NUMBER_FORMAT = new DefaultDecimalFormat( "0" );
    
    private final Climate _climate;
    private final ClimateListener _climateListener;
    
    public ELAValueNode( Climate climate ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        setFont( FONT );
        setTextPaint( TEXT_COLOR );
        
        _climate = climate;
        _climateListener = new ClimateListener() {

            public void snowfallChanged() {
                update();
            }

            public void temperatureChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        update();
    }
    
    public void cleanup() {
        _climate.removeClimateListener( _climateListener );
    }
    
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            update();
        }
    }
    
    private void update() {
        if ( getVisible() ) {
            final double ela = _climate.getELA();
            String s = NUMBER_FORMAT.format( ela );
            setText( "ELA = " + s + " " + GlaciersStrings.UNITS_METERS );
        }
    }
}
