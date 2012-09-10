// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import edu.colorado.phet.linegraphing.common.model.LineFormsModel;
import edu.colorado.phet.linegraphing.common.view.LineFormsCanvas;
import edu.colorado.phet.linegraphing.common.view.LineFormsViewProperties;

/**
 * Canvas for the "Point-Slope" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeCanvas extends LineFormsCanvas {

    public PointSlopeCanvas( final LineFormsModel model, LineFormsViewProperties viewProperties ) {
        super( model, viewProperties,
               new PointSlopeGraphNode( model, viewProperties ),
               new PointSlopeEquationControls( model, viewProperties ) );
    }
}
