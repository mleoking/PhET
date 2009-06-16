package edu.colorado.phet.acidbasesolutions.module.comparing;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AbstractEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.AcidEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.BaseEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions.WaterEquilibriumExpressionNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AcidReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.BaseReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.WaterReactionEquationNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class ComparingEquationsNode extends PhetPNode {
    
    private static final double Y_SPACING = 40;
    private static final boolean LEWIS_STRUCTURES_ENABLED = false;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private AbstractReactionEquationNode soluteReactionEquationNode;
    private final WaterReactionEquationNode waterReactionEquationNode;
    private AbstractEquilibriumExpressionNode soluteEquilibriumExpressionNode;
    private final AbstractEquilibriumExpressionNode waterEquilibriumExpressionNode;
    
    public ComparingEquationsNode( final AqueousSolution solution ) {
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
        
        // solute reaction equation, set based on solution
        soluteReactionEquationNode = null;
        
        // water reaction equation
        waterReactionEquationNode = new WaterReactionEquationNode( solution );
        waterReactionEquationNode.setStructuresEnabled( LEWIS_STRUCTURES_ENABLED );
        addChild( waterReactionEquationNode );
        
        // solute equilibrium expression, will be set based on solution
        soluteEquilibriumExpressionNode = null;
        
        // water equilibrium expression
        waterEquilibriumExpressionNode = new WaterEquilibriumExpressionNode( solution );
        addChild( waterEquilibriumExpressionNode );
        
        handleSoluteChanged();
    }

    private void handleSoluteChanged() {

        // remove any existing solute equation
        if ( soluteReactionEquationNode != null ) {
            removeChild( soluteReactionEquationNode );
            soluteReactionEquationNode = null;
        }
        if ( soluteEquilibriumExpressionNode != null ) {
            removeChild( soluteEquilibriumExpressionNode );
            soluteEquilibriumExpressionNode = null;
        }

        // create the proper type of solute equations 
        if ( solution.isAcidic() ) {
            soluteReactionEquationNode = new AcidReactionEquationNode( solution );
            soluteEquilibriumExpressionNode = new AcidEquilibriumExpressionNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteReactionEquationNode = new BaseReactionEquationNode( solution );
            soluteEquilibriumExpressionNode = new BaseEquilibriumExpressionNode( solution );
        }

        // add the new solute equations
        if ( soluteReactionEquationNode != null ) {
            addChild( soluteReactionEquationNode );
            soluteReactionEquationNode.setStructuresEnabled( LEWIS_STRUCTURES_ENABLED );
            soluteReactionEquationNode.setScalingEnabled( waterReactionEquationNode.isScalingEnabled() );
        }
        if ( soluteEquilibriumExpressionNode != null ) {
            addChild( soluteEquilibriumExpressionNode );
            soluteEquilibriumExpressionNode.setScalingEnabled( waterEquilibriumExpressionNode.isScalingEnabled() );
        }
        
        // update the water equation
        waterReactionEquationNode.update();
        waterEquilibriumExpressionNode.update();
        
        updateLayout();
    }

    private void handleConcentrationOrStrengthChanged() {
        if ( soluteReactionEquationNode != null ) {
            soluteReactionEquationNode.update();
        }
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.update();
        }
        waterReactionEquationNode.update();
        waterEquilibriumExpressionNode.update();
    }

    public void setScalingEnabled( boolean enabled ) {
        if ( soluteReactionEquationNode != null ) {
            soluteReactionEquationNode.setScalingEnabled( enabled );
        }
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.setScalingEnabled( enabled );
        }
        waterReactionEquationNode.setScalingEnabled( enabled );
        waterEquilibriumExpressionNode.setScalingEnabled( enabled );
    }
    
    private void updateLayout() {
        
        // do layout with scaling off
        final boolean scalingWasEnabled = waterReactionEquationNode.isScalingEnabled();
        setScalingEnabled( false );
        
        double xOffset = 0;
        double yOffset = 0;
        
        // solute reaction equation
        if ( soluteReactionEquationNode != null ) {
            soluteReactionEquationNode.setOffset( xOffset, yOffset );
            yOffset = yOffset + soluteReactionEquationNode.getFullBoundsReference().getHeight() + Y_SPACING;
        }
        
        // water reaction equation
        waterReactionEquationNode.setOffset( xOffset, yOffset );
        yOffset = yOffset + waterReactionEquationNode.getFullBoundsReference().getHeight() + Y_SPACING;
        
        // solute equilibrium expression
        if ( soluteEquilibriumExpressionNode != null ) {
            soluteEquilibriumExpressionNode.setOffset( xOffset, yOffset );
            yOffset = yOffset + soluteEquilibriumExpressionNode.getFullBoundsReference().getHeight() + Y_SPACING;
        }
        
        // water equilibrium expression
        waterEquilibriumExpressionNode.setOffset( xOffset, yOffset );
        yOffset = yOffset + waterEquilibriumExpressionNode.getFullBoundsReference().getHeight() + Y_SPACING;
        
        // restore scaling
        setScalingEnabled( scalingWasEnabled );
    }
}
