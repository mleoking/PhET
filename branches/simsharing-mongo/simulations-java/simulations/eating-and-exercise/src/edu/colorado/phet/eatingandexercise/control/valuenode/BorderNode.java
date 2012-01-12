// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.eatingandexercise.control.valuenode;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Created by: Sam
 * Jun 24, 2008 at 8:46:37 PM
 */
public class BorderNode extends PNode {
    private PhetPPath borderPath;
    private PNode child;
    private double dw;
    private double dh;

    public BorderNode( PNode child, double dw, double dh ) {
        this.child = child;
        this.dw = dw;
        this.dh = dh;
        addChild( child );
        borderPath = new PhetPPath( new BasicStroke( 1 ), Color.black );
        addChild( borderPath );
        relayout();
    }

    private void relayout() {
        borderPath.setPathTo( RectangleUtils.expand( child.getFullBounds(), dw, dh ) );
    }

}
