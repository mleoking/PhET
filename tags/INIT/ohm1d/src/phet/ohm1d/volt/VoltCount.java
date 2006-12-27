package phet.ohm1d.volt;

import phet.paint.TextPainter;
import phet.phys2d.Law;
import phet.phys2d.System2D;

public class VoltCount implements Law {
    Batt batt;
    TextPainter tp;
    TextPainter tot;
    TextPainter rightTp;

    public VoltCount( TextPainter tp, Batt batt, TextPainter tot, TextPainter right ) {
        this.rightTp = right;
        this.tot = tot;
        this.tp = tp;
        this.batt = batt;
    }

    public void iterate( double dt, System2D sys ) {
        int left = batt.countLeft();
        int right = batt.countRight();
        int total = right - left;
        String text = right + " electrons";
        String textRight = "- " + left + " electrons";
        rightTp.setText( textRight );
        tp.setText( text );
        String tp2 = "= " + total + " \"Volts\"";
        tot.setText( tp2 );
    }
}
