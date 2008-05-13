/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerListener;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;
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
    
    private static final Font FONT = new PhetFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat TEMPERATURE_FORMAT = new DefaultDecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Thermometer _thermometer;
    private Glacier _glacier;
    private ThermometerListener _thermometerListener;
    private MovableListener _movableListener;
    private GlacierListener _glacierListener;
    private ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ThermometerNode( Thermometer thermometer, Glacier glacier, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super( thermometer, mvt, trashCanIconNode );
        
        _thermometer = thermometer;
        _glacier = glacier;
        
        _thermometerListener = new ThermometerListener() {
            public void temperatureChanged() {
                updateTemperature();
            }
        };
        _thermometer.addThermometerListener( _thermometerListener );
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updateTemperature();
            }
        };
        _thermometer.addMovableListener( _movableListener );
        
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                updateTemperature();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        PNode arrowNode = new LeftToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        PNode glassNode = new GlassNode();
        addChild( glassNode );
        glassNode.setOffset( arrowNode.getFullBoundsReference().getMaxX(), -glassNode.getFullBoundsReference().getHeight() );
        
        _valueNode = new ValueNode();
        addChild( _valueNode );
        _valueNode.setOffset( glassNode.getFullBoundsReference().getMaxX(), -_valueNode.getFullBoundsReference().getHeight() / 2 );
        
        // initial state
        _valueNode.setTemperatureUnknown();
    }
    
    public void cleanup() {
        _thermometer.removeThermometerListener( _thermometerListener );
        _thermometer.removeMovableListener( _movableListener );
        _glacier.removeGlacierListener( _glacierListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The thermometer's glass bottle.
     */
    private static class GlassNode extends PComposite {
        public GlassNode() {
            super();
            PImage imageNode = new PImage( GlaciersImages.THERMOMETER );
            addChild( imageNode );
        }
    }
    
    /*
     * Displays the temperature value.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _celsiusLabel;
        private JLabel _fahrenheitLabel;
        private PSwing _pswing;
        
        public ValueNode() {
            super();
            
            _celsiusLabel = new JLabel( "0" );
            _celsiusLabel.setFont( FONT );
            
            _fahrenheitLabel = new JLabel( "0" );
            _fahrenheitLabel.setFont( FONT );
            
            JPanel panel = new JPanel();
            panel.setBackground( Color.WHITE );
            panel.setBorder( BORDER );
            EasyGridBagLayout layout = new EasyGridBagLayout( panel );
            layout.setAnchor( GridBagConstraints.EAST );
            panel.setLayout( layout );
            layout.addComponent( _celsiusLabel, 0, 0 );
            layout.addComponent( _fahrenheitLabel, 1, 0 );
            
            _pswing = new PSwing( panel );
            addChild( _pswing );
        }
        
        public void setTemperature( double celsius ) {
            // Celsius
            String textC = TEMPERATURE_FORMAT.format( celsius ) + " " + GlaciersStrings.UNITS_CELSIUS;
            _celsiusLabel.setText( textC );
            // Fahrenheit
            double valueF = Thermometer.celsiusToFahrenheit( celsius );
            String textF = TEMPERATURE_FORMAT.format( valueF ) + " " + GlaciersStrings.UNITS_FAHRENHEIT;
            _fahrenheitLabel.setText( textF );
            
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
        
        public void setTemperatureUnknown() {
            _celsiusLabel.setText( "? " + GlaciersStrings.UNITS_CELSIUS );
            _fahrenheitLabel.setText( "? " + GlaciersStrings.UNITS_FAHRENHEIT );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the temperature display to match the model.
     */
    private void updateTemperature() {
        double glacierSurfaceY = _glacier.getSurfaceElevation( _thermometer.getX() );
        if ( _thermometer.getY() > glacierSurfaceY ) {
            _valueNode.setTemperature( _thermometer.getTemperature() ); 
        }
        else {
            _valueNode.setTemperatureUnknown();
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        return GlaciersImages.THERMOMETER;
    }
}
