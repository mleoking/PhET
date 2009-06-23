package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode.BeakerViewChangeListener;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.Solute;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionAdapter;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;

/**
 * Beaker controls used in the Solutions module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SolutionsBeakerControlsNode extends BeakerControlsNode {
    
    private final AqueousSolution solution;
    
    /**
     * Public constructor, handles connection to model.
     * @param beakerNode
     * @param background
     * @param solution
     */
    public SolutionsBeakerControlsNode( Color background, final BeakerNode beakerNode, AqueousSolution solution ) {
        super( background );
        
        this.solution = solution;
        
        setDissociatedComponentsRatioSelected( beakerNode.isDisassociatedRatioComponentsVisible() );
        setHydroniumHydroxideRatioSelected( beakerNode.isHydroniumHydroxideRatioVisible() );
        setMoleculeCountsSelected( beakerNode.isMoleculeCountsVisible() );
        setLabelSelected( beakerNode.isBeakerLabelVisible() );
        
        // update view when controls change
        addBeakerViewChangeListener( new BeakerViewChangeListener() {

            public void disassociatedComponentsRatioChanged( boolean selected ) {
                beakerNode.setDisassociatedRatioComponentsVisible( selected );
            }

            public void hydroniumHydroxideRatioChanged( boolean selected ) {
                beakerNode.setHydroniumHydroxideRatioVisible( selected );
            }

            public void moleculeCountsChanged( boolean selected ) {
                beakerNode.setMoleculeCountsVisible( selected );
            }

            public void labelChanged( boolean selected ) {
                beakerNode.setBeakerLabelVisible( selected );
            }
        });
        
        // update controls when model changes
        solution.addSolutionListener( new SolutionAdapter() {

            public void soluteChanged() {
                updateDisassociatedComponentsCheckBox();
            }

            public void strengthChanged() {
                updateDisassociatedComponentsCheckBox();
            }

        } );
    }

    private void updateDisassociatedComponentsCheckBox() {
        Solute solute = solution.getSolute();
        setDissociatedComponentsCheckBoxVisible( !solution.isPureWater() );
        setDissociatedComponents( solute.getSymbol(), solute.getConjugateSymbol() );
    }
}
