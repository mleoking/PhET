// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * When a line's slope is undefined, we display "x = # (slope undefined)" in place of an equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeUndefinedNode extends PhetPText {
    public SlopeUndefinedNode( Line line, PhetFont font, Color color ) {
        super( MessageFormat.format( Strings.SLOPE_UNDEFINED, Strings.SYMBOL_X, line.x1 ), font, color );
        assert ( line.undefinedSlope() );
        setPickable( false );
    }
}
