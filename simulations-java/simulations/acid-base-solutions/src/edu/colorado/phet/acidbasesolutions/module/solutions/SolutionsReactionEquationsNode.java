package edu.colorado.phet.acidbasesolutions.module.solutions;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.acidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AbstractReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.AcidReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.BaseReactionEquationNode;
import edu.colorado.phet.acidbasesolutions.view.reactionequations.WaterReactionEquationNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;


public class SolutionsReactionEquationsNode extends PhetPNode {
    
    private static final double X_MARGIN = 20;
    private static final double Y_SPACING = 50;
    private static final boolean LEWIS_STRUCTURES_ENABLED = true;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private final EquationScalingControl scalingControl;
    private final PSwing scalingControlWrapper;
    private AbstractReactionEquationNode soluteNode;
    private final WaterReactionEquationNode waterNode;
    
    public SolutionsReactionEquationsNode( final AqueousSolution solution ) {
        super();

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
        
        // scaling control
        scalingControl = new EquationScalingControl();
        scalingControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setScalingEnabled( scalingControl.isScalingEnabled(), true /* animated */ );
            }
        });
        scalingControlWrapper = new PSwing( scalingControl );
        addChild( scalingControlWrapper );
        
        // solute reaction equation, set based on solution
        soluteNode = null;
        
        // water reaction equation
        waterNode = new WaterReactionEquationNode( solution );
        waterNode.setStructuresEnabled( LEWIS_STRUCTURES_ENABLED );
        addChild( waterNode );
        
        handleSoluteChanged();
    }

    private void handleSoluteChanged() {

        // remove any existing solute equation
        if ( soluteNode != null ) {
            removeChild( soluteNode );
            soluteNode = null;
        }

        // create the proper type of solute equations 
        if ( solution.isAcidic() ) {
            soluteNode = new AcidReactionEquationNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteNode = new BaseReactionEquationNode( solution );
        }

        // add the new solute equations
        if ( soluteNode != null ) {
            addChild( soluteNode );
            soluteNode.setStructuresEnabled( LEWIS_STRUCTURES_ENABLED );
            soluteNode.setScalingEnabled( waterNode.isScalingEnabled(), false /* animated */ );
        }
        
        // update the water equation
        waterNode.update();
        
        updateLayout();
    }

    private void handleConcentrationOrStrengthChanged() {
        if ( soluteNode != null ) {
            soluteNode.update();
        }
        waterNode.update();
    }

    public void setScalingEnabled( boolean enabled, boolean animated ) {
        scalingControl.setScalingEnabled( enabled );
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( enabled, animated );
        }
        waterNode.setScalingEnabled( enabled, animated );
    }
    
    public boolean isScalingEnabled() {
        return waterNode.isScalingEnabled();
    }
    
    private void updateLayout() {
        
        // do layout with scaling off
        final boolean scalingWasEnabled = waterNode.isScalingEnabled();
        setScalingEnabled( false /* enabled */, false /* animated */ );
        
        scalingControlWrapper.setOffset( 0, Y_SPACING );
        
        double xOffset = scalingControlWrapper.getXOffset();
        double yOffset = scalingControlWrapper.getFullBoundsReference().getMaxY();
        
        // solute reaction equation
        if ( soluteNode != null ) {
            xOffset = xOffset - PNodeUtils.getOriginXOffset( soluteNode ) + X_MARGIN;
            yOffset = yOffset - PNodeUtils.getOriginYOffset( soluteNode ) + Y_SPACING;
            soluteNode.setOffset( xOffset, yOffset );
            xOffset = 0;
            yOffset = soluteNode.getFullBoundsReference().getMaxY();
        }
        
        // water reaction equation
        xOffset = xOffset - PNodeUtils.getOriginXOffset( waterNode ) + X_MARGIN;
        yOffset = yOffset - PNodeUtils.getOriginYOffset( waterNode ) + Y_SPACING;
        waterNode.setOffset( xOffset, yOffset );
        yOffset = waterNode.getFullBoundsReference().getMaxY() - PNodeUtils.getOriginYOffset( waterNode ) + Y_SPACING;
        
        // restore scaling
        setScalingEnabled( scalingWasEnabled, false /* animated */ );
    }
}
