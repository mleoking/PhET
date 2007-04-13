/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 9:43:52 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class MutableColor {
    private Color color;
    private ArrayList listeners = new ArrayList();

    public MutableColor( Color color ) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setColor( Color color ) {
        this.color = color;
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.colorChanged();
        }
    }

    public int getRed() {
        return color.getRed();
    }

    public int getGreen() {
        return color.getGreen();
    }

    public int getBlue() {
        return color.getBlue();
    }

    public static interface Listener {
        public void colorChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
