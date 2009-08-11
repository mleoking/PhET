package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AbstractEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AcidEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.BaseEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.WaterEquilibriumExpressionNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class SolutionsEquilibriumExpressionsNode extends PhetPNode {
    
    private static final double Y_SPACING = 65;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private AbstractEquilibriumExpressionNode soluteEquilibriumExpressionNode;
    private final AbstractEquilibriumExpressionNode waterEquilibriumExpressionNode;
    
    public SolutionsEquilibriumExpressionsNode( final AqueousSolution solution ) {
        super();

        // not interactive
        setPickable( false );
        setChildrenPickable( false );

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
        
        // solute equilibrium expression, will be set based on solution
        soluteEquilibriumExpressionNode = null;
        
        // water equilibrium expression
        waterEquilibriumExpressionNode = new WaterEquilibriumExpressionNode( solution );
        addChild( waterEquilibriumExpressionNode );
        
        handleSoluteChanged();
    }

    private void handleSoluteChanged() {

        // remove any existing solute equation
        if ( soluteEquilibriumExpressionNode != null ) {
            removeChild( soluteEquilibriumExpressionNode );
            soluteEquilibriumExpressionNode = null;
        }

        // create the proper type of solute equations 
        if ( solution.isAcidic() ) {
            soluteEquilibriumExpressionNode = new AcidEquilibriumExpressionNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteEquilibriumExpressionNode = new BaseEquilibriumExpressionNode( solution );
        }

        // add the new solute equations
        if ( soluteEquilibriumExpressionNode != null ) {
            addChild( soluteEquilibriumExpressionNode );
            soluteEquilibriumExpressionNode.setScalingEnabled( waterEquilibriumExpressionNode.isScalingEnabled() );
        }
        
        // update the water equation
        waterEquilibriumExpressionNode.update();
        
        updateLayout();
    }

    private void handleConcentrationOrStrengthChanged() {
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.update();
        }
        waterEquilibriumExpressionNode.update();
    }

    public void setScalingEnabled( boolean enabled ) {
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.setScalingEnabled( enabled );
        }
        waterEquilibriumExpressionNode.setScalingEnabled( enabled );
    }
    
    private void updateLayout() {
        
        // do layout with scaling off
        final boolean scalingWasEnabled = waterEquilibriumExpressionNode.isScalingEnabled();
        setScalingEnabled( false );
        
        double xOffset = 0;
        double yOffset = 0;
        
        // solute equilibrium expression
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.setOffset( xOffset, yOffset );
            yOffset = soluteEquilibriumExpressionNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        }
        
        // water equilibrium expression
        waterEquilibriumExpressionNode.setOffset( xOffset, yOffset );
        yOffset = waterEquilibriumExpressionNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        
        // restore scaling
        setScalingEnabled( scalingWasEnabled );
    }
}
