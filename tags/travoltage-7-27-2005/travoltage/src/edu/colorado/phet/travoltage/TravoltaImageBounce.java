package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.propagators.PositionUpdate;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;

public class TravoltaImageBounce implements Propagator {

    Raster r;
    int[] pixel;
    PositionUpdate pu = new PositionUpdate();
    PixelVelocity pv;
//    ParticlePanel pp;
//    AudioClip[] ac;

    public TravoltaImageBounce( BufferedImage bi, PixelVelocity pv ) {
//        this.ac = ac;
//        this.pp = pp;
        this.pv = pv;
        this.r = bi.getData();
        this.pixel = new int[r.getNumBands()];
    }

    public void propagate( double dt, Particle p ) {
        //edu.colorado.phet.common.util.Debug.traceln();
        DoublePoint pos = p.getPosition();
        int x = (int)pos.getX();
        int y = (int)pos.getY();
        boolean inside = false;
        if( x >= r.getWidth() || y >= r.getHeight() || x <= 0 || y <= 0 ) //turn around immediately, outside bounds.
        {
            DoublePoint v = p.getVelocity();
            v = v.multiply( -1 );
            p.setVelocity( v );
            pu.propagate( 1.2 * dt, p );
        }
        else {
            pixel = r.getPixel( x, y, pixel );
            if( pixel == null ) {
                throw new RuntimeException( "No pixel defined for r=" + r + ", x=" + x + ", y=" + y );
            }
            inside = isInside( pixel );
            DoublePoint v = pv.getVelocity( pixel );
            if( v != null ) {
                DoublePoint curVel = p.getVelocity();
                double mag = curVel.getLength();
                double vMag = v.getLength();
                v = v.multiply( mag / vMag );
                //  				DoublePoint v=p.getVelocity();
                //  				v=v.multiply(-1);
                p.setVelocity( v );
                pu.propagate( 1.2 * dt, p );
            }
        }
    }

    public boolean isInside( int[] pixel ) {
        for( int i = 0; i < pixel.length; i++ ) {
            if( pixel[i] != 0 ) {
                return false;
            }
        }
        return true;
    }
}
