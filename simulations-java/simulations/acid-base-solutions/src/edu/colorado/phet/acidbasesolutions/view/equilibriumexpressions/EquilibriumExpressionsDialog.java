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
    private final EquilibriumExpressionNode soluteNode;
    private final EquilibriumExpressionNode waterNode;
    private ExpressionUpdateStrategy soluteUpdateStrategy;
    private final WaterExpressionUpdateStrategy waterUpdateStrategy;
    
    public EquilibriumExpressionsDialog( Frame owner, final AqueousSolution solution ) {
        super( owner );
        setTitle( ABSStrings.TITLE_EQUILIBRIUM_EXPRESSIONS );
        setResizable( false );
        
        waterUpdateStrategy = new WaterExpressionUpdateStrategy();
        this.solution = solution;
        
        this.solutionListener = new SolutionListener() {
            public void soluteChanged() {
                handleSoluteChanged();
            }

            public void concentrationChanged() {
                soluteUpdateStrategy.update( soluteNode, solution );
                waterUpdateStrategy.update( waterNode, solution );
            }

            public void strengthChanged() {
                soluteUpdateStrategy.update( soluteNode, solution );
                waterUpdateStrategy.update( waterNode, solution );
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
        
        // solute expression
        soluteNode = new EquilibriumExpressionNode( true );
        topCanvas.getLayer().addChild( soluteNode );
        
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
        waterNode = new EquilibriumExpressionNode( false );
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
        soluteNode.setVisible( !solution.isPureWater() );
        soluteUpdateStrategy = ExpressionUpdateStrategyFactory.getUpdateStrategy( solution );
        if ( soluteUpdateStrategy != null ) {
            soluteUpdateStrategy.update( soluteNode, solution );
            updateTopLayout();
        }
        waterUpdateStrategy.update( waterNode, solution );
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        soluteNode.setScalingEnabled( enabled );
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
        final boolean isScalingEnabled = node.isScalingEnabled();
        node.setScalingEnabled( false ); // do the layout with scaling off
        double xOffset = ( canvas.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = ( ( canvas.getHeight() - node.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( node );
        node.setOffset( xOffset, yOffset );
        node.setScalingEnabled( isScalingEnabled ); // restore scaling
    }
    
    private static class ExpressionUpdateStrategyFactory {

        private ExpressionUpdateStrategyFactory() {}

        public static ExpressionUpdateStrategy getUpdateStrategy( AqueousSolution solution ) {
            ExpressionUpdateStrategy strategy = null;
            if ( solution.isAcidic() ) {
                strategy = new AcidExpressionUpdateStrategy();
            }
            else if ( solution.isBasic() ) {
                strategy = new BaseExpressionUpdateStrategy();
            }
            return strategy;
        }
    }
    
    private interface ExpressionUpdateStrategy {
        public void update( EquilibriumExpressionNode node, AqueousSolution solution );
    }
    
    private static class AcidExpressionUpdateStrategy implements ExpressionUpdateStrategy {

        public void update( EquilibriumExpressionNode node, AqueousSolution solution ) {
            assert( solution.isAcidic() );
            
            Solute solute = solution.getSolute();

            // symbols and colors
            node.setKLabel( ABSSymbols.Ka );
            node.setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            node.setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            node.setDenominatorProperties( solute.getSymbol(), solute.getColor() );

            // K value
            node.setKValue( solute.getStrength() );
            node.setLargeValueVisible( solute.isStrong() );

            // concentration scaling
            node.scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
            node.scaleRightNumeratorToConcentration( solution.getProductConcentration() );
            node.scaleDenominatorToConcentration( solution.getReactantConcentration() );
        }
    }
    
    private static class BaseExpressionUpdateStrategy implements ExpressionUpdateStrategy {

        public void update( EquilibriumExpressionNode node, AqueousSolution solution ) {
            assert( solution.isBasic() );
            
            Solute solute = solution.getSolute();

            // symbols and colors
            node.setKLabel( ABSSymbols.Kb );
            node.setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
            node.setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            node.setDenominatorProperties( solute.getSymbol(), solute.getColor() );

            // K value
            node.setKValue( solute.getStrength() );
            node.setLargeValueVisible( solute.isStrong() );

            // concentration scaling
            node.scaleLeftNumeratorToConcentration( solution.getProductConcentration() );
            node.scaleRightNumeratorToConcentration( solution.getOHConcentration() );
            node.scaleDenominatorToConcentration( solution.getReactantConcentration() );
        }
    }
    
    private static class WaterExpressionUpdateStrategy implements ExpressionUpdateStrategy {

        public void update( EquilibriumExpressionNode node, AqueousSolution solution ) {
           
            // symbols and colors
            node.setKLabel( ABSSymbols.Kw );
            node.setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
            node.setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
            
            // K value
            node.setKValue( Water.getEquilibriumConstant() );
            
            // concentration scaling
            node.scaleLeftNumeratorToConcentration( solution.getH3OConcentration() );
            node.scaleRightNumeratorToConcentration( solution.getOHConcentration() );
        }
    }
}
