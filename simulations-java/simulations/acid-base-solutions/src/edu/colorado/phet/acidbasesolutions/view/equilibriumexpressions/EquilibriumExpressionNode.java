package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;


public class EquilibriumExpressionNode extends AbstractEquilibriumExpressionNode {

    public EquilibriumExpressionNode( AqueousSolution solution ) {
        super();
        solution.addSolutionListener( new ModelViewController( solution, this ) );
    }
    
    private static class ModelViewController implements SolutionListener {
        
        private final AqueousSolution solution;
        private final EquilibriumExpressionNode expressionNode;
        
        public ModelViewController( AqueousSolution solution, EquilibriumExpressionNode expressionNode ) {
            this.solution = solution;
            this.expressionNode = expressionNode;
            updateView();
        }
        
        public void soluteChanged() {
            updateView();
        }
        
        public void concentrationChanged() {
            updateView();
        }

        public void strengthChanged() {
            updateView();
        }
        
        private void updateView() {
            Solute solute = solution.getSolute();
            
            expressionNode.setVisible( !solution.isPureWater() );
            
            // labels and colors
            if ( solute instanceof Acid ) {
                // Ka = [H3O+][A-] / [HA] = value
                expressionNode.setKLabel( solute.getStrengthSymbol() );
                expressionNode.setLeftNumeratorProperties( ABSSymbols.H3O_PLUS, ABSConstants.H3O_COLOR );
                expressionNode.setRightNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
                expressionNode.setDenominatorProperties( solute.getSymbol(), solute.getColor() );
            }
            else if ( solute instanceof Base ) {
                // Kb = [BH+][OH-] / [B] = value
                expressionNode.setKLabel( solute.getStrengthSymbol() );
                expressionNode.setLeftNumeratorProperties( solute.getConjugateSymbol(), solute.getConjugateColor() );
                expressionNode.setRightNumeratorProperties( ABSSymbols.OH_MINUS, ABSConstants.OH_COLOR );
                expressionNode.setDenominatorProperties( solute.getSymbol(), solute.getColor() );
            }
            
            // "Large" value
            expressionNode.setLargeValueVisible( solute.isStrong() );
            
            // set scale
        }
    }
}
