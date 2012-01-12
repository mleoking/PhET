// Copyright 2002-2011, University of Colorado

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Nov 13, 2002
 * Time: 5:59:11 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.batteryresistorcircuit.volt;

import java.awt.*;

import edu.colorado.phet.batteryresistorcircuit.common.paint.Painter;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.WirePatch;
import edu.colorado.phet.batteryresistorcircuit.common.wire1d.paint.WirePatchPainter;
import edu.colorado.phet.batteryresistorcircuit.gui.CoreCountListener;
import edu.colorado.phet.batteryresistorcircuit.gui.VoltageListener;

public class Filament implements Painter, CoreCountListener, VoltageListener {
    BasicStroke stroke;
    WirePatch patch;
    WirePatchPainter painter;
    double v = 1;
    double r = 1;
    double powMax;
    ColorMap cm;

    public Filament( BasicStroke stroke, edu.colorado.phet.batteryresistorcircuit.common.wire1d.WirePatch patch, double vMax, double rMax, ColorMap cm ) {
        this.cm = cm;
        //ColorTemperature cx=new ColorTemperature();
        //cx.colorMixRGB()
        this.stroke = stroke;
        this.patch = patch;
        this.powMax = vMax * vMax / rMax;
    }

    public void paint( Graphics2D g ) {
        //see if we need to keep changing continuously.
        if ( cm.isChanging() ) {
            fixPainter();
        }
        painter.paint( g );
    }

    public void coreCountChanged( int val ) {
        this.r = val;
        fixPainter();
    }

    private void fixPainter() {
        double power = v * v / r;
        Color c = powerToColor( power );
        this.painter = new WirePatchPainter( stroke, c, patch );
    }

    private Color powerToColor( double power ) {
        double powRatio = power / powMax;
        return cm.toColor( powRatio );//new Color(rInt,gInt,bInt);
    }

    public void valueChanged( double val ) {
        this.v = val;
        fixPainter();
    }


}
