// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.test;

import java.awt.Dimension;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.rutherfordscattering.model.AlphaParticle;
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtom;
import edu.colorado.phet.rutherfordscattering.model.RutherfordScattering;


public class TestRutherfordScattering {

    public static void main( String[] args ) {
        
        // Initial conditions used in Sam McKagan's Mathematica example
        final double dt = 1; // clock step
        final double x0 = 0.01;
        final int L = 700; // box size
        final int pd = 79; // default number of protons
        final int p = 20; // current number of protons
        final double sd = 10; // default particle speed
        final double s0 = 12; // initial particle speed
        
        Dimension boxSize = new Dimension( L, L );
        
        Point2D particlePosition = new Point2D.Double( x0, 0 ); // bottom center of the box is (0,0)
        double particleOrientation = Math.toRadians( -90 ); // heading straight up
        AlphaParticle particle = new AlphaParticle( particlePosition, particleOrientation, s0, sd );
        
        Point2D atomPosition = new Point2D.Double( 0, -L/2 ); // center of the box
        double atomOrientation = 0; // don't care
        final double electronAngularSpeed = Math.toRadians( 1 ); // don't care
        IntegerRange protonsRange = new IntegerRange( 20, 100, pd ); // number of protons: min,max,default
        IntegerRange neutronsRange = new IntegerRange( 20, 150, 118 ); // don't care
        RutherfordAtom atom = new  RutherfordAtom( atomPosition, atomOrientation, electronAngularSpeed, protonsRange, neutronsRange, boxSize );
        atom.setNumberOfProtons( p );
        
        for ( int i = 0; i < 100; i++ ) {
            RutherfordScattering.moveParticle( dt, particle, atom, boxSize );
        }
    }
}
