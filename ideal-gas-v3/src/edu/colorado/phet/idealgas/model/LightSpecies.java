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

    //----------------------------------------------------------------
    // Static fields and methods
    //----------------------------------------------------------------

    private static double s_radius = 3.5;
    private static float s_mass = 4; // Helium
    private static ArrayList s_instances = new ArrayList( 100 );

    public static void clear() {
        s_instances.removeAll( s_instances );
    }

    public static double getMoleculeMass() {
        return s_mass;
    }

    public static void setMoleculeMass( double mass ) {
        s_mass = (float)mass;
    }

    public static void setMoleculeRadius( double radius ) {
        s_radius = radius;
    }

    public static void removeParticle( LightSpecies particle ) {
        s_instances.remove( particle );
    }

    public static Point2D getCm() {
        return GasMolecule.getCm( s_instances );
    }

    public static int getCnt() {
        return s_instances.size();
    }


    /**
     * Constructor
     */
    public LightSpecies( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position, velocity, acceleration, s_mass, s_radius );
        init();
        super.setRadius( s_radius );
    }

    /**
     *
     */
    private void init() {
        s_instances.add( this );
    }

    public void removeYourselfFromSystem() {
        super.removeYourselfFromSystem();
        LightSpecies.removeParticle( this );
//        notifyObservers();
    }
}
