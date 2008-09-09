/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.text.NumberFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Thermometer;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.model.Thermometer.ThermometerListener;
import edu.colorado.phet.glaciers.util.UnitsConverter;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
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
    
    private static final double MAX_TEMPERATURE = 10; // C
    private static final double MIN_TEMPERATURE = -20; // C
    private static final PDimension THERMOMETER_SIZE = new PDimension( 15, 60 );
    private static final NumberFormat TEMPERATURE_FORMAT = new DefaultDecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final Thermometer _thermometer;
    private final Glacier _glacier;
    private final ThermometerListener _thermometerListener;
    private final MovableListener _movableListener;
    private final GlacierListener _glacierListener;
    private final GlassNode _glassNode;
    private final ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ThermometerNode( Thermometer thermometer, Glacier glacier, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super( thermometer, mvt, trashCan );
        
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
        
        _glassNode = new GlassNode( THERMOMETER_SIZE );
        addChild( _glassNode );
        
        _valueNode = new ValueNode( getValueFont(), getValueBorder() );
        addChild( _valueNode );
        
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        _glassNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 2, -_glassNode.getFullBoundsReference().getHeight() + _glassNode.getBulbDiameter()/2 );
        _valueNode.setOffset( _glassNode.getFullBoundsReference().getMaxX() + 2, -_valueNode.getFullBoundsReference().getHeight() );
        
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
     * The glass thermometer.
     */
    private static class GlassNode extends LiquidExpansionThermometerNode {
        public GlassNode( PDimension size ) {
            super( size );
            setInnerWallVisible( false );
        }
    }
    
    /*
     * Displays the temperature value.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _celsiusLabel;
        private JLabel _fahrenheitLabel;
        private PSwing _pswing;
        
        public ValueNode( Font font, Border border ) {
            super();
            
            _celsiusLabel = new JLabel( "0" );
            _celsiusLabel.setFont( font );
            
            _fahrenheitLabel = new JLabel( "0" );
            _fahrenheitLabel.setFont( font );
            
            JPanel panel = new JPanel();
            panel.setBackground( Color.WHITE );
            panel.setBorder( border );
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
            double valueF = UnitsConverter.celsiusToFahrenheit( celsius );
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
     * Display "?" when the thermometer is below the ice surface.
     */
    private void updateTemperature() {
        double glacierSurfaceY = _glacier.getSurfaceElevation( _thermometer.getX() );
        if ( _thermometer.getY() > glacierSurfaceY ) {
            final double temperature = _thermometer.getTemperature();
            double percent = 1 - ( MAX_TEMPERATURE - temperature ) / ( MAX_TEMPERATURE - MIN_TEMPERATURE );
            if ( percent < 0 ) {
                percent = 0;
            }
            else if ( percent > 1 ) {
                percent = 1;
            }
            _glassNode.setLiquidHeight( percent );
            _valueNode.setTemperature( temperature ); 
        }
        else {
            _glassNode.setLiquidHeight( 0 );
            _valueNode.setTemperatureUnknown();
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        GlassNode glassNode = new GlassNode( THERMOMETER_SIZE );
        glassNode.setLiquidHeight( 0.5 );
        return glassNode.toImage();
    }
}
