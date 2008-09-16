/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 13, 2002
 * Time: 7:00:36 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.ohm1d.volt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Vector;

import edu.colorado.phet.ohm1d.common.utils.DoubleSeries;

public class Spectrum implements ColorMap {
    BufferedImage bi;
    DoubleSeries series;
    int numSame = 0;
    double lastAverage;
    Vector listeners = new Vector();

    public Spectrum( BufferedImage bi, int seriesSize ) {
        this.series = new DoubleSeries( seriesSize );
        this.bi = bi;
    }

    public Color toColor( double ratio ) {
        if ( ratio < 0 ) {
            ratio = 0;
        }
        if ( ratio > 1 ) {
            ratio = 1;
        }
        //System.err.println("Ratio="+ratio);
        series.add( ratio );
        ratio = series.average();
        int x = (int) ( bi.getWidth() * ratio );
        if ( x < 0 ) {
            x = 0;
        }
        if ( x >= bi.getWidth() ) {
            x = bi.getWidth() - 1;
        }
        int[] rgb = new int[4];
        rgb = bi.getRaster().getPixel( x, 0, rgb );
        double average = ratio;
        if ( lastAverage == average ) {
            numSame++;
        }
        else {
            numSame = 0;
        }
        lastAverage = average;
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (PowerListener) ( listeners.get( i ) ) ).powerChanged( average );
        }
        return new Color( rgb[0], rgb[1], rgb[2] );
    }

    public boolean isChanging() {
        if ( numSame < series.numElements() ) {
            return true;
        }
        else {
            return false;
        }
    }

    public void addPowerListener( PowerListener pow ) {
        listeners.add( pow );
    }

}
