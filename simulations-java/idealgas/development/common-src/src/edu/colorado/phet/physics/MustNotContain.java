/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:00:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.PhysicalEntity;

public abstract class MustNotContain extends Constraint {

    public MustNotContain( PhysicalEntity container, PhysicalEntity contained ) {
        setSpec( new MustNotContain.Spec( this, container, contained ));
    }

    public abstract Object apply( Constraint.Spec spec );

    //
    // Static fields and methods
    //
    public static final Object CONTAINER = new Object();
    public static final Object EXCLUDED = new Object();

    //
    // Inner classes
    //
    public class Spec extends Constraint.Spec {

        public Spec( Constraint constraint, PhysicalEntity container, PhysicalEntity excluded ) {
            super();
            put( CONTAINER, container );
            put( EXCLUDED, excluded );
        }

        public PhysicalEntity getContainer( ){
            PhysicalEntity container = (PhysicalEntity)get( CONTAINER );
            if( container == null ) {
                throw new RuntimeException( "Null argument in method getContainer() " +
                                            "in class edu.colorado.phet.physics.MustNotContain.Spec" );
            }
            return container;
        }

        public PhysicalEntity getExcluded(){
            PhysicalEntity contained = (PhysicalEntity)get( EXCLUDED );
            if( contained == null ) {
                throw new RuntimeException( "Null argument in method getContained() " +
                                            "in class edu.colorado.phet.physics.MustNotContain.Spec" );
            }
            return contained;
        }

    }
}
