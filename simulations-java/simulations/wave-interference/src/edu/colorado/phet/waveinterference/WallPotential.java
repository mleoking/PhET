// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import edu.colorado.phet.waveinterference.model.Potential;
import edu.colorado.phet.waveinterference.model.WaveModel;

/**
 * Created by: Sam
 * Feb 7, 2008 at 1:21:38 PM
 */
public class WallPotential implements Potential {
    private Point srcPoint;
    private Point dstPoint;
    private WaveModel waveModel;
    private int thickness = 3;
    private Shape shape;

    public WallPotential( Point srcPoint, Point dstPoint, WaveModel waveModel ) {
        this.srcPoint = srcPoint;
        this.dstPoint = dstPoint;
        this.waveModel = waveModel;
        update();
    }

    private void update() {
        Line2D.Double line = new Line2D.Double( srcPoint, dstPoint );
        shape = new BasicStroke( thickness ).createStrokedShape( line );
    }

    public void setSrcPoint( Point srcPoint ) {
        this.srcPoint = srcPoint;
        update();
        notifyListener();
    }

    public void setDstPoint( Point dstPoint ) {
        this.dstPoint = dstPoint;
        update();
        notifyListener();
    }

    public void setThickness( int thickness ) {
        this.thickness = thickness;
        update();
        notifyListener();
    }

    public int getThickness() {
        return thickness;
    }

    public double getPotential( int i, int j, int time ) {
        return waveModel.containsLocation( i, j ) && shape.contains( i, j ) ? 100 : 0;
    }

    public Point getSource() {
        return srcPoint;
    }

    public Point getDestination() {
        return dstPoint;
    }

    public void translate( int latticeDX, int latticeDY ) {
        this.srcPoint = new Point( srcPoint.x + latticeDX, srcPoint.y + latticeDY );
        this.dstPoint = new Point( dstPoint.x + latticeDX, dstPoint.y + latticeDY );
        update();
        notifyListener();
    }

    public static interface Listener {
        void changed();
    }

    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).changed();
        }
    }
}
