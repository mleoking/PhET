// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.colorado.phet.linegraphing.common.view.EquationNode;

/**
 * Factory that creates line equations in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font, Color color ) {
        return new PointSlopeInteractiveEquationNode( line, font, color );
    }
}
