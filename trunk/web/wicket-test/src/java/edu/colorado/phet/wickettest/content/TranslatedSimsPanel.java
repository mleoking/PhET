package edu.colorado.phet.wickettest.content;

import java.util.*;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetLocalizer;
import edu.colorado.phet.wickettest.util.HibernateTask;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.StringUtils;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class TranslatedSimsPanel extends PhetPanel {
    public TranslatedSimsPanel( String id, final PageContext context ) {
        super( id, context );

        final List<LocalizedSimulation> localizedSimulations = new LinkedList<LocalizedSimulation>();
        final Map<Locale, List<LocalizedSimulation>> localeMap = new HashMap<Locale, List<LocalizedSimulation>>();
        final List<Locale> locales = new LinkedList<Locale>();
        final Map<Locale, String> localeNames = new HashMap<Locale, String>();
        final Map<Simulation, String> simNameDefault = new HashMap<Simulation, String>();

        Long a = System.currentTimeMillis();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {

                List lsims = session.createQuery( "select ls from LocalizedSimulation as ls" ).list();

                for ( Object lsim : lsims ) {
                    LocalizedSimulation localizedSimulation = (LocalizedSimulation) lsim;

                    if ( !localizedSimulation.getSimulation().getProject().isVisible() ) {
                        continue;
                    }

                    localizedSimulations.add( localizedSimulation );

                    List<LocalizedSimulation> localeSimList = localeMap.get( localizedSimulation.getLocale() );
                    if ( localeSimList == null ) {
                        localeSimList = new LinkedList<LocalizedSimulation>();
                        localeMap.put( localizedSimulation.getLocale(), localeSimList );
                    }

                    if ( localizedSimulation.getLocale().equals( context.getLocale() ) ) {
                        simNameDefault.put( localizedSimulation.getSimulation(), localizedSimulation.getTitle() );
                    }

                    localeSimList.add( localizedSimulation );
                }

                return true;
            }
        } );

        Long b = System.currentTimeMillis();

        for ( Locale locale : localeMap.keySet() ) {
            locales.add( locale );
            localeNames.put( locale, StringUtils.getLocaleTitle( locale, context.getLocale(), (PhetLocalizer) getLocalizer() ) );
        }

        Collections.sort( locales, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return localeNames.get( a ).compareTo( localeNames.get( b ) );
            }
        } );

        for ( List<LocalizedSimulation> simulationList : localeMap.values() ) {
            HibernateUtils.orderSimulations( simulationList, context.getLocale() );
        }

        Long c = System.currentTimeMillis();

        add( new ListView( "locale-list", locales ) {
            protected void populateItem( ListItem item ) {
                Locale locale = (Locale) item.getModel().getObject();

                // TODO: override with language.name when possible
                item.add( new Label( "locale-title-translated", locale.getDisplayName( locale ) ) );

                PhetLink link = new PhetLink( "locale-link", "#" + LocaleUtils.localeToString( locale ) );
                link.add( new Label( "locale-title", localeNames.get( locale ) ) );
                item.add( link );

                // TODO: localize
                item.add( new Label( "number-of-translations", String.valueOf( localeMap.get( locale ).size() ) ) );

                if ( item.getIndex() % 2 == 0 ) {
                    item.add( new AttributeAppender( "class", new Model( "highlight-background" ), " " ) );
                }

            }
        } );

        add( new ListView( "translation-locales", locales ) {
            protected void populateItem( ListItem item ) {
                final Locale locale = (Locale) item.getModel().getObject();

                item.setMarkupId( LocaleUtils.localeToString( locale ) );
                item.setOutputMarkupId( true );

                // TODO: localize order of translated title: {0} - {1} ex.
                item.add( new Label( "locale-header", locale.getDisplayName( locale ) + " " + localeNames.get( locale ) ) );
                item.add( new ListView( "translations", localeMap.get( locale ) ) {
                    protected void populateItem( ListItem subItem ) {
                        LocalizedSimulation lsim = (LocalizedSimulation) subItem.getModel().getObject();

                        subItem.add( new Label( "title-translated", lsim.getTitle() ) );
                        String otherTitle = simNameDefault.get( lsim.getSimulation() );
                        if ( otherTitle == null ) {
                            otherTitle = "-";
                        }
                        subItem.add( new Label( "title", otherTitle ) );
                        subItem.add( lsim.getRunLink( "run-now-link" ) );
                        subItem.add( lsim.getDownloadLink( "download-link" ) );

                        if ( subItem.getIndex() % 2 == 0 ) {
                            subItem.add( new AttributeAppender( "class", new Model( "highlight-background" ), " " ) );
                        }
                    }
                } );
            }
        } );

        add( HeaderContributor.forCss( "/css/translated-sims-v1.css" ) );

        Long d = System.currentTimeMillis();

        System.out.println( "a-b: " + ( b - a ) );
        System.out.println( "b-c: " + ( c - b ) );
        System.out.println( "c-d: " + ( d - c ) );

    }

    public static String getKey() {
        return "simulations.translated";
    }

    public static String getUrl() {
        return "simulations/translated";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
