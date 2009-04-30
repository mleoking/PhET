package edu.colorado.phet.acidbasesolutions.test;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode.*;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;


public class TestReactionEquations extends JFrame {
    
    private static final int DEFAULT_SCALE = 100; // percent
    private static final int MAX_SCALE = 200; // percent
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 525, 175 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final PhetPCanvas topCanvas;
    private AbstractReactionEquationNode topEquation;
    private final AbstractReactionEquationNode bottomEquation;
    private final ScaleSlider[] topSliders, bottomSliders;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final EquationComboBox equationComboBox;
    
    public TestReactionEquations() {
        super( "Reaction Equations" );
        setResizable( false );
        
        // equation combo box
        equationComboBox = new EquationComboBox();
        equationComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTopEquation();
            }
        });
        
        topCanvas = new PhetPCanvas();
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topEquation = equationComboBox.getSelectedNode();
        topCanvas.getLayer().addChild( topEquation );
        
        PhetPCanvas bottomCanvas = new PhetPCanvas();
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomEquation = new WaterReactionEquationNode();
        bottomEquation.setOffset( 50, 50 );
        bottomCanvas.getLayer().addChild( bottomEquation );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( "<html>Symbol sizes change<br>with concentration:" );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateScale();
            }
        };
        scaleOnRadioButton = new JRadioButton( "on" );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( "off" );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOnRadioButton.setSelected( true );
        JPanel scaleOnOffPanel = new JPanel();
        scaleOnOffPanel.setLayout( new BoxLayout( scaleOnOffPanel, BoxLayout.X_AXIS ) );
        scaleOnOffPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        scaleOnOffPanel.add( scaleOnOffLabel );
        scaleOnOffPanel.add( scaleOnRadioButton );
        scaleOnOffPanel.add( scaleOffRadioButton );
        
        // scale sliders for top equations
        ChangeListener topChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( topEquation != null & e.getSource() instanceof ScaleSlider && isScaleEnabled() ) {
                    ScaleSlider slider = (ScaleSlider) e.getSource();
                    topEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                }
            }
        };
        JPanel topSliderPanel = new JPanel();
        topSliderPanel.setLayout( new BoxLayout( topSliderPanel, BoxLayout.Y_AXIS ) );
        topSliderPanel.setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
        topSliders = new ScaleSlider[ topEquation.getNumberOfTerms() ];
        for ( int i = 0; i < topSliders.length; i++ ) {
            ScaleSlider slider = new ScaleSlider( i );
            slider.addChangeListener( topChangeListener );
            topSliders[i] = slider;
            topSliderPanel.add( slider );
        }
        
        // scale sliders for bottom equation
        ChangeListener bottomChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( bottomEquation != null & e.getSource() instanceof ScaleSlider && isScaleEnabled() ) {
                    ScaleSlider slider = (ScaleSlider) e.getSource();
                    bottomEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                }
            }
        };
        JPanel bottomSliderPanel = new JPanel();
        bottomSliderPanel.setLayout( new BoxLayout( bottomSliderPanel, BoxLayout.Y_AXIS ) );
        bottomSliderPanel.setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
        bottomSliders = new ScaleSlider[ bottomEquation.getNumberOfTerms() ];
        for ( int i = 0; i < bottomSliders.length; i++ ) {
            ScaleSlider slider = new ScaleSlider( i );
            slider.addChangeListener( bottomChangeListener );
            bottomSliders[i] = slider;
            bottomSliderPanel.add( slider );
        }
        
        // main panel
        JPanel panel = new JPanel( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0; // column
        constraints.gridy = GridBagConstraints.RELATIVE; // row
        panel.add( scaleOnOffPanel, constraints );
        panel.add( topCanvas, constraints );
        panel.add( bottomCanvas, constraints );
        constraints.gridx = 1; // column
        panel.add( equationComboBox, constraints );
        panel.add( topSliderPanel, constraints );
        panel.add( bottomSliderPanel, constraints );
            
        updateTopEquation();
        setContentPane( panel );
        pack();
    }
    
    private boolean isScaleEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void updateScale() {
        updateScale( topEquation, topSliders );
        updateScale( bottomEquation, bottomSliders );
    }
    
    private void updateScale( AbstractReactionEquationNode equation, ScaleSlider[] sliders ) {
        for ( int i = 0; i < sliders.length; i++ ) {
            ScaleSlider slider = sliders[i];
            slider.setEnabled( isScaleEnabled() );
            double scale = ( isScaleEnabled() ? slider.getValue() / 100.0 : 1.0 );
            equation.setTermScale( i, scale );
        }
        
    }
    
    private void updateTopEquation() {
        if ( topEquation != null ) {
            topCanvas.getLayer().removeChild( topEquation );
        }
        topEquation = equationComboBox.getSelectedNode();
        topCanvas.getLayer().addChild( topEquation );
        topEquation.setOffset( 50, 50 );
        updateScale( topEquation, topSliders );
    }
    
    private static class EquationChoice {

        private final String name;
        private final AbstractReactionEquationNode node;
        
        public EquationChoice( String name, AbstractReactionEquationNode node ) {
            this.name = name;
            this.node = node;
        }
        
        public String toString() {
            return name;
        }
        
        public AbstractReactionEquationNode getNode() {
            return node;
        }
    }
    
    private static class EquationComboBox extends JComboBox {
        
        public EquationComboBox() {
            addItem( new EquationChoice( "weak acid", new WeakAcidReactionEquationNode() ) );
            addItem( new EquationChoice( "strong acid", new StrongAcidReactionEquationNode() ) );
            addItem( new EquationChoice( "weak base", new WeakBaseReactionEquationNode() ) );
            addItem( new EquationChoice( "strong base", new StrongBaseReactionEquationNode() ) );
            addItem( new EquationChoice( "chlorous acid", new WeakAcidReactionEquationNode( "HClO<sub>2</sub>", "ClO<sub>2</sub><sup>-</sup>" ) ) );
            addItem( new EquationChoice( "hydrochloric acid", new StrongAcidReactionEquationNode( "HCl", "Cl<sup>-</sup>" ) ) );
            addItem( new EquationChoice( "ammonia", new WeakBaseReactionEquationNode( "NH<sub>3</sub>", "NH<sub>4</sub><sup>+</sup>" ) ) );
            addItem( new EquationChoice( "sodium hydroxide", new StrongBaseReactionEquationNode( "NaOH", "Na<sup>+</sup>" ) ) );
        }
        
        public AbstractReactionEquationNode getSelectedNode() {
            return ((EquationChoice) getSelectedItem()).getNode();
        }
    }
    
    private static class HorizontalLayoutStrategy implements ILayoutStrategy {

        public HorizontalLayoutStrategy() {}
        
        public void doLayout( AbstractValueControl valueControl ) {

            // Get the components that will be part of the layout
            JComponent slider = valueControl.getSlider();
            JComponent textField = valueControl.getTextField();
            JComponent valueLabel = valueControl.getValueLabel();
            JComponent unitsLabel = valueControl.getUnitsLabel();

            // Label - slider - textfield - units.
            EasyGridBagLayout layout = new EasyGridBagLayout( valueControl );
            valueControl.setLayout( layout );
            layout.addComponent( valueLabel, 0, 0 );
            layout.addFilledComponent( slider, 0, 1, GridBagConstraints.HORIZONTAL );
            layout.addFilledComponent( textField, 0, 2, GridBagConstraints.HORIZONTAL );
            layout.addComponent( unitsLabel, 0, 3 );
        }
    }
    
    private static class ScaleSlider extends LinearValueControl {

        private final int termIndex;

        public ScaleSlider( int termIndex ) {
            super( 1, MAX_SCALE, "Term " + (termIndex+1) + ":", "##0", "%", new HorizontalLayoutStrategy() );
            this.termIndex = termIndex;
            setValue( DEFAULT_SCALE );
            getSlider().setPaintTicks( false );
            getSlider().setPaintLabels( false );
        }

        public int getTermIndex() {
            return termIndex;
        }
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestReactionEquations();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
