package edu.colorado.phet.website.panels.contribution;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Session;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.util.PhetLocales;
import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.cache.SimulationCache;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.data.contribution.Level;
import edu.colorado.phet.website.data.contribution.Type;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HtmlUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;
import edu.colorado.phet.website.util.hibernate.HibernateTask;
import edu.colorado.phet.website.util.hibernate.HibernateUtils;

/**
 * Displays 4 multiple-choices boxes to select from simulations, types, levels, and languages, and has a text box to
 * search (browse) for contributions. Mainly based on the query string parameters that it passes to itself (retrieved
 * through the PageParameters variable), and automatically selects the correct items after searches.
 */
public class ContributionSearchPanel extends PhetPanel {

    private static final Logger logger = Logger.getLogger( ContributionSearchPanel.class.getName() );

    public ContributionSearchPanel( String id, final PageContext context, final PageParameters params ) {
        super( id, context );

        final String[] simStrings = params.getStringArray( "sims" );
        final String[] typeStrings = params.getStringArray( "types" );
        final String[] levelStrings = params.getStringArray( "levels" );
        final String[] localeStrings = params.getStringArray( "locales" );
        final String queryString = params.getString( "query" );

        final LinkedList<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        add( HeaderContributor.forCss( CSS.CONTRIBUTION_BROWSE ) );

        logger.debug( "A" );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                logger.debug( "X" );
                //HibernateUtils.addPreferredFullSimulationList( simulations, getHibernateSession(), context.getLocale() );
                //logger.debug( "Y" );
                //HibernateUtils.orderSimulations( simulations, context.getLocale() );
                SimulationCache.addSortedLocalizedSimulations( simulations, session, context.getLocale() );
                logger.debug( "Z" );
                return true;
            }
        } );

        logger.debug( "B" );

        simulations.add( 0, null ); // will indicate all simulations in the selection panel

        add( new ListView<LocalizedSimulation>( "simulations", simulations ) {
            protected void populateItem( ListItem<LocalizedSimulation> item ) {
                LocalizedSimulation lsim = item.getModelObject();
                String value;
                Component optionComponent;
                if ( lsim == null ) {
                    optionComponent = new LocalizedText( "simulation-title", "contribution.search.allSimulations" );
                    value = "all";
                }
                else {
                    optionComponent = new Label( "simulation-title", lsim.getTitle() );
                    item.add( new AttributeModifier( "title", true, new Model<String>( HtmlUtils.encode( lsim.getTitle() ) ) ) );
                    value = Integer.toString( lsim.getSimulation().getId() );
                }
                optionComponent.setRenderBodyOnly( true );
                item.add( optionComponent );
                setValueAndSelection( item, value, simStrings );
            }
        } );

        List<Type> ctypes = ContributionType.getCurrentTypes();
        List<Option<Type>> types = new LinkedList<Option<Type>>();
        types.add( new Option<Type>() );
        for ( Type type : ctypes ) {
            types.add( new Option<Type>( type ) );
        }

        add( new ListView<Option<Type>>( "types", types ) {
            protected void populateItem( ListItem<Option<Type>> item ) {
                Option<Type> type = item.getModelObject();
                String value;
                Component optionComponent;
                if ( !type.hasValue() ) {
                    optionComponent = new LocalizedText( "type", "contribution.search.allTypes" );
                    value = "all";
                }
                else {
                    optionComponent = new LocalizedText( "type", type.getValue().getAbbreviatedTranslationKey() );
                    item.add( new AttributeModifier( "title", true, new ResourceModel( type.getValue().getTranslationKey() ) ) );
                    value = type.getValue().toString();
                }
                optionComponent.setRenderBodyOnly( true );
                item.add( optionComponent );
                setValueAndSelection( item, value, typeStrings );
            }
        } );

        List<Level> clevels = ContributionLevel.getCurrentLevels();
        List<Option<Level>> levels = new LinkedList<Option<Level>>();
        levels.add( new Option<Level>() );
        for ( Level level : clevels ) {
            levels.add( new Option<Level>( level ) );
        }

        add( new ListView<Option<Level>>( "levels", levels ) {
            protected void populateItem( ListItem<Option<Level>> item ) {
                Option<Level> level = item.getModelObject();
                String value;
                Component optionComponent;
                if ( !level.hasValue() ) {
                    optionComponent = new LocalizedText( "level", "contribution.search.allLevels" );
                    value = "all";
                }
                else {
                    optionComponent = new LocalizedText( "level", level.getValue().getAbbreviatedTranslationKey() );
                    item.add( new AttributeModifier( "title", true, new ResourceModel( level.getValue().getTranslationKey() ) ) );
                    value = level.getValue().toString();
                }
                optionComponent.setRenderBodyOnly( true );
                item.add( optionComponent );
                setValueAndSelection( item, value, levelStrings );
            }
        } );

        logger.debug( "C" );

        final PhetLocales phetLocales = ( (PhetWicketApplication) getApplication() ).getSupportedLocales();

        List<Locale> locales = new LinkedList<Locale>();
        final Map<Locale, String> names = new HashMap<Locale, String>();

        for ( String name : phetLocales.getSortedNames() ) {
            Locale locale = phetLocales.getLocale( name );
            locales.add( locale );
            names.put( locale, StringUtils.getLocaleTitle( locale, getLocale(), getPhetLocalizer() ) );
        }

        Collections.sort( locales, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return names.get( a ).compareTo( names.get( b ) );
            }
        } );

        locales.add( 0, null );

        add( new ListView<Locale>( "locales", locales ) {
            protected void populateItem( ListItem<Locale> item ) {
                Locale locale = item.getModelObject();
                String value;
                Component optionComponent;
                if ( locale == null ) {
                    optionComponent = new LocalizedText( "locale", "contribution.search.allLocales" );
                    value = "all";
                }
                else {
                    String localeTitle = names.get( locale );
                    optionComponent = new Label( "locale", localeTitle );
                    item.add( new AttributeModifier( "title", true, new Model<String>( localeTitle ) ) );
                    value = LocaleUtils.localeToString( locale );
                }
                optionComponent.setRenderBodyOnly( true );
                item.add( optionComponent );
                setValueAndSelection( item, value, localeStrings );
            }
        } );

        logger.debug( "D" );

        String textValue = queryString != null && queryString.length() > 0 ? queryString : "";
        Label queryLabel = new Label( "query-text", "" );
        queryLabel.add( new AttributeModifier( "value", true, new Model<String>( textValue ) ) );
        add( queryLabel );

    }

    /**
     * Set the value (sent in the query string), and if applicable select the element depending on the currently
     * selected items
     *
     * @param item           ListView item
     * @param value          Value
     * @param selectedValues Selected values
     */
    private void setValueAndSelection( ListItem item, String value, String[] selectedValues ) {
        item.add( new AttributeModifier( "value", true, new Model<String>( value ) ) );
        if ( selectedValues == null || selectedValues.length == 0 ) {
            if ( value.equals( "all" ) ) {
                item.add( new AttributeModifier( "selected", true, new Model<String>( "selected" ) ) );
            }
            return;
        }
        for ( String possibleValue : selectedValues ) {
            if ( value.equals( possibleValue ) ) {
                item.add( new AttributeModifier( "selected", true, new Model<String>( "selected" ) ) );
                return;
            }
        }
    }

    @Override
    public String getStyle( String key ) {
        /*
         * we need to override the action attribute on the form. Wicket 1.4 seems to muck up the action attribute in an
         * attempt to help 
         */
        if ( key.equals( "style.action" ) ) {
            return getPanelPath();
        }
        return super.getStyle( key );
    }

    private static class Option<E> implements Serializable {
        private E val;
        private boolean hasValue = false;

        public Option() {
        }

        public Option( E val ) {
            this.val = val;
            hasValue = true;
        }

        public boolean hasValue() {
            return hasValue;
        }

        public E getValue() {
            return val;
        }
    }

}