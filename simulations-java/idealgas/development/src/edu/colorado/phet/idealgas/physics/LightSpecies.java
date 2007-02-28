/*
 * Class: LightSpecies
 * Package: edu.colorado.phet.physics.idealgas
 *
 * Created by: Ron LeMaster
 * Date: Nov 4, 2002
 */
package edu.colorado.phet.idealgas.physics;

import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.Vector2D;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 */
public class LightSpecies extends GasMolecule {

    /**
     * Constructor
     */
    public LightSpecies( Vector2D position, Vector2D velocity, Vector2D acceleration,
                         float mass ) {
        super( position, velocity, acceleration, s_defaultMass );
        init();
    }

    /**
     * Constructor
     */
    public LightSpecies( Vector2D position, Vector2D velocity, Vector2D acceleration,
                         float mass, float charge ) {
        super( position, velocity, acceleration, s_defaultMass, charge );
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
        super.removeFromSystem();
        LightSpecies.removeParticle( this );
    }


    //
    // Static fields and methods
    //
    private static float s_defaultMass = 10;
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
        return s_aveSpeed;
    }

    public static Integer getNumMolecules() {
        return new Integer( s_instances.size() );
    }

    public static void removeParticle( Particle particle ) {
        s_instances.remove( particle );
    }

    public static Vector2D getCm() {
        Vector2D result = new Vector2D( 0, 0 );
        for( Iterator it = s_instances.iterator(); it.hasNext(); ) {
            LightSpecies lightSpecies = (LightSpecies)it.next();
            result = result.add( lightSpecies.getPosition() );
        }
        if( s_instances.size() != 0 ) {
            result.multiply( 1.0f / s_instances.size() );
        }
        return result;
    }
}
