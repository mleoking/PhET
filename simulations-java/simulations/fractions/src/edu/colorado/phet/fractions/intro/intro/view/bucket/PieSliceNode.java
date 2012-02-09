// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view.bucket;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.intro.view.FractionsIntroCanvas;
import edu.colorado.phet.fractions.intro.intro.view.PieSetFractionNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PieSliceNode extends PNode {

    public final Shape shape;

    public PieSliceNode( int denominator ) {
        double anglePerSlice = 360.0 / denominator;
        shape = denominator == 1 ? new Ellipse2D.Double( 0, 0, PieSetFractionNode.DIAMETER, PieSetFractionNode.DIAMETER ) : new Arc2D.Double( 0, 0, PieSetFractionNode.DIAMETER, PieSetFractionNode.DIAMETER, -anglePerSlice / 2 + 90, anglePerSlice, Arc2D.PIE );
        addChild( new PhetPPath( shape, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 1 ), Color.darkGray ) );
        addInputEventListener( new CursorHandler() );
    }
}