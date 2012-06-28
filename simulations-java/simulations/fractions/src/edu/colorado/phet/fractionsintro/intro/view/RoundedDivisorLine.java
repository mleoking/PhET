// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * The line that goes between the numerator and denominator.  This has its own class so we can ensure consistent look between different types of fractions (controllable, mixed, reduced, etc.)
 *
 * @author Sam Reid
 */
public class RoundedDivisorLine extends RichPNode {
    public RoundedDivisorLine() {
        final PhetPPath line = new PhetPPath( new Line2D.Double( 0, 0, 150, 0 ), new BasicStroke( 12, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER ), Color.black );
        addChild( line );
    }
}