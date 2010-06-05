/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.module.ABSCanvas;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.PHMeterNode;

/**
 * Canvas for the "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionCanvas extends ABSCanvas {

    private final ABSModel model;
    
    public TestSolutionCanvas( ABSModel model ) {
        
        this.model = model;
        
        BeakerNode beakerNode = new BeakerNode( model.getBeaker(), model.getSolution() );
        addNode( beakerNode );
        beakerNode.setOffset( model.getBeaker().getLocationReference() );
        
        PHMeterNode pHMeterNode = new PHMeterNode( ABSConstants.PH_METER_HEIGHT, model.getSolution() );
        addNode( pHMeterNode );
        pHMeterNode.setOffset( model.getBeaker().getLocationReference().getX(), model.getBeaker().getLocationReference().getY() - model.getBeaker().getHeight() );
    }

}
