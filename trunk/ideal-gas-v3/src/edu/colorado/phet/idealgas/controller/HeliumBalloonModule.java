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
import edu.colorado.phet.idealgas.model.*;

import java.awt.geom.Point2D;

public class HeliumBalloonModule extends IdealGasModule {

    private static final float initialVelocity = 30;

    private HollowSphere balloon;
//    private HollowSphereControlPanel hsaControlPanel;

    public HeliumBalloonModule( AbstractClock clock ) {
        super( clock, "Helium Balloon" );

        float xOrigin = 200;
        float yOrigin = 250;
        float xDiag = 434;
        float yDiag = 397;

//        Box2D box = application.getIdealGasSystem().getBox();

        balloon = new Balloon( new Point2D.Double( 300, 350 ),
                               new Vector2D.Double( 0, 0 ),
                               new Vector2D.Double( 0, 0 ),
                               100,
                               50,
                               getIdealGasModel().getBox(),
                               clock );
        getIdealGasModel().addModelElement( balloon );
        Constraint constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), balloon,
                                                                getIdealGasModel() );
        balloon.addConstraint( constraintSpec );

//        for( int i = 0; i < 0; i++ ) {
        for( int i = 0; i < 100; i++ ) {
//        for( int i = 0; i < 50; i++ ) {
            float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            float vx = (float)Math.cos( theta ) * initialVelocity;
            float vy = (float)Math.sin( theta ) * initialVelocity;
            float m = 10;
            IdealGasParticle p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                                    new Vector2D.Double( vx, vy ),
                                                    new Vector2D.Double( 0, 0 ),
                                                    m );
            getIdealGasModel().addModelElement( p1 );
            constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), p1, getIdealGasModel() );
            p1.addConstraint( constraintSpec );

            constraintSpec = new HollowSphereMustNotContainParticle( balloon, p1, getIdealGasModel() );
            p1.addConstraint( constraintSpec );
        }

        // Put some light gas inside the balloon
        IdealGasParticle p1 = null;
//        int num = 0;
//        int num = 1;
        int num = 6;
//        int num = 4;
        int sign = 1;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity * HeavySpecies.getMoleculeMass() / LightSpecies.getMoleculeMass();
                double theta = Math.random() * Math.PI * 2;
                float vx = (float)( Math.cos( theta ) * v );
                float vy = (float)( Math.sin( theta ) * v );
                p1 = new LightSpecies( new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                       new Vector2D.Double( vx, vy ),
                                       new Vector2D.Double( 0, 0 ),
                                       10 );
                balloon.addContainedBody( p1 );
                getIdealGasModel().addModelElement( p1 );

                constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), p1, getIdealGasModel() );
                p1.addConstraint( constraintSpec );

                constraintSpec = new HollowSphereMustContainParticle( balloon, p1, getIdealGasModel() );
                p1.addConstraint( constraintSpec );
            }
        }
//        application.run();

        // Turn on gravity
//        getIdealGasApplication().setGravityEnabled( true );
//        getIdealGasApplication().setGravity( 15 );

        // Set the size of the box
//        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );

        // Set up the door for the box
//        ResourceLoader loader = new ResourceLoader();
//        Image doorImg = loader.loadImage( IdealGasConfig.DOOR_IMAGE_FILE ).getImage();
//        doorGraphicImage = new BoxDoorGraphic(
//                doorImg,
//                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175,
//                IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 175,
//                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175 );
//        this.addGraphic( doorGraphicImage, -6 );


        // Add the specific controls we need to the control panel
//        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
//        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
//        mainControlPanel.add( hsaControlPanel );
//        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );
    }

//    public void deactivate() {
//        super.deactivate();
//
//        // Remove our specifc controls from the control panel
//        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
//        mainControlPanel.remove( hsaControlPanel );
//    }

//    protected GasMolecule pumpGasMolecule() {
//        GasMolecule newMolecule = super.pumpGasMolecule();
//        Constraint constraintSpec = new  HollowSphereMustNotContainParticle( balloon, newMolecule );
//        newMolecule.addConstraint( constraintSpec );
//
//        constraintSpec = new BoxMustContainParticle( getIdealGasSystem().getBox(), newMolecule );
//        newMolecule.addConstraint( constraintSpec );
//
//        return newMolecule;
//    }
}
