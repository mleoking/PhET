/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.testsolution;

import edu.colorado.phet.acidbasesolutions.constants.ABSConstants;
import edu.colorado.phet.acidbasesolutions.model.ABSModel;
import edu.colorado.phet.acidbasesolutions.module.ABSCanvas;
import edu.colorado.phet.acidbasesolutions.view.BeakerNode;
import edu.colorado.phet.acidbasesolutions.view.MagnifyingGlassNode;
import edu.colorado.phet.acidbasesolutions.view.PHMeterNode;

/**
 * Canvas for the "Test Solution" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestSolutionCanvas extends ABSCanvas {

    private final ABSModel model;
    
    private final BeakerNode beakerNode;
    private final PHMeterNode pHMeterNode;
    private final MagnifyingGlassNode magnifyingGlassNode;
    
    public TestSolutionCanvas( ABSModel model ) {
        
        this.model = model;
        
        beakerNode = new BeakerNode( model.getBeaker(), model.getSolution() );
        addNode( beakerNode );
        
        pHMeterNode = new PHMeterNode( ABSConstants.PH_METER_HEIGHT, model );
        addNode( pHMeterNode );
        
        magnifyingGlassNode = new MagnifyingGlassNode( model );
        addNode( magnifyingGlassNode );
        
        doStaticLayout();
    }
    
    private void doStaticLayout() {
        double x = 0;
        double y = 0;
        beakerNode.setOffset( model.getBeaker().getLocationReference() );
        x = beakerNode.getXOffset();
        y = beakerNode.getYOffset() - ( model.getMagnifyingGlass().getDiameter() / 2 );
        magnifyingGlassNode.setOffset( x, y );
        x = model.getBeaker().getLocationReference().getX();
        y = model.getBeaker().getLocationReference().getY() - model.getBeaker().getHeight() - ( 0.5 * pHMeterNode.getFullBoundsReference().getHeight() );
        pHMeterNode.setOffset( x, y );
    }
    
}
