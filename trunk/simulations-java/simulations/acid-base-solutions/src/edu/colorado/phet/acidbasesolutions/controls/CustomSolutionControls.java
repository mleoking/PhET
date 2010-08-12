/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.ABSModel.ModelChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.AqueousSolutionChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.ICustomSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongAcidSolution.CustomStrongAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.StrongBaseSolution.CustomStrongBaseSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakAcidSolution.CustomWeakAcidSolution;
import edu.colorado.phet.acidbasesolutions.model.WeakBaseSolution.CustomWeakBaseSolution;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls used to set the properties of a custom solution.
 * Mutable properties include the type of solute (acid or base),
 * the concentration of the solution in solution, and the 
 * strength (weak or strong) of the solute.  For weak solutes,
 * the strength can be specified.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CustomSolutionControls extends JPanel {
    
    private final ABSModel model;
    private AqueousSolution solution;
    private final TypePanel typePanel;
    private final ConcentrationPanel concentrationPanel;
    private final StrengthPanel strengthPanel;
    private final AqueousSolutionChangeListener solutionChangeListener;
    private boolean isSyncingWithModel;
    
    public CustomSolutionControls( ABSModel model ) {
        
        // border
        {
            TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
            titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
            titledBorder.setBorder( ABSConstants.TITLE_BORDER_BORDER );
            setBorder( titledBorder );
        }
        
        // model
        {
            this.model = model;
            isSyncingWithModel = false;
            
            this.model.addModelChangeListener( new ModelChangeListener() {
                public void solutionChanged() {
                    updateControls();
                }
            } );
            
            this.solution = model.getSolution();
            solutionChangeListener = new AqueousSolutionChangeListener() {

                public void concentrationChanged() {
                    updateControls();
                }

                public void strengthChanged() {
                    updateControls();
                }
            };
            solution.addAqueousSolutionChangeListener( solutionChangeListener );
        }
        
        // subpanels
        {
            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateModel();
                }
            };
            typePanel = new TypePanel( changeListener );
            concentrationPanel = new ConcentrationPanel( changeListener );
            strengthPanel = new StrengthPanel( changeListener );
        }
        
        // main panel layout
        {
            int row = 0;
            int column = 0;
            EasyGridBagLayout layout = new EasyGridBagLayout( this );
            setLayout( layout );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.addComponent( typePanel, row++, column );
            layout.addComponent( concentrationPanel, row++, column );
            layout.addComponent( strengthPanel, row++, column );
        }
        
        // default state
        {
            updateControls();
        }
    }
    
    /*
     * Updates this control to match the model.
     */
    private void updateControls() {
        
        isSyncingWithModel = true;
        
        // move the solution listener
        solution.removeAqueousSolutionChangeListener( solutionChangeListener );
        solution = model.getSolution();
        solution.addAqueousSolutionChangeListener( solutionChangeListener );
        
        // adjust control settings
        typePanel.setAcidSelected( solution instanceof AcidSolution );
        concentrationPanel.setConcentration( model.getSolution().getConcentration() );
        strengthPanel.setWeakSelected( ( solution instanceof WeakAcidSolution ) || ( solution instanceof WeakBaseSolution ) );
        strengthPanel.setStrength( model.getSolution().getStrength() );
        
        isSyncingWithModel = false;
    }
    
    /*
     * Updates the model to match this control.
     */
    private void updateModel() {
        
        if ( !isSyncingWithModel ) {

            AqueousSolution currentSolution = model.getSolution();
            assert ( currentSolution instanceof ICustomSolution );

            final double strength = strengthPanel.getStrength();
            final double concentration = concentrationPanel.getConcentration();

            if ( typePanel.isAcidSelected() ) {
                // acids
                if ( strengthPanel.isWeakSelected() ) {
                    // weak acid
                    if ( currentSolution instanceof CustomWeakAcidSolution ) {
                        ( (CustomWeakAcidSolution) currentSolution ).setStrength( strength );
                        ( (CustomWeakAcidSolution) currentSolution ).setConcentration( concentration );
                    }
                    else {
                        CustomWeakAcidSolution newSoluton = new CustomWeakAcidSolution( strength, concentration );
                        model.setSolution( newSoluton );
                    }
                }
                else {
                    // strong acid
                    if ( currentSolution instanceof CustomStrongAcidSolution ) {
                        ( (CustomStrongAcidSolution) currentSolution ).setConcentration( concentration );
                    }
                    else {
                        CustomStrongAcidSolution newSoluton = new CustomStrongAcidSolution( concentration );
                        model.setSolution( newSoluton );
                    }
                }
            }
            else {
                // bases
                if ( strengthPanel.isWeakSelected() ) {
                    // weak base
                    if ( currentSolution instanceof CustomWeakBaseSolution ) {
                        ( (CustomWeakBaseSolution) currentSolution ).setStrength( strength );
                        ( (CustomWeakBaseSolution) currentSolution ).setConcentration( concentration );
                    }
                    else {
                        CustomWeakBaseSolution newSoluton = new CustomWeakBaseSolution( strength, concentration );
                        model.setSolution( newSoluton );
                    }
                }
                else {
                    // strong base
                    if ( currentSolution instanceof CustomStrongBaseSolution ) {
                        ( (CustomStrongBaseSolution) currentSolution ).setConcentration( concentration );
                    }
                    else {
                        CustomStrongBaseSolution newSoluton = new CustomStrongBaseSolution( concentration );
                        model.setSolution( newSoluton );
                    }
                }
            }
        }
    }
    
    /*
     * A subpanel of this control that notifies one ChangeListener
     * when the value mangaged by the panel is changed.
     * This is a convenient way to prevent subpanels from having to know about the model,
     * and keep all knowledge of the model in the main panel.
     */
    private static class ChangeablePanel extends JPanel {
        
        private final ChangeListener listener;
        
        public ChangeablePanel( ChangeListener listener ) {
            this.listener = listener;
        }

        protected void fireStateChanged() {
            listener.stateChanged( new ChangeEvent( this ) );
        }
    }
    
    /*
     * Panel for setting the solute type.
     */
    private static class TypePanel extends ChangeablePanel {
        
        private final JRadioButton acidRadioButton, baseRadioButton;
        
        public TypePanel( ChangeListener changeListener ) { 
            super( changeListener );
            
            // radio buttons
            {
                ButtonGroup group = new ButtonGroup();
                
                ActionListener actionListener = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        fireStateChanged();
                    }
                };
                
                acidRadioButton = new JRadioButton( ABSStrings.ACID );
                group.add( acidRadioButton );
                acidRadioButton.addActionListener( actionListener );
                
                baseRadioButton = new JRadioButton( ABSStrings.BASE );
                group.add( baseRadioButton );
                baseRadioButton.addActionListener( actionListener );
            }
            
            // layout
            {
                EasyGridBagLayout layout = new EasyGridBagLayout( this );
                setLayout( layout );
                int row = 0;
                int column = 0;
                layout.addComponent( acidRadioButton, row, column++ );
                layout.addComponent( baseRadioButton, row, column++ );
            }
        }
        
        public void setAcidSelected( boolean selected ) {
            acidRadioButton.setSelected( selected );
        }
        
        public boolean isAcidSelected() {
            return acidRadioButton.isSelected();
        }
    }
    
    /* 
     * Panel for setting the concentration.
     */
    private static class ConcentrationPanel extends ChangeablePanel {
        
        private final LogarithmicValueControl concentrationControl;
        
        public ConcentrationPanel( ChangeListener changeListener ) {
            super( changeListener );

            // logarithmic concentration control
            {
                double min = ABSConstants.CONCENTRATION_RANGE.getMin();
                double max = ABSConstants.CONCENTRATION_RANGE.getMax();
                double value = ABSConstants.CONCENTRATION_RANGE.getDefault();
                String label = ABSStrings.CONCENTRATION;
                String textFieldPattern = "0.000";
                String units = ABSStrings.MOLES_PER_LITER;
                concentrationControl = new LogarithmicValueControl( min, max, label, textFieldPattern, units );
                concentrationControl.setValue( value );
                concentrationControl.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        fireStateChanged();
                    }
                } );
            }
            
            // labels on the slider
            {
                // labels at each 10x interval
                Hashtable<Double, JLabel> concentrationLabelTable = new Hashtable<Double, JLabel>();
                DecimalFormat format = new DecimalFormat( "#.###" );
                int numberOfLabels = 0;
                for ( double c = concentrationControl.getMinimum(); c <= concentrationControl.getMaximum(); c *= 10 ) {
                    concentrationLabelTable.put( new Double( c ), new JLabel( format.format( c )) );
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
            
            // layout
            {
                setBorder( new EtchedBorder() );
                EasyGridBagLayout layout = new EasyGridBagLayout( this );
                setLayout( layout );
                int row = 0;
                int column = 0;
                layout.addComponent( concentrationControl, row, column );
            }
        }
        
        public void setConcentration( double concentration ) {
            concentrationControl.setValue( concentration );
        }
        
        public double getConcentration() {
            return concentrationControl.getValue();
        }
    }
    
    /*
     * Panel for setting the strength.
     * For strong solutes, the strength is fixed.
     * For weak solutes, the strength can be adjusted via a slider.
     */
    private static class StrengthPanel extends ChangeablePanel {
        
        private final JRadioButton weakRadioButton, strongRadioButton;
        private final LogarithmicValueControl weakStrengthControl;
        
        public StrengthPanel( ChangeListener changeListener ) {
            super( changeListener );
            
            // dynamic strength label
            JLabel strengthLabel = new JLabel( ABSStrings.STRENGTH );
            
            // radio buttons
            {
                ButtonGroup group = new ButtonGroup();
                
                ActionListener actionListener = new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        fireStateChanged();
                    }
                };
                
                weakRadioButton = new JRadioButton( ABSStrings.WEAK );
                group.add( weakRadioButton );
                weakRadioButton.addActionListener( actionListener );
                
                strongRadioButton = new JRadioButton( ABSStrings.STRONG );
                group.add( strongRadioButton );
                strongRadioButton.addActionListener( actionListener );
            }
            
            // strength control
            {
                double min = ABSConstants.WEAK_STRENGTH_RANGE.getMin();
                double max = ABSConstants.WEAK_STRENGTH_RANGE.getMax();
                double value = ABSConstants.WEAK_STRENGTH_RANGE.getDefault();
                String label = "";
                String textFieldPattern = "";
                String units = "";
                weakStrengthControl = new LogarithmicValueControl( min, max, label, textFieldPattern, units, new SliderLayoutStrategy() );
                weakStrengthControl.setValue( value );
                Hashtable<Double, JLabel> strengthLabelTable = new Hashtable<Double, JLabel>();
                strengthLabelTable.put( new Double( weakStrengthControl.getMinimum() ), new JLabel( ABSStrings.WEAKER ) );
                strengthLabelTable.put( new Double( weakStrengthControl.getMaximum() ), new JLabel( ABSStrings.STRONGER ) );
                weakStrengthControl.setTickLabels( strengthLabelTable );
                weakStrengthControl.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        fireStateChanged();
                    }
                } );
            }
            
            // layout with inner panel
            {
                setBorder( new EtchedBorder() );
                EasyGridBagLayout layout = new EasyGridBagLayout( this );
                setLayout( layout );
                int row = 0;
                int column = 0;
                layout.addComponent( strengthLabel, row++, column, 2, 1 );
                layout.addComponent( weakRadioButton, row, column++, 1, 1 );
                layout.addComponent( strongRadioButton, row++, column, 1, 1 );
                column = 0;
                layout.addComponent( weakStrengthControl, row, column++, 2, 1 );
                // add struts to maintain panel size when weakStrengthControl is made invisible
                layout.addComponent( Box.createVerticalStrut( weakStrengthControl.getPreferredSize().height ), row++, column );
                column = 0;
                layout.addComponent( Box.createHorizontalStrut( weakStrengthControl.getPreferredSize().width ), row, column, 2, 1 );
            }
        }
        
        public void setWeakSelected( boolean selected ) {
            weakRadioButton.setSelected( selected );
            weakStrengthControl.setVisible( selected );
        }
        
        public boolean isWeakSelected() {
            return weakRadioButton.isSelected();
        }
        
        /*
         * HACK ALERT:
         * It's a little odd to just ignore strength values that are out of range.
         * But the strength slider only applies to weak acids/bases, and will only
         * be visible for weak acids/bases. And its convenient to be able to call
         * this for any solution, and simply have it be ignored for strong acids/bases.
         */
        public void setStrength( double strength ) {
            if ( ABSConstants.WEAK_STRENGTH_RANGE.contains( strength ) ) {
                weakStrengthControl.setValue( strength );
            }
        }
        
        public double getStrength() {
            return weakStrengthControl.getValue();
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
