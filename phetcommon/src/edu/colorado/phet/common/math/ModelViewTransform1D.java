/* Copyright 2004, Sam Reid */
package edu.colorado.phet.common.math;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 14, 2004
 * Time: 8:22:30 PM
 * Copyright (c) Dec 14, 2004 by Sam Reid
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
        modelToView.setInput( minModel, maxModel );
        update();//TODO check to see that we changed before firing an update.
    }

    public void setViewRange( int minView, int maxView ) {
        modelToView.setOutput( minView, maxView );
        update();
    }
}
