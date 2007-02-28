package edu.colorado.phet.bernoulli.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 11:42:56 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class CenteredRectangle {
    int width;
    int height;

    public CenteredRectangle( int width, int height ) {
        this.width = width;
        this.height = height;
    }

    public Rectangle center( Point p ) {
        return new Rectangle( p.x - width / 2, p.y - height / 2, width, height );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth( int width ) {
        this.width = width;
    }

    public void setHeight( int height ) {
        this.height = height;
    }
}
