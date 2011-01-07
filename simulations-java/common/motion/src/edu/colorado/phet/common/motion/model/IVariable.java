// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion.model;

/**
 * User: Sam Reid
 * Date: Dec 29, 2006
 * Time: 9:15:36 AM
 */

public interface IVariable {
    void setValue( double value );

    double getValue();

    public void addListener( Listener listener );

    public void removeListener( Listener listener );

    public static interface Listener {
        void valueChanged();
    }
}
