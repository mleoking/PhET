/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.help;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 9, 2005
 * Time: 9:32:50 AM
 * Copyright (c) May 9, 2005 by Sam Reid
 */

public class PhetGraphicTarget extends HelpTarget {
    private PhetGraphic target;

    public PhetGraphicTarget( PhetGraphic target ) {
        this.target = target;
        target.addPhetGraphicListener( new PhetGraphicListener() {
            public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                notifyLocationChanged();
            }

            public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                notifyVisibilityChanged();
            }
        } );
    }

    public boolean isVisible() {
        return target.isVisible();
    }

    public Point getLocation() {
        return RectangleUtils.getCenter( target.getBounds() );
    }

    public PhetGraphic getTarget() {
        return target;
    }

    public static class Left extends PhetGraphicTarget {
        public Left( PhetGraphic target ) {
            super( target );
        }

        public Point getLocation() {
            return RectangleUtils.getLeftCenter( getTarget().getBounds() );
        }
    }

    public static class Right extends PhetGraphicTarget {
        public Right( PhetGraphic target ) {
            super( target );
        }

        public Point getLocation() {
            return RectangleUtils.getRightCenter( getTarget().getBounds() );
        }
    }

    public static class Bottom extends PhetGraphicTarget {
        public Bottom( PhetGraphic target ) {
            super( target );
        }

        public Point getLocation() {
            return RectangleUtils.getBottomCenter( getTarget().getBounds() );
        }
    }

    public static class Top extends PhetGraphicTarget {
        public Top( PhetGraphic target ) {
            super( target );
        }

        public Point getLocation() {
            return RectangleUtils.getTopCenter( getTarget().getBounds() );
        }
    }
}