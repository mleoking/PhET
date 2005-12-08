/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import edu.colorado.phet.solublesalts.model.crystal.Lattice;

import java.util.*;

/**
 * Salt
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Salt {
    private Lattice lattice;
    private Class anionClass;
    private Class cationClass;
    private Component[] components;

    protected Salt( List components, Lattice lattice, Class anionClass, Class cationClass ) {
        this.components = new Component[components.size()];
        for( int i = 0; i < components.size(); i++ ) {
            Component component = (Component)components.get( i );
            this.components[i] = component;
        }
        // sort the array of components
        boolean done = false;
        while( !done ) {
            done = true;
            for( int i = 0; i < this.components.length - 1; i++ ) {
                for( int j = i + 1; j < this.components.length; j++ ) {
                    if( this.components[i].getLatticeUnitFraction().intValue()
                        > this.components[j].getLatticeUnitFraction().intValue() ) {
                        Component temp = this.components[i];
                        this.components[i] = this.components[j];
                        this.components[j] = temp;
                        done = false;
                    }
                }
            }
        }

        this.lattice = lattice;
        this.anionClass = anionClass;
        this.cationClass = cationClass;
    }

    /**
     * Returns an array of the Components in the salt. The array is sorted in order of the
     * number of instances of the component in the salt. Eg, in  Cu(OH)2, the Cu component
     * comes before (OH)2 in the array.
     *
     * @return
     */
    public Component[] getComponents() {
        return components;
    }

    public Lattice getLattice() {
        return lattice;
    }

    public Class getAnionClass() {
        return anionClass;
    }

    public Class getCationClass() {
        return cationClass;
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A descriptor for a component in the salt. A component consists of
     * an ion class and the number of instances of that ion in a unit of
     * the lattice structure.
     */
    public static class Component {
        // The number of instances of this component in a lattice uit
        Integer latticeUnitFraction;
        // The class of this component
        Class ionClass;

        public Component( Class ionClass, Integer latticeUnitFraction ) {
            this.latticeUnitFraction = latticeUnitFraction;
            this.ionClass = ionClass;
        }

        public Integer getLatticeUnitFraction() {
            return latticeUnitFraction;
        }

        public Class getIonClass() {
            return ionClass;
        }
    }
}
