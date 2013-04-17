// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slope.view;

import edu.colorado.phet.linegraphing.common.view.GraphControls;
import edu.colorado.phet.linegraphing.common.view.LineFormsCanvas;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;
import edu.colorado.phet.linegraphing.slope.model.SlopeModel;

/**
 * Canvas for the "Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeCanvas extends LineFormsCanvas {

    public SlopeCanvas( final SlopeModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties,
               new SlopeGraphNode( model, viewProperties ),
               new SlopeEquationControls( model, viewProperties ),
               new GraphControls( viewProperties.linesVisible, viewProperties.slopeVisible ) );
    }
}
