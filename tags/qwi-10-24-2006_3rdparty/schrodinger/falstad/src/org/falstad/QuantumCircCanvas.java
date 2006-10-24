/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 17, 2006
 * Time: 5:48:43 PM
 * Copyright (c) Jan 17, 2006 by Sam Reid
 */
public class QuantumCircCanvas extends Canvas {
    QuantumCircFrame pg;

    QuantumCircCanvas( QuantumCircFrame p ) {
        pg = p;
    }

    public Dimension getPreferredSize() {
        return new Dimension( 300, 400 );
    }

    public void update( Graphics g ) {
        pg.updateQuantumCirc( g );
    }

    public void paint( Graphics g ) {
        pg.updateQuantumCirc( g );
    }
}
