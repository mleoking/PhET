package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jul 10, 2006
 * Time: 9:23:35 AM
 * Copyright (c) Jul 10, 2006 by Sam Reid
 */

public class HighlightNode extends PPath {
    public HighlightNode( Image image ) {
        setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5, new float[]{15, 5}, 0 ) );
//        setStroke( new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 5, new float[]{10,30}, 0 ) );
//        setPaint( Color.yellow );
        setStrokePaint( Color.yellow );
        setPathToRectangle( 0, 0, image.getWidth( null ), image.getHeight( null ) );
    }
}
