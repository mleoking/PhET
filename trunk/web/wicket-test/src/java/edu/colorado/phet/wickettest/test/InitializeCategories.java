package edu.colorado.phet.wickettest.test;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.util.HibernateUtils;

public class InitializeCategories {

    public static void addSimsToCategory( Session session, Category category, String[] names ) {
        category.setAuto( false );
        List list = category.getSimulations();
        for ( String name : names ) {
            Simulation sim = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :name" ).setString( "name", name ).uniqueResult();
            list.add( sim );
        }
    }

    public static void main( String[] args ) {

        Transaction tx = null;
        Session session = HibernateUtils.getInstance().openSession();
        try {
            tx = session.beginTransaction();

            Category root = new Category( null, "root" );
            Category featured = new Category( root, "featured" );
            addSimsToCategory( session, featured, new String[]{
                    "circuit-construction-kit-dc", "stretching-dna", "glaciers", "moving-man", "soluble-salts",
                    "equation-grapher", "projectile-motion", "lasers", "faraday", "quantum-tunneling", "wave-interference",
                    "radio-waves"} );
            Category newSims = new Category( root, "new" );
            addSimsToCategory( session, newSims, new String[]{
                    //"ladybug-motion-2d",
                    "eating-and-exercise",
                    //"alpha-decay",
                    //"states-of-matter",
                    "ph-scale",
                    //"nuclear-fission",
                    "glaciers",
                    "pendulum-lab",
                    "curve-fitting"
            } );
            Category physics = new Category( root, "physics" );
            physics.setAuto( true );
            Category motion = new Category( physics, "motion" );
            addSimsToCategory( session, motion, new String[]{
                    //"ladybug-motion-2d",
                    "moving-man",
                    "mass-spring-lab",
                    "rotation",
                    "energy-skate-park",
                    "pendulum-lab",
                    "torque",
                    "projectile-motion",
                    "my-solar-system",
                    //"the-ramp",
                    "friction",
                    "lunar-lander",
                    "forces-1d",
                    "maze-game",
                    "motion-2d"
            } );
            Category soundAndWaves = new Category( physics, "sound-and-waves" );
            addSimsToCategory( session, soundAndWaves, new String[]{
                    "wave-interference",
                    "optical-tweezers",
                    "radio-waves",
                    "fourier",
                    "sound",
                    "wave-on-a-string",
                    "microwaves"
            } );
            Category workEnergyPower = new Category( physics, "work-energy-and-power" );
            addSimsToCategory( session, workEnergyPower, new String[]{
                    //"states-of-matter",
                    //"nuclear-fission",
                    "generator",
                    "faraday",
                    "mass-spring-lab",
                    "my-solar-system",
                    "energy-skate-park",
                    //"the-ramp",
                    "gas-properties",
                    "balloons-and-buoyancy"
            } );
            Category heatThermo = new Category( physics, "heat-and-thermodynamics" );
            addSimsToCategory( session, heatThermo, new String[]{
                    //"states-of-matter",
                    //"nuclear-fission",
                    "blackbody-spectrum",
                    "friction",
                    "gas-properties",
                    "balloons-and-buoyancy",
                    "reactions-and-rates",
                    "greenhouse",
                    "microwaves",
                    "reversible-reactions"
            } );
            Category quantumPhenomena = new Category( physics, "quantum-phenomena" );
            addSimsToCategory( session, quantumPhenomena, new String[]{
                    //"alpha-decay",
                    //"nuclear-fission",
                    "photoelectric",
                    "quantum-tunneling",
                    "bound-states",
                    "quantum-wave-interference",
                    "lasers",
                    "discharge-lamps",
                    "fourier",
                    "mri",
                    "hydrogen-atom",
                    "stern-gerlach",
                    "conductivity",
                    "semiconductor",
                    "rutherford-scattering",
                    "davisson-germer",
                    "blackbody-spectrum",
                    "covalent-bonds",
                    "band-structure"
            } );
            Category lightAndRadiation = new Category( physics, "light-and-radiation" );
            addSimsToCategory( session, lightAndRadiation, new String[]{
                    //"nuclear-fission",
                    //"alpha-decay",
                    "stretching-dna",
                    "color-vision",
                    "molecular-motors",
                    "geometric-optics",
                    "optical-tweezers",
                    "blackbody-spectrum",
                    "fourier",
                    "greenhouse",
                    "lasers",
                    "discharge-lamps",
                    "photoelectric",
                    "radio-waves",
                    "wave-interference",
                    "microwaves",
                    "quantum-wave-interference",
                    "mri",
                    "hydrogen-atom"
            } );
            Category electricity = new Category( physics, "electricity-magnets-and-circuits" );
            addSimsToCategory( session, electricity, new String[]{
                    "magnet-and-compass",
                    "magnets-and-electromagnets",
                    "generator",
                    "balloons",
                    "circuit-construction-kit-dc",
                    "circuit-construction-kit-ac",
                    "electric-hockey",
                    "charges-and-fields",
                    "faraday",
                    "faradays-law",
                    "travoltage",
                    "radio-waves",
                    "ohms-law",
                    "battery-voltage",
                    "battery-resistor-circuit",
                    "efield",
                    "resistance-in-a-wire",
                    "conductivity",
                    "semiconductor",
                    "signal-circuit"
            } );
            Category biology = new Category( root, "biology" );
            addSimsToCategory( session, biology, new String[]{
                    "eating-and-exercise",
                    "reactions-and-rates",
                    "mri",
                    "stretching-dna",
                    "curve-fitting",
                    "plinko-probability",
                    "optical-tweezers",
                    "color-vision",
                    "sound",
                    "balloons",
                    "soluble-salts",
                    "ph-scale",
                    "blackbody-spectrum",
                    "molecular-motors"
            } );
            Category chemistry = new Category( root, "chemistry" );
            addSimsToCategory( session, chemistry, new String[]{
                    //"states-of-matter",
                    "balloons",
                    "ph-scale",
                    //"nuclear-fission",
                    //"alpha-decay",
                    "soluble-salts",
                    "reactions-and-rates",
                    "discharge-lamps",
                    "wave-on-a-string",
                    "radio-waves",
                    "blackbody-spectrum",
                    "hydrogen-atom",
                    "gas-properties",
                    "rutherford-scattering",
                    "balloons-and-buoyancy",
                    "greenhouse",
                    "photoelectric",
                    "microwaves",
                    "reversible-reactions"
            } );
            Category earthScience = new Category( root, "earth-science" );
            addSimsToCategory( session, earthScience, new String[]{
                    "glaciers",
                    "balloons-and-buoyancy",
                    "gas-properties",
                    "balloons",
                    "sound",
                    "greenhouse",
                    "wave-on-a-string",
                    "blackbody-spectrum",
                    "ph-scale",
                    "wave-interference",
                    "my-solar-system"
            } );
            Category math = new Category( root, "math" );
            math.setAuto( true );
            Category mathTools = new Category( math, "tools" );
            addSimsToCategory( session, mathTools, new String[]{
                    "equation-grapher",
                    "vector-addition",
                    "resistance-in-a-wire",
                    "fourier",
                    "blackbody-spectrum",
                    "curve-fitting",
                    "arithmetic",
                    "plinko-probability",
                    "ohms-law",
                    "estimation"
            } );
            Category mathApplications = new Category( math, "applications" );
            addSimsToCategory( session, mathApplications, new String[]{
                    "moving-man",
                    //"the-ramp",
                    "projectile-motion",
                    "pendulum-lab",
                    "my-solar-system",
                    "motion-2d",
                    "maze-game",
                    "maze-game",
                    "mass-spring-lab",
                    "rotation",
                    "gas-properties",
                    "forces-1d",
                    "ph-scale",
                    "wave-on-a-string",
                    "torque"
            } );
            Category cuttingEdge = new Category( root, "cutting-edge-research" );
            addSimsToCategory( session, cuttingEdge, new String[]{
                    "optical-tweezers",
                    "molecular-motors",
                    "stretching-dna",
                    "optical-quantum-control",
                    "lasers",
                    "self-driven-particle-model"
            } );

            session.save( root );
            session.save( featured );
            session.save( newSims );
            session.save( physics );
            session.save( motion );
            session.save( soundAndWaves );
            session.save( workEnergyPower );
            session.save( heatThermo );
            session.save( quantumPhenomena );
            session.save( lightAndRadiation );
            session.save( electricity );
            session.save( biology );
            session.save( chemistry );
            session.save( earthScience );
            session.save( math );
            session.save( mathTools );
            session.save( mathApplications );
            session.save( cuttingEdge );

            tx.commit();
        }
        catch( RuntimeException e ) {
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "Error rolling back transaction" );
                }
                throw e;
            }
        }

    }

    public static void traceCategory( Category category, String buf ) {
        System.out.println( buf + "CAT name: " + category.getName() + " id: " + category.getId() );
        for ( Object o : category.getSimulations() ) {
            Simulation sim = (Simulation) o;
            System.out.println( buf + "  " + sim.getName() );
        }
        for ( Object o : category.getSubcategories() ) {
            Category cat = (Category) o;
            traceCategory( cat, buf + "    " );
        }
    }


}
