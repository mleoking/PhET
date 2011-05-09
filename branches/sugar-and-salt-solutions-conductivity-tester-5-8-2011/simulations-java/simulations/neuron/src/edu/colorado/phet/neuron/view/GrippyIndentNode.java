// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.view;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This node is meant to portray a small round indentation on a surface.  This
 * is a modern user interface paradigm that is intended to convey the concept
 * of "gripability" (sp?), i.e. something that the user can click on and
 * subsequently grab.  This is meant to look somewhat 3D, much like etched
 * borders do.
 *
 * @author John Blanco
 */
public class GrippyIndentNode extends PNode {

    /**
     * Constructor.  The base color should be the color of the item on
     * which this indent is being placed, and it will
     * Constructor.
     */
    public GrippyIndentNode( double diameter, Color baseColor ) {
        Color baseDarkerColor = ColorUtils.darkerColor( baseColor, 0.9 );
        Color translucentDarkerColor = new Color( baseDarkerColor.getRed(), baseDarkerColor.getGreen(),
                baseDarkerColor.getBlue(), baseColor.getAlpha() );
        Color baseLighterColor = ColorUtils.brighterColor( baseColor, 0.9 );
        Color translucentBrighterColor = new Color( baseLighterColor.getRed(), baseLighterColor.getGreen(),
                baseLighterColor.getBlue(), baseColor.getAlpha() );
        addChild( new PhetPPath( new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter ),
                translucentBrighterColor ) );
        double offsetFactor = 0.8;
        addChild( new PhetPPath( new Ellipse2D.Double( -diameter / 2, -diameter / 2,
                diameter * offsetFactor, diameter * offsetFactor ), translucentDarkerColor ) );
    }
}
