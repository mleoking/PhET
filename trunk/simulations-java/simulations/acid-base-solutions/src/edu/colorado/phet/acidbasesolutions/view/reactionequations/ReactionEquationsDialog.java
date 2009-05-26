package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.ReactionEquationNode.*;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;


public class ReactionEquationsDialog extends PaintImmediateDialog {
    
    private static final int DEFAULT_SCALE = 100; // percent
    private static final int MAX_SCALE = 500; // percent
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 600, 175 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final PhetPCanvas topCanvas, bottomCanvas;
    private ReactionEquationNode topEquation;
    private final ReactionEquationNode bottomEquation;
    
    // developer controls
    private EquationComboBox equationComboBox;
    private GlobalScaleSlider globalScaleSlider;
    private TermScaleSlider[] topSliders, bottomSliders;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PText topCanvasSizeNode, bottomCanvasSizeNode;
    
    public ReactionEquationsDialog( Frame owner ) {
        this( owner, true ); //XXX default should be dev=false
    }
    
    public ReactionEquationsDialog( Frame owner, boolean dev ) {
        super( owner, ABSStrings.TITLE_REACTION_EQUATIONS );
        setResizable( dev ); // resizable only if in dev mode
        
        JComponent devControls = createDevControls();
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateScale();
            }
        };
        scaleOnRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
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
        topCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
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
        bottomCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
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
            mainPanel.add( devControls, BorderLayout.EAST );
        }
            
        updateTopEquation();
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private JComponent createDevControls() {
        
        // global scale slider
        globalScaleSlider = new GlobalScaleSlider( "global scale:" );
        globalScaleSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateGlobalScale();
            }
        });
        
        // top equations controls
        JPanel topControls = new JPanel();
        {
            topControls.setLayout( new BoxLayout( topControls, BoxLayout.Y_AXIS ) );
            topControls.setBorder( new TitledBorder( "top equation" ) );
            
            // equation combo box
            equationComboBox = new EquationComboBox();
            topControls.add( equationComboBox );
            equationComboBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    updateTopEquation();
                }
            } );
            
            // sliders
            ChangeListener topChangeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( topEquation != null & e.getSource() instanceof TermScaleSlider && isScaleEnabled() ) {
                        TermScaleSlider slider = (TermScaleSlider) e.getSource();
                        topEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                    }
                }
            };
            topSliders = new TermScaleSlider[4];
            for ( int i = 0; i < topSliders.length; i++ ) {
                TermScaleSlider slider = new TermScaleSlider( i );
                slider.addChangeListener( topChangeListener );
                topSliders[i] = slider;
                topControls.add( slider );
            }
        }

        // bottom equation controls
        JPanel bottomControls = new JPanel();
        {
            bottomControls.setLayout( new BoxLayout( bottomControls, BoxLayout.Y_AXIS ) );
            bottomControls.setBorder( new TitledBorder( "bottom equation" ) );
            
            // sliders
            ChangeListener bottomChangeListener = new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( bottomEquation != null & e.getSource() instanceof TermScaleSlider && isScaleEnabled() ) {
                        TermScaleSlider slider = (TermScaleSlider) e.getSource();
                        bottomEquation.setTermScale( slider.getTermIndex(), slider.getValue() / 100.0 );
                    }
                }
            };
            bottomSliders = new TermScaleSlider[4];
            for ( int i = 0; i < bottomSliders.length; i++ ) {
                TermScaleSlider slider = new TermScaleSlider( i );
                slider.addChangeListener( bottomChangeListener );
                bottomSliders[i] = slider;
                bottomControls.add( slider );
            }
        }
        
        // layout
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        TitledBorder border = new TitledBorder( "Developer Controls" );
        border.setTitleColor( Color.RED );
        border.setBorder( new LineBorder( Color.RED, 2 ) );
        panel.setBorder( border );
        panel.add( globalScaleSlider );
        panel.add( topControls );
        panel.add( bottomControls );
        JScrollPane scrollPane = new JScrollPane( panel );
        
        return scrollPane;
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
    
    private void updateGlobalScale() {
        topEquation.setScale( globalScaleSlider.getValue() / 100.0 );
        updateTopLayout();
        bottomEquation.setScale( globalScaleSlider.getValue() / 100.0 );
        updateBottomLayout();
    }
    
    private void updateScale() {
        updateScale( topEquation, topSliders );
        updateScale( bottomEquation, bottomSliders );
    }
    
    private void updateScale( ReactionEquationNode equation, TermScaleSlider[] sliders ) {
        for ( int i = 0; i < sliders.length; i++ ) {
            TermScaleSlider slider = sliders[i];
            slider.setEnabled( isScaleEnabled() );
            double scale = ( isScaleEnabled() ? slider.getValue() / 100.0 : 1.0 );
            equation.setTermScale( i, scale );
        }
        // disabled term1 slider for strong bases
        sliders[0].setEnabled( !( equation instanceof StrongBaseReactionEquationNode ) );
    }
    
    private void updateTopEquation() {
        // clean up existing top equation
        double scale = 1.0;
        if ( topEquation != null ) {
            scale = topEquation.getScale();
            topCanvas.getLayer().removeChild( topEquation );
        }
        // set the new equation
        topEquation = equationComboBox.getSelectedNode();
        topEquation.setScale( scale );
        topEquation.setAllTermScale( 1 );
        topCanvas.getLayer().addChild( topEquation );
        // update layouts
        updateTopLayout();
        updateScale( topEquation, topSliders );
    }
    
    private static class EquationChoice {

        private final String name;
        private final ReactionEquationNode node;
        
        public EquationChoice( String name, ReactionEquationNode node ) {
            this.name = name;
            this.node = node;
        }
        
        public String toString() {
            return name;
        }
        
        public ReactionEquationNode getNode() {
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
        
        public ReactionEquationNode getSelectedNode() {
            return ((EquationChoice) getSelectedItem()).getNode();
        }
    }
    
    private static class GlobalScaleSlider extends LinearValueControl {

        public GlobalScaleSlider( String label ) {
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
        JDialog dialog = new ReactionEquationsDialog( null, true );
        dialog.setVisible( true );
    }
}
