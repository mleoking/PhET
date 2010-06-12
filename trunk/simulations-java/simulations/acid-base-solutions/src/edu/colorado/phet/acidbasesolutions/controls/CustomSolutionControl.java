/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.view.ABSRadioButton;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Control used to set the properties of a custom solution.
 * Mutable properties include the type of solute (acid or base),
 * the concentration of the solution in solution, and the 
 * strength (weak or strong) of the solute.  For weak solutes,
 * the strength can be specified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionControl extends JPanel {
    
    public CustomSolutionControl( ABSModel model ) {
        
        // border
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
        // subpanels
        TypePanel typePanel = new TypePanel();
        ConcentrationPanel concentrationPanel = new ConcentrationPanel();
        StrengthPanel strengthPanel = new StrengthPanel();
        
        // main panel layout
        int row = 0;
        int column = 0;
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.addComponent( typePanel, row++, column );
        layout.addComponent( concentrationPanel, row++, column );
        layout.addComponent( strengthPanel, row++, column );
    }
    
    /*
     * Panel for setting the solute type.
     */
    private static class TypePanel extends JPanel {
        
        private final JRadioButton acidRadioButton, baseRadioButton;
        
        public TypePanel() { 
            
            // radio buttons
            ButtonGroup group = new ButtonGroup();
            ActionListener listener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            };
            acidRadioButton = new ABSRadioButton( ABSStrings.ACID, group, listener );
            baseRadioButton = new ABSRadioButton( ABSStrings.BASE, group, listener );
            
            // type panel
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( acidRadioButton, row, column++ );
            layout.addComponent( baseRadioButton, row, column++ );
            
            // default state
            acidRadioButton.setSelected( true ); //XXX
        }
    }
    
    /* 
     * Panel for setting the concentration.
     */
    private static class ConcentrationPanel extends JPanel {
        
        private final LogarithmicValueControl concentrationControl;
        
        public ConcentrationPanel() {

            // logarithmic control
            double min = ABSConstants.CONCENTRATION_RANGE.getMin();
            double max = ABSConstants.CONCENTRATION_RANGE.getMax();
            String label = ABSStrings.CONCENTRATION;
            String textFieldPattern = "0.000";
            String units = ABSStrings.MOLAR;
            concentrationControl = new LogarithmicValueControl( min, max, label, textFieldPattern, units );
            
            // labels on the slider
            {
                // labels at each 10x interval
                Hashtable<Double, JLabel> concentrationLabelTable = new Hashtable<Double, JLabel>();
                int numberOfLabels = 0;
                for ( double c = concentrationControl.getMinimum(); c <= concentrationControl.getMaximum(); c *= 10 ) {
                    concentrationLabelTable.put( new Double( c ), new JLabel( String.valueOf( c ) ) );
                    numberOfLabels++;
                }
                concentrationControl.setTickLabels( concentrationLabelTable );
                
                /*
                 * Setting tick marks for a LogarithmicValueControl is unfortunately a bit of a hack.
                 * The underlying JSlider is linear and has an integer range, and we need to set the
                 * tick spacing based on this range, not our logarithmic "model" range.
                 * Here we assume that the ticks are evenly spaced, and compute the tick spacing
                 * based on the integer range of the underlying JSlider.
                 */
                int tickSpacing = ( concentrationControl.getSlider().getMaximum() - concentrationControl.getSlider().getMinimum() ) / ( numberOfLabels - 1 );
                concentrationControl.getSlider().setMajorTickSpacing( tickSpacing );
            }
            
            concentrationControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    //XXX update model
                }
            } );
            
            // layout
            setBorder( new EtchedBorder() );
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( concentrationControl, row, column );
            
            // default state
            concentrationControl.setValue( ABSConstants.CONCENTRATION_RANGE.getDefault() ); //XXX
        }
    }
    
    /*
     * Panel for setting the strength.
     * For strong solutes, the strength is fixed.
     * For weak solutes, the strength can be adjusted via a slider.
     */
    private static class StrengthPanel extends JPanel {
        
        private final JLabel strengthLabel;
        private final JRadioButton weakRadioButton, strongRadioButton;
        private final LogarithmicValueControl weakStrengthControl;
        
        public StrengthPanel() {
            
            // dynamic strength label
            strengthLabel = new JLabel( ABSStrings.PATTERN_STRENGTH_STRONG ); //XXX
            
            // radio buttons
            ButtonGroup group = new ButtonGroup();
            ActionListener listener = new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            };
            weakRadioButton = new ABSRadioButton( ABSStrings.WEAK, group, listener );
            strongRadioButton = new ABSRadioButton( ABSStrings.STRONG, group, listener );
            
            // strength
            double min = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
            double max = ABSConstants.WEAK_STRENGTH_RANGE.getMax();
            String label = "";
            String textFieldPattern = "";
            String units = "";
            weakStrengthControl = new LogarithmicValueControl( min, max, label, textFieldPattern, units, new SliderLayoutStrategy() );
            Hashtable<Double, JLabel> strengthLabelTable = new Hashtable<Double, JLabel>();
            strengthLabelTable.put( new Double( weakStrengthControl.getMinimum() ), new JLabel( ABSStrings.WEAKER ) );
            strengthLabelTable.put( new Double( weakStrengthControl.getMaximum() ), new JLabel( ABSStrings.STRONGER ) );
            weakStrengthControl.setTickLabels( strengthLabelTable );
            weakStrengthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    //XXX update model
                }
            } );
            
            // strength panel
            setBorder( new EtchedBorder() );
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            int row = 0;
            int column = 0;
            layout.addComponent( strengthLabel, row++, column, 2, 1 );
            layout.addComponent( weakRadioButton, row, column++, 1, 1 );
            layout.addComponent( strongRadioButton, row++, column, 1, 1 );
            column = 0;
            layout.addComponent( weakStrengthControl, row++, column, 2, 1 );
            
            // default layout
            weakRadioButton.setSelected( true ); //XXX
            weakStrengthControl.setValue( weakStrengthControl.getMinimum() ); //XXX
        }
    }
    
    /*
     * Value control layout that has only the slider.
     * The label, text field and units are omitted.
     */
    private static class SliderLayoutStrategy implements ILayoutStrategy {

        public SliderLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {
            valueControl.add( valueControl.getSlider() );
        }
    }
}
