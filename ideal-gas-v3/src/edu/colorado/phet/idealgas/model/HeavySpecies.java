/*
 * Class: HeavySpecies
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
public class HeavySpecies extends GasMolecule {

    public HeavySpecies( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position, velocity, acceleration, s_defaultMass );
        init();
    }

    private void init() {
        synchronized( s_instances ) {
            s_instances.add( this );
        }
    }

    public void removeYourselfFromSystem() {
        super.removeYourselfFromSystem();
        HeavySpecies.removeParticle( this );
        notifyObservers();
    }


    //
    // Static fields and methods
    //
    private static float s_defaultMass = 28; /* To make Kathy happy: Nitrogen */
    //    private static float s_defaultMass = 30; /* float.POSITIVE_INFINITY; */
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

//    public static void computeTemperature() {
//        s_temperature = GasMolecule.computeTemperature( s_instances );
//    }
//
//    public static Double getTotalEnergy( IdealGasModel ideaGasModel ) {
//        return GasMolecule.getTotalEnergy( s_instances, ideaGasModel );
//    }
//
//    public static void computeAveSpeed() {
//        s_aveSpeed = GasMolecule.computeAveSpeed( s_instances );
//    }
//
//    public static Double getTemperature() {
//        return s_temperature;
//    }
//
//    public static Double getAveSpeed() {
//        synchronized( s_aveSpeed ) {
//            return s_aveSpeed;
//        }
//    }
//
//    public static Integer getNumMolecules() {
//        return new Integer( s_instances.size() );
//    }

    public static void removeParticle( HeavySpecies particle ) {
        s_instances.remove( particle );
    }

    public static Point2D getCm() {
        return GasMolecule.getCm( s_instances );
    }
}
