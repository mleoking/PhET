
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.control;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Slider thumb with an arrow tip that points down. 
 * Origin is at the geometric center.
 */
public class SliderThumbArrowNode extends PhetPNode {

    private final PPath pathNode;
    
    public SliderThumbArrowNode( PDimension size, Color fillColor, Color strokeColor, Stroke stroke ) {
        super();

        float w = (float) size.getWidth();
        float h = (float) size.getHeight();
        float hTip = 0.35f * h;

        // start at the tip, move clockwise
        GeneralPath knobPath = new GeneralPath();
        knobPath.moveTo( 0f, h / 2f );
        knobPath.lineTo( -w / 2f, ( h / 2f ) - hTip );
        knobPath.lineTo( -w / 2f, -h / 2f );
        knobPath.lineTo( w / 2, -h / 2f );
        knobPath.lineTo( w / 2, ( h / 2f ) - hTip );
        knobPath.closePath();

        pathNode = new PPath();
        pathNode.setPathTo( knobPath );
        pathNode.setPaint( fillColor );
        pathNode.setStroke( stroke );
        pathNode.setStrokePaint( strokeColor );
        addChild( pathNode );
    }
    
    public void setThumbPaint( Paint paint ) {
        pathNode.setPaint( paint );
    }
}
