/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Piccolo node that represents a scale on which an atom can be weighed.
 *
 * @author John Blanco
 */
public class AtomScaleNode extends PNode {

    private static final Dimension2D SIZE = new PDimension( 200, 100 );

    public AtomScaleNode() {
        addChild(new PhetPPath( new Rectangle2D.Double(0, 0, SIZE.getWidth(), SIZE.getHeight()), Color.PINK ));
    }
}
