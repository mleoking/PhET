package edu.colorado.phet.acidbasesolutions.view.equations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.view.equations.EquilibriumExpressionNode.*;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.HorizontalLayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    //TODO localize
    private static final String TITLE = "Equilibrium Expressions";
    private static final String ON = "on";
    private static final String OFF = "off";
    private static final String SYMBOL_SIZES_CHANGE = "<html>Symbol sizes change<br>with concentration:";
    
    private static final int DEFAULT_SCALE = 100; // percent
    private static final int MAX_SCALE = 500; // percent
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 600, 175 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final PhetPCanvas topCanvas, bottomCanvas;
    private EquilibriumExpressionNode topExpression;
    private final EquilibriumExpressionNode bottomExpression;
    
    // developer controls
    private EquationComboBox equationComboBox;
    private GlobalScaleSlider globalScaleSlider;
    private TermScaleSlider topKSlider, topLeftNumeratorSlider, topRightNumeratorSlider, topDenominatorSlider;
    private TermScaleSlider bottomKSlider, bottomLeftNumeratorSlider, bottomRightNumeratorSlider;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PText topCanvasSizeNode, bottomCanvasSizeNode;
    
    public EquilibriumExpressionsDialog( Frame owner ) {
        this( owner, true ); //XXX default should be dev=false
    }
    
    public EquilibriumExpressionsDialog( Frame owner, boolean dev ) {
        super( owner, TITLE );
        setResizable( dev ); // resizable only if in dev mode
        
        JComponent devControls = createDevControls();
        
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
        bottomExpression = new WaterEquilibriumExpressionNode();
        bottomCanvas.getLayer().addChild( bottomExpression );
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
                updateNominalScale();
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
            
            topKSlider = new TermScaleSlider( "K:" );
            topControls.add( topKSlider );
            topKSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    topExpression.setKScale( topKSlider.getValue() / 100.0 );
                }
            });
            
            topLeftNumeratorSlider = new TermScaleSlider( "numerator left:" );
            topControls.add( topLeftNumeratorSlider );
            topLeftNumeratorSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    topExpression.setLeftNumeratorScale( topLeftNumeratorSlider.getValue() / 100.0 );
                }
            });
            
            topRightNumeratorSlider = new TermScaleSlider( "numerator right:" );
            topControls.add( topRightNumeratorSlider );
            topRightNumeratorSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    topExpression.setRightNumeratorScale( topRightNumeratorSlider.getValue() / 100.0 );
                }
            });
            
            topDenominatorSlider = new TermScaleSlider( "denominator" );
            topControls.add( topDenominatorSlider );
            topDenominatorSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    topExpression.setDenominatorScale( topDenominatorSlider.getValue() / 100.0 );
                }
            });
        }

        // bottom equation controls
        JPanel bottomControls = new JPanel();
        {
            bottomControls.setLayout( new BoxLayout( bottomControls, BoxLayout.Y_AXIS ) );
            bottomControls.setBorder( new TitledBorder( "bottom equation" ) );
            
            bottomKSlider = new TermScaleSlider( "K:" );
            bottomControls.add( bottomKSlider );
            bottomKSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    bottomExpression.setKScale( bottomKSlider.getValue() / 100.0 );
                }
            });
            
            bottomLeftNumeratorSlider = new TermScaleSlider( "numerator left:" );
            bottomControls.add( bottomLeftNumeratorSlider );
            bottomLeftNumeratorSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    bottomExpression.setLeftNumeratorScale( bottomLeftNumeratorSlider.getValue() / 100.0 );
                }
            });
            
            bottomRightNumeratorSlider = new TermScaleSlider( "numerator right:" );
            bottomControls.add( bottomRightNumeratorSlider );
            bottomRightNumeratorSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    bottomExpression.setRightNumeratorScale( bottomRightNumeratorSlider.getValue() / 100.0 );
                }
            });
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
        double xOffset = ( topCanvas.getWidth() - topExpression.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( topCanvas.getHeight() - topExpression.getFullBoundsReference().getHeight() ) / 2;
        topExpression.setOffset( xOffset, yOffset );
        Dimension canvasSize = topCanvas.getSize();
        topCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private void updateBottomLayout() {
        double xOffset = ( bottomCanvas.getWidth() - bottomExpression.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( bottomCanvas.getHeight() - bottomExpression.getFullBoundsReference().getHeight() ) / 2;
        bottomExpression.setOffset( xOffset, yOffset );
        Dimension canvasSize = bottomCanvas.getSize();
        bottomCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private void updateNominalScale() {
        topExpression.setScale( globalScaleSlider.getValue() / 100.0 );
        updateTopLayout();
        bottomExpression.setScale( globalScaleSlider.getValue() / 100.0 );
        updateBottomLayout();
    }
    
    private void updateScale() {
        final boolean enabled = scaleOnRadioButton.isSelected();
        topKSlider.setEnabled( enabled );
        topLeftNumeratorSlider.setEnabled( enabled );
        topRightNumeratorSlider.setEnabled( enabled );
        topDenominatorSlider.setEnabled( enabled );
        bottomKSlider.setEnabled( enabled );
        bottomLeftNumeratorSlider.setEnabled( enabled );
        bottomRightNumeratorSlider.setEnabled( enabled );
        if ( enabled ) {
            // top
            topExpression.setKScale( topKSlider.getValue() / 100.0 );
            topExpression.setLeftNumeratorScale( topLeftNumeratorSlider.getValue() / 100.0 );
            topExpression.setRightNumeratorScale( topRightNumeratorSlider.getValue() / 100.0 );
            topExpression.setDenominatorScale( topDenominatorSlider.getValue() / 100.0 );
            // bottom
            bottomExpression.setKScale( bottomKSlider.getValue() / 100.0 );
            bottomExpression.setLeftNumeratorScale( bottomLeftNumeratorSlider.getValue() / 100.0 );
            bottomExpression.setRightNumeratorScale( bottomRightNumeratorSlider.getValue() / 100.0 );
        }
        else {
            topExpression.setScaleAll( 1 );
            bottomExpression.setScaleAll( 1 );
        }
    }
    
    private void updateTopEquation() {
        // clean up existing top equation
        double scale = 1.0;
        if ( topExpression != null ) {
            scale = topExpression.getScale();
            topCanvas.getLayer().removeChild( topExpression );
        }
        // set the new equation
        topExpression = equationComboBox.getSelectedNode();
        topExpression.setScale( scale );
        topExpression.setScaleAll( 1 );
        topCanvas.getLayer().addChild( topExpression );
        // update layouts
        updateTopLayout();
        updateScale();
    }
    
    private static class EquationChoice {

        private final String name;
        private final EquilibriumExpressionNode node;
        
        public EquationChoice( String name, EquilibriumExpressionNode node ) {
            this.name = name;
            this.node = node;
        }
        
        public String toString() {
            return name;
        }
        
        public EquilibriumExpressionNode getNode() {
            return node;
        }
    }
    
    private static class EquationComboBox extends JComboBox {
        
        public EquationComboBox() {
            addItem( new EquationChoice( "weak acid", new WeakAcidEquilibriumExpressionNode() ) );
            addItem( new EquationChoice( "strong acid", new StrongAcidEquilibriumExpressionNode() ) );
            addItem( new EquationChoice( "weak base", new WeakBaseEquilibriumExpressionNode() ) );
            addItem( new EquationChoice( "strong base", new StrongBaseEquilibriumExpressionNode() ) );
            addItem( new EquationChoice( "acetic acid", new WeakAcidEquilibriumExpressionNode( "CH<sub>3</sub>COO<sup>-</sup>", "CH<sub>3</sub>COOH" ) ) );
            addItem( new EquationChoice( "hydrochloric acid", new StrongAcidEquilibriumExpressionNode( "Cl<sup>-</sup>", "HCl" ) ) );
            addItem( new EquationChoice( "pyridine", new WeakBaseEquilibriumExpressionNode( "C<sub>5</sub>H<sub>5</sub>NH<sup>+</sup>", "C<sub>5</sub>H<sub>5</sub>N" ) ) );
            addItem( new EquationChoice( "sodium hydroxide", new StrongBaseEquilibriumExpressionNode( "Na<sup>+</sup>", "NaOH" ) ) );
        }
        
        public EquilibriumExpressionNode getSelectedNode() {
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

        public TermScaleSlider( String label ) {
            super( 1, MAX_SCALE, label, "##0", "%", new HorizontalLayoutStrategy() );
            setValue( DEFAULT_SCALE );
            getSlider().setPaintTicks( false );
            getSlider().setPaintLabels( false );
        }
    }
    
    public static void main( String[] args ) {
        JDialog frame = new EquilibriumExpressionsDialog( null, true );
        frame.setVisible( true );
    }
}
