package edu.colorado.phet.ohm1d.volt;


import edu.colorado.phet.ohm1d.Ohm1DStrings;
import edu.colorado.phet.ohm1d.common.paint.TextPainter;
import edu.colorado.phet.ohm1d.common.phys2d.Law;
import edu.colorado.phet.ohm1d.common.phys2d.System2D;

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
        String text = right + " " + Ohm1DStrings.get( "VoltCount.Electrons" );
        String textRight = "- " + left + " " + Ohm1DStrings.get( "VoltCount.Electrons" );
        rightTp.setText( textRight );
        tp.setText( text );
        String tp2 = "= " + total + " " + Ohm1DStrings.get( "VoltCount.Volts" );
        tot.setText( tp2 );
    }
}
