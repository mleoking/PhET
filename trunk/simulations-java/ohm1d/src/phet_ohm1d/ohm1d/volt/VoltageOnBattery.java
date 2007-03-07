package phet_ohm1d.ohm1d.volt;

import phet_ohm1d.ohm1d.gui.VoltageListener;
import phet_ohm1d.paint.Painter;
import phet_ohm1d.paint.TextPainter;
import phet_ohm1d.view.util.SimStrings;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 8, 2002
 * Time: 12:47:01 PM
 * To change this template use Options | File Templates.
 */
public class VoltageOnBattery implements Painter, VoltageListener {
    TextPainter tp;
    DecimalFormat nf = new DecimalFormat();

    Point pos;
    Point neg;

    public VoltageOnBattery( Point pos, Point neg, Font font, String defaultText ) {
        this.pos = pos;
        this.neg = neg;
        Color myColor = new Color( 100, 100, 250 );
        this.tp = new TextPainter( defaultText, pos.x, pos.y, font, myColor );
        nf.setMinimumFractionDigits( 2 );
        nf.setMaximumFractionDigits( 2 );
    }

    public void paint( Graphics2D g ) {
        tp.paint( g );

    }

    public void valueChanged( double val ) {
        if( val < 0 ) {
            tp.setPosition( neg.x, neg.y );
        }
        else {
            tp.setPosition( pos.x, pos.y );
        }
        val = Math.abs( val );
        String str = nf.format( val );
        tp.setText( str + " " + SimStrings.get( "VoltageOnBattery.VoltsAbbreviation" ) );
    }
}
