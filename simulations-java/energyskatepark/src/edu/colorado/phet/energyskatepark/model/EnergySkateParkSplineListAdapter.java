package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.test.phys1d.ParticleStage;

/**
 * Author: Sam Reid
 * Mar 16, 2007, 1:45:23 PM
 */
public class EnergySkateParkSplineListAdapter extends ParticleStage {
    private EnergySkateParkModel energySkateParkModel;

    public EnergySkateParkSplineListAdapter( EnergySkateParkModel energySkateParkModel ) {
        this.energySkateParkModel = energySkateParkModel;
        energySkateParkModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void splinesChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        clear();
        for( int i = 0; i < energySkateParkModel.numSplineSurfaces(); i++ ) {
            EnergySkateParkSpline energySkateParkSpline = energySkateParkModel.splineSurfaceAt( i );
            addCubicSpline2D( energySkateParkSpline.getParametricFunction2D() );
        }
    }
    
}
