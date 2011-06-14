// Copyright 2002-2011, University of Colorado

/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:00:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.phetcommon.model.ModelElement;


public abstract class MustContain extends Constraint {

    public MustContain( ModelElement container, ModelElement contained ) {
        setSpec( new Spec( this, container, contained ) );
    }

    public abstract Object apply( Constraint.Spec spec );

    //
    // Static fields and methods
    //
    public static final Object CONTAINER = new Object();
    public static final Object CONTAINED = new Object();

    //
    // Inner classes
    //
    public class Spec extends Constraint.Spec {

        public Spec( Constraint constraint, ModelElement container, ModelElement contained ) {
            super();
            put( CONTAINER, container );
            put( CONTAINED, contained );
        }

        public ModelElement getContainer() {
            ModelElement container = (ModelElement)get( CONTAINER );
            if( container == null ) {
                throw new RuntimeException( "Null argument in method getContainer() " +
                                            "in class edu.colorado.phet.physics.MustContain.Spec" );
            }
            return container;
        }

        public ModelElement getContained() {
            ModelElement contained = (ModelElement)get( CONTAINED );
            if( contained == null ) {
                throw new RuntimeException( "Null argument in method getContained() " +
                                            "in class edu.colorado.phet.physics.MustContain.Spec" );
            }
            return contained;
        }
    }
}
