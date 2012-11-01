// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.GraphControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsCanvas;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;

/**
 * Canvas for the "Slope-Intercept" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptCanvas extends LineFormsCanvas {

    public SlopeInterceptCanvas( LineFormsModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties,
               new SlopeInterceptGraphNode( model, viewProperties ),
               new SlopeInterceptEquationControls( model, viewProperties ),
               new GraphControls( viewProperties.linesVisible, viewProperties.slopeVisible, model.standardLines ) );
    }
}
