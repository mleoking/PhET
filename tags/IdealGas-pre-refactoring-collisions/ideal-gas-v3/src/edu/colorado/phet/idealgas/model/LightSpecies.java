/*
 * Class: LightSpecies
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 */
public class LightSpecies extends GasMolecule {

    /**
     * Constructor
     */
    public LightSpecies( Point2D position, Vector2D velocity, Vector2D acceleration,
                         double mass ) {
        super( position, velocity, acceleration, s_defaultMass );
        init();
    }

    /**
     *
     */
    private void init() {
        s_instances.add( this );
    }

    /**
     *
     */
    /**
     *
     */
    public void removeYourselfFromSystem() {
        LightSpecies.removeParticle( this );
        notifyObservers();
    }


    //
    // Static fields and methods
    //
    private static float s_defaultMass = 4; // To Make Kathy happy!
    //    private static float s_defaultMass = 10;
    private static Double s_aveSpeed = new Double( 0.0 );
    private static Double s_temperature = new Double( 0.0 );
    private static ArrayList s_instances = new ArrayList( 100 );
    private static Vector2D.Double s_tempVector = new Vector2D.Double();

    public static void clear() {
        s_instances.removeAll( s_instances );
    }

    public static double getMoleculeMass() {
        return s_defaultMass;
    }

    public static void computeTemperature() {
        s_temperature = GasMolecule.computeTemperature( s_instances );
    }

    public static Double getTotalEnergy( IdealGasModel idealGasModel ) {
        return GasMolecule.getTotalEnergy( s_instances, idealGasModel );
    }

    public static void computeAveSpeed() {
        s_aveSpeed = GasMolecule.computeAveSpeed( s_instances );
    }

    public static Double getTemperature() {
        return s_temperature;
    }

    public static Double getAveSpeed() {
        return s_aveSpeed;
    }

    public static Integer getNumMolecules() {
        return new Integer( s_instances.size() );
    }

    public static void removeParticle( LightSpecies particle ) {
        s_instances.remove( particle );
    }

    public static Point2D getCm() {
        return GasMolecule.getCm( s_instances );
    }
}
