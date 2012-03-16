// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.paint;

import java.awt.*;

public class SwitchablePainter implements Painter {
    Painter p;

    public SwitchablePainter( Painter p ) {
        this.p = p;
    }

    public void paint( Graphics2D g ) {
        p.paint( g );
    }
}
