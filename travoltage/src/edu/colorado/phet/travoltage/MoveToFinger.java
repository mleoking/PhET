/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 2, 2006
 * Time: 12:53:51 AM
 * Copyright (c) Jul 2, 2006 by Sam Reid
 */

public class MoveToFinger implements ModelElement {
    private TravoltageModule module;

    public MoveToFinger( TravoltageModule module ) {
        this.module = module;
    }

    public void stepInTime( double dt ) {
        JadeElectronSet electronSet = module.getTravoltageModel().getJadeElectronSet();
        for( int i = 0; i < electronSet.getNumElectrons(); i++ ) {
            stepInTime( electronSet.getJadeElectron( i ), dt );
        }
    }

    private void stepInTime( JadeElectron jadeElectron, double dt ) {
        ArmNode armNode = module.getArmNode();
        Point2D dst = armNode.getGlobalFingertipPoint();
        double speed = 10;
        Point2D loc = jadeElectron.getPosition();
        AbstractVector2D vec = new Vector2D.Double( loc, dst );
        vec = vec.getInstanceOfMagnitude( speed );
        Point2D newLoc = vec.getDestination( loc );
        jadeElectron.setLocation( newLoc.getX(), newLoc.getY() );
    }
}
