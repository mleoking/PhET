package edu.colorado.phet.website.panels.simulation;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
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
import org.hibernate.event.PostDeleteEvent;
import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostUpdateEvent;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.borders.SmallOrangeButtonBorder;
import edu.colorado.phet.website.cache.EventDependency;
import edu.colorado.phet.website.components.*;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.content.DonatePanel;
import edu.colorado.phet.website.content.about.AboutLegendPanel;
import edu.colorado.phet.website.content.contribution.ContributionCreatePage;
import edu.colorado.phet.website.content.simulations.SimsByKeywordPage;
import edu.colorado.phet.website.content.simulations.TranslatedSimsPage;
import edu.colorado.phet.website.data.*;
import edu.colorado.phet.website.data.contribution.Contribution;
import edu.colorado.phet.website.data.util.AbstractChangeListener;
import edu.colorado.phet.website.data.util.HibernateEventListener;
import edu.colorado.phet.website.data.util.IChangeListener;
import edu.colorado.phet.website.panels.PearsonSponsorPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.panels.contribution.ContributionBrowsePanel;
import edu.colorado.phet.website.translation.PhetLocalizer;
import edu.colorado.phet.website.util.*;

import static edu.colorado.phet.website.util.HtmlUtils.encode;

public class SimulationMainPanel extends PhetPanel {

    private String title;

    private static final Logger logger = Logger.getLogger( SimulationMainPanel.class.getName() );

