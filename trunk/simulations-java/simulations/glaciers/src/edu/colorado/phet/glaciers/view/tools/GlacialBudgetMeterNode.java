/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter.GlacialBudgetMeterListener;
import edu.colorado.phet.glaciers.model.Movable.MovableAdapter;
import edu.colorado.phet.glaciers.model.Movable.MovableListener;
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
    
    private static final Font FONT = new PhetFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final NumberFormat ELEVATION_FORMAT = new DefaultDecimalFormat( "0" );
    private static final NumberFormat ACCUMULATION_FORMAT = new DefaultDecimalFormat( "0.0" );
    private static final NumberFormat ABLATION_FORMAT = new DefaultDecimalFormat( "0.0" );
    private static final NumberFormat GLACIAL_BUDGET_FORMAT = new DefaultDecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlacialBudgetMeter _glacialBudgetMeter;
    private GlacialBudgetMeterListener _glacialBudgetMeterListener;
    private MovableListener _movableListener;
    private ValueNode _valueNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacialBudgetMeterNode( GlacialBudgetMeter glacialBudgetMeter, ModelViewTransform mvt, TrashCanIconNode trashCanIconNode ) {
        super( glacialBudgetMeter, mvt, trashCanIconNode );
        
        _glacialBudgetMeter = glacialBudgetMeter;
        
        _glacialBudgetMeterListener = new GlacialBudgetMeterListener() {
            public void accumulationChanged() {
                updateAccumulation();
            }
            public void ablationChanged() {
                updateAblation();
            }
            public void glacialBudgetChanged() {
                updateGlacialBudget();
            }
        };
        _glacialBudgetMeter.addGlacialBudgetMeterListener( _glacialBudgetMeterListener );
        
        _movableListener = new MovableAdapter() {
            public void positionChanged() {
                updateElevation();
            }
        };
        _glacialBudgetMeter.addMovableListener( _movableListener );
        
        PNode arrowNode = new LeftToolOriginNode();
        addChild( arrowNode );
        arrowNode.setOffset( 0, 0 ); // this node identifies the origin
        
        PImage imageNode = new PImage( GlaciersImages.GLACIAL_BUDGET_METER );
        addChild( imageNode );
        imageNode.setOffset( arrowNode.getFullBoundsReference().getMaxX() + 1, -imageNode.getFullBoundsReference().getHeight() / 2 );
        
        _valueNode = new ValueNode();
        addChild( _valueNode );
        _valueNode.setOffset( imageNode.getFullBounds().getMaxX() + 1, -_valueNode.getFullBounds().getHeight() / 2 );
        
        // initial state
        updateElevation();
        updateAccumulation();
        updateAblation();
        updateGlacialBudget();
    }
    
    public void cleanup() {
        _glacialBudgetMeter.removeGlacialBudgetMeterListener( _glacialBudgetMeterListener );
        _glacialBudgetMeter.removeMovableListener( _movableListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------
    
    /*
     * Displays the values measured by the meter.
     */
    private static class ValueNode extends PComposite {
        
        private JLabel _elevationLabel;
        private JLabel _accumulationLabel;
        private JLabel _ablationLabel;
        private JLabel _glacialBudgetLabel;
        private PSwing _pswing;
        
        public ValueNode() {
            super();
            
            JLabel elevationLabel = new JLabel( GlaciersStrings.LABEL_ELEVATION + ":" );
            elevationLabel.setFont( FONT );
            _elevationLabel = new JLabel( "0" );
            _elevationLabel.setFont( FONT );
            
            JLabel accumulationLabel = new JLabel( GlaciersStrings.LABEL_ACCUMULATION + ":" );
            accumulationLabel.setFont( FONT );
            _accumulationLabel = new JLabel( "0" );
            _accumulationLabel.setFont( FONT );
            _accumulationLabel.setForeground( GlaciersConstants.ACCUMULATION_COLOR );
            
            JLabel ablationLabel = new JLabel( GlaciersStrings.LABEL_ABLATION + ":" );
            ablationLabel.setFont( FONT );
            _ablationLabel = new JLabel( "0" );
            _ablationLabel.setFont( FONT );
            _ablationLabel.setForeground( GlaciersConstants.ABLATION_COLOR );
            
            JLabel glacialBudgetLabel = new JLabel( GlaciersStrings.LABEL_GLACIAL_BUDGET + ":" );
            glacialBudgetLabel.setFont( FONT );
            _glacialBudgetLabel = new JLabel( "0" );
            _glacialBudgetLabel.setFont( FONT );
            _glacialBudgetLabel.setForeground( GlaciersConstants.GLACIAL_BUDGET_COLOR );
            
            JPanel displayPanel = new JPanel();
            displayPanel.setBackground( Color.WHITE );
            displayPanel.setBorder( BORDER );
            EasyGridBagLayout layout = new EasyGridBagLayout( displayPanel );
            displayPanel.setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addAnchoredComponent( elevationLabel, row, column++, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _elevationLabel, row++, column++, GridBagConstraints.WEST );
            column = 0;
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
        
        public void setElevation( double elevation ) {
            String text = ELEVATION_FORMAT.format( elevation ) + " " + GlaciersStrings.UNITS_ELEVATION;
            _elevationLabel.setText( text );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
        
        public void setAccumulation( double accumulation ) {
            String text = ACCUMULATION_FORMAT.format( accumulation ) + " " + GlaciersStrings.UNITS_ACCUMULATION;
            _accumulationLabel.setText( text );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
        
        public void setAblation( double ablation ) {
            String text = ABLATION_FORMAT.format( ablation ) + " " + GlaciersStrings.UNITS_ABLATION;
            _ablationLabel.setText( text );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
        
        public void setGlacialBudget( double glacialBudget ) {
            String text = GLACIAL_BUDGET_FORMAT.format( glacialBudget )  + " " + GlaciersStrings.UNITS_GLACIAL_BUDGET;
            _glacialBudgetLabel.setText( text );
            _pswing.computeBounds(); //WORKAROUND: PSwing doesn't handle changing size of a JPanel properly
        }
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the elevation display to match the model.
     */
    private void updateElevation() {
        _valueNode.setElevation( _glacialBudgetMeter.getPosition().getY() );
    }
    
    /*
     * Updates the accumulation display to match the model.
     */
    private void updateAccumulation() {
        _valueNode.setAccumulation( _glacialBudgetMeter.getAccumulation() );
    }
    
    /*
     * Updates the ablation display to match the model.
     */
    private void updateAblation() {
        _valueNode.setAblation( _glacialBudgetMeter.getAblation() );
    }
    
    /*
     * Updates the "glacial budget" display to match the model.
     */
    private void updateGlacialBudget() {
        _valueNode.setGlacialBudget( _glacialBudgetMeter.getGlacialBudget() );
    }
}
