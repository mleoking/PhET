package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.graphics.BoundedGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 10:50:53 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public interface ITextGraphic extends BoundedGraphic {
    Rectangle getBounds();

    void setPosition( int x, int y );
}
