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
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter.GlacialBudgetMeterListener;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
import edu.colorado.phet.glaciers.util.UnitsConverter;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.tools.AbstractToolOriginNode.LeftToolOriginNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * GlacialBudgetMeterNode is the visual representation of a glacial budget meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacialBudgetMeterNode extends AbstractToolNode {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean SHOW_ELEVATION = false;
    
    private static final NumberFormat ELEVATION_FORMAT = new DefaultDecimalFormat( "0" );
    private static final NumberFormat ACCUMULATION_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final NumberFormat ABLATION_FORMAT = new DefaultDecimalFormat( "0.00" );
    private static final NumberFormat GLACIAL_BUDGET_FORMAT = new DefaultDecimalFormat( "0.00" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlacialBudgetMeter _glacialBudgetMeter;
    private final Glacier _glacier;
    private final GlacialBudgetMeterListener _glacialBudgetMeterListener;
    private final GlacierListener _glacierListener;
    private final MovableListener _movableListener;
    private final ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacialBudgetMeterNode( GlacialBudgetMeter glacialBudgetMeter, Glacier glacier, ModelViewTransform mvt, TrashCanDelegate trashCan, boolean englishUnits ) {
        super( glacialBudgetMeter, mvt, trashCan );
        
        System.out.println( "GlacialBudgetMeterNode.init englishUnits=" + englishUnits );//XXX
        
        _glacialBudgetMeter = glacialBudgetMeter;
        _glacier = glacier;
        
        _glacialBudgetMeterListener = new GlacialBudgetMeterListener() {
            public void accumulationChanged() {
                update();
            }
            public void ablationChanged() {
                update();
            }
            public void glacialBudgetChanged() {
                update();
            }
        };
        _glacialBudgetMeter.addGlacialBudgetMeterListener( _glacialBudgetMeterListener );
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                update();
            }
        };
        _glacialBudgetMeter.addMovableListener( _movableListener );
        
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                update();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        PNode arrowNode = new LeftToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        PNode meterNode = new MeterNode();
        addChild( meterNode );
        meterNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 2, -meterNode.getFullBoundsReference().getHeight() / 2 );
        
        _valueNode = new ValueNode( getValueFont(), getValueBorder(), englishUnits );
        addChild( _valueNode );
        _valueNode.setOffset( meterNode.getFullBounds().getMaxX() + 2, -_valueNode.getFullBounds().getHeight() / 2 );
        
        // initial state
        _valueNode.setValuesUnknown();
    }
    
    public void cleanup() {
        _glacialBudgetMeter.removeGlacialBudgetMeterListener( _glacialBudgetMeterListener );
        _glacialBudgetMeter.removeMovableListener( _movableListener );
        _glacier.removeGlacierListener( _glacierListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * The meter.
     */
    private static class MeterNode extends PComposite {
        
        public MeterNode() {
            super();
            PImage imageNode = new PImage( GlaciersImages.GLACIAL_BUDGET_METER );
            addChild( imageNode );
        }
    }
    
    /*
     * Displays the values measured by the meter.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _elevationLabel;
        private JLabel _accumulationLabel;
        private JLabel _ablationLabel;
        private JLabel _glacialBudgetLabel;
        private PSwing _pswing;
        private final boolean _englishUnits;
        private final String _units;
        
        public ValueNode( Font font, Border border, boolean englishUnits ) {
            super();
            
            _englishUnits = englishUnits;
            _units = ( englishUnits ? GlaciersStrings.UNITS_FEET_PER_YEAR : GlaciersStrings.UNITS_METERS_PER_YEAR );
            
            JLabel elevationLabel = new JLabel( GlaciersStrings.LABEL_ELEVATION + ":" );
            elevationLabel.setFont( font );
            _elevationLabel = new JLabel( "0" );
            _elevationLabel.setFont( font );
            
            JLabel accumulationLabel = new JLabel( GlaciersStrings.LABEL_ACCUMULATION + ":" );
            accumulationLabel.setFont( font );
            _accumulationLabel = new JLabel( "0" );
            _accumulationLabel.setFont( font );
            _accumulationLabel.setForeground( GlaciersConstants.ACCUMULATION_COLOR );
            
            JLabel ablationLabel = new JLabel( GlaciersStrings.LABEL_ABLATION + ":" );
            ablationLabel.setFont( font );
            _ablationLabel = new JLabel( "0" );
            _ablationLabel.setFont( font );
            _ablationLabel.setForeground( GlaciersConstants.ABLATION_COLOR );
            
            JLabel glacialBudgetLabel = new JLabel( GlaciersStrings.LABEL_GLACIAL_BUDGET + ":" );
            glacialBudgetLabel.setFont( font );
            _glacialBudgetLabel = new JLabel( "0" );
            _glacialBudgetLabel.setFont( font );
            _glacialBudgetLabel.setForeground( GlaciersConstants.GLACIAL_BUDGET_COLOR );
            
            JPanel displayPanel = new JPanel();
            displayPanel.setBackground( Color.WHITE );
            displayPanel.setBorder( border );
            EasyGridBagLayout layout = new EasyGridBagLayout( displayPanel );
            displayPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            if ( SHOW_ELEVATION ) {
                layout.addAnchoredComponent( elevationLabel, row, column++, GridBagConstraints.EAST );
                layout.addAnchoredComponent( _elevationLabel, row++, column++, GridBagConstraints.WEST );
                column = 0;
            }
            layout.addAnchoredComponent( accumulationLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _accumulationLabel, row++, column++, GridBagConstraints.WEST );
            column = 0;
            layout.addAnchoredComponent( ablationLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _ablationLabel, row++, column++, GridBagConstraints.WEST );
            column = 0;
            layout.addAnchoredComponent( glacialBudgetLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _glacialBudgetLabel, row++, column++, GridBagConstraints.WEST );
            
            _pswing = new PSwing( displayPanel );
            addChild( _pswing );
        }

        public void setValues( double elevation, double accumulation, double ablation, double glacialBudget ) {
            if ( _englishUnits ) {
                _elevationLabel.setText( ELEVATION_FORMAT.format( UnitsConverter.metersToFeet( elevation ) ) + " " + _units );
                _accumulationLabel.setText( ACCUMULATION_FORMAT.format( UnitsConverter.metersToFeet( accumulation ) ) + " " + _units );
                _ablationLabel.setText( ABLATION_FORMAT.format( UnitsConverter.metersToFeet( ablation ) ) + " " + _units );
                _glacialBudgetLabel.setText( GLACIAL_BUDGET_FORMAT.format( UnitsConverter.metersToFeet( glacialBudget ) ) + " " + _units );
            }
            else {
                _elevationLabel.setText( ELEVATION_FORMAT.format( elevation ) + " " + _units );
                _accumulationLabel.setText( ACCUMULATION_FORMAT.format( accumulation ) + " " + _units );
                _ablationLabel.setText( ABLATION_FORMAT.format( ablation ) + " " + _units );
                _glacialBudgetLabel.setText( GLACIAL_BUDGET_FORMAT.format( glacialBudget ) + " " + _units );
            }
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }

        public void setValuesUnknown() {
            final String unknownValue = "?";
            _elevationLabel.setText( unknownValue + " " + _units );
            _accumulationLabel.setText( unknownValue + " " + _units );
            _ablationLabel.setText( unknownValue + " " + _units );
            _glacialBudgetLabel.setText( unknownValue + " " + _units );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the display to match the model.
     * When the meter is not on the surface, display "?" for all values.
     */
    private void update() {
        double glacierSurfaceY = _glacier.getSurfaceElevation( _glacialBudgetMeter.getX() );
        if ( _glacialBudgetMeter.getX() >= _glacier.getHeadwallX() && _glacialBudgetMeter.getY() == glacierSurfaceY ) {
            _valueNode.setValues( 
                _glacialBudgetMeter.getPosition().getY(),
                _glacialBudgetMeter.getAccumulation(),
                _glacialBudgetMeter.getAblation(),
                _glacialBudgetMeter.getGlacialBudget() );
        }
        else {
            _valueNode.setValuesUnknown();
        }
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        return GlaciersImages.GLACIAL_BUDGET_METER;
    }
}
