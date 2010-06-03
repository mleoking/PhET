package edu.colorado.phet.acidbasesolutions.controls;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.constants.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class CustomSolutionControl extends JPanel {
    
    public CustomSolutionControl() {
        TitledBorder titledBorder = new TitledBorder( ABSStrings.SOLUTION );
        titledBorder.setTitleFont( ABSConstants.TITLED_BORDER_FONT );
        setBorder( titledBorder );
        
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
    
    public static class TypePanel extends JPanel {
        
        private final JRadioButton acidRadioButton, baseRadioButton;
        
        public TypePanel() { 
            
            acidRadioButton = new JRadioButton( ABSStrings.ACID );
            acidRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            });
            baseRadioButton = new JRadioButton( ABSStrings.BASE );
            baseRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            });
            ButtonGroup typeButtonGroup = new ButtonGroup();
            typeButtonGroup.add( acidRadioButton );
            typeButtonGroup.add( baseRadioButton );
            
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
    
    public static class ConcentrationPanel extends JPanel {
        
        private final LogarithmicValueControl concentrationControl;
        
        public ConcentrationPanel() {

            double min = ABSConstants.CONCENTRATION_RANGE.getMin();
            double max = ABSConstants.CONCENTRATION_RANGE.getMax();
            String label = ABSStrings.CONCENTRATION;
            String textFieldPattern = "0.000";
            String units = ABSStrings.MOLAR;
            concentrationControl = new LogarithmicValueControl( min, max, label, textFieldPattern, units );
            // label at each 10x interval
            Hashtable<Double, JLabel> concentrationLabelTable = new Hashtable<Double, JLabel>();
            for ( double c = concentrationControl.getMinimum(); c <= concentrationControl.getMaximum(); c *= 10 ) {
                concentrationLabelTable.put( new Double( c ), new JLabel( String.valueOf( c ) ) );
            }
            concentrationControl.setTickLabels( concentrationLabelTable );
            concentrationControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    //XXX update model
                }
            });
            
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
    
    public static class StrengthPanel extends JPanel {
        
        private final JLabel strengthLabel;
        private final JRadioButton weakRadioButton, strongRadioButton;
        private final LogarithmicValueControl weakStrengthControl;
        
        public StrengthPanel() {
            
            // dynamic strength label
            strengthLabel = new JLabel( ABSStrings.PATTERN_STRENGTH_STRONG ); //XXX
            
            // choice of weak or strong
            weakRadioButton = new JRadioButton( ABSStrings.WEAK );
            weakRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            });
            strongRadioButton = new JRadioButton( ABSStrings.STRONG );
            strongRadioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //XXX update model
                }
            });
            ButtonGroup strengthButtonGroup = new ButtonGroup();
            strengthButtonGroup.add( weakRadioButton );
            strengthButtonGroup.add( strongRadioButton );
            
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
            });
            
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
        }
    }
    
    /**
     * Value control layout that has only the slider.
     * The label, text field and units are omitted.
     */
    public static class SliderLayoutStrategy implements ILayoutStrategy {

        public SliderLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {

            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();

            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addFilledComponent( slider, 1, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}
