/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:22:41 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class MoveElectrons implements ModelElement {
    private ElectronSetNode electronSetNode;

    public MoveElectrons( ElectronSetNode electronSetNode ) {
        this.electronSetNode = electronSetNode;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < electronSetNode.getNumElectrons(); i++ ) {
            ElectronNodeJade node = electronSetNode.getElectronNode( i );
            stepInTime( node, dt );
        }
    }

    private void stepInTime( ElectronNodeJade mover, double dt ) {
        AbstractVector2D acceleration = new Vector2D.Double();
        for( int i = 0; i < electronSetNode.getNumElectrons(); i++ ) {
            ElectronNodeJade neighbor = electronSetNode.getElectronNode( i );
            if( neighbor != mover ) {
                acceleration = acceleration.getAddedInstance( getForce( mover, neighbor ) );
            }
        }
        mover.stepInTime( acceleration, dt );
    }

    private AbstractVector2D getForce( ElectronNodeJade mover, ElectronNodeJade neighbor ) {
        AbstractVector2D vec = new Vector2D.Double( mover.getOffset(), neighbor.getOffset() );
        double k = 0.1;
        return vec.getInstanceOfMagnitude( -k / vec.getMagnitude() );
    }
}
