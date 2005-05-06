/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 6, 2005
 * Time: 3:16:00 PM
 * Copyright (c) May 6, 2005 by Sam Reid
 */
public abstract class AnimationStep {
    int keycode;

    protected AnimationStep( int keycode ) {
        this.keycode = keycode;
    }

    abstract boolean step();

    public static class Moving extends AnimationStep {
        private ApparatusPanel2 rampPanel;
        private int dx;
        private int dy;

        public Moving( int keycode, ApparatusPanel2 rampPanel, int dx, int dy ) {
            super( keycode );
            this.rampPanel = rampPanel;
            this.dx = dx;
            this.dy = dy;
        }

        public boolean step() {
            Point2D vpo = rampPanel.getTransformManager().getViewPortOrigin();
            rampPanel.getTransformManager().setViewPortOrigin( vpo.getX() + dx, vpo.getY() + dy );
            return true;
        }
    }

    public static class Zooming extends AnimationStep {
        private ApparatusPanel2 panel;
        private double scaleZ;

        public Zooming( int keycode, ApparatusPanel2 panel, double scaleZ ) {
            super( keycode );
            this.panel = panel;
            this.scaleZ = scaleZ;
        }

        public boolean step() {
            double scale = panel.getScale();
            double newScale = scale * scaleZ;
            panel.setScale( newScale );
            return false;
        }

    }
}
