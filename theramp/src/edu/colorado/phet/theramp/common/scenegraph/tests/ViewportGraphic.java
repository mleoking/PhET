/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.theramp.common.scenegraph.AbstractGraphic;
import edu.colorado.phet.theramp.common.scenegraph.GraphicListNode;
import edu.colorado.phet.theramp.common.scenegraph.OutlineGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 1:47:40 AM
 * Copyright (c) Jun 8, 2005 by Sam Reid
 */

public class ViewportGraphic extends GraphicListNode {
    private AbstractGraphic graphic;
    private Clipper clipper;
    private AbstractGraphic postGraphic;
    private Dimension clip;

    public ViewportGraphic( AbstractGraphic graphic, Dimension clip ) {
        this.clip = clip;
        this.graphic = graphic;
        clipper = new ViewportGraphic.Clipper( graphic, new Rectangle( clip ) );
        postGraphic = new OutlineGraphic( new Rectangle( clip ), new BasicStroke(), Color.black );
        addGraphic( clipper );
        addGraphic( postGraphic );
    }

    public boolean containsMousePointLocal( double x, double y ) {
        return new Rectangle( clip ).contains( x, y );
    }

    private class Clipper extends GraphicListNode {
        private AbstractGraphic graphic;

        public Clipper( AbstractGraphic graphic, Rectangle clip ) {
            this.graphic = graphic;
            addGraphic( graphic );
            setClip( clip );
        }
    }

}
