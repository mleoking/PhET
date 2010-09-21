package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.signalcircuit.paint.BufferedImagePainter;
import edu.colorado.phet.signalcircuit.paint.Painter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Switch implements Painter, MouseListener {
    BufferedImagePainter on;
    BufferedImagePainter off;
    boolean isOn;
    JSlider js;

    public Switch( BufferedImagePainter on, BufferedImagePainter off, boolean isOn, JSlider js ) {
        this.js = js;
        this.on = on;
        this.off = off;
        this.isOn = isOn;
    }

    public BufferedImagePainter getPainter() {
        if( isOn ) {
            return on;
        }
        else {
            return off;
        }
    }

    public void mouseClicked( MouseEvent me ) {
    }

    public void mouseReleased( MouseEvent me ) {
        BufferedImagePainter bip = on;
        if( bip.contains( me.getX(), me.getY() ) ) {
            isOn = !isOn;
            if( isOn ) {
                js.setValue( 0 );
            }
            else {
                js.setValue( 45 );
            }
        }
    }

    public void mouseEntered( MouseEvent me ) {
    }

    public void mouseExited( MouseEvent me ) {
    }

    public void mousePressed( MouseEvent me ) {
    }

    public void setState( boolean isOn ) {
        this.isOn = isOn;
    }

    public boolean getState() {
        return isOn;
    }

    public void paint( Graphics2D g ) {
        if( isOn ) {
            on.paint( g );
        }
        else {
            off.paint( g );
        }
    }
//     public static Switch createSwitch(ImageLoader i,boolean on,int x,int y,JSlider js)
//     {
// 	BufferedImage a=i.loadBufferedImage("signal-circuit/images/switches/A.jpg");
// 	BufferedImage b=i.loadBufferedImage("signal-circuit/images/switches/B.jpg");
// 	return new Switch(new BufferedImagePainter(a,x,y),new BufferedImagePainter(b,x,y),on,js);
//     }
}
