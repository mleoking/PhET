/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.particles;

import edu.colorado.phet.cck3.CCKModule;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.BranchSet;
import edu.colorado.phet.cck3.circuit.CircuitListenerAdapter;
import edu.colorado.phet.cck3.model.CCKModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:54:25 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ConstantDensityLayout extends CircuitListenerAdapter {
    private CCKModule module;
    boolean dolayout = true;
//    boolean dolayout=false;

    public ConstantDensityLayout( CCKModule module ) {
        this.module = module;
    }

    public void branchesMoved( Branch[] branches ) {
        if( !dolayout ) {
            return;
        }
//        ArrayList relay=new ArrayList( );
        BranchSet bs = new BranchSet( module.getCircuit(), branches );
        for( int i = 0; i < branches.length; i++ ) {
            bs.addBranches( module.getCircuit().getStrongConnections( branches[i].getStartJunction() ) );
            bs.addBranches( module.getCircuit().getStrongConnections( branches[i].getEndJunction() ) );
        }
        Branch[] torelayout = bs.getBranches();
        relayout( torelayout );
    }

    public void branchesMovedOrig( Branch[] branches ) {
//        ArrayList relay=new ArrayList( );


        ArrayList moved = new ArrayList( Arrays.asList( branches ) );
//        int num = module.getParticleSet().numParticles();
//        System.out.println( "num= " + num );
        relayout( branches );
        ArrayList branchesToRelayout = new ArrayList();
        Branch[] all = module.getCircuit().getBranches();
        for( int i = 0; i < all.length; i++ ) {
            Branch branch = all[i];
            if( branch.getCurrent() != 0 ) {
                branchesToRelayout.add( branch );
            }
            else if( moved.contains( branch ) ) {
                branchesToRelayout.add( branch );
            }
        }
        Branch[] torelayout = (Branch[])branchesToRelayout.toArray( new Branch[0] );
        relayout( torelayout );
//        int numAfter = module.getParticleSet().numParticles();
//        System.out.println( "numAfter = " + numAfter );
    }

    public void relayout( Branch[] branches ) {
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
//        Electron[] rem = ps.getParticles( branch );
//        if( rem.length != 0 ) {
//            System.out.println( "Didn't remove all particles." );
//        }
        if( module.isElectronsVisible() ) {
            double offset = CCKModel.ELECTRON_DX / 2;
            double startingPoint = offset;
            double endingPoint = branch.getLength() - offset;
            //compress or expand, but fix a particle at startingPoint and endingPoint.
            double L = endingPoint - startingPoint;
            double desiredDensity = 1 / CCKModel.ELECTRON_DX;
            double N = L * desiredDensity;
            int integralNumberParticles = (int)Math.ceil( N );
            double mydensity = ( integralNumberParticles - 1 ) / L;
            double dx = 1 / mydensity;
            if( mydensity == 0 ) {
                integralNumberParticles = 0;
            }
            for( int i = 0; i < integralNumberParticles; i++ ) {
                double x = i * dx + startingPoint;
                Electron e = new Electron( branch, x );
                ps.addParticle( e );
                psg.addGraphic( e );
            }
        }
    }
}
