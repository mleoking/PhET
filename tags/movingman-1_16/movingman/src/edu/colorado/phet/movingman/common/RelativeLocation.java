/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.common;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;

/**
 * User: Sam Reid
 * Date: Mar 28, 2005
 * Time: 11:20:07 AM
 * Copyright (c) Mar 28, 2005 by Sam Reid
 */

public abstract class RelativeLocation {
    private PhetGraphic anchor;

    public RelativeLocation( PhetGraphic anchor ) {
        this.anchor = anchor;
    }

    public PhetGraphic getAnchor() {
        return anchor;
    }

    public abstract void layout( PhetGraphic b );

    public static class InsideNortheast extends RelativeLocation {
        private int insetX;
        private int insetY;

        public InsideNortheast( PhetGraphic anchor, int insetX, int insetY ) {
            super( anchor );
            this.insetX = insetX;
            this.insetY = insetY;
        }

        public void layout( PhetGraphic inner ) {
            int x = (int)( getAnchor().getBounds().x + getAnchor().getBounds().getWidth() - inner.getWidth() - insetX );
            int y = getAnchor().getBounds().y + insetY;
            inner.setLocation( x, y );
        }
    }
}
