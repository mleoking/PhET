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

    //---------------------------------------------------------------
    // Static fields and methods
    //---------------------------------------------------------------

    private static double s_radius = 5;
    private static float s_mass = 28; /* To make Kathy happy: Nitrogen */
    private static ArrayList s_instances = new ArrayList( 100 );

    public static void clear() {
        s_instances.removeAll( s_instances );
    }

    public static double getMoleculeRadius() {
        return s_radius;
    }

    public static double getMoleculeMass() {
        return s_mass;
    }

    public static void removeParticle( HeavySpecies particle ) {
        s_instances.remove( particle );
    }

    public static Point2D getCm() {
        return GasMolecule.getCm( s_instances );
    }

    public static int getCnt() {
        return s_instances.size();
    }

    public HeavySpecies( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position, velocity, acceleration, s_mass, s_radius );
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
//        notifyObservers();
    }

}
