package edu.colorado.phet.website.util;

import it.sauronsoftware.cron4j.Scheduler;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.data.Category;
import edu.colorado.phet.website.data.Keyword;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.translation.PhetLocalizer;

public class SearchUtils {

    // TODO: catch index exceptions to try triggering re-building or fail-out?

    private static final Logger logger = Logger.getLogger( SearchUtils.class.getName() );

    private static Directory directory = null;

    private static IndexSearcher searcher = null;
    private static IndexWriter writer = null;
    private static Thread indexerThread = null;

    private static Scheduler indexScheduler;

    private static boolean indexing = false;

    /**
     * Don't call this more than once
     */
    public static void initialize() {
        if ( directory != null ) {
            throw new RuntimeException( "attempt to initialize SearchUtils multiple times" );
        }
        try {

            final PhetWicketApplication app = PhetWicketApplication.get();
            final PhetLocalizer localizer = PhetLocalizer.get();

            directory = FSDirectory.open( new File( "/tmp/testindex" ) );

            reindex( app, localizer );

            indexScheduler = new Scheduler();
            indexScheduler.schedule( "59 23 * * *", new Runnable() {
                public void run() {
                    reindex( app, localizer );
                }
            } );

            indexScheduler.start();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static synchronized void destroy() {
        try {
            if ( indexScheduler != null ) {
                indexScheduler.stop();
            }
            if ( indexerThread != null ) {
                indexerThread.interrupt();
            }
            if ( searcher != null ) {
                searcher.close();
            }
            if ( directory != null ) {
                directory.close();
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static synchronized IndexSearcher getSearcher() {
        return searcher;
    }

    public static synchronized void reindex( final PhetWicketApplication app, final PhetLocalizer localizer ) {
        if ( indexing ) {
            logger.error( "already indexing!" );
            return;
        }
        onIndexingStarted();
        indexerThread = new Thread() {
            @Override
            public void run() {
                logger.info( "starting indexing thread" );
                addAllDocuments( app, localizer );
                logger.info( "indexing complete" );

                try {
                    searcher = new IndexSearcher( directory, true );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }

                onIndexingCompleted();
            }
        };
        indexerThread.setPriority( Thread.MIN_PRIORITY );
        indexerThread.start();
    }

    private static synchronized void onIndexingStarted() {
        indexing = true;
    }

    private static synchronized void onIndexingCompleted() {
        indexing = false;
    }

    private static synchronized void addAllDocuments( final PhetWicketApplication app, final PhetLocalizer localizer ) {
        try {
            Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_CURRENT );
            writer = new IndexWriter( directory, analyzer, true, new IndexWriter.MaxFieldLength( 25000 ) );

            HibernateUtils.wrapSession( new HibernateTask() {
                public boolean run( Session session ) {
                    try {
                        logger.debug( "adding simulations" );

                        List s = session.createQuery( "select s from Simulation as s" ).list();
                        for ( Object o : s ) {
                            Simulation sim = (Simulation) o;
                            if ( !sim.isVisible() ) {
                                continue;
                            }
                            logger.debug( "processing " + sim.getName() );
                            Document doc = simulationToDocument( session, sim, app, localizer );
                            logger.debug( "processed" );
                            writer.addDocument( doc );
                            logger.debug( "added" );
                            //logger.debug( "adding document: " + doc );
                        }

                        logger.debug( "adding contributions" );

                        List l = session.createQuery( "select c from Contribution as c" ).list();
                        for ( Object o : l ) {
                            Contribution contribution = (Contribution) o;
                            if ( !contribution.isVisible() ) {
                                continue;
                            }

                            logger.debug( "processing contribution " + contribution.getTitle() );
                            Document doc = contributionToDocument( session, contribution, app, localizer );
                            writer.addDocument( doc );
                            logger.debug( "doc: " + doc );
                        }
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                    return true;
                }
            } );

            writer.optimize();
            writer.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static synchronized void removeAllDocuments() {
        try {
            final IndexReader reader = IndexReader.open( directory, false );
            int dropped = reader.deleteDocuments( new Term( "droppable", "true" ) );
            logger.info( "dropped " + dropped + " documents" );
            reader.close();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static Document contributionToDocument( Session session, Contribution contribution, PhetWicketApplication app, PhetLocalizer localizer ) {

        Document doc = new Document();
        doc.add( new Field( "droppable", "true", Field.Store.NO, Field.Index.NOT_ANALYZED ) );
        doc.add( new Field( "contribution_id", String.valueOf( contribution.getId() ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        if ( contribution.getTitle() != null ) {
            doc.add( new Field( "contribution_title", contribution.getTitle(), Field.Store.YES, Field.Index.ANALYZED ) );
        }
        if ( contribution.getAuthors() != null ) {
            doc.add( new Field( "contribution_authors", contribution.getAuthors(), Field.Store.NO, Field.Index.ANALYZED ) );
        }
        if ( contribution.getAuthorOrganization() != null ) {
            doc.add( new Field( "contribution_organization", contribution.getAuthorOrganization(), Field.Store.NO, Field.Index.ANALYZED ) );
        }
        if ( contribution.getKeywords() != null ) {
            doc.add( new Field( "contribution_keywords", contribution.getKeywords(), Field.Store.NO, Field.Index.ANALYZED ) );
        }
        if ( contribution.getDescription() != null ) {
            doc.add( new Field( "contribution_description", contribution.getDescription(), Field.Store.NO, Field.Index.ANALYZED ) );
        }
        doc.add( new Field( "contribution_locale", contribution.getLocale().toString(), Field.Store.NO, Field.Index.NOT_ANALYZED ) );
        Map<Locale, String> titleMap = new HashMap<Locale, String>();
        for ( Object o : contribution.getSimulations() ) {
            Simulation sim = (Simulation) o;
            for ( Object ob : sim.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) ob;
                String str = titleMap.get( lsim.getLocale() );
                if ( str == null ) {
                    str = "";
                }
                str += " " + lsim.getTitle();
                titleMap.put( lsim.getLocale(), str );
            }
        }
        for ( Locale locale : titleMap.keySet() ) {
            String str = titleMap.get( locale );
            doc.add( new Field( "contribution_" + LocaleUtils.localeToString( locale ) + "_sims", str, Field.Store.NO, Field.Index.ANALYZED ) );
        }
        doc.setBoost( ( contribution.isGoldStar() ? 2.5f : 1.0f ) * ( contribution.isFromPhet() ? 1.5f : 1.0f ) );
        return doc;
    }

    public static Document simulationToDocument( Session session, Simulation sim, PhetWicketApplication app, PhetLocalizer localizer ) {
        Document doc = new Document();
        doc.add( new Field( "droppable", "true", Field.Store.NO, Field.Index.NOT_ANALYZED ) );
        doc.add( new Field( "sim_id", String.valueOf( sim.getId() ), Field.Store.YES, Field.Index.NOT_ANALYZED ) );
        Field nameField = new Field( "sim_name", sim.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED );
        nameField.setBoost( 0.3f );
        doc.add( nameField );
        for ( Object o1 : sim.getLocalizedSimulations() ) {
            LocalizedSimulation lsim = (LocalizedSimulation) o1;
            String prefix = lsim.getLocaleString();
            Field titleField = new Field( "sim_" + prefix + "_title", lsim.getTitle(), Field.Store.YES, Field.Index.ANALYZED );
            titleField.setBoost( 4.5f );
            doc.add( titleField );

            // don't endlessly slaughter the localizer for locales without website translations.
            if ( app.isVisibleLocale( lsim.getLocale() ) ) {

                String description = localizer.getBestStringWithinTransaction( session, sim.getDescriptionKey(), lsim.getLocale() );
                if ( description != null ) {
                    doc.add( new Field( "sim_" + prefix + "_description", description, Field.Store.NO, Field.Index.ANALYZED ) );
                }

                String goals = localizer.getBestStringWithinTransaction( session, sim.getLearningGoalsKey(), lsim.getLocale() );
                if ( goals != null ) {
                    doc.add( new Field( "sim_" + prefix + "_goals", goals, Field.Store.NO, Field.Index.ANALYZED ) );
                }

                String keywords = "";
                for ( Object o2 : sim.getKeywords() ) {
                    Keyword keyword = (Keyword) o2;
                    String key = localizer.getBestStringWithinTransaction( session, keyword.getLocalizationKey(), lsim.getLocale() );
                    if ( key != null ) {
                        keywords += key + " ";
                    }
                }
                if ( keywords.length() > 0 ) {
                    doc.add( new Field( "sim_" + prefix + "_keywords", keywords, Field.Store.NO, Field.Index.ANALYZED ) );
                }

                String topics = "";
                for ( Object o2 : sim.getTopics() ) {
                    Keyword keyword = (Keyword) o2;
                    String key = localizer.getBestStringWithinTransaction( session, keyword.getLocalizationKey(), lsim.getLocale() );
                    if ( key != null ) {
                        topics += key + " ";
                    }
                }
                if ( topics.length() > 0 ) {
                    doc.add( new Field( "sim_" + prefix + "_topics", topics, Field.Store.NO, Field.Index.ANALYZED ) );
                }

                String categories = "";
                for ( Object o2 : sim.getCategories() ) {
                    Category category = (Category) o2;
                    String key = localizer.getBestStringWithinTransaction( session, category.getNavLocation( app.getMenu() ).getLocalizationKey(), lsim.getLocale() );
                    if ( key != null ) {
                        categories += key + " ";
                    }
                }
                if ( categories.length() > 0 ) {
                    doc.add( new Field( "sim_" + prefix + "_categories", categories, Field.Store.NO, Field.Index.ANALYZED ) );
                }
            }
        }
        return doc;
    }

    private static void addBoostedTermQuery( BooleanQuery query, String field, String term, float boost ) {
        TermQuery tquery = new TermQuery( new Term( field, term ) );
        tquery.setBoost( boost );
        query.add( tquery, BooleanClause.Occur.SHOULD );
    }

    public static List<Contribution> contributionSearch( Session session, String queryString, final Locale locale ) {
        final List<Contribution> ret = new LinkedList<Contribution>();
        final IndexSearcher isearcher = getSearcher();

        try {
            StringTokenizer tokenizer = new StringTokenizer( queryString );
            BooleanQuery query = new BooleanQuery();

            while ( tokenizer.hasMoreTokens() ) {
                String term = tokenizer.nextToken();
                addBoostedTermQuery( query, "contribution_title", term, 1.0f );
                addBoostedTermQuery( query, "contribution_authors", term, 1.0f );
                addBoostedTermQuery( query, "contribution_organization", term, 1.0f );
                addBoostedTermQuery( query, "contribution_keywords", term, 1.0f );
                addBoostedTermQuery( query, "contribution_description", term, 1.0f );
                addBoostedTermQuery( query, "contribution_locale", term, 1.0f );
                addBoostedTermQuery( query, "contribution_en_sims", term, 0.3f );
                if ( !locale.equals( PhetWicketApplication.getDefaultLocale() ) ) {
                    addBoostedTermQuery( query, "contribution_" + LocaleUtils.localeToString( locale ) + "_sims", term, 0.5f );
                }
            }

            logger.debug( "query: " + query );

            final ScoreDoc[] hits = isearcher.search( query, null, 1000 ).scoreDocs;

            HibernateUtils.wrapTransaction( session, new HibernateTask() {
                public boolean run( Session session ) {
                    for ( ScoreDoc hit : hits ) {
                        try {
                            float score = hit.score;
                            Document doc = isearcher.doc( hit.doc );
                            Contribution contribution = (Contribution) session.load( Contribution.class, Integer.parseInt( doc.get( "contribution_id" ) ) );
                            logger.debug( score + ": " + contribution.getTitle() + " " + doc.get( "contribution_title" ) );
                            ret.add( contribution );
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                        }

                    }
                    return true;
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return ret;
    }

    public static List<LocalizedSimulation> simulationSearch( Session session, String queryString, final Locale locale ) {
        final List<LocalizedSimulation> ret = new LinkedList<LocalizedSimulation>();

        final IndexSearcher isearcher = getSearcher();

        try {
            StringTokenizer tokenizer = new StringTokenizer( queryString );
            BooleanQuery query = new BooleanQuery();

            String localeString = LocaleUtils.localeToString( locale );

            while ( tokenizer.hasMoreTokens() ) {
                String term = tokenizer.nextToken();
                addBoostedTermQuery( query, "sim_name", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_title", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_description", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_goals", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_keywords", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_topics", term, 1.0f );
                addBoostedTermQuery( query, "sim_en_categories", term, 1.0f );
                if ( !localeString.equals( "en" ) ) {
                    addBoostedTermQuery( query, "sim_" + localeString + "_title", term, 4.0f );
                    addBoostedTermQuery( query, "sim_" + localeString + "_description", term, 4.0f );
                    addBoostedTermQuery( query, "sim_" + localeString + "_goals", term, 4.0f );
                    addBoostedTermQuery( query, "sim_" + localeString + "_keywords", term, 4.0f );
                    addBoostedTermQuery( query, "sim_" + localeString + "_topics", term, 4.0f );
                    addBoostedTermQuery( query, "sim_" + localeString + "_categories", term, 4.0f );
                }
            }

            logger.debug( "query: " + query );

            final ScoreDoc[] hits = isearcher.search( query, null, 1000 ).scoreDocs;

            HibernateUtils.wrapTransaction( session, new HibernateTask() {
                public boolean run( Session session ) {
                    for ( ScoreDoc hit : hits ) {
                        try {
                            float score = hit.score;
                            Document doc = isearcher.doc( hit.doc );
                            Simulation sim = (Simulation) session.load( Simulation.class, Integer.parseInt( doc.get( "sim_id" ) ) );
                            logger.debug( score + ": " + sim.getName() + " " + doc.get( "sim_en_title" ) );
                            LocalizedSimulation lsim = sim.getBestLocalizedSimulation( locale );
                            if ( lsim != null ) {
                                ret.add( lsim );
                            }
                        }
                        catch( IOException e ) {
                            e.printStackTrace();
                        }

                    }
                    return true;
                }
            } );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        return ret;
    }

    public static void main( String[] args ) throws IOException, ParseException {

//        Analyzer analyzer = new StandardAnalyzer( Version.LUCENE_CURRENT );
//
//        Directory directory = new RAMDirectory();
//        //Directory directory = FSDirectory.open("/tmp/testindex");
//
//        IndexWriter iwriter = new IndexWriter( directory, analyzer, true, new IndexWriter.MaxFieldLength( 25000 ) );
//        Document doc = new Document();
//        doc.add( new Field( "fieldname", "This is the text to be indexed.", Field.Store.YES, Field.Index.ANALYZED ) );
//        iwriter.addDocument( doc );
//        iwriter.close();
//
//        IndexSearcher isearcher = new IndexSearcher( directory, true );
//        QueryParser parser = new QueryParser( Version.LUCENE_CURRENT, "fieldname", analyzer );
//        Query query = parser.parse( "text" );
//        ScoreDoc[] hits = isearcher.search( query, null, 1000 ).scoreDocs;
//
//        for ( int i = 0; i < hits.length; i++ ) {
//            Document hitDoc = isearcher.doc( hits[i].doc );
//            System.out.println( "score: " + hits[i].score );
//            System.out.println( "field: " + hitDoc.get( "fieldname" ) );
//        }
//
//        isearcher.close();
//        directory.close();
    }
}
