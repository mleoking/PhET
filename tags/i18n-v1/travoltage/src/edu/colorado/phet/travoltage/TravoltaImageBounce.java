package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phys2d.DoublePoint;
import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.Propagator;
import edu.colorado.phet.common.phys2d.propagators.PositionUpdate;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.util.Random;

public class TravoltaImageBounce implements Propagator {
    static final Random randy = new Random();

    BufferedImage bi;
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
        this.bi = bi;
        this.r = bi.getData();
        this.pixel = new int[r.getNumBands()];
    }

    public void setImage( BufferedImage bi ) {
        this.bi = bi;
        this.r = bi.getData();
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

    private void exitElectron() {
//  				//edu.colorado.phet.common.util.Debug.traceln("EXIT!");
//  				/*Remove the edu.colorado.phet.common.*/
//  			sys.remove(p);
//  			for (int k=0;k<sys.numLaws();k++)
//  			    if (sys.lawAt(k) instanceof ParticleContainer)
//  				((ParticleContainer)sys.lawAt(k)).remove(p);
			
//  			DoublePoint right=new DoublePoint(12,0);
//  			p.setVelocity(right);
//  			pu.propagate(1.2*dt,p);
//  			DoublePoint newPos=p.getPosition();
//  			if (newPos.getX()>r.getWidth()-10)
//  			    {
//  			    }
//  			pp.repaint();
//  			pp.remove(p);
			
//  			int rand=randy.nextInt(ac.length);
//  			AudioClip a=ac[rand];
//  			a.play();


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
