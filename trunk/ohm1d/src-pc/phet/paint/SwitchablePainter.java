package phet.paint;

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
