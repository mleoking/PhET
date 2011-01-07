// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.model.physics.ParticleStage;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 1:45:23 PM
 */
public class EnergySkateParkSplineListAdapter extends ParticleStage {
    private EnergySkateParkModel energySkateParkModel;

    public EnergySkateParkSplineListAdapter( EnergySkateParkModel energySkateParkModel ) {
        this.energySkateParkModel = energySkateParkModel;
        energySkateParkModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void splineCountChanged() {
                update();
            }

            public void floorChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        clear();
        for( int i = 0; i < energySkateParkModel.getNumSplines(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = energySkateParkModel.getSpline( i );
            addCubicSpline2D( energySkateParkSpline.getParametricFunction2D() );
        }
        if( energySkateParkModel.getFloor() != null ) {
            addCubicSpline2D( energySkateParkModel.getFloor().getParametricFunction2D() );
        }
    }

    public boolean isSplineUserControlled() {
        for( int i = 0; i < energySkateParkModel.getNumSplines(); i++ ) {
            if( energySkateParkModel.getSpline( i ).isUserControlled() ) {
                return true;
            }
        }
        return false;
    }

}
