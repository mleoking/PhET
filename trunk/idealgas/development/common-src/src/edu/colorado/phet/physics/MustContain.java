/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 10, 2003
 * Time: 8:00:09 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.physics;

import edu.colorado.phet.physics.body.PhysicalEntity;

public abstract class MustContain extends Constraint {

    public MustContain( PhysicalEntity container, PhysicalEntity contained ) {
        setSpec( new Spec( this, container, contained ));
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

        public Spec( Constraint constraint, PhysicalEntity container, PhysicalEntity contained ) {
            super();
            put( CONTAINER, container );
            put( CONTAINED, contained );
        }

        public PhysicalEntity getContainer( ){
            PhysicalEntity container = (PhysicalEntity)get( CONTAINER );
            if( container == null ) {
                throw new RuntimeException( "Null argument in method getContainer() " +
                                            "in class edu.colorado.phet.physics.MustContain.Spec" );
            }
            return container;
        }

        public PhysicalEntity getContained(){
            PhysicalEntity contained = (PhysicalEntity)get( CONTAINED );
            if( contained == null ) {
                throw new RuntimeException( "Null argument in method getContained() " +
                                            "in class edu.colorado.phet.physics.MustContain.Spec" );
            }
            return contained;
        }

    }
}
