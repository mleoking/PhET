/*
 * Class: BunchOfMoleculesTest
 * Package: edu.colorado.phet.controller
 *
 * Created by: Ron LeMaster
 * Date: Nov 6, 2002
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.controller.IdealGasApplication;
import edu.colorado.phet.idealgas.controller.IdealGasConfig;
import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.idealgas.physics.body.Particle;


/**
 *
 */
public class BunchOfMoleculesTest {


    public BunchOfMoleculesTest( IdealGasApplication application ) {

        application.init();
        application.run();

        /*
        ArrayList list = new ArrayList();
        Particle p1 = new HeavySpecies (
                new Vector2D( s_intakePortX, s_intakePortY ),
                new Vector2D( 0, 0 ),
                new Vector2D( 0, 0 ), 10 );
        p1.setPhysicalSystem( application.getPhysicalSystem() );
        list.add( p1 );

        Particle p2 = new HeavySpecies (
                new Vector2D( s_intakePortX + 3, s_intakePortY ),
                new Vector2D( 10, 0 ),
                new Vector2D( 0, 0 ), 10 );
        list.add( p2 );
        p2.setPhysicalSystem( application.getPhysicalSystem() );

        Particle p3 = new HeavySpecies (
                new Vector2D( s_intakePortX, s_intakePortY ),
                new Vector2D( 12, 0 ),
                new Vector2D( 0, 0 ), 10 );
        list.add( p3 );
        p3.setPhysicalSystem( application.getPhysicalSystem() );
        application.addBodies( list );

        application.getPhysicalSystem().getSystemClock().setSingleStepEnabled( true );
        */


        // Most recent: 1/1/03
                float xOrigin = 200;
                float yOrigin = 250;
                float xDiag = 434;
                float yDiag = 397;

                for( int i = 0; i < 5; i++ ) {
                    float x = (float)Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
                    float y = (float)Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
                    float vx = (float)Math.random() * 50;
                    float vy = (float)Math.random() * 50;
                    //float m = Math.random() * 20;
                    float m = 10;
                    //Particle p1 = new Particle(
                    Particle p1 = new HeavySpecies (
                            new Vector2D( x, y ),
                            new Vector2D( vx, vy ),
                            new Vector2D( 0, 0 ),
                            m );
                    application.addBody( p1 );
                }

        // Pumps in at hose inlet
        //for( int i = 0; i < 140; i++ ) {
        //    GasMolecule molecule = BaseIdealGasApparatusPanel.pumpGasMolecule(
        //            HeavySpecies.class,
        //            application.getIdealGasSystem() );
        //    application.addBody( molecule );
        //    long now = System.currentTimeMillis();
        //}
        //System.out.println( "done" );


        //        for( int i = 0; i < 20; i++ ) {
        //            double x = xOrigin;
        //            double y = yOrigin + 10 * i;
        //            double vx = 50;
        //            double vy = 0;
        //            double m = 10;
        //            Particle p1 = new HeavySpecies (
        //                    new Vector2D( x, y ),
        //                    new Vector2D( vx, vy ),
        //                    new Vector2D( 0, 0 ),
        //                    m );
        //            application.addBody( p1 );
        //        }
        //
        //        for( int i = 0; i < 20; i++ ) {
        //            double x = xOrigin + 30 + 10 * i;
        //            double y = yOrigin;
        //            double vx = 0;
        //            double vy = 50;
        //            double m = 10;
        //            Particle p1 = new HeavySpecies (
        //                    new Vector2D( x, y ),
        //                    new Vector2D( vx, vy ),
        //                    new Vector2D( 0, 0 ),
        //                    m );
        //            application.addBody( p1 );
        //        }

        application.setClockParams( 0.1f, 20, 0.0f );


    }


    //
    // Static fields and methods
    //
    private static final float PI_OVER_2 = (float)Math.PI / 2;
    private static final float PI_OVER_4 = (float)Math.PI / 4;
    private static final float MAX_V = -30;

    // Coordinates of the intake port on the box
    private static final float s_intakePortX = 400 + IdealGasConfig.X_BASE_OFFSET;
    //private static final float s_intakePortX = 420 + IdealGasConfig.X_BASE_OFFSET;
    private static final float s_intakePortY = 400 + IdealGasConfig.Y_BASE_OFFSET;

    /**
     *
     * @param species
     * @param physicalSystem
     * @return
     */
    public static GasMolecule pumpGasMolecule( Class species, PhysicalSystem physicalSystem ) {

        float temperature = ( ( HeavySpecies.getTemperature().floatValue() * HeavySpecies.getNumMolecules().intValue() )
                + ( LightSpecies.getTemperature().floatValue() * LightSpecies.getNumMolecules().intValue() ) )
                / ( HeavySpecies.getNumMolecules().intValue() + LightSpecies.getNumMolecules().intValue() );
        float theta = (float)Math.random() * PI_OVER_2 - PI_OVER_4;
        float v = (float)( ( Math.random() / 10 ) + 1 ) * MAX_V;
        float xV = v * (float)Math.cos( theta );
        float yV = v * (float)Math.sin( theta );

        // TODO: Make a gas factory or something. We only have a class to work with right now
        // and we can't call newInstance()
        GasMolecule newMolecule = null;
        if( species == LightSpecies.class ) {

            // There is already gas in the box, inject molecules with the same temperature
            //double currAveSpeed = LightSpecies.getAveSpeed().doubleValue();
            float currAveSpeed = (float)Math.sqrt( 2 * temperature / LightSpecies.getMoleculeMass() );
            if( currAveSpeed != 0 && !Double.isNaN( currAveSpeed ) ) {
                v = currAveSpeed;
            }
            xV = v * (float)Math.cos( theta );
            yV = v * (float)Math.sin( theta );
            newMolecule = new LightSpecies(
                    new Vector2D( s_intakePortX, s_intakePortY ),
                    new Vector2D( xV, yV ),
                    new Vector2D( 0, 0 ),
                    5.0f, 0.0f );
        } else if( species == HeavySpecies.class ) {
            //            double currAveSpeed = HeavySpecies.getAveSpeed().doubleValue();
            float currAveSpeed = (float)Math.sqrt( 2 * temperature / HeavySpecies.getMoleculeMass() );
            if( currAveSpeed != 0 && !Double.isNaN( currAveSpeed ) ) {
                v = currAveSpeed;
            }
            xV = v * (float)Math.cos( theta );
            yV = v * (float)Math.sin( theta );
            newMolecule = new HeavySpecies(
                    new Vector2D( s_intakePortX, s_intakePortY ),
                    new Vector2D( xV, yV ),
                    new Vector2D( 0, 0 ),
                    5.0f, 0.0f );
        } else {
            throw new RuntimeException( "No gas species set in application" );
        }
        newMolecule.setPhysicalSystem( physicalSystem );
        return newMolecule;
    }
}
