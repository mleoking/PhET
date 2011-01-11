/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo node that represents a scale on which an atom can be weighed.  This
 * node is intended to have a faux 3D look to it, but is not truly 3D in any
 * way.
 *
 * @author John Blanco
 */
public class AtomScaleNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 200, 100 );
    private static final Stroke STROKE = new BasicStroke( 2 );
    private static final Paint STROKE_PAINT = Color.BLACK;

    public AtomScaleNode() {
        addChild(new PhetPPath( new Rectangle2D.Double(0, 0, SIZE.getWidth(), SIZE.getHeight()), Color.PINK ));

        // Draw the top of the scale, where the atom will sit.  This is drawn
        // as a trapazoid that is smaller on the top, which is meant to look
        // like the scale viewed at an angle.
        DoubleGeneralPath weighPlateTopShape = new DoubleGeneralPath();
        weighPlateTopShape.moveTo( SIZE.getWidth() * 0.25, 0 );
        weighPlateTopShape.lineTo( SIZE.getWidth() * 0.75, 0 );
        weighPlateTopShape.lineTo( SIZE.getWidth(), SIZE.getHeight() * 0.25 );
        weighPlateTopShape.lineTo( 0, SIZE.getHeight() * 0.25 );
        weighPlateTopShape.closePath();

        PNode scaleTop = new PhetPPath( weighPlateTopShape.getGeneralPath(), Color.LIGHT_GRAY, STROKE, STROKE_PAINT );
        addChild( scaleTop );

        // Add the rectangle the sits just below the top of the scale.
        Rectangle2D bottomOfWeighPlate = new Rectangle2D.Double( 0, SIZE.getHeight() * 0.25, SIZE.getWidth(), SIZE.getHeight() * 0.1 );
        addChild( new PhetPPath( bottomOfWeighPlate, Color.LIGHT_GRAY, STROKE, STROKE_PAINT ) );

        // Add the top portion of the scale body.  This is meant to look like
        // a tilted rectangle.


    }
}
