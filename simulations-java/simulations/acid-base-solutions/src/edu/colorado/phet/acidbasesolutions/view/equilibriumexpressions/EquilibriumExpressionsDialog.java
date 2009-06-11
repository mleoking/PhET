package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.Water;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PCanvas;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 500, 130 );
    private static final Dimension BOTTOM_CANVAS_SIZE = new Dimension( TOP_CANVAS_SIZE.width, 100 );
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PhetPCanvas topCanvas, bottomCanvas;
    private EquilibriumExpressionNode soluteNode;
    private final EquilibriumExpressionNode waterNode;
    
    public EquilibriumExpressionsDialog( Frame owner, final AqueousSolution solution ) {
        super( owner );
        setTitle( ABSStrings.TITLE_EQUILIBRIUM_EXPRESSIONS );
        setResizable( false );
        
        this.solution = solution;
        
        this.solutionListener = new SolutionListener() {
            
            public void soluteChanged() {
                handleSoluteChanged();
            }

            public void concentrationChanged() {
                handleConcentrationOrStrengthChanged();
            }

            public void strengthChanged() {
                handleConcentrationOrStrengthChanged();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setScalingEnabled( scaleOnRadioButton.isSelected() );
            }
        };
        scaleOnRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOffRadioButton.setSelected( true );
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
                super.updateLayout();
                updateTopLayout();
            }
        };
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topCanvas.setBackground( ABSConstants.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        
        // solute expression, will be set based on solution
        soluteNode = null;
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomCanvas.setBackground( ABSConstants.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        
        // water expression
        waterNode = new WaterEquilibriumExpressionNode( solution );
        bottomCanvas.getLayer().addChild( waterNode );
        
        // layout
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout( new BoxLayout( canvasPanel, BoxLayout.Y_AXIS ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
        
        handleSoluteChanged();
            
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private void cleanup() {
        solution.removeSolutionListener( solutionListener );
    }
    
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    private void handleSoluteChanged() {
        
        // remove any existing solute expression
        if ( soluteNode != null ) {
            topCanvas.getLayer().removeChild( soluteNode );
            soluteNode = null;
        }
        
        // create the proper type of solute expression
        if ( solution.isAcidic() ) {
            soluteNode = new AcidEquilibriumExpressionNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteNode = new BaseEquilibriumExpressionNode( solution );
        }
        
        // add the new solute expression
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( isScalingEnabled() );
            topCanvas.getLayer().addChild( soluteNode );
            updateTopLayout();
        }
        
        // update the water expression
        waterNode.update();
    }
    
    private void handleConcentrationOrStrengthChanged() {
        if ( soluteNode != null ) {
            soluteNode.update();
        }
        waterNode.update();
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( enabled );
        }
        waterNode.setScalingEnabled( enabled );
    }
    
    public boolean isScalingEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void updateTopLayout() {
        centerExpression( soluteNode, topCanvas );
    }
    
    private void updateBottomLayout() {
        centerExpression( waterNode, bottomCanvas );
    }
    
    private static void centerExpression( EquilibriumExpressionNode node, PCanvas canvas ) {
        if ( node != null ) {
            final boolean isScalingEnabled = node.isScalingEnabled();
            node.setScalingEnabled( false ); // do the layout with scaling off
            double xOffset = ( canvas.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = ( ( canvas.getHeight() - node.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( node );
            node.setOffset( xOffset, yOffset );
            node.setScalingEnabled( isScalingEnabled ); // restore scaling
        }
    }
    
    private static class AcidEquilibriumExpressionNode extends EquilibriumExpressionNode {
        
        private final AqueousSolution solution;
        
        public AcidEquilibriumExpressionNode( AqueousSolution solution ) {
            super( true );
            assert( solution.isAcidic() );
            this.solution = solution;
            Solute solute = solution.getSolute();
            setKLabel( ABSSymbols.Ka );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            setDenominatorProperties( solute.getSymbol(), solute.getColor() );
            update();
        }

        public void update() {
            assert( solution.isAcidic() );
            
            // K value
            Solute solute = solution.getSolute();
            setKValue( solute.getStrength() );
            setLargeValueVisible( solute.isStrong() );

            // concentration scaling
            scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
            scaleRightNumeratorToConcentration( solution.getProductConcentration() );
            scaleDenominatorToConcentration( solution.getReactantConcentration() );
        }
    }
    
    private static class BaseEquilibriumExpressionNode extends EquilibriumExpressionNode {
        
        private final AqueousSolution solution;
        
        public BaseEquilibriumExpressionNode( AqueousSolution solution ) {
            super( true );
            assert( solution.isBasic() );
            this.solution = solution;
            Solute solute = solution.getSolute();
            setKLabel( ABSSymbols.Kb );
            setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            setDenominatorProperties( solute.getSymbol(), solute.getColor() );
            update();
        }

        public void update() {
            assert( solution.isBasic() );
            
            Solute solute = solution.getSolute();
            
            // strong vs weak base
            setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            setDenominatorProperties( solute.getSymbol(), solute.getColor() );

            // K value
            setKValue( solute.getStrength() );
            setLargeValueVisible( solute.isStrong() );

            // concentration scaling
            scaleLeftNumeratorToConcentration( solution.getProductConcentration() );
            scaleRightNumeratorToConcentration( solution.getOHConcentration() );
            scaleDenominatorToConcentration( solution.getReactantConcentration() );
        }
    }
    
    private static class WaterEquilibriumExpressionNode extends EquilibriumExpressionNode {
        
        private final AqueousSolution solution;
        
        public WaterEquilibriumExpressionNode( AqueousSolution solution ) {
            super( false );
            this.solution = solution;
            setKLabel( ABSSymbols.Kw );
            setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            update();
        }
        
        public void update() {
           
            // K value
            setKValue( Water.getEquilibriumConstant() );
            
            // concentration scaling
            scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
            scaleRightNumeratorToConcentration( solution.getOHConcentration() );
        }
    }
}
