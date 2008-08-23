/*  */
package edu.colorado.phet.forces1d.phetcommon.math;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 14, 2004
 * Time: 8:22:30 PM
 */

public class ModelViewTransform1D {
    private Function.LinearFunction modelToView;
    private ArrayList transformListeners = new ArrayList();
    private double minModel;
    private double maxModel;

    public interface Observer {
        void transformChanged( ModelViewTransform1D transform );
    }

    public ModelViewTransform1D( double minModel, double maxModel, int minView, int maxView ) {
        this.modelToView = new Function.LinearFunction( minModel, maxModel, minView, maxView );
        this.minModel = minModel;
        this.maxModel = maxModel;
    }

    public int modelToView( double x ) {
        return (int) modelToView.evaluate( x );
    }

    public int modelToViewDifferential( double dModel ) {
        return modelToView( dModel ) - modelToView( 0 );
    }

    public double viewToModelDifferential( int dView ) {
        return viewToModel( dView ) - viewToModel( 0 );
    }

    public double viewToModel( int x ) {
        return modelToView.createInverse().evaluate( x );
    }

    public void addListener( Observer observer ) {
        transformListeners.add( observer );
    }

    public void update() {
        for ( int i = 0; i < transformListeners.size(); i++ ) {
            Observer observer = (Observer) transformListeners.get( i );
            observer.transformChanged( this );
        }
    }

    public void setModelRange( double minModel, double maxModel ) {
        modelToView.setInput( minModel, maxModel );
        update();//TODO check to see that we changed before firing an update.
    }

    public void setViewRange( int minView, int maxView ) {
        modelToView.setOutput( minView, maxView );
        update();
    }
}
