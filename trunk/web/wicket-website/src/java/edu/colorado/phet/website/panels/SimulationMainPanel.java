package edu.colorado.phet.website.panels;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.SimsByKeywordPage;
import edu.colorado.phet.website.content.about.AboutLegendPanel;
import edu.colorado.phet.website.data.Keyword;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.HibernateUtils;
import static edu.colorado.phet.website.util.HtmlUtils.encode;
import edu.colorado.phet.website.util.PageContext;

// TODO: add ratings (none / under construction / tested)
// TODO: add optional guidance recommended
// TODO: add main topics
// TODO: add sample learning goals
// TODO: add thanks to
// TODO: add development team

// TODO: add third party libraries
public class SimulationMainPanel extends PhetPanel {

    private String title;

    public SimulationMainPanel( String id, LocalizedSimulation simulation, final PageContext context ) {
        super( id, context );

        String simulationVersionString = simulation.getSimulation().getProject().getVersionString();

        add( new Label( "simulation-main-title", simulation.getTitle() ) );

        if ( simulation.getLocale().equals( context.getLocale() ) ) {
            add( new InvisibleComponent( "untranslated-sim-text" ) );
        }
        else {
            add( new LocalizedText( "untranslated-sim-text", "simulationMainPanel.untranslatedMessage" ) );
        }

        PhetLink link = new PhetLink( "simulation-main-link-run-main", simulation.getRunUrl() );
        // TODO: localize
        link.add( new StaticImage( "simulation-main-screenshot", simulation.getSimulation().getImageUrl(), null, new StringResourceModel( "simulationMainPanel.screenshot.alt", this, null, new String[]{encode( simulation.getTitle() )} ) ) );
        add( link );

        //add( new Label( "simulation-main-description", simulation.getDescription() ) );
        add( new LocalizedText( "simulation-main-description", simulation.getSimulation().getDescriptionKey() ) );
        add( new Label( "simulationMainPanel.version", new StringResourceModel( "simulationMainPanel.version", this, null, new String[]{simulationVersionString} ) ) );
        add( new Label( "simulationMainPanel.kilobytes", new StringResourceModel( "simulationMainPanel.kilobytes", this, null, new Object[]{simulation.getSimulation().getKilobytes()} ) ) );

        if ( simulation.getSimulation().isUnderConstruction() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-under-construction-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-under-construction-image", "/images/ratings/under-construction.png", null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-under-construction-link" ) );
        }

        if ( simulation.getSimulation().isGuidanceRecommended() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-guidance-recommended-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-guidance-recommended-image", "/images/ratings/guidance-recommended.png", null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-guidance-recommended-link" ) );
        }

        if ( simulation.getSimulation().isClassroomTested() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-classroom-tested-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-classroom-tested-image", "/images/ratings/classroom-tested.png", null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-classroom-tested-link" ) );
        }


        List<LocalizedSimulation> simulations = HibernateUtils.getLocalizedSimulationsMatching( getHibernateSession(), null, simulation.getSimulation().getName(), null );
        HibernateUtils.orderSimulations( simulations, context.getLocale() );

        List<IModel> models = new LinkedList<IModel>();

        // TODO: improve model?
        for ( final LocalizedSimulation sim : simulations ) {
            if ( !sim.getLocale().equals( simulation.getLocale() ) ) {
                models.add( new IModel() {
                    public Object getObject() {
                        return sim;
                    }

                    public void setObject( Object o ) {

                    }

                    public void detach() {

                    }
                } );
            }
        }

        // TODO: allow localization of locale display names
        ListView simulationList = new ListView( "simulation-main-translation-list", models ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation simulation = (LocalizedSimulation) ( ( (IModel) ( item.getModel().getObject() ) ).getObject() );
                Locale simLocale = simulation.getLocale();
                PhetLink link = new PhetLink( "simulation-main-translation-link", simulation.getRunUrl() );
                //link.add( new Label( "simulation-main-translation-locale-name", simLocale.getDisplayName( context.getLocale() ) ) );
                String defaultLanguageName = simLocale.getDisplayName( context.getLocale() );
                String languageName = ( (PhetLocalizer) getLocalizer() ).getString( "language.names." + LocaleUtils.localeToString( simLocale ), this, null, defaultLanguageName, false );
                link.add( new Label( "simulation-main-translation-locale-name", languageName ) );
                item.add( link );
                item.add( new Label( "simulation-main-translation-locale-translated-name", simLocale.getDisplayName( simLocale ) ) );
                item.add( new Label( "simulation-main-translation-title", simulation.getTitle() ) );
            }
        };
        add( simulationList );

        // TODO: move from direct links to page redirections, so bookmarkables will be minimized
        add( new PhetLink( "run-online-link", simulation.getRunUrl() ) );
        add( new PhetLink( "run-offline-link", simulation.getDownloadUrl() ) );

        List<Keyword> keywords = new LinkedList<Keyword>();
        List<Keyword> topics = new LinkedList<Keyword>();

        Transaction tx = null;
        try {
            Session session = getHibernateSession();
            tx = session.beginTransaction();

            Simulation sim = (Simulation) session.load( Simulation.class, simulation.getSimulation().getId() );
            //System.out.println( "Simulation keywords for " + sim.getName() );
            for ( Object o : sim.getKeywords() ) {
                Keyword keyword = (Keyword) o;
                keywords.add( keyword );
                //System.out.println( keyword.getKey() );
            }
            for ( Object o : sim.getTopics() ) {
                Keyword keyword = (Keyword) o;
                topics.add( keyword );
                //System.out.println( keyword.getKey() );
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
            System.out.println( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    System.out.println( "ERROR: Error rolling back transaction" );
                }
                throw e;
            }
        }

        ListView topicList = new ListView( "topic-list", topics ) {
            protected void populateItem( ListItem item ) {
                Keyword keyword = (Keyword) item.getModel().getObject();
                item.add( new Label( "topic-label", new ResourceModel( keyword.getKey() ) ) );
            }
        };
        add( topicList );
        if ( topics.isEmpty() ) {
            topicList.setVisible( false );
        }

        ListView keywordList = new ListView( "keyword-list", keywords ) {
            protected void populateItem( ListItem item ) {
                Keyword keyword = (Keyword) item.getModel().getObject();
                Link link = SimsByKeywordPage.getLinker( keyword.getSubKey() ).getLink( "keyword-link", context, getPhetCycle() );
//                Link link = new StatelessLink( "keyword-link" ) {
//                    public void onClick() {
//                        // TODO: fill in keyword links!
//                    }
//                };
                link.add( new Label( "keyword-label", new ResourceModel( keyword.getKey() ) ) );
                item.add( link );
            }
        };
        add( keywordList );
        if ( keywords.isEmpty() ) {
            keywordList.setVisible( false );
        }

        // system requirements
        if ( simulation.getSimulation().isJava() ) {
            add( new Label( "windows-req", "Sun Java 1.5.0_15 or later" ) );
            add( new Label( "mac-req", "Sun Java 1.5.0_19 or later" ) );
            add( new Label( "linux-req", "Sun Java 1.5.0_15 or later" ) );
        }
        else if ( simulation.getSimulation().isFlash() ) {
            add( new Label( "windows-req", "Macromedia Flash 8 or later" ) );
            add( new Label( "mac-req", "Macromedia Flash 8 or later" ) );
            add( new Label( "linux-req", "Macromedia Flash 8 or later" ) );
        }
        else {
            add( new InvisibleComponent( "windows-req" ) );
            add( new InvisibleComponent( "mac-req" ) );
            add( new InvisibleComponent( "linux-req" ) );
        }

        // so we don't emit an empty <table></table> that isn't XHTML Strict compatible
        if ( models.isEmpty() ) {
            simulationList.setVisible( false );
        }

        add( HeaderContributor.forCss( "/css/simulation-main-v1.css" ) );

        //new StringResourceModel( "simulationPage.title", this, null, new String[]{simulation.getTitle(), simulation.getSimulation().getProject().getVersionString()} )
        PhetLocalizer localizer = (PhetLocalizer) getLocalizer();

        List<String> titleParams = new LinkedList<String>();
        titleParams.add( simulation.getTitle() );
        for ( Keyword keyword : keywords ) {
            titleParams.add( localizer.getString( keyword.getKey(), this ) );
        }

        if ( keywords.size() < 3 ) {
            title = simulation.getTitle();
        }
        else {
            try {
                title = MessageFormat.format( localizer.getString( "simulationPage.title", this ), (Object[]) titleParams.toArray() );
            }
            catch( RuntimeException e ) {
                e.printStackTrace();
                title = simulation.getTitle();
            }
        }

        List<String> designTeam = new LinkedList<String>();
        List<String> libraries = new LinkedList<String>();
        List<String> thanks = new LinkedList<String>();
        List<String> learningGoals = new LinkedList<String>();

        String rawDesignTeam = simulation.getSimulation().getDesignTeam();
        if ( rawDesignTeam != null ) {
            for ( String item : rawDesignTeam.split( "<br/>" ) ) {
                if ( item != null && item.length() > 0 ) {
                    designTeam.add( item );
                }
            }
        }

        String rawLibraries = simulation.getSimulation().getLibraries();
        if ( rawLibraries != null ) {
            for ( String item : rawLibraries.split( "<br/>" ) ) {
                if ( item != null && item.length() > 0 ) {
                    libraries.add( item );
                }
            }
        }

        String rawThanks = simulation.getSimulation().getThanksTo();
        if ( rawThanks != null ) {
            for ( String item : rawThanks.split( "<br/>" ) ) {
                if ( item != null && item.length() > 0 ) {
                    thanks.add( item );
                }
            }
        }

        String rawLearningGoals = getLocalizer().getString( simulation.getSimulation().getLearningGoalsKey(), this );
        if ( rawLearningGoals != null ) {
            for ( String item : rawLearningGoals.split( "<br/>" ) ) {
                if ( item != null && item.length() > 0 ) {
                    learningGoals.add( item );
                }
            }
        }

        ListView designView = new ListView( "design-list", designTeam ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObjectAsString();
                item.add( new Label( "design-item", str ) );
            }
        };
        if ( designTeam.isEmpty() ) {
            designView.setVisible( false );
        }
        add( designView );

        ListView libraryView = new ListView( "library-list", libraries ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObjectAsString();
                item.add( new Label( "library-item", str ) );
            }
        };
        if ( libraries.isEmpty() ) {
            libraryView.setVisible( false );
        }
        add( libraryView );

        ListView thanksView = new ListView( "thanks-list", thanks ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObjectAsString();
                item.add( new Label( "thanks-item", str ) );
            }
        };
        if ( thanks.isEmpty() ) {
            thanksView.setVisible( false );
        }
        add( thanksView );

        // TODO: consolidate common behavior for these lists
        ListView learningGoalsView = new ListView( "learning-goals", learningGoals ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObjectAsString();
                item.add( new Label( "goal", str ) );
            }
        };
        if ( learningGoals.isEmpty() ) {
            learningGoalsView.setVisible( false );
        }
        add( learningGoalsView );
    }

    public String getTitle() {
        return title;
    }

}