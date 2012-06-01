// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.view.FNode;

/**
 * Bar graph for showing the value of fractions.
 *
 * @author Sam Reid
 */
public class BarGraphNodeBars extends FNode {
    public BarGraphNodeBars( double leftScaleValue, Color leftColor, double rightScaleValue, Color rightColor ) {
        final double leftBarHeight = leftScaleValue * 100;
        final int barWidth = 17;
        addChild( new PhetPPath( new Rectangle2D.Double( -20, 200 - leftBarHeight, barWidth, leftBarHeight ), leftColor, new BasicStroke( 1 ), Color.black ) );

        final double rightBarHeight = rightScaleValue * 100;
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 200 - rightBarHeight, barWidth, rightBarHeight ), rightColor, new BasicStroke( 1 ), Color.black ) );
    }
}