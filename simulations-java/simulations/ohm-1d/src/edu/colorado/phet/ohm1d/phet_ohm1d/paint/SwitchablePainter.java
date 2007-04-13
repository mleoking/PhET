package edu.colorado.phet.ohm1d.phet_ohm1d.paint;

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
