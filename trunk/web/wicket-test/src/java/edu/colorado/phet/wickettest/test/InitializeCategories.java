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
            Category physics = new Category( root, "physics" );
            Category motion = new Category( physics, "motion" );
            Category soundAndWaves = new Category( physics, "sound-and-waves" );

            Simulation movingMan = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :name" ).setString( "name", "moving-man" ).uniqueResult();
            Simulation masses = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :name" ).setString( "name", "mass-spring-lab" ).uniqueResult();
            Simulation sound = (Simulation) session.createQuery( "select s from Simulation as s where s.name = :name" ).setString( "name", "sound" ).uniqueResult();

            motion.getSimulations().add( movingMan );
            motion.getSimulations().add( masses );
            soundAndWaves.getSimulations().add( sound );
            soundAndWaves.getSimulations().add( masses );

            session.save( root );
            session.save( featured );
            session.save( physics );
            session.save( motion );
            session.save( soundAndWaves );

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
