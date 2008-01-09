/* Copyright 2007, University of Colorado */

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


public class GlacialBudgetMeterNode extends AbstractToolNode {

    private static final Font FONT = new PhetDefaultFont( 10 );
    private static final Border BORDER = BorderFactory.createLineBorder( Color.BLACK, 1 );
    private static final DecimalFormat ACCUMULATION_FORMAT = new DecimalFormat( "0.0" );
    private static final DecimalFormat ABLATION_FORMAT = new DecimalFormat( "0.0" );
    private static final DecimalFormat GLACIAL_BUDGET_FORMAT = new DecimalFormat( "0.0" );
    
    private GlacialBudgetMeter _glacialBudgetMeter;
    private GlacialBudgetMeterListener _glacialBudgetMeterListener;
    
    private JLabel _accumulationValue;
    private JLabel _ablationValue;
    private JLabel _glacialBudgetValue;
    
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
        _accumulationValue = new JLabel();
        _accumulationValue.setFont( FONT );
        JLabel ablationLabel = new JLabel( GlaciersStrings.LABEL_ABLATION );
        ablationLabel.setFont( FONT );
        _ablationValue = new JLabel();
        _ablationValue.setFont( FONT );
        JLabel glacialBudgetLabel = new JLabel( GlaciersStrings.LABEL_GLACIAL_BUDGET );
        glacialBudgetLabel.setFont( FONT );
        _glacialBudgetValue = new JLabel();
        _glacialBudgetValue.setFont( FONT );
        
        JPanel displayPanel = new JPanel();
        displayPanel.setBackground( Color.WHITE );
        displayPanel.setBorder( BORDER );
        EasyGridBagLayout layout = new EasyGridBagLayout( displayPanel );
        displayPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( accumulationLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _accumulationValue, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( ablationLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _ablationValue, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( glacialBudgetLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _glacialBudgetValue, row++, column, GridBagConstraints.WEST );
        
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
    
    private void updateAccumulation() {
        double value = _glacialBudgetMeter.getAccumulation();
        String text = ACCUMULATION_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ACCUMULATION;
        _accumulationValue.setText( text );
    }
    
    private void updateAblation() {
        double value = _glacialBudgetMeter.getAblation();
        String text = ABLATION_FORMAT.format( value ) + " " + GlaciersStrings.UNITS_ABLATION;
        _ablationValue.setText( text );
    }
    
    private void updateGlacialBudget() {
        double value = _glacialBudgetMeter.getGlacialBudget();
        String text = GLACIAL_BUDGET_FORMAT.format( value )  + " " + GlaciersStrings.UNITS_GLACIAL_BUDGET;
        _glacialBudgetValue.setText( text );
    }
}