    public SimulationMainPanel( String id, final LocalizedSimulation simulation, final PageContext context ) {
        super( id, context );

        String simulationVersionString = simulation.getSimulation().getProject().getVersionString();

        add( new Label( "simulation-main-title", simulation.getTitle() ) );

        add( HeaderContributor.forCss( CSS.SIMULATION_MAIN ) );

        if ( simulation.getLocale().equals( context.getLocale() ) ) {
            add( new InvisibleComponent( "untranslated-sim-text" ) );
        }
        else {
            add( new LocalizedText( "untranslated-sim-text", "simulationMainPanel.untranslatedMessage" ) );
        }

        RawLink link = new RawLink( "simulation-main-link-run-main", simulation.getRunUrl() );
        // TODO: localize
        link.add( new StaticImage( "simulation-main-screenshot", simulation.getSimulation().getImageUrl(), null, new StringResourceModel( "simulationMainPanel.screenshot.alt", this, null, new String[]{encode( simulation.getTitle() )} ) ) );
        add( link );

        //add( new Label( "simulation-main-description", simulation.getDescription() ) );
        add( new LocalizedText( "simulation-main-description", simulation.getSimulation().getDescriptionKey() ) );
        add( new Label( "simulationMainPanel.version", new StringResourceModel( "simulationMainPanel.version", this, null, new String[]{simulationVersionString} ) ) );
        add( new Label( "simulationMainPanel.kilobytes", new StringResourceModel( "simulationMainPanel.kilobytes", this, null, new Object[]{simulation.getSimulation().getKilobytes()} ) ) );

        SmallOrangeButtonBorder orangeButton = new SmallOrangeButtonBorder( "orange-button", context );
        add( orangeButton );
        orangeButton.add( DonatePanel.getLinker().getLink( "support-link", context, getPhetCycle() ) );

        //----------------------------------------------------------------------------
        // rating icons
        //----------------------------------------------------------------------------

        if ( simulation.getSimulation().isUnderConstruction() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-under-construction-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-under-construction-image", Images.UNDER_CONSTRUCTION_SMALL, null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-under-construction-link" ) );
        }

        if ( simulation.getSimulation().isGuidanceRecommended() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-guidance-recommended-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-guidance-recommended-image", Images.GUIDANCE_RECOMMENDED_SMALL, null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-guidance-recommended-link" ) );
        }

        if ( simulation.getSimulation().isClassroomTested() ) {
            Link uclink = AboutLegendPanel.getLinker().getLink( "rating-classroom-tested-link", context, getPhetCycle() );
            uclink.add( new StaticImage( "rating-classroom-tested-image", Images.CLASSROOM_TESTED_SMALL, null ) );
            add( uclink );
        }
        else {
            add( new InvisibleComponent( "rating-classroom-tested-link" ) );
        }

        //----------------------------------------------------------------------------
        // teacher's guide
        //----------------------------------------------------------------------------

        final List<TeachersGuide> guides = new LinkedList<TeachersGuide>();
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List li = session.createQuery( "select tg from TeachersGuide as tg where tg.simulation = :sim" )
                        .setEntity( "sim", simulation.getSimulation() ).list();
                if ( !li.isEmpty() ) {
                    guides.add( (TeachersGuide) li.get( 0 ) );
                }
                return true;
            }
        } );
        if ( !guides.isEmpty() ) {
            add( new LocalizedText( "guide-text", "simulationMainPanel.teachersGuide", new Object[]{
                    guides.get( 0 ).getLinker().getHref( context, getPhetCycle() )
            } ) );
        }
        else {
            // make the teachers guide text (and whole section) invisible
            add( new InvisibleComponent( "guide-text" ) );
        }

        //----------------------------------------------------------------------------
        // contributions
        //----------------------------------------------------------------------------

        if ( DistributionHandler.displayContributions( getPhetCycle() ) ) {
            final List<Contribution> contributions = new LinkedList<Contribution>();
            HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                public boolean run( Session session ) {
                    List list = session.createQuery( "select c from Contribution as c where :simulation member of c.simulations and c.approved = true" )
                            .setEntity( "simulation", simulation.getSimulation() ).list();
                    for ( Object o : list ) {
                        Contribution contribution = (Contribution) o;
                        contributions.add( contribution );

                        // we need to read levels
                        contribution.getLevels();

                        // we also need to read the types
                        contribution.getTypes();

                        for ( Object x : contribution.getSimulations() ) {
                            Simulation sim = (Simulation) x;

                            // we need to be able to read these to determine the localized simulation title later
                            sim.getLocalizedSimulations();
                        }
                    }
                    return true;
                }
            } );
            add( new ContributionBrowsePanel( "contributions-panel", context, contributions, false ) );
            Label visLabel = new Label( "teacher-ideas-visible", "" );
            visLabel.setRenderBodyOnly( true ); // don't make anything appear
            add( visLabel );
        }
        else {
            add( new InvisibleComponent( "contributions-panel" ) );
            add( new InvisibleComponent( "teacher-ideas-visible" ) );
        }

        //----------------------------------------------------------------------------
        // translations
        //----------------------------------------------------------------------------

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
                RawLink runLink = new RawLink( "simulation-main-translation-link", simulation.getRunUrl() );
                RawLink downloadLink = new RawLink( "simulation-main-translation-download", simulation.getDownloadUrl() );
                String defaultLanguageName = simLocale.getDisplayName( context.getLocale() );
                String languageName = ( (PhetLocalizer) getLocalizer() ).getString( "language.names." + LocaleUtils.localeToString( simLocale ), this, null, defaultLanguageName, false );
                item.add( runLink );
                if ( DistributionHandler.displayJARLink( getPhetCycle(), simulation ) ) {
                    item.add( downloadLink );
                }
                else {
                    item.add( new InvisibleComponent( "simulation-main-translation-download" ) );
                }
                item.add( new Label( "simulation-main-translation-title", simulation.getTitle() ) );
                Link lang1 = TranslatedSimsPage.getLinker( simLocale ).getLink( "language-link-1", context, getPhetCycle() );
                item.add( lang1 );
                Link lang2 = TranslatedSimsPage.getLinker( simLocale ).getLink( "language-link-2", context, getPhetCycle() );
                item.add( lang2 );
                lang1.add( new Label( "simulation-main-translation-locale-name", languageName ) );
                lang2.add( new Label( "simulation-main-translation-locale-translated-name", simLocale.getDisplayName( simLocale ) ) );

                WicketUtils.highlightListItem( item );
            }
        };
        add( simulationList );

        //----------------------------------------------------------------------------
        // run / download links
        //----------------------------------------------------------------------------

        // TODO: move from direct links to page redirections, so bookmarkables will be minimized
        add( new RawLink( "run-online-link", simulation.getRunUrl() ) );
        add( new RawLink( "run-offline-link", simulation.getDownloadUrl() ) );

        //----------------------------------------------------------------------------
        // keywords / topics
        //----------------------------------------------------------------------------

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
            logger.warn( "Exception: " + e );
            if ( tx != null && tx.isActive() ) {
                try {
                    tx.rollback();
                }
                catch( HibernateException e1 ) {
                    logger.error( "ERROR: Error rolling back transaction", e1 );
                }
                throw e;
            }
        }

        ListView topicList = new ListView( "topic-list", topics ) {
            protected void populateItem( ListItem item ) {
                Keyword keyword = (Keyword) item.getModel().getObject();
                item.add( new RawLabel( "topic-label", new ResourceModel( keyword.getKey() ) ) );
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
                link.add( new RawLabel( "keyword-label", new ResourceModel( keyword.getKey() ) ) );
                item.add( link );
            }
        };
        add( keywordList );
        if ( keywords.isEmpty() ) {
            keywordList.setVisible( false );
        }

        //----------------------------------------------------------------------------
        // system requirements
        //----------------------------------------------------------------------------

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

        PhetLocalizer localizer = (PhetLocalizer) getLocalizer();

        //----------------------------------------------------------------------------
        // title
        //----------------------------------------------------------------------------

        // we initialize the title in the panel. then whatever page that wants to adopt this panel's "title" as the page
        // title can
        List<String> titleParams = new LinkedList<String>();
        titleParams.add( simulation.getEncodedTitle() );
        for ( Keyword keyword : keywords ) {
            titleParams.add( localizer.getString( keyword.getKey(), this ) );
        }

        if ( keywords.size() < 3 ) {
            title = simulation.getEncodedTitle();
        }
        else {
            try {
                title = StringUtils.messageFormat( localizer.getString( "simulationPage.title", this ), (Object[]) titleParams.toArray() );
            }
            catch( RuntimeException e ) {
                e.printStackTrace();
                title = simulation.getEncodedTitle();
            }
        }

        addCacheParameter( "title", title );

        //----------------------------------------------------------------------------
        // more info (design team, libraries, thanks, etc)
        //----------------------------------------------------------------------------

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
                String str = item.getModelObject().toString();
                item.add( new Label( "design-item", str ) );
            }
        };
        if ( designTeam.isEmpty() ) {
            designView.setVisible( false );
        }
        add( designView );

        ListView libraryView = new ListView( "library-list", libraries ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObject().toString();
                item.add( new Label( "library-item", str ) );
            }
        };
        if ( libraries.isEmpty() ) {
            libraryView.setVisible( false );
        }
        add( libraryView );

        ListView thanksView = new ListView( "thanks-list", thanks ) {
            protected void populateItem( ListItem item ) {
                String str = item.getModelObject().toString();
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
                String str = item.getModelObject().toString();
                item.add( new RawLabel( "goal", str ) );
            }
        };
        if ( learningGoals.isEmpty() ) {
            learningGoalsView.setVisible( false );
        }
        add( learningGoalsView );

        addDependency( new EventDependency() {

            private IChangeListener projectListener;
            private IChangeListener stringListener;
            private IChangeListener teacherGuideListener;

            @Override
            protected void addListeners() {
                projectListener = new AbstractChangeListener() {
                    public void onUpdate( Object object, PostUpdateEvent event ) {
                        if ( HibernateEventListener.getSafeHasChanged( event, "visible" ) ) {
                            invalidate();
                        }
                    }
                };
                teacherGuideListener = new AbstractChangeListener() {
                    @Override
                    public void onInsert( Object object, PostInsertEvent event ) {
                        TeachersGuide guide = (TeachersGuide) object;
                        if ( guide.getSimulation().getId() == simulation.getSimulation().getId() ) {
                            invalidate();
                        }
                    }

                    @Override
                    public void onUpdate( Object object, PostUpdateEvent event ) {
                        TeachersGuide guide = (TeachersGuide) object;
                        if ( guide.getSimulation().getId() == simulation.getSimulation().getId() ) {
                            invalidate();
                        }
                    }

                    @Override
                    public void onDelete( Object object, PostDeleteEvent event ) {
                        TeachersGuide guide = (TeachersGuide) object;
                        if ( guide.getSimulation().getId() == simulation.getSimulation().getId() ) {
                            invalidate();
                        }
                    }
                };
                stringListener = createTranslationChangeInvalidator( context.getLocale() );
                HibernateEventListener.addListener( Project.class, projectListener );
                HibernateEventListener.addListener( TranslatedString.class, stringListener );
                HibernateEventListener.addListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.addListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }

            @Override
            protected void removeListeners() {
                HibernateEventListener.removeListener( Project.class, projectListener );
                HibernateEventListener.removeListener( TranslatedString.class, stringListener );
                HibernateEventListener.removeListener( Simulation.class, getAnyChangeInvalidator() );
                HibernateEventListener.removeListener( LocalizedSimulation.class, getAnyChangeInvalidator() );
            }
        } );

        if ( simulation.getSimulation().getName().equals( "mass-spring-lab" ) ) {
            // TODO: improve for RTL if we keep this
            add( new PearsonSponsorPanel( "pearson-sponsor", context ) );
        }
        else {
            add( new InvisibleComponent( "pearson-sponsor" ) );
        }

        add( new LocalizedText( "submit-a", "simulationMainPanel.submitActivities", new Object[]{
                ContributionCreatePage.getLinker().getHref( context, getPhetCycle() )
        } ) );
    }

    public String getTitle() {
        return title;
    }

}