package edu.colorado.phet.bernoulli.watertower;

import edu.colorado.phet.bernoulli.Vessel;
import edu.colorado.phet.bernoulli.Water;

import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Aug 21, 2003
 * Time: 2:02:36 AM
 * Copyright (c) Aug 21, 2003 by Sam Reid
 */
public class RectangularTank extends Vessel {
    double x;
    double y;
    double width;
    double height;
    double volume;
    double waterVolume;

    public RectangularTank( double x, double y, double width, double height ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.volume = width * height;
    }

    public double getWaterVolume() {
        return waterVolume;
    }

    public void setWaterVolume( double waterVolume ) {
        if( waterVolume > volume ) {
            throw new RuntimeException( "Water volume exceeds max volume." );
        }
        this.waterVolume = waterVolume;
        updateObservers();
    }

    public double getVolume() {
        return volume;
    }

    public boolean isFullOfWater() {
        return volume == waterVolume;
    }

    public void fillTank() {
        this.waterVolume = volume;
        updateObservers();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public void setWidth( double width ) {
        this.width = width;
        this.volume = width * height;
        updateObservers();
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    public double getAirVolume() {
        return volume - waterVolume;
    }

    public Rectangle2D.Double getRectangle2D() {
        return new Rectangle2D.Double( x, y, width, height );
    }

    public void setHeight( double height ) {
        this.height = height;
        this.volume = width * height;
        updateObservers();
    }

    public void setRectangle( Rectangle2D.Double rectangle ) {
        this.height = rectangle.height;
        this.width = rectangle.width;
        this.x = rectangle.x;
        this.y = rectangle.y;
        updateObservers();
    }

    public double getWaterHeight() {
        return ( waterVolume ) / width;
    }

    public boolean isPrettyMuchFullOfWater() {
        return Math.abs( waterVolume - volume ) < .0001;
    }

    //TODO allocating lots o' memory here.
    public boolean waterContainsPoint( double x, double y ) {
        return new Rectangle2D.Double( this.x, this.y, width, getWaterHeight() ).contains( x, y );
    }

    public double getPressure( double x, double y ) {
//        double h = y - this.y;
        double topOfWater = this.y + this.getWaterHeight();
        double h = topOfWater - y;
        return Water.rho * Water.g * h;
    }

    public void removeWater( double dv ) {
        setWaterVolume( getWaterVolume() - dv );
    }

}
