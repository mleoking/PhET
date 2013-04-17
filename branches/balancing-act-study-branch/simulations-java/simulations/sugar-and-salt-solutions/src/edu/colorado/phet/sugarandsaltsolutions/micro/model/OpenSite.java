// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Particle;

/**
 * Location in a crystal where a new atom could attach.
 *
 * @author Sam Reid
 */
public class OpenSite<T extends Particle> {

    //Position relative to the origin of the crystal
    public final Vector2D relativePosition;

    //Absolute location for checking bounds against water bounds
    public final Shape shape;

    //Absolute position in the model
    public final Vector2D absolutePosition;

    private final Function0<T> newInstance;

    public OpenSite( Vector2D relativePosition, Shape shape, Function0<T> newInstance, Vector2D absolutePosition ) {
        this.relativePosition = relativePosition;
        this.shape = shape;
        this.newInstance = newInstance;
        this.absolutePosition = absolutePosition;
    }

    public Constituent<T> toConstituent() {
        return new Constituent<T>( newInstance.apply(), relativePosition );
    }

    public boolean matches( Particle other ) {
        return newInstance.apply().getClass().equals( other.getClass() );
    }

    public boolean matches( Class<? extends Particle> type ) {
        return newInstance.apply().getClass().equals( type );
    }
}
