package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode.*;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;


public class ReactionEquationsDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Reaction Equations";
    private static final String ON = "on";
    private static final String OFF = "off";
    private static final String SYMBOL_SIZES_CHANGE = "<html>Symbol sizes change<br>with concentration:";
    
    private static final int DEFAULT_SCALE = 100; // percent
    private static final int MAX_SCALE = 500; // percent
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 600, 175 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final PhetPCanvas topCanvas, bottomCanvas;
    private AbstractReactionEquationNode topEquation;
    private final AbstractReactionEquationNode bottomEquation;
    
    // developer controls
    private EquationComboBox equationComboBox;
    private BaseScaleSlider baseScaleSlider;
    private TermScaleSlider[] topSliders, bottomSliders;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PText topCanvasSizeNode, bottomCanvasSizeNode;
    
    public ReactionEquationsDialog( Frame owner ) {
        this( owner, true ); //XXX default should be dev=false
    }
    
    public ReactionEquationsDialog( Frame owner, boolean dev ) {
        super( owner, TITLE );
        setResizable( dev ); // resizable only if in dev mode
        
        JPanel devControlPanel = createDevControlPanel();
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( SYMBOL_SIZES_CHANGE );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateScale();
            }
        };
        scaleOnRadioButton = new JRadioButton( ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( OFF );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOnRadioButton.setSelected( true );
        JPanel scaleOnOffPanel = new JPanel( new GridBagLayout() );
        scaleOnOffPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = GridBagConstraints.RELATIVE;
        scaleOnOffPanel.add( scaleOnOffLabel );
        scaleOnOffPanel.add( scaleOnRadioButton );
        scaleOnOffPanel.add( scaleOffRadioButton );
        
        // top canvas
        topCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                updateTopLayout();
            }
        };
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topCanvasSizeNode = new PText();
        topCanvasSizeNode.setOffset( 5, 5 );
        if ( dev ) {
            topCanvas.getLayer().addChild( topCanvasSizeNode );
        }
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomEquation = new WaterReactionEquationNode();
        bottomCanvas.getLayer().addChild( bottomEquation );
        bottomCanvasSizeNode = new PText();
        bottomCanvasSizeNode.setOffset( 5, 5 );
        if ( dev ) {
            bottomCanvas.getLayer().addChild( bottomCanvasSizeNode );
        }
        
        // layout
        JPanel canvasPanel = new JPanel( new GridLayout( 0, 1 ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
        if ( dev ) {
            mainPanel.add( devControlPanel, BorderLayout.EAST );
        }
            
        updateTopEquation();
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private JPanel createDevControlPanel() {
        
        // equation combo box
        equationComboBox = new EquationComboBox();
        equationComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTopEquation();
            }
        });
        
        // base scale slider
        baseScaleSlider = new BaseScaleSlider( "base:" );
        baseScaleSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateNominalScale();
            }
        });
        
        // scale sliders for top equations
        ChangeListener topChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( topEquation != null & e.getSource() instanceof TermScaleSlider && isScaleEnabled() ) {
                    TermScaleSlider slider = (TermScaleSlider) e.getSource();
                    topEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                }
            }
        };
        JPanel topSliderPanel = new JPanel();
        topSliderPanel.setLayout( new BoxLayout( topSliderPanel, BoxLayout.Y_AXIS ) );
        topSliderPanel.setBorder( new TitledBorder( "top equation" ) );
        topSliders = new TermScaleSlider[ 4 ];
        for ( int i = 0; i < topSliders.length; i++ ) {
            TermScaleSlider slider = new TermScaleSlider( i );
            slider.addChangeListener( topChangeListener );
            topSliders[i] = slider;
            topSliderPanel.add( slider );
        }
        
        // scale sliders for bottom equation
        ChangeListener bottomChangeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( bottomEquation != null & e.getSource() instanceof TermScaleSlider && isScaleEnabled() ) {
                    TermScaleSlider slider = (TermScaleSlider) e.getSource();
                    bottomEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                }
            }
        };
        JPanel bottomSliderPanel = new JPanel();
        bottomSliderPanel.setLayout( new BoxLayout( bottomSliderPanel, BoxLayout.Y_AXIS ) );
        bottomSliderPanel.setBorder( new TitledBorder( "bottom equation" ) );
        bottomSliders = new TermScaleSlider[ 4 ];
        for ( int i = 0; i < bottomSliders.length; i++ ) {
            TermScaleSlider slider = new TermScaleSlider( i );
            slider.addChangeListener( bottomChangeListener );
            bottomSliders[i] = slider;
            bottomSliderPanel.add( slider );
        }
        
        // layout
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setBorder( new TitledBorder( "Developer Controls" ) );
        panel.add( equationComboBox );
        panel.add( baseScaleSlider );
        panel.add( topSliderPanel );
        panel.add( bottomSliderPanel );
        
        return panel;
    }
    
    private void updateTopLayout() {
        double xOffset = ( topCanvas.getWidth() - topEquation.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( topCanvas.getHeight() - topEquation.getFullBoundsReference().getHeight() ) / 2;
        topEquation.setOffset( xOffset, yOffset );
        Dimension canvasSize = topCanvas.getSize();
        topCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private void updateBottomLayout() {
        double xOffset = ( bottomCanvas.getWidth() - bottomEquation.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( bottomCanvas.getHeight() - bottomEquation.getFullBoundsReference().getHeight() ) / 2;
        bottomEquation.setOffset( xOffset, yOffset );
        Dimension canvasSize = bottomCanvas.getSize();
        bottomCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private boolean isScaleEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void updateNominalScale() {
        topEquation.setScale( baseScaleSlider.getValue() / 100.0 );
        updateTopLayout();
        bottomEquation.setScale( baseScaleSlider.getValue() / 100.0 );
        updateBottomLayout();
    }
    
    private void updateScale() {
        updateScale( topEquation, topSliders );
        updateScale( bottomEquation, bottomSliders );
    }
    
    private void updateScale( AbstractReactionEquationNode equation, TermScaleSlider[] sliders ) {
        for ( int i = 0; i < sliders.length; i++ ) {
            TermScaleSlider slider = sliders[i];
            slider.setEnabled( isScaleEnabled() );
            double scale = ( isScaleEnabled() ? slider.getValue() / 100.0 : 1.0 );
            equation.setTermScale( i, scale );
        }
        
    }
    
    private void updateTopEquation() {
        double scale = 1.0;
        if ( topEquation != null ) {
            scale = topEquation.getScale();
            topCanvas.getLayer().removeChild( topEquation );
        }
        topEquation = equationComboBox.getSelectedNode();
        topEquation.setScale( scale );
        topEquation.setAllTermScale( 1 );
        topCanvas.getLayer().addChild( topEquation );
        updateTopLayout();
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
            addItem( new EquationChoice( "acetic acid", new WeakAcidReactionEquationNode( "CH<sub>3</sub>COOH", "CH<sub>3</sub>COO<sup>-</sup>" ) ) );
            addItem( new EquationChoice( "hydrochloric acid", new StrongAcidReactionEquationNode( "HCl", "Cl<sup>-</sup>" ) ) );
            addItem( new EquationChoice( "pyridine", new WeakBaseReactionEquationNode( "C<sub>5</sub>H<sub>5</sub>N", "C<sub>5</sub>H<sub>5</sub>NH<sup>+</sup>" ) ) );
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
    
    private static class BaseScaleSlider extends LinearValueControl {

        public BaseScaleSlider( String label ) {
            super( 50, 200, label, "##0", "%", new HorizontalLayoutStrategy() );
            setValue( DEFAULT_SCALE );
            getSlider().setPaintTicks( false );
            getSlider().setPaintLabels( false );
        }
    }
    
    private static class TermScaleSlider extends LinearValueControl {

        private final int termIndex;

        public TermScaleSlider( int termIndex ) {
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
        JDialog frame = new ReactionEquationsDialog( null, true );
        frame.setVisible( true );
    }
}
