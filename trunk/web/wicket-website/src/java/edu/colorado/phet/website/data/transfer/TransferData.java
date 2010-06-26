package edu.colorado.phet.website.data.transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.apache.wicket.util.crypt.Base64;
import org.hibernate.Session;

import edu.colorado.phet.buildtools.util.FileUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.TeachersGuide;
import edu.colorado.phet.website.data.Translation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;

/**
 * Code to transfer the current MySQL data into the new PostgreSQL data
 * <p/>
 * Once the Wicket site is the main site, this code never be called
 */
public class TransferData {

    private static final Logger logger = Logger.getLogger( TransferData.class.getName() );

    public static boolean transferTeachersGuides( Session session, final ServletContext servletContext ) {
        boolean success = HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( final Session session ) {
                logger.info( "transferring teachers guides" );

                File teacherRoot = PhetWicketApplication.get().getTeachersGuideRoot();
                FileUtils.delete( teacherRoot );
                teacherRoot.mkdir();

                final List<Object> newObs = new LinkedList<Object>();

                // map old teachers guide IDs to simulations. with style.
                final Map<Integer, Simulation> badlyNamedVariable = new HashMap<Integer, Simulation>();

                boolean sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM simulation", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        int id = result.getInt( "sim_teachers_guide_id" );
                        String name = result.getString( "sim_name" );
                        if ( id == 0 ) {
                            // don't handle non-existing ones?
                            logger.warn( "id == 0, so hopefully no guide? sim name is " + name );
                            return true;
                        }
                        Simulation simulation = (Simulation) ( session.createQuery( "select s from Simulation as s where s.name = :name" )
                                .setString( "name", name ).uniqueResult() );
                        if ( simulation == null ) {
                            logger.warn( "could not find simulation for name " + name );
                            return false;
                        }
                        badlyNamedVariable.put( id, simulation );
                        logger.debug( "  registering guide #" + id + " with sim name " + name + " to actual sim id " + simulation.getId() );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return false; }


                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM teachers_guide", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        TeachersGuide guide = new TeachersGuide();

                        newObs.add( guide );
                        Simulation simulation = badlyNamedVariable.get( new Integer( result.getInt( "teachers_guide_id" ) ) );
                        if ( simulation == null ) {
                            logger.warn( "couldn't find simulation for teachers guide id " + result.getInt( "teachers_guide_id" ) );
                            return false;
                        }
                        guide.setSimulation( simulation );
                        guide.setFilename( simulation.getName() + "-guide.pdf" );
                        //guide.setLocation( guide.getFileLocation().getAbsolutePath() );

                        Blob blob = result.getBlob( "teachers_guide_contents" );
                        File file = guide.getFileLocation();
                        try {
                            file.getParentFile().mkdirs();
                            writeBlobToFile( blob, file );
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                            logger.error( e );
                            return false;
                        }
                        guide.setSize( (int) file.length() );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return false; }


                deleteAllInQuery( session, "select tg from TeachersGuide as tg" );

                for ( Object ob : newObs ) {
                    session.save( ob );
                }

