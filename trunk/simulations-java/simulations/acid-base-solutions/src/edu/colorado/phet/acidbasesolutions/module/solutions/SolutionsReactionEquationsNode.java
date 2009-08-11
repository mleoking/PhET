package edu.colorado.phet.acidbasesolutions.module.solutions;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AcidReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.BaseReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.WaterReactionEquationNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class SolutionsReactionEquationsNode extends PhetPNode {
    
    private static final double Y_SPACING = 65;
    private static final boolean LEWIS_STRUCTURES_ENABLED = true;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private AbstractReactionEquationNode soluteReactionEquationNode;
    private final WaterReactionEquationNode waterReactionEquationNode;
    
    public SolutionsReactionEquationsNode( final AqueousSolution solution ) {
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
        
        handleSoluteChanged();
    }

    private void handleSoluteChanged() {

        // remove any existing solute equation
        if ( soluteReactionEquationNode != null ) {
            removeChild( soluteReactionEquationNode );
            soluteReactionEquationNode = null;
        }

        // create the proper type of solute equations 
        if ( solution.isAcidic() ) {
            soluteReactionEquationNode = new AcidReactionEquationNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteReactionEquationNode = new BaseReactionEquationNode( solution );
        }

        // add the new solute equations
        if ( soluteReactionEquationNode != null ) {
            addChild( soluteReactionEquationNode );
            soluteReactionEquationNode.setStructuresEnabled( LEWIS_STRUCTURES_ENABLED );
            soluteReactionEquationNode.setScalingEnabled( waterReactionEquationNode.isScalingEnabled() );
        }
        
        // update the water equation
        waterReactionEquationNode.update();
        
        updateLayout();
    }

    private void handleConcentrationOrStrengthChanged() {
        if ( soluteReactionEquationNode != null ) {
            soluteReactionEquationNode.update();
        }
        waterReactionEquationNode.update();
    }

    public void setScalingEnabled( boolean enabled ) {
        if ( soluteReactionEquationNode != null ) {
            soluteReactionEquationNode.setScalingEnabled( enabled );
        }
        waterReactionEquationNode.setScalingEnabled( enabled );
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
            yOffset = soluteReactionEquationNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        }
        
        // water reaction equation
        waterReactionEquationNode.setOffset( xOffset, yOffset );
        yOffset = waterReactionEquationNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        
        // restore scaling
        setScalingEnabled( scalingWasEnabled );
    }
}
