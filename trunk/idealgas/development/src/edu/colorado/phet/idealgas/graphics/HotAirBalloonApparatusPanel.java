/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 3:35:40 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.physics.collision.Box2D;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.idealgas.physics.*;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.HotAirBalloonControlPanel;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.Constraint;
import edu.colorado.phet.physics.MustContain;
import edu.colorado.phet.physics.MustNotContain;
import edu.colorado.phet.graphics.util.ResourceLoader;
import edu.colorado.phet.graphics.MovableImageGraphic;

import javax.swing.*;
import java.awt.*;

public class HotAirBalloonApparatusPanel extends BaseIdealGasApparatusPanel {

    private HotAirBalloon balloon;
    private HotAirBalloonControlPanel habControlPanel = new HotAirBalloonControlPanel();

    public HotAirBalloonApparatusPanel( PhetApplication application ) {
        super( application, "Hot Air Balloon" );
    }

    /**
     *
     */
    public void activate() {
        super.activate();

        // Add the specific controls we need for the hot air balloon
        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        mainControlPanel.add( habControlPanel );

        float xOrigin = 200;
        float yOrigin = 250;
        float xDiag = 434;
        float yDiag = 397;

        Box2D box = getIdealGasSystem().getBox();

        balloon = new HotAirBalloon(
                        new Vector2D( 400, 350 ),
                        new Vector2D( 0, 0 ),
                        new Vector2D( 0, 0 ),
                        200, 50,
                        60 );

        // Give the balloon a high layer number so it always is over whatever
        // particles are pumped into the box
        getIdealGasApplication().addBody( balloon, 6 );
        Constraint constraintSpec = new BoxMustContainParticle( box, balloon );
        balloon.addConstraint( constraintSpec );

        // Put some particles in the box outside the balloonn
//        for( int i = 0; i < 0; i++ ) {
//        for( int i = 0; i < 1; i++ ) {
        for( int i = 0; i < 50; i++ ) {
            float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            float vx = (float)Math.random() * 80;
            float vy = (float)Math.random() * 80;
            float m = 10;
            Particle p1 = new HeavySpecies(
                    new Vector2D( x, y ),
                    new Vector2D( vx, vy ),
                    new Vector2D( 0, 0 ),
                    m );
            getIdealGasApplication().addBody( p1, 2 );
            constraintSpec = new BoxMustContainParticle( box, p1 );
            p1.addConstraint( constraintSpec );

            constraintSpec = new HotAirBalloonMustNotContainParticle( balloon, p1 );
            p1.addConstraint( constraintSpec );
        }

        // Put some particles inside the balloon
        Particle p1 = null;
//        int num = 1;
        int num = 5;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {

                float vx = 50;
                float vy = 0;
//                float vx = (float)Math.random() * 50;
//                float vy = (float)Math.random() * 50;

                float m = 10;
                p1 = new HeavySpecies(
                        new Vector2D( 350 + i * 10, 350 + j * 10 ),
                        new Vector2D( vx, vy ),
                        new Vector2D( 0, 0 ),
                        m );
                balloon.addContainedBody( p1 );
                getIdealGasApplication().addBody( p1, 2 );

                constraintSpec = new BoxMustContainParticle( box, p1 );
                p1.addConstraint( constraintSpec );

                constraintSpec = new HotAirBalloonMustContainParticle( balloon, p1 );
//                p1.addConstraint( constraintSpec );
            }
        }
        getIdealGasApplication().run();

        // Set the size of the box
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );

        // Set up the door for the box
        ResourceLoader loader = new ResourceLoader();
        Image doorImg = loader.loadImage( IdealGasConfig.DOOR_IMAGE_FILE ).getImage();
        doorGraphicImage = new BoxDoorGraphic(
                doorImg,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175,
                IdealGasConfig.X_BASE_OFFSET + 150, IdealGasConfig.Y_BASE_OFFSET + 175,
                IdealGasConfig.X_BASE_OFFSET + 280, IdealGasConfig.Y_BASE_OFFSET + 175 );
        this.addGraphic( doorGraphicImage, -6 );

        // Turn on gravity
        getIdealGasApplication().setGravityEnabled( true );
        getIdealGasApplication().setGravity( 15 );
    }

    public void deactivate() {
        super.deactivate();

        // Remove the controls specific to the hot air balloon
        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        mainControlPanel.remove( habControlPanel );
    }

    protected GasMolecule pumpGasMolecule() {
        GasMolecule molecule = super.pumpGasMolecule();
        Constraint constraintSpec = new HotAirBalloonMustNotContainParticle(
                                                                  balloon, molecule );
        molecule.addConstraint( constraintSpec );

        constraintSpec = new BoxMustContainParticle( getIdealGasSystem().getBox(), molecule );
        molecule.addConstraint( constraintSpec );
        return molecule;
    }
}