                return sqlSuccess;
            }
        } );

        return success;
    }

    private static class TPair {
        private int translationId;
        private String email;

        private TPair( int translationId, String email ) {
            this.translationId = translationId;
            this.email = email;
        }

        public int getTranslationId() {
            return translationId;
        }

        public String getEmail() {
            return email;
        }
    }

    public static boolean transferUsersContributions( Session session, final ServletContext servletContext ) {

        final List<ContributionFile> files = new LinkedList<ContributionFile>();
        final List<TPair> pairs = new LinkedList<TPair>();

        boolean startsuccess = HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select t from Translation as t" ).list();
                for ( Object o : list ) {
                    Translation translation = (Translation) o;
                    for ( Object o1 : translation.getAuthorizedUsers() ) {
                        PhetUser user = (PhetUser) o1;
                        pairs.add( new TPair( translation.getId(), user.getEmail() ) );
                    }
                }
                return true;
            }
        } );

        if ( !startsuccess ) {
            logger.error( "wasn't able to construct the translation-user email mapping" );
            return false;
        }

        boolean oversuccess = HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( final Session session ) {

                // for now, clear out everything under activities
                logger.info( "clearing activities directory" );
                File acts = PhetWicketApplication.get().getActivitiesRoot();
                FileUtils.delete( acts );
                acts.mkdir();
                try {
                    // add htaccess properties that force downloading
                    File htaccess = new File( acts, ".htaccess" );
                    FileUtils.writeString( htaccess, "ForceType application/octet-stream\n" +
                                                     "Header set Content-Disposition attachment\n" );
                }
                catch( IOException e ) {
                    logger.error( e );
                    return false;
                }

                final List<Object> newObs = new LinkedList<Object>();

                final Map<Integer, PhetUser> userIdMap = new HashMap<Integer, PhetUser>();
                final Map<Integer, Contribution> contributionIdMap = new HashMap<Integer, Contribution>();
                boolean sqlSuccess;

                // because our old data has many many holes!
                final PhetUser anonymous = new PhetUser();
                anonymous.setEmail( "anonymous@unknown.com" );
                anonymous.setHashedPassword( "nothing will hash to this" );
                anonymous.setReceiveEmail( false );
                newObs.add( anonymous );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contributor", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        PhetUser user = new PhetUser();

                        userIdMap.put( result.getInt( "contributor_id" ), user );
                        newObs.add( user );
                        user.setEmail( result.getString( "contributor_email" ) );
                        user.setHashedPassword( result.getString( "contributor_password" ) );
                        user.setTeamMember( result.getBoolean( "contributor_is_team_member" ) );
                        user.setName( result.getString( "contributor_name" ) );
                        user.setOrganization( result.getString( "contributor_organization" ) );
                        user.setDescription( result.getString( "contributor_desc" ) );
                        user.setAddress1( result.getString( "contributor_address" ) );
                        user.setAddress2( result.getString( "contributor_office" ) );
                        user.setCity( result.getString( "contributor_city" ) );
                        user.setJobTitle( result.getString( "contributor_title" ) );
                        user.setState( result.getString( "contributor_state" ) );
                        user.setCountry( result.getString( "contributor_country" ) );
                        user.setZipcode( result.getString( "contributor_postal_code" ) );
                        user.setPhone1( result.getString( "contributor_primary_phone" ) );
                        user.setPhone2( result.getString( "contributor_secondary_phone" ) );
                        user.setFax( result.getString( "contributor_fax" ) );
                        user.setReceiveEmail( result.getBoolean( "contributor_receive_email" ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial user setup complete" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        Contribution contribution = new Contribution();

                        int oldId = result.getInt( "contribution_id" );
                        contributionIdMap.put( oldId, contribution );
                        newObs.add( contribution );
                        PhetUser user = userIdMap.get( result.getInt( "contributor_id" ) );
                        if ( user == null ) {
                            logger.warn( "contribution with non-existant user: contribution id: " + result.getInt( "contribution_id" ) + " and contributor " + result.getInt( "contributor_id" ) );
                            user = anonymous;
                        }
                        contribution.setPhetUser( user );
                        contribution.setTitle( result.getString( "contribution_title" ) );
                        contribution.setAuthors( result.getString( "contribution_authors" ) );
                        contribution.setKeywords( result.getString( "contribution_keywords" ) );
                        contribution.setApproved( result.getBoolean( "contribution_approved" ) );
                        contribution.setDescription( result.getString( "contribution_desc" ) );
                        contribution.setDuration( result.getInt( "contribution_duration" ) );
                        contribution.setAnswersIncluded( result.getBoolean( "contribution_answers_included" ) );
                        contribution.setContactEmail( result.getString( "contribution_contact_email" ) );
                        contribution.setAuthorOrganization( result.getString( "contribution_authors_organization" ) );
                        contribution.setDateCreated( result.getDate( "contribution_date_created" ) );
                        contribution.setDateUpdated( result.getDate( "contribution_date_updated" ) );
                        if ( contribution.getDateUpdated() == null ) {
                            // apparently some contributions have bad data for date updated
                            contribution.setDateUpdated( contribution.getDateCreated() );
                        }
                        contribution.setFromPhet( result.getBoolean( "contribution_from_phet" ) );
                        contribution.setGoldStar( result.getBoolean( "contribution_is_gold_star" ) );
                        contribution.setOldId( oldId );

                        String standards = result.getString( "contribution_standards_compliance" );

                        contribution.setStandardK4A( hasStandard( standards, 1 ) );
                        contribution.setStandard58A( hasStandard( standards, 2 ) );
                        contribution.setStandard912A( hasStandard( standards, 3 ) );

                        contribution.setStandardK4B( hasStandard( standards, 4 ) );
                        contribution.setStandard58B( hasStandard( standards, 5 ) );
                        contribution.setStandard912B( hasStandard( standards, 6 ) );

                        contribution.setStandardK4C( hasStandard( standards, 7 ) );
                        contribution.setStandard58C( hasStandard( standards, 8 ) );
                        contribution.setStandard912C( hasStandard( standards, 9 ) );

                        contribution.setStandardK4D( hasStandard( standards, 10 ) );
                        contribution.setStandard58D( hasStandard( standards, 11 ) );
                        contribution.setStandard912D( hasStandard( standards, 12 ) );

                        contribution.setStandardK4E( hasStandard( standards, 13 ) );
                        contribution.setStandard58E( hasStandard( standards, 14 ) );
                        contribution.setStandard912E( hasStandard( standards, 15 ) );

                        contribution.setStandardK4F( hasStandard( standards, 16 ) );
                        contribution.setStandard58F( hasStandard( standards, 17 ) );
                        contribution.setStandard912F( hasStandard( standards, 18 ) );

                        contribution.setStandardK4G( hasStandard( standards, 19 ) );
                        contribution.setStandard58G( hasStandard( standards, 20 ) );
                        contribution.setStandard912G( hasStandard( standards, 21 ) );

                        contribution.setLocale( PhetWicketApplication.getDefaultLocale() );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution setup complete" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_comment", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionComment comment = new ContributionComment();

                        newObs.add( comment );
                        PhetUser user = userIdMap.get( result.getInt( "contributor_id" ) );
                        if ( user == null ) {
                            user = anonymous;
                        }
                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        contribution.addComment( comment );
                        comment.setPhetUser( user );
                        comment.setText( result.getString( "contribution_comment_text" ) );
                        comment.setDateCreated( result.getDate( "contribution_comment_created" ) );
                        comment.setDateUpdated( result.getDate( "contribution_comment_updated" ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution comment setup complete" );

                File downloadMainDir = PhetWicketApplication.get().getActivitiesRoot();

                if ( downloadMainDir == null ) { return false; }

                logger.info( "download root OK" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_file", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionFile cfile = new ContributionFile();

                        logger.debug( "transferring \"" + result.getString( "contribution_file_name" ) + "\" with id " + result.getInt( "contribution_id" ) );

                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        if ( contribution == null ) {
                            // skip files where we don't know of the contribution
                            return true;
                        }
                        newObs.add( cfile );
                        files.add( cfile );
                        contribution.addFile( cfile );
                        String filename = result.getString( "contribution_file_name" );
                        cfile.setFilename( filename );
                        cfile.setOldId( result.getInt( "contribution_file_id" ) );
                        Blob blob = result.getBlob( "contribution_file_contents" );
                        File file = cfile.getTmpFileLocation( String.valueOf( result.getInt( "contribution_id" ) ) );
                        try {
                            file.getParentFile().mkdirs();
                            writeBlobToFile( blob, file );
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                            logger.error( e );
                            return false;
                        }
                        //cfile.setLocation( String.valueOf( result.getInt( "contribution_id" ) ) );
                        cfile.setSize( (int) file.length() );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution file setup partially handled (normal)" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_level", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionLevel level = new ContributionLevel();

                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        if ( contribution == null ) {
                            // skip files where we don't know of the contribution
                            return true;
                        }

                        newObs.add( level );
                        contribution.addLevel( level );
                        level.setLevel( Level.getLevelFromOldAbbrev( result.getString( "contribution_level_desc_abbrev" ) ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution level setup complete" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_subject", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionSubject subject = new ContributionSubject();

                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        if ( contribution == null ) {
                            // skip files where we don't know of the contribution
                            return true;
                        }

                        newObs.add( subject );
                        contribution.addSubject( subject );
                        subject.setSubject( Subject.getSubjectFromOldAbbrev( result.getString( "contribution_subject_desc_abbrev" ) ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution subject setup complete" );

                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM contribution_type", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        ContributionType type = new ContributionType();

                        Contribution contribution = contributionIdMap.get( result.getInt( "contribution_id" ) );
                        if ( contribution == null ) {
                            // skip files where we don't know of the contribution
                            return true;
                        }

                        newObs.add( type );
                        contribution.addType( type );
                        type.setType( Type.getTypeFromOldAbbrev( result.getString( "contribution_type_desc_abbrev" ) ) );

                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution type setup complete" );

                // maps old sim IDs to new sims
                final Map<Integer, Integer> simulationIdMap = new HashMap<Integer, Integer>();

                // pull out simulation ID mappings
                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT sim_id, sim_dirname, sim_flavorname FROM simulation", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        int oldId = result.getInt( "sim_id" );
                        String project = result.getString( "sim_dirname" );
                        String simulation = result.getString( "sim_flavorname" );
                        List list = session.createQuery( "select s from Simulation as s where s.project.name = :project and s.name = :simulation and s.simulationVisible = true and s.project.visible = true" )
                                .setString( "simulation", simulation ).setString( "project", project ).list();
                        if ( list.isEmpty() || list.size() > 1 ) {
                            logger.error( "Could not find exactly 1 match for sim oldId " + oldId + ", project " + project + ", simulation " + simulation );
                            logger.error( "Continuing by ignoring " + list.size() + " matches!" );

                            // continue
                            return true;
                        }
                        int newId = ( (Simulation) list.get( 0 ) ).getId();
                        simulationIdMap.put( oldId, newId );
                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution simulation mapping setup progress" );

                // add simulation-contribution mappings
                sqlSuccess = SqlUtils.wrapTransaction( servletContext, "SELECT * FROM simulation_contribution", new SqlResultTask() {
                    public boolean process( ResultSet result ) throws SQLException {
                        int oldSimId = result.getInt( "sim_id" );
                        int oldContributionId = result.getInt( "contribution_id" );
                        if ( !simulationIdMap.containsKey( oldSimId ) ) {
                            logger.error( "Could not detect simulation of oldId " + oldSimId );

                            // continue
                            return true;
                        }
                        if ( !contributionIdMap.containsKey( oldContributionId ) ) {
                            logger.error( "Could not detect contribution of oldId " + oldContributionId );

                            // continue
                            return true;
                        }
                        Simulation simulation = (Simulation) session.load( Simulation.class, simulationIdMap.get( oldSimId ) );
                        contributionIdMap.get( oldContributionId ).addSimulation( simulation );
                        return true;
                    }
                } );

                if ( !sqlSuccess ) { return sqlSuccess; }

                logger.info( "initial contribution simulation mapping setup complete" );

                deleteAllInQuery( session, "select x from ContributionComment as x" );
                deleteAllInQuery( session, "select x from ContributionFile as x" );
                deleteAllInQuery( session, "select x from ContributionLevel as x" );
                deleteAllInQuery( session, "select x from ContributionSubject as x" );
                deleteAllInQuery( session, "select x from ContributionType as x" );

                List currentContributions = session.createQuery( "select c from Contribution as c" ).list();
                for ( Object o : currentContributions ) {
                    //session.delete( o );
                    ( (Contribution) o ).deleteMe( session );
                }

                List currentUsers = session.createQuery( "select u from PhetUser as u" ).list();
                for ( Object o : currentUsers ) {
                    PhetUser user = (PhetUser) o;
                    Set obs = new HashSet( user.getTranslations() );
                    for ( Object o1 : obs ) {
                        Translation translation = (Translation) o1;
                        translation.removeUser( user );
                        session.update( translation );
                    }
                    session.delete( user );
                }

                for ( Object ob : newObs ) {
                    session.save( ob );
                }


                return sqlSuccess;
            }
        } );

        if ( oversuccess ) {

            oversuccess = HibernateUtils.wrapTransaction( session, new HibernateTask() {
                public boolean run( Session session ) {

                    // copy over all of the contribution files to their permanent homes
                    List cfiles = session.createQuery( "select f from ContributionFile as f" ).list();
                    for ( Object o : cfiles ) {
                        ContributionFile cfile = (ContributionFile) o;
                        File oldFile = cfile.getTmpFileLocation( Integer.toString( cfile.getContribution().getOldId() ) );
                        File newFile = cfile.getFileLocation();
                        newFile.getParentFile().mkdirs();
                        oldFile.renameTo( newFile );
                        oldFile.getParentFile().delete();
                        //cfile.setLocation( cfile.getFileLocation().getAbsolutePath() );
                        session.update( cfile );
                    }

                    List contribs = session.createQuery( "select c from Contribution as c" ).list();
                    try {
                        for ( Object o : contribs ) {
                            Contribution contribution = (Contribution) o;
                            contribution.createZipFile();
                        }
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                        logger.warn( "contribution zipping", e );
                        return false;
                    }

                    for ( TPair pair : pairs ) {
                        Translation translation = (Translation) session.load( Translation.class, pair.getTranslationId() );
                        List list = session.createQuery( "select u from PhetUser as u where u.email = :email" )
                                .setString( "email", pair.getEmail() ).list();
                        if ( list.isEmpty() ) {
                            logger.warn( "wasn't able to manually re-add user email " + pair.getEmail() + " to translation " + pair.getTranslationId() );
                        }
                        else {
                            PhetUser user = (PhetUser) list.get( 0 );
                            translation.addUser( user );
                            session.update( user );
                            session.update( translation );
                        }
                    }

                    return true;
                }
            } );
        }

        if ( oversuccess ) {

        }

        return oversuccess;

    }

    private static final void deleteAllInQuery( Session session, String query ) {
        List obs = session.createQuery( query ).list();
        for ( Object o : obs ) {
            session.delete( o );
        }
    }

    private static final int MAXBUFSIZE = 4096;

    private static void writeBlobToFile( Blob blob, File file ) throws SQLException, IOException {
        byte[] blobData = blob.getBytes( 1, (int) blob.length() );
        byte[] fileData = Base64.decodeBase64( blobData );

        if ( !file.exists() ) {
            file.createNewFile();
        }

        FileOutputStream fileOut = new FileOutputStream( file );
        fileOut.write( fileData, 0, fileData.length );

        fileOut.flush();
        fileOut.close();
    }

    private static boolean hasStandard( String str, int standard ) {
        return str.indexOf( "checkbox_standards_" + standard ) >= 0;
    }
}
