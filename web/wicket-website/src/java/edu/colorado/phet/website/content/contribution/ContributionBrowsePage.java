package edu.colorado.phet.website.content.contribution;

import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.website.data.Simulation;
import edu.colorado.phet.website.data.contribution.*;
import edu.colorado.phet.website.panels.contribution.ContributionBrowsePanel;
import edu.colorado.phet.website.panels.contribution.ContributionEmptyPanel;
import edu.colorado.phet.website.panels.contribution.ContributionSearchPanel;
import edu.colorado.phet.website.templates.PhetRegularPage;
import edu.colorado.phet.website.util.*;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributionBrowsePage extends PhetRegularPage {

    private static final Logger logger = Logger.getLogger( ContributionBrowsePage.class.getName() );

    public ContributionBrowsePage( PageParameters parameters ) {
        super( parameters );

        logger.debug( System.currentTimeMillis() + " start" );

        initializeLocation( getNavMenu().getLocationByKey( "teacherIdeas.browse" ) );

        setContentWidth( 906 );

        addTitle( new ResourceModel( "contribution.search.title" ) );

        add( new ContributionSearchPanel( "contribution-search-panel", getPageContext(), parameters ) );

        final String[] simStrings = parameters.getStringArray( "sims" );
        final String[] typeStrings = parameters.getStringArray( "types" );
        final String[] levelStrings = parameters.getStringArray( "levels" );
        final String[] localeStrings = parameters.getStringArray( "locales" );
        final String queryString = parameters.getString( "query" );

        boolean noCriteria = simStrings == null && typeStrings == null && levelStrings == null & localeStrings == null & queryString == null;

        logger.debug( "AfterSearch" );

        // TODO: document this code

        if ( noCriteria ) {
            //add( new InvisibleComponent( "contribution-browse-panel" ) );
            add( new ContributionEmptyPanel( "contribution-browse-panel", getPageContext() ) );
        }
        else {
            final List<Contribution> contributions = new LinkedList<Contribution>();

            final boolean allSims = hasAll( simStrings );
            final boolean allTypes = hasAll( typeStrings );
            final boolean allLevels = hasAll( levelStrings );
            final boolean allLocales = hasAll( localeStrings );
            final boolean allText = queryString == null || queryString.length() == 0;

            logger.debug( "PRE" );

            if ( !allText ) {
                contributions.addAll( SearchUtils.contributionSearch( getHibernateSession(), queryString, getLocale() ) );
            }
            else {
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        logger.debug( "V" );
                        Criteria criteria = session.createCriteria( Contribution.class )
                                .setFetchMode( "levels", FetchMode.SELECT )
                                .setFetchMode( "types", FetchMode.SELECT )
                                .setFetchMode( "simulations", FetchMode.SELECT )
                                .add( Restrictions.eq( "approved", new Boolean( true ) ) );
                        if ( !allSims ) {
                            Criteria tmp = criteria.createCriteria( "simulations" );
                            Disjunction d = Restrictions.disjunction();
                            for ( String simId : simStrings ) {
                                Integer id = Integer.parseInt( simId );
                                d.add( Restrictions.eq( "id", id ) );
                            }
                            tmp.add( d );
                        }
                        if ( !allTypes ) {
                            Criteria tmp = criteria.createCriteria( "types" );
                            Disjunction d = Restrictions.disjunction();
                            for ( String typeText : typeStrings ) {
                                Type type = Type.valueOf( typeText );
                                d.add( Restrictions.eq( "type", type ) );
                            }
                            tmp.add( d );
                        }
                        if ( !allLevels ) {
                            Criteria tmp = criteria.createCriteria( "levels" );
                            Disjunction d = Restrictions.disjunction();
                            for ( String levelText : levelStrings ) {
                                Level level = Level.valueOf( levelText );
                                d.add( Restrictions.eq( "level", level ) );
                            }
                            tmp.add( d );
                        }
                        if ( !allLocales ) {
                            Disjunction d = Restrictions.disjunction();
                            for ( String localeString : localeStrings ) {
                                Locale locale = LocaleUtils.stringToLocale( localeString );
                                d.add( Restrictions.eq( "locale", locale ) );
                            }
                            criteria.add( d );
                        }
                        List list = criteria.list();
                        logger.debug( "W" );
                        for ( Object o : list ) {
                            contributions.add( (Contribution) o );
                        }

                        // NOTE: KEEP FOR NOW. localized sims should be loaded already in memory by the search panel!
                        // preload localized simulations for each simulation
                        //List unusedList = session.createCriteria( Simulation.class ).setFetchMode( "localizedSimulations", FetchMode.JOIN ).list();

                        return true;
                    }
                } );
            }

            logger.debug( "intial contribs: " + contributions.size() );

            logger.debug( "A" );

            final List<Contribution> finalContributions = new LinkedList<Contribution>();

            if ( allText ) {
                finalContributions.addAll( contributions );
            }
            else {
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        for ( Contribution contribution : contributions ) {
                            if ( !allSims && !simMatch( contribution, simStrings ) ) {
                                continue;
                            }
                            if ( !allTypes && !typeMatch( contribution, typeStrings ) ) {
                                continue;
                            }
                            if ( !allLevels && !levelMatch( contribution, levelStrings ) ) {
                                continue;
                            }
                            if ( !allLocales && !localeMatch( contribution, localeStrings ) ) {
                                continue;
                            }
                            finalContributions.add( contribution );
                        }
                        return true;
                    }
                } );
            }

            logger.debug( "B" );

            if ( !allText ) {
                HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
                    public boolean run( Session session ) {
                        for ( Contribution contribution : finalContributions ) {
                            contribution.getLevels();
                            contribution.getTypes();
                            contribution.getSimulations();
                        }
                        // preload localized simulations for each simulation
                        logger.debug( "X" );
                        List unusedList = session.createCriteria( Simulation.class ).setFetchMode( "localizedSimulations", FetchMode.JOIN ).list();
                        logger.debug( "Y" );
                        return true;
                    }
                } );
            }

            logger.debug( "C" );

            Collections.sort( finalContributions, new Comparator<Contribution>() {
                public int compare( Contribution a, Contribution b ) {
                    return a.displayCompareTo( b, getLocale() );
                }
            } );

            logger.debug( "D" );

            add( new ContributionBrowsePanel( "contribution-browse-panel", getPageContext(), finalContributions ) );
        }

        logger.debug( "POST" );

