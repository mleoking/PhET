/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter;
import edu.colorado.phet.glaciers.model.GlacialBudgetMeter.GlacialBudgetMeterListener;
import edu.umd.cs.piccolo.nodes.PImage;
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
    
    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final DecimalFormat ACCUMULATION_FORMAT = new DecimalFormat( "0.0" );
    private static final DecimalFormat ABLATION_FORMAT = new DecimalFormat( "0.0" );
    private static final DecimalFormat GLACIAL_BUDGET_FORMAT = new DecimalFormat( "0.0" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlacialBudgetMeter _glacialBudgetMeter;
    private GlacialBudgetMeterListener _glacialBudgetMeterListener;
    
    private JLabel _accumulationDisplay;
    private JLabel _ablationDisplay;
    private JLabel _glacialBudgetDisplay;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public GlacialBudgetMeterNode( GlacialBudgetMeter glacialBudgetMeter, ModelViewTransform mvt ) {
        super( glacialBudgetMeter, mvt );
        
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
        
        PImage imageNode = new PImage( GlaciersImages.GLACIAL_BUDGET_METER );
        addChild( imageNode );
        imageNode.setOffset( -imageNode.getFullBoundsReference().getWidth() / 2, -imageNode.getFullBoundsReference().getHeight() ); // lower center
        
        JLabel accumulationLabel = new JLabel( GlaciersStrings.LABEL_ACCUMULATION );
        accumulationLabel.setFont( FONT );
        _accumulationDisplay = new JLabel();
        _accumulationDisplay.setFont( FONT );
        JLabel ablationLabel = new JLabel( GlaciersStrings.LABEL_ABLATION );
        ablationLabel.setFont( FONT );
        _ablationDisplay = new JLabel();
        _ablationDisplay.setFont( FONT );
        JLabel glacialBudgetLabel = new JLabel( GlaciersStrings.LABEL_GLACIAL_BUDGET );
        glacialBudgetLabel.setFont( FONT );
        _glacialBudgetDisplay = new JLabel();
        _glacialBudgetDisplay.setFont( FONT );
        
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground( Color.WHITE );
        displayPanel.setBorder( BORDER );
        EasyGridBagLayout layout = new EasyGridBagLayout( displayPanel );
        layout.setAnchor( GridBagConstraints.EAST );
        displayPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( accumulationLabel, row, column++ );
        layout.addComponent( _accumulationDisplay, row++, column++ );
        column = 0;
        layout.addComponent( ablationLabel, row, column++ );
        layout.addComponent( _ablationDisplay, row++, column++ );
        column = 0;
        layout.addComponent( glacialBudgetLabel, row, column++ );
        layout.addComponent( _glacialBudgetDisplay, row++, column++ );
        
        PSwing panelNode = new PSwing( displayPanel );
        addChild( panelNode );
        // center above meter
        double xOffset = imageNode.getFullBoundsReference().getX() - ( panelNode.getFullBoundsReference().getWidth() - imageNode.getFullBoundsReference().getWidth() );
        double yOffset = imageNode.getFullBoundsReference().getY() - panelNode.getFullBoundsReference().getHeight() - 2;
        panelNode.setOffset( xOffset, yOffset );
        
        // initial state
        updateAccumulation();
        updateAblation();
        updateGlacialBudget();
    }
    
    public void cleanup() {
        _glacialBudgetMeter.removeGlacialBudgetMeterListener( _glacialBudgetMeterListener );
        super.cleanup();
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the accumulation display to match the model.
     */
    private void updateAccumulation() {
        double value = _glacialBudgetMeter.getAccumulation();
        String text = ACCUMULATION_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ACCUMULATION;
        _accumulationDisplay.setText( text );
    }
    
    /*
     * Updates the ablation display to match the model.
     */
    private void updateAblation() {
        double value = _glacialBudgetMeter.getAblation();
        String text = ABLATION_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ABLATION;
        _ablationDisplay.setText( text );
    }
    
    /*
     * Updates the "glacial budget" display to match the model.
     */
    private void updateGlacialBudget() {
        double value = _glacialBudgetMeter.getGlacialBudget();
        String text = GLACIAL_BUDGET_FORMAT.format( value )  + " " + GlaciersStrings.UNITS_GLACIAL_BUDGET;
        _glacialBudgetDisplay.setText( text );
    }
}
