// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.colorado.phet.linegraphing.common.view.EquationNode;

/**
 * Factory that creates line equations in slope-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font, Color color ) {
        assert ( line.x1 == 0 ); // line is in slope-intercept form
        return new SlopeInterceptInteractiveEquationNode( line, font, color );
    }
}
