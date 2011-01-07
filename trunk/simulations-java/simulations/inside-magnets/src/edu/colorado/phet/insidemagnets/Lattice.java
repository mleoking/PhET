// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.insidemagnets;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.colorado.phet.common.phetcommon.util.Function0;

/**
 * @author Sam Reid
 */
public class Lattice<T> {
    private HashMap<Point, T> map = new HashMap<Point, T>();
    int width;
    int height;

    public Lattice( int width, int height, Function0<T> newInstance ) {
        this.width = width;
        this.height = height;
        for ( int i = 0; i < width; i++ ) {
            for ( int j = 0; j < height; j++ ) {
                map.put( new Point( i, j ), newInstance.apply() );
            }
        }
    }

    public Lattice( int width, int height, HashMap<Point, T> map ) {
        this.width = width;
        this.height = height;
        this.map = map;
    }

    public Set<Point> getLocations() {
        return map.keySet();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public T getValue( Point location ) {
        return map.get( location );
    }

    public T getValue( int x, int y ) {
        if ( !map.containsKey( new Point( x, y ) ) ) {
            System.out.println( "looked off lattice: x = " + x + ", y = " + y );
        }
        return map.get( new Point( x, y ) );
    }

    public ArrayList<Point> getNeighborCells( Point point ) {
        ArrayList<Point> proposedPoints = new ArrayList<Point>();
        proposedPoints.add( new Point( point.x - 1, point.y ) );
        proposedPoints.add( new Point( point.x + 1, point.y ) );
        proposedPoints.add( new Point( point.x, point.y - 1 ) );
        proposedPoints.add( new Point( point.x, point.y + 1 ) );
        ArrayList<Point> points = new ArrayList<Point>();
        for ( int i = 0; i < proposedPoints.size(); i++ ) {
            if ( containsPoint( proposedPoints.get( i ) ) ) {
                points.add( proposedPoints.get( i ) );
            }
        }
        return points;
    }

    public boolean containsPoint( Point point ) {
        return point.x >= 0 && point.x < width && point.y >= 0 && point.y < height;
    }

    public ArrayList<T> getNeighborValues( Point point ) {
        ArrayList<Point> cells = getNeighborCells( point );
        ArrayList<T> neighborValues = new ArrayList<T>();
        for ( int i = 0; i < cells.size(); i++ ) {
            neighborValues.add( getValue( cells.get( i ) ) );
        }
        return neighborValues;
    }


}
