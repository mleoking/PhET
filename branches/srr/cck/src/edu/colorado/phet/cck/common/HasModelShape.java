package edu.colorado.phet.cck.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2003
 * Time: 11:02:51 AM
 * Copyright (c) Dec 17, 2003 by Sam Reid
 */
public interface HasModelShape {
    public Shape getShape();

    public void addObserver(SimpleObserver so);
}
