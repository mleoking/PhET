/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.view.phetgraphics;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2004
 * Time: 5:50:37 PM
 * Copyright (c) Dec 17, 2004 by Sam Reid
 */

public class FalseComponent extends Component {
    private RepaintStrategy repaintStrategy;

    public FalseComponent( RepaintStrategy repaintStrategy ) {
        this.repaintStrategy = repaintStrategy;
    }

    public void repaint( int x, int y, int width, int height ) {
        repaintStrategy.repaint( x, y, width, height );
    }
}
