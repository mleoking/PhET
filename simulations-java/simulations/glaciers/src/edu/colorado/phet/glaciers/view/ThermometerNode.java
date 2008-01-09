/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerListener;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ThermometerNode is the visual representation of a thermometer.
 * It's origin is at the bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThermometerNode extends AbstractToolNode {
    
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final DecimalFormat TEMPERATURE_FORMAT = new DecimalFormat( "0.0" );
    
    private Thermometer _thermometer;
    private ThermometerListener _thermometerListener;
    private JLabel _temperatureLabel;
    
    public ThermometerNode( Thermometer thermometer, ModelViewTransform mvt ) {
        super( thermometer, mvt );
        
        _thermometer = thermometer;
        _thermometerListener = new ThermometerListener() {
            public void temperatureChanged() {
                updateTemperature();
            }
        };
        _thermometer.addThermometerListener( _thermometerListener );
        
        PImage imageNode = new PImage( GlaciersImages.THERMOMETER );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() );
        
        _temperatureLabel = new JLabel();
        _temperatureLabel.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBorder( BORDER );
        panel.add( _temperatureLabel );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( 0, -imageNode.getFullBoundsReference().getHeight() / 2 );
        
        // initial state
        updatePosition();
        updateTemperature();
    }
    
    public void cleanup() {
        _thermometer.removeThermometerListener( _thermometerListener );
        super.cleanup();
    }
    
    private void updateTemperature() {
        double value = _thermometer.getTemperature();
        String text = TEMPERATURE_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_TEMPERATURE;
        _temperatureLabel.setText( text );
    }
}
