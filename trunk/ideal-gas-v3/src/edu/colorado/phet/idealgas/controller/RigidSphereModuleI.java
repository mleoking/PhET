/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import java.awt.geom.Point2D;

public class RigidSphereModuleI extends IdealGasModule {

    private static final float initialVelocity = 35;

    private HollowSphere sphere;

    public RigidSphereModuleI( AbstractClock clock ) {
        super( clock, "Rigid Hollow Sphere" );

        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        Box2D box = getIdealGasModel().getBox();
        sphere = new HollowSphere( new Point2D.Double( ( box.getMinX() + box.getMaxX() ) / 2,
                                                       ( box.getMinY() + box.getMaxY() ) / 2 ),
                                   new Vector2D.Double( 0, 0 ),
                                   new Vector2D.Double( 0, 0 ),
                                   100,
                                   50 );

        new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
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
        int num = 0;
        //        int num = 6;
        //        int num = 4;
        int sign = 1;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                sign *= -1;
                double v = initialVelocity;
                double theta = Math.random() * Math.PI * 2;
                double vx = Math.cos( theta ) * v;
                double vy = Math.sin( theta ) * v;
                p1 = new HeavySpecies( //                p1 = new HeavySpecies(
                        new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                        //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                        new Vector2D.Double( vx, vy ),
                        new Vector2D.Double( 0, 0 ),
                        10 );
                //                sphere.addContainedBody( p1 );
                new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();

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

        // Set the size of the box
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );

        // Add the specific controls we need to the control panel
        //        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
        //        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        //        mainControlPanel.add( hsaControlPanel );
        //        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );
    }
}
