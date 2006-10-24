/* Copyright 2004, Sam Reid */
package org.falstad;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:20:11 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
class RippleCanvas extends Canvas {
    RippleFrame pg;

    RippleCanvas( RippleFrame p ) {
        pg = p;
    }

    public Dimension getPreferredSize() {
        return new Dimension( 300, 400 );
    }

    public void update( Graphics g ) {
        pg.updateRipple( g );
    }

    public void paint( Graphics g ) {
        pg.updateRipple( g );
    }
}
