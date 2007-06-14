/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/math/ModelViewTransform1D.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.9 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.math;

import java.util.ArrayList;

/**
 * Performs a linear transform between model and view coordinates.
 */
public class ModelViewTransform1D {
    private Function.LinearFunction modelToView;
    private ArrayList transformListeners = new ArrayList();

    public interface Observer {
        void transformChanged( ModelViewTransform1D transform );
    }

    public ModelViewTransform1D( double minModel, double maxModel, int minView, int maxView ) {
        this.modelToView = new Function.LinearFunction( minModel, maxModel, minView, maxView );
    }

    public int modelToView( double x ) {
        return (int)modelToView.evaluate( x );
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
        for( int i = 0; i < transformListeners.size(); i++ ) {
            Observer observer = (Observer)transformListeners.get( i );
            observer.transformChanged( this );
        }
    }

    public void setModelRange( double minModel, double maxModel ) {
        if( modelToView.getMinInput() != minModel || modelToView.getMinInput() != maxModel ) {
            modelToView.setInput( minModel, maxModel );
            update();
        }
    }

    public void setViewRange( int minView, int maxView ) {
        if( modelToView.getMinOutput() != minView && modelToView.getMaxOutput() != maxView ) {
            modelToView.setOutput( minView, maxView );
            update();
        }
    }
}