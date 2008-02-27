/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerListener;
import edu.colorado.phet.glaciers.view.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * ThermometerNode is the visual representation of a thermometer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThermometerNode extends AbstractToolNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat TEMPERATURE_FORMAT = new DefaultDecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Thermometer _thermometer;
    private ThermometerListener _thermometerListener;
    private JLabel _temperatureDisplay;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ThermometerNode( Thermometer thermometer, ModelViewTransform mvt ) {
        super( thermometer, mvt );
        
        _thermometer = thermometer;
        _thermometerListener = new ThermometerListener() {
            public void temperatureChanged() {
                updateTemperature();
            }
        };
        _thermometer.addThermometerListener( _thermometerListener );
        
        PNode arrowNode = new LeftToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        PImage imageNode = new PImage( GlaciersImages.THERMOMETER );
        addChild( imageNode );
        imageNode.setOffset( arrowNode.getFullBoundsReference().getMaxX(), -imageNode.getFullBoundsReference().getHeight() / 2 );
        
        _temperatureDisplay = new JLabel( "0" );
        _temperatureDisplay.setFont( FONT );
        JPanel panel = new JPanel();
        panel.setBorder( BORDER );
        panel.add( _temperatureDisplay );
        PSwing panelNode = new PSwing( panel );
        addChild( panelNode );
        panelNode.setOffset( imageNode.getFullBounds().getMaxX(), -panelNode.getFullBounds().getHeight() / 2 );
        
        // initial state
        updatePosition();
        updateTemperature();
    }
    
    public void cleanup() {
        _thermometer.removeThermometerListener( _thermometerListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the temperature display to match the model.
     */
    private void updateTemperature() {
        double value = _thermometer.getTemperature();
        String text = TEMPERATURE_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_TEMPERATURE;
        _temperatureDisplay.setText( text );
    }
}
