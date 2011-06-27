// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class defines the box from which the user can drag weights that can
 * then be set on the balance.
 *
 * @author John Blanco
 */
public class WeightBoxNode extends PNode {

    private static final PDimension SIZE = new PDimension( 220, 300 );

    private final ModelViewTransform mvt;

    /**
     * Constructor.
     */
    public WeightBoxNode( ModelViewTransform mvt ) {
        this.mvt = mvt;
        PNode backgroundBox = new PhetPPath( new RoundRectangle2D.Double( 0, 0, SIZE.getWidth(), SIZE.getHeight(), 20, 20 ),
                                             Color.WHITE,
                                             new BasicStroke( 3 ),
                                             Color.BLACK );
        addChild( backgroundBox );
    }
}
