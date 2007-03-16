/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.util.SimpleObservable;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:11 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangularObject extends SimpleObservable {
    private QWIModel QWIModel;
    private int x;
    private int y;
    private int width;
    private int height;

    private FractionalSize fractionalSize = new FractionalSize();

    public RectangularObject( final QWIModel QWIModel, int x, int y, int width, int height ) {
        this.QWIModel = QWIModel;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        QWIModel.addListener( new QWIModel.Adapter() {
            public void sizeChanged() {
                Rectangle b = fractionalSize.getBounds( QWIModel.getWavefunction().getWidth(), QWIModel.getWavefunction().getHeight() );
                setBoundsInternal( b.x, b.y, b.width, b.height );
            }
        } );
        updateFractionalSize();
    }

    private void updateFractionalSize() {
        this.fractionalSize.update( getBounds(), QWIModel.getWavefunction().getWidth(), QWIModel.getWavefunction().getHeight() );
    }

    public Rectangle getBounds() {
        return new Rectangle( x, y, width, height );
    }

    public void translate( int dx, int dy ) {
        x += dx;
        y += dy;
        updateFractionalSize();
        notifyObservers();
    }

    private void setBoundsInternal( int x, int y, int width, int height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        notifyObservers();
    }

    public void setLocation( int x, int y ) {
        this.x = x;
        this.y = y;
        updateFractionalSize();
        notifyObservers();
    }

    public void setBounds( int x, int y, int width, int height ) {
        setSize( width, height );
        setLocation( x, y );
    }

    public void setSize( int width, int height ) {
        this.width = width;
        this.height = height;
        updateFractionalSize();
        notifyObservers();
    }

    public Point getLocation() {
        return new Point( x, y );
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Dimension getDimension() {
        return new Dimension( width, height );
    }

    public Point getCenter() {
        return new Point( x + width / 2, y + height / 2 );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    static class FractionalSize {
        double x;
        double y;
        double width;
        double height;

        public void update( Rectangle bounds, int width, int height ) {
            this.x = ( (double)bounds.x ) / width;
            this.y = ( (double)bounds.y ) / height;
            this.width = ( (double)bounds.width ) / width;
            this.height = ( (double)bounds.height ) / height;
        }

        public Rectangle getBounds( int latticeWidth, int latticeHeight ) {
            return new Rectangle( (int)( x * latticeWidth ), (int)( y * latticeHeight ), (int)( width * latticeWidth ), (int)( height * latticeHeight ) );
        }
    }
}
