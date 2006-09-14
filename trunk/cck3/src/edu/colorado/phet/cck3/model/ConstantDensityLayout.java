/** Sam Reid*/
package edu.colorado.phet.cck3.model;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Jun 8, 2004
 * Time: 1:54:25 PM
 * Copyright (c) Jun 8, 2004 by Sam Reid
 */
public class ConstantDensityLayout extends CircuitListenerAdapter {
    private boolean dolayout = true;
    private Circuit circuit;
    private ParticleSet particleSet;
    private boolean electronsVisible = true;

    public ConstantDensityLayout( Circuit circuit, ParticleSet particleSet ) {
        this.circuit = circuit;
        this.particleSet = particleSet;
    }

    public void branchesMoved( Branch[] branches ) {
        if( !dolayout ) {
            return;
        }
//        ArrayList relay=new ArrayList( );
        BranchSet bs = new BranchSet( getCircuit(), branches );
        for( int i = 0; i < branches.length; i++ ) {
            bs.addBranches( getCircuit().getStrongConnections( branches[i].getStartJunction() ) );
            bs.addBranches( getCircuit().getStrongConnections( branches[i].getEndJunction() ) );
        }
        Branch[] torelayout = bs.getBranches();
        layoutElectrons( torelayout );
    }

    private Circuit getCircuit() {
        return circuit;
    }

    public void branchesMovedOrig( Branch[] branches ) {
        ArrayList moved = new ArrayList( Arrays.asList( branches ) );
        layoutElectrons( branches );
        ArrayList branchesToRelayout = new ArrayList();
        Branch[] all = getCircuit().getBranches();
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
        layoutElectrons( torelayout );
    }

    public void layoutElectrons( Branch[] branches ) {
        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            layoutElectrons( branch );
        }
    }

    private void layoutElectrons( Branch branch ) {
        particleSet.removeParticles( branch );

        if( getElectronsVisible() ) {
            double offset = CCKModel.ELECTRON_DX / 2;
            double endingPoint = branch.getLength() - offset;
            //compress or expand, but fix a particle at startingPoint and endingPoint.
            double L = endingPoint - offset;
            double desiredDensity = 1 / CCKModel.ELECTRON_DX;
            double N = L * desiredDensity;
            int integralNumberParticles = (int)Math.ceil( N );
            double mydensity = ( integralNumberParticles - 1 ) / L;
            double dx = 1 / mydensity;
            if( mydensity == 0 ) {
                integralNumberParticles = 0;
            }
            for( int i = 0; i < integralNumberParticles; i++ ) {
                particleSet.addParticle( new Electron( branch, i * dx + offset ) );
            }
        }
    }

    private boolean getElectronsVisible() {
        return electronsVisible;
    }
}
