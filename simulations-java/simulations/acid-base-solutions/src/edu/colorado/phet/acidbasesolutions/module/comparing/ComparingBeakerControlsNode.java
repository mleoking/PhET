package edu.colorado.phet.acidbasesolutions.module.comparing;

import java.awt.Color;

import edu.colorado.phet.acidbasesolutions.control.BeakerControlsNode;
import edu.colorado.phet.acidbasesolutions.view.beaker.BeakerNode;

/**
 * Beaker controls used in the "Comparing Solutions" module.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ComparingBeakerControlsNode extends BeakerControlsNode {
    
    public ComparingBeakerControlsNode( Color background, final BeakerNode beakerNodeLeft, final BeakerNode beakerNodeRight ) {
        super( background );
        
        // make sure left and right are the same
        beakerNodeRight.setDisassociatedRatioComponentsVisible( beakerNodeLeft.isDisassociatedRatioComponentsVisible() );
        beakerNodeRight.setHydroniumHydroxideRatioVisible( beakerNodeLeft.isHydroniumHydroxideRatioVisible() );
        beakerNodeRight.setMoleculeCountsVisible( beakerNodeLeft.isMoleculeCountsVisible() );
        beakerNodeRight.setBeakerLabelVisible( beakerNodeLeft.isBeakerLabelVisible() );
        
        // sync controls with left
        setDissociatedComponentsRatioSelected( beakerNodeLeft.isDisassociatedRatioComponentsVisible() );
        setHydroniumHydroxideRatioSelected( beakerNodeLeft.isHydroniumHydroxideRatioVisible() );
        setMoleculeCountsSelected( beakerNodeLeft.isMoleculeCountsVisible() );
        setLabelSelected( beakerNodeLeft.isBeakerLabelVisible() );
        
        // update view when controls change
        addBeakerViewChangeListener( new BeakerViewChangeListener() {

            public void disassociatedComponentsRatioChanged( boolean selected ) {
                beakerNodeLeft.setDisassociatedRatioComponentsVisible( selected );
                beakerNodeRight.setDisassociatedRatioComponentsVisible( selected );
            }

            public void hydroniumHydroxideRatioChanged( boolean selected ) {
                beakerNodeLeft.setHydroniumHydroxideRatioVisible( selected );
                beakerNodeRight.setHydroniumHydroxideRatioVisible( selected );
            }
            
            public void moleculeCountsChanged( boolean selected ) {
                beakerNodeLeft.setMoleculeCountsVisible( selected );
                beakerNodeRight.setMoleculeCountsVisible( selected );
            }

            public void labelChanged( boolean selected ) {
                beakerNodeLeft.setBeakerLabelVisible( selected );
                beakerNodeRight.setBeakerLabelVisible( selected );
            }
        });
    }
}
