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
import java.util.Iterator;

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
    public void removeFromSystem() {
        throw new RuntimeException( "not implemented" );
        //        super.removeFromSystem();
        //        LightSpecies.removeParticle( this );
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

    public static void removeParticle( IdealGasParticle particle ) {
        s_instances.remove( particle );
    }

    // todo: float this up to the superclass
    public static Vector2D getCm() {
        Vector2D cm = new Vector2D.Double();
        for( Iterator it = s_instances.iterator(); it.hasNext(); ) {
            HeavySpecies heavySpecies = (HeavySpecies)it.next();
            s_tempVector.setComponents( heavySpecies.getPosition().getX(),
                                        heavySpecies.getPosition().getY() );
            cm = cm.add( s_tempVector );
        }
        if( s_instances.size() != 0 ) {
            cm.scale( 1.0 / s_instances.size() );
        }
        return cm;
    }
}
