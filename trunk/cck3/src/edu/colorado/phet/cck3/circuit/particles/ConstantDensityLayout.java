/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CircuitListenerAdapter;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:54:25 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ConstantDensityLayout extends CircuitListenerAdapter {
//    double electronDX = .2;
    CCK3Module module;

    public ConstantDensityLayout( CCK3Module module ) {
        this.module = module;
    }

    public void branchesMoved( Branch[] branches ) {
        int num = module.getParticleSet().numParticles();
//        System.out.println( "num= " + num );
        relayout( branches );
        int numAfter = module.getParticleSet().numParticles();
//        System.out.println( "numAfter = " + numAfter );
    }

    public void relayout( Branch[] branches ) {
//
//        System.out.println( "TIME=" + System.currentTimeMillis() + ", Started layout, branches=" + Arrays.asList( branches ) );

        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            relayout( branch );
        }
    }

    private void relayout( Branch branch ) {
        ParticleSet ps = module.getParticleSet();
        ParticleSetGraphic psg = module.getParticleSetGraphic();
        Electron[] electrons = ps.removeParticles( branch );
        psg.removeGraphics( electrons );
        double offset = CCK3Module.ELECTRON_DX / 2;
        double startingPoint = offset;
        double endingPoint = branch.getLength() - offset;
        //compress or expand, but fix a particle at startingPoint and endingPoint.
        double L = endingPoint - startingPoint;
        double desiredDensity = 1 / CCK3Module.ELECTRON_DX;
        double N = L * desiredDensity;
//        int integralNumberParticles = (int)N;
//        integralNumberParticles++;
        int integralNumberParticles = (int)Math.ceil( N );
        double mydensity = ( integralNumberParticles - 1 ) / L;
//        double dx = CCK3Module.ELECTRON_DX;
        double dx = 1 / mydensity;
//        System.out.println( "dx = " + dx );
//        System.out.println( "desiredDensity = " + desiredDensity );
//        System.out.println( "mydensity = " + mydensity );
//        System.out.println( "st = " + startingPoint );
//        System.out.println( "endingPoint = " + endingPoint );
        for( int i = 0; i < integralNumberParticles; i++ ) {

            double x = i * dx + startingPoint;
//            System.out.println( "i = " + i );
//            System.out.println( "x = " + x );
            Electron e = new Electron( branch, x );
            ps.addParticle( e );
            psg.addGraphic( e );
        }
//        for( double x = startingPoint; x <= endingPoint; x += dx ) {
//            Electron e = new Electron( branch, x );
//            ps.addParticle( e );
//            psg.addGraphic( e );
//        }
    }
}
