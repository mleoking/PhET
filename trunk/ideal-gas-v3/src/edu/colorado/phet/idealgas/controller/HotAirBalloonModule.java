/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 3:35:40 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;

public class HotAirBalloonModule extends IdealGasModule {

    //
    // Statics
    //
    private static final float initialVelocity = 35;

    private HotAirBalloon balloon;
    private HotAirBalloonControlPanel habControlPanel = new HotAirBalloonControlPanel();

    public HotAirBalloonModule( AbstractClock clock ) {
        super( clock, "Hot Air Balloon" );

        /**
         // Add the specific controls we need for the hot air balloon
         //        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
         //        mainControlPanel.add( habControlPanel );

         float xOrigin = 200;
         float yOrigin = 250;
         float xDiag = 434;
         float yDiag = 397;

         Box2D box = getIdealGasModel().getBox();

         balloon = new HotAirBalloon(
         new Point2D.Double( 400, 350 ),
         new Vector2D.Double( 0, 0 ),
         new Vector2D.Double( 0, 0 ),
         200, 50,
         60 );

         // Give the balloon a high layer number so it always is over whatever
         // particles are pumped into the box
         getIdealGasModel().addModelElement( balloon );
         Constraint constraintSpec = new BoxMustContainParticle( box, balloon, getIdealGasModel()  );
         balloon.addConstraint( constraintSpec );

         // Put some particles in the box outside the balloonn
         //        for( int i = 0; i < 0; i++ ) {
         //        for( int i = 0; i < 1; i++ ) {
         //        for( int i = 0; i < 50; i++ ) {
         for( int i = 0; i < 100; i++ ) {
         float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
         float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
         double theta = Math.random() * Math.PI * 2;
         float vx = (float)Math.cos( theta ) * initialVelocity;
         float vy = (float)Math.sin( theta ) * initialVelocity;
         float m = 10;
         IdealGasParticle p1 = new HeavySpecies(
         new Point2D.Double( x, y ),
         new Vector2D.Double( vx, vy ),
         new Vector2D.Double( 0, 0 ),
         m );
         getIdealGasModel().addModelElement( p1  );
         constraintSpec = new BoxMustContainParticle( box, p1, getIdealGasModel() );
         p1.addConstraint( constraintSpec );

         constraintSpec = new HotAirBalloonMustNotContainParticle( balloon, p1 );
         p1.addConstraint( constraintSpec );
         }

         // Put some particles inside the balloon
         IdealGasParticle p1 = null;
         //        int num = 1;
         int num = 7;
         //        int num = 5;
         for( int i = 1; i <= num; i++ ) {
         for( int j = 0; j < num; j++ ) {

         float vx = initialVelocity;
         float vy = 0;

         float m = 10;
         p1 = new HeavySpecies(
         new Point2D.Double( 350 + i * 10, 350 + j * 10 ),
         new Vector2D.Double( vx, vy ),
         new Vector2D.Double( 0, 0 ),
         m );
         balloon.addContainedBody( p1 );
         getIdealGasModel().addModelElement( p1 );
         //                getIdealGasApplication().addBody( p1, 2 );

         constraintSpec = new BoxMustContainParticle( box, p1, getIdealGasModel() );
         p1.addConstraint( constraintSpec );

         constraintSpec = new HotAirBalloonMustContainParticle( balloon, p1 );
         //                p1.addConstraint( constraintSpec );
         }
         }

         // Set the size of the box
         box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
         **/
    }

}
