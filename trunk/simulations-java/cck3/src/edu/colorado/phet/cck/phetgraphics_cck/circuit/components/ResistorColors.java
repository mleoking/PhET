/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.phetgraphics_cck.circuit.components;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 21, 2003
 * Time: 4:58:39 PM
 * Copyright (c) Apr 21, 2003 by Sam Reid
 */
public class ResistorColors {
    public Color[] to3Colors( int value ) {
        //first 2 digits for value, third digit for scale.
        if( value < 10 ) {
            return new Color[]{Color.black, digitToColor( value ), Color.black, Color.yellow};
        }
        else if( value < 100 ) {
            int firstdigit = value / 10;
            int seconddig = value % 10;
            return new Color[]{digitToColor( firstdigit ), digitToColor( seconddig ), Color.black, Color.yellow};
        }
        else {
            String s = "" + value;
            int firstdigit = Integer.parseInt( s.charAt( 0 ) + "" );
            int seconddig = Integer.parseInt( s.charAt( 1 ) + "" );
            int factor = s.length() - 2;
//            int factor=value/100;
//            int firstdigit=value/factor;
//            int seconddig=value%factor;

            double predicted = ( ( firstdigit * 10 + seconddig ) * Math.pow( 10, factor ) );
            double offby = ( value - predicted ) / predicted * 100;
//            O.d("predicted="+predicted+", actual="+value+", off="+offby);
            Color tolerance = null;
            if( offby < 5 ) {
                tolerance = Color.yellow;
            }
            else if( offby < 20 ) {
                tolerance = Color.gray;
            }
            else {
                tolerance = null;
            }
//            O.d("factor=" + factor + ", firstdig=" + firstdigit + ", sec=" + seconddig + ", offby=" + offby);
            return new Color[]{digitToColor( firstdigit ), digitToColor( seconddig ), digitToColor( factor ), tolerance};
        }
    }

    static Color brown = new Color( 200, 150, 100 );
    static Color violet = new Color( 148, 0, 211 );
    static final Color[] colors = new Color[]{Color.black, brown, Color.red, Color.orange, Color.yellow, Color.green,
            Color.blue, violet, Color.gray, Color.white};

    public Color digitToColor( int digit ) {

        if( digit < 0 || digit >= 10 ) {
            throw new RuntimeException( "Out of range: " + digit );
        }

        return colors[digit];
    }
}
