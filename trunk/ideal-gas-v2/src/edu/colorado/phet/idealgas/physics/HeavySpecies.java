/*
 * Class: HeavySpecies
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.physics.body.IdealGasParticle;
import edu.colorado.phet.common.math.Vector2D;
//import edu.colorado.phet.physics.Vector2D;

import java.util.ArrayList;
import java.util.Iterator;
import java.awt.geom.Point2D;

/**
 *
 */
public class HeavySpecies extends GasMolecule {

    /**
     * Constructor
     */
    public HeavySpecies( Point2D position, Vector2D velocity, Vector2D acceleration ) {
//    public HeavySpecies( Point2D position, Vector2D velocity, Vector2D acceleration, float mass ) {
        super( position, velocity, acceleration, s_defaultMass );
        init();
    }

    /**
     * Constructor
     */
//    public HeavySpecies( Point2D position, Vector2D velocity, Vector2D acceleration,
//                         double mass, double charge ) {
//        super( position, velocity, acceleration, s_defaultMass, charge );
//        init();
//    }

    /**
     *
     */
    private void init() {
        synchronized( s_instances ) {
            s_instances.add( this );
        }
    }

    /**
     *
     */
    public void removeFromSystem() {
//        super.removeFromSystem();
        HeavySpecies.removeParticle( this );
    }


    //
    // Static fields and methods
    //
    private static double s_defaultMass = 28; /* To make Kathy happy: Nitrogen */
//    private static float s_defaultMass = 28; /* To make Kathy happy: Nitrogen */
//    private static float s_defaultMass = 30; /* float.POSITIVE_INFINITY; */
    private static Double s_aveSpeed = new Double( 0.0 );
    private static Double s_temperature = new Double( 0.0 );
    private static ArrayList s_instances = new ArrayList( 100 );

    public static void clear() {
        s_instances.removeAll( s_instances );
    }

    public static double getMoleculeMass() {
        return s_defaultMass;
    }
    public static void computeTemperature() {
        s_temperature = GasMolecule.computeTemperature( s_instances );
    }

    public static Double getTotalEnergy() {
        return GasMolecule.getTotalEnergy( s_instances );
    }

    public static void computeAveSpeed() {
        s_aveSpeed = GasMolecule.computeAveSpeed( s_instances );
    }

    public static Double getTemperature() {
        return s_temperature;
    }

    public static Double getAveSpeed() {
        synchronized( s_aveSpeed ) {
            return s_aveSpeed;
        }
    }

    public static Integer getNumMolecules() {
        return new Integer( s_instances.size() );
    }

    public static void removeParticle( HeavySpecies particle ) {
//    public static void removeParticle( IdealGasParticle particle ) {
        s_instances.remove( particle );
    }

    public static Point2D getCm() {
//    public static Vector2D getCm() {
//        Vector2D cm = new Vector2D.Double( 0, 0 );
        double xSum = 0;
        double ySum = 0;
        for( Iterator it = s_instances.iterator(); it.hasNext(); ) {
            HeavySpecies heavySpecies = (HeavySpecies)it.next();
            xSum += heavySpecies.getPosition().getX();
            ySum += heavySpecies.getPosition().getY();
//            cm = cm.add( new Vector2D.Double( heavySpecies.getPosition().getX(),
//                                              heavySpecies.getPosition().getY() ));
        }
        Point2D.Double cm = new Point2D.Double();
        if( s_instances.size() != 0 ) {
            cm.setLocation( xSum / s_instances.size(), ySum / s_instances.size() );
//            cm.scale( 1.0f / s_instances.size() );
        }
        return cm;
    }
}
