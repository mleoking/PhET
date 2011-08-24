// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Sam Reid
 */
public class Formula implements Iterable<Class<? extends Particle>> {
    public final Class<? extends Particle>[] elements;

    public Formula( Class<? extends Particle>... elements ) {
        this.elements = elements;
    }

    public Iterator<Class<? extends Particle>> iterator() {
        return Arrays.asList( elements ).iterator();
    }
}