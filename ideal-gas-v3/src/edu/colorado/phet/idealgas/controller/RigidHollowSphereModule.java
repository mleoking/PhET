/**
 * Class: RigidHollowSphereModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 18, 2004
 * Time: 12:35:56 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import java.awt.geom.Point2D;

public abstract class RigidHollowSphereModule extends IdealGasModule {
    private static final float initialVelocity = 35;

    private HollowSphere sphere;
    private Class gasSpecies;

    public RigidHollowSphereModule( AbstractClock clock, String name, Class gasSpecies ) {
        super( clock, name );
        this.gasSpecies = gasSpecies;

        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert( getIdealGasModel(), clock.getDt() ) );

        // Set the size of the box
        final Box2D box = getIdealGasModel().getBox();
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
        sphere = new HollowSphere( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                       box.getMinY() + box.getHeight() / 2 ),
                                   new Vector2D.Double( 0, 0 ),
                                   new Vector2D.Double( 0, 0 ),
                                   100,
                                   50 );
        box.setMinimumWidth( sphere.getRadius() * 3 );

        new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
        getIdealGasModel().getBox().addContainedBody( sphere );
        addGraphic( new HollowSphereGraphic( getApparatusPanel(), sphere ), 20 );

        //        Constraint constraintSpec = new BoxMustContainParticle( box, sphere, getIdealGasModel() );
        //        sphere.addConstraint( constraintSpec );

        // Put some heavy gas outside the sphere
        for( int i = 0; i < 0; i++ ) {
            //        for( int i = 0; i < 100; i++ ) {
            double x = Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            double y = Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            double vx = (double)Math.cos( theta ) * initialVelocity;
            double vy = (double)Math.sin( theta ) * initialVelocity;
            double m = 10;
            GasMolecule p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                               new Vector2D.Double( vx, vy ),
                                               new Vector2D.Double( 0, 0 ),
                                               m );
            new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
            //            constraintSpec = new BoxMustContainParticle( box, p1, getIdealGasModel() );
            //            p1.addConstraint( constraintSpec );
            //
            //            constraintSpec = new HollowSphereMustNotContainParticle( sphere, p1 );
            //            p1.addConstraint( constraintSpec );
        }

        // Put some heavy gas in the sphere
        GasMolecule p1 = null;
//        int num = 0;
        //        int num = 6;
        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity;
                double theta = Math.random() * Math.PI * 2;
                double vx = Math.cos( theta ) * v;
                double vy = Math.sin( theta ) * v;
                if( HeavySpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new HeavySpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ),
                                           10 );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                if( LightSpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new LightSpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ),
                                           10 );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                sphere.addContainedBody( p1 );

                //                constraintSpec = new BoxMustContainParticle( box, p1 );
                //                p1.addConstraint( constraintSpec );
                //
                //                constraintSpec = new HollowSphereMustContainParticle( sphere, p1 );
                //                p1.addConstraint( constraintSpec );
            }
        }

        // Turn on gravity
        //        getIdealGasApplication().setGravityEnabled( true );
        //        getIdealGasApplication().setGravity( 15 );


        // Add the specific controls we need to the control panel
        //        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
        //        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        //        mainControlPanel.add( hsaControlPanel );
        //        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );
    }
}