//        final List<Contribution> contributions = new LinkedList<Contribution>();
//
//        logger.debug( System.currentTimeMillis() + " A" );
//
//        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
//            public boolean run( Session session ) {
//                logger.debug( "V" );
//                List list = session.createCriteria( Contribution.class )
//                        .setFetchMode( "levels", FetchMode.SELECT )
//                        .setFetchMode( "types", FetchMode.SELECT )
//                        .setFetchMode( "simulations", FetchMode.SELECT )
//                        .add( Restrictions.eq( "approved", new Boolean( true ) ) )
//                        .list();
//                logger.debug( "W" );
//                for ( Object o : list ) {
//                    contributions.add( (Contribution) o );
//                }
//
//                // preload localized simulations for each simulation
//                logger.debug( "X" );
//                List unusedList = session.createCriteria( Simulation.class ).setFetchMode( "localizedSimulations", FetchMode.JOIN ).list();
//                logger.debug( "Y" );
//
//                return true;
//            }
//        } );
//
//        logger.debug( System.currentTimeMillis() + " B" );
//
//        add( new ContributionBrowsePanel( "contribution-browse-panel", getPageContext(), contributions ) );
//
//        logger.debug( System.currentTimeMillis() + " finish init" );

    }

    private boolean simMatch( Contribution contribution, String[] strings ) {
        boolean pass = false;
        for ( Object o : contribution.getSimulations() ) {
            Simulation sim = (Simulation) o;
            String simId = Integer.toString( sim.getId() );
            for ( String id : strings ) {
                if ( id.equals( simId ) ) {
                    pass = true;
                    break;
                }
            }
            if ( pass == true ) {
                break;
            }
        }
        return pass;
    }

    private boolean typeMatch( Contribution contribution, String[] strings ) {
        boolean pass = false;
        for ( Object o : contribution.getTypes() ) {
            ContributionType type = (ContributionType) o;
            String typeString = type.getType().toString();
            for ( String string : strings ) {
                if ( typeString.equals( string ) ) {
                    pass = true;
                    break;
                }
            }
            if ( pass == true ) {
                break;
            }
        }
        return pass;
    }

    private boolean levelMatch( Contribution contribution, String[] strings ) {
        boolean pass = false;
        for ( Object o : contribution.getLevels() ) {
            ContributionLevel level = (ContributionLevel) o;
            String levelString = level.getLevel().toString();
            for ( String string : strings ) {
                if ( levelString.equals( string ) ) {
                    pass = true;
                    break;
                }
            }
            if ( pass == true ) {
                break;
            }
        }
        return pass;
    }

    private boolean localeMatch( Contribution contribution, String[] strings ) {
        boolean pass = false;
        Locale locale = contribution.getLocale();
        String localeString = LocaleUtils.localeToString( locale );
        for ( String string : strings ) {
            if ( localeString.equals( string ) ) {
                pass = true;
                break;
            }
        }
        return pass;
    }

    private boolean hasAll( String[] strings ) {
        for ( String string : strings ) {
            if ( string.equals( "all" ) ) {
                return true;
            }
        }
        return false;
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^for-teachers/browse-activities$", ContributionBrowsePage.class );
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return "for-teachers/browse-activities";
            }
        };
    }

}