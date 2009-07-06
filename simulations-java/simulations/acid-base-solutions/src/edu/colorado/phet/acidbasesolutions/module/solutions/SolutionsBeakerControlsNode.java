package edu.colorado.phet.acidbasesolutions.module.solutions;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.control.RatioCheckBox;
import edu.colorado.phet.acidbasesolutions.control.RatioCheckBox.SpecificSoluteRatioCheckBox;
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
        
        setSoluteComponentsRatioSelected( beakerNode.isSoluteComponentsRatioVisible() );
        setHydroniumHydroxideRatioSelected( beakerNode.isHydroniumHydroxideRatioVisible() );
        setMoleculeCountsSelected( beakerNode.isMoleculeCountsVisible() );
        setLabelSelected( beakerNode.isBeakerLabelVisible() );
        
        // update view when controls change
        addBeakerViewChangeListener( new BeakerViewChangeListener() {

            public void soluteComponentsRatioChanged( boolean selected ) {
                beakerNode.setSoluteComponentsRatioVisible( selected );
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
                updateSoluteIonRatioCheckBox();
            }

            public void strengthChanged() {
                updateSoluteIonRatioCheckBox();
            }

        } );
    }

    private void updateSoluteIonRatioCheckBox() {
        Solute solute = solution.getSolute();
        setSoluteComponentsRatioCheckBoxVisible( !solution.isPureWater() );
        if ( !solution.isPureWater() ) {
            setSoluteComponents( solute.getSymbol(), solute.getColor(), solute.getConjugateSymbol(), solute.getConjugateColor() );
        }
    }
    
    protected RatioCheckBox getSoluteComponentsRatioCheckBox() {
        return new SpecificSoluteRatioCheckBox();
    }
}
