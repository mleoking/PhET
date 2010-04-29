package edu.colorado.phet.website.panels.contribution;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.hibernate.Session;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.data.contribution.ContributionType;
import edu.colorado.phet.website.data.contribution.Type;
import edu.colorado.phet.website.data.contribution.Level;
import edu.colorado.phet.website.data.contribution.ContributionLevel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class ContributionSearchPanel extends PhetPanel {

    private static Logger logger = Logger.getLogger( ContributionSearchPanel.class.getName() );

    public ContributionSearchPanel( String id, final PageContext context, final PageParameters params ) {
        super( id, context );

        final String[] simStrings = params.getStringArray( "sims" );
        final String[] typeStrings = params.getStringArray( "types" );
        final String[] levelStrings = params.getStringArray( "levels" );

        final LinkedList<LocalizedSimulation> simulations = new LinkedList<LocalizedSimulation>();

        add( HeaderContributor.forCss( "/css/contribution-browse-v1.css" ) );

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                HibernateUtils.addPreferredFullSimulationList( simulations, getHibernateSession(), context.getLocale() );
                HibernateUtils.orderSimulations( simulations, context.getLocale() );
                return true;
            }
        } );

        simulations.add( 0, null ); // will indicate all simulations in the selection panel

        add( new ListView( "simulations", simulations ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                String value;
                if ( lsim == null ) {
                    item.add( new LocalizedText( "simulation-title", "contribution.search.allSimulations" ) );
                    value = "all";
                }
                else {
                    item.add( new Label( "simulation-title", lsim.getTitle() ) );
                    item.add( new AttributeModifier( "title", true, new Model( lsim.getTitle() ) ) );
                    value = Integer.toString( lsim.getSimulation().getId() );
                }
                setValueAndSelection( item, value, simStrings );
            }
        } );

        List<Type> ctypes = ContributionType.getCurrentTypes();
        List<Option<Type>> types = new LinkedList<Option<Type>>();
        types.add( new Option<Type>() );
        for ( Type type : ctypes ) {
            types.add( new Option<Type>( type ) );
        }

        add( new ListView( "types", types ) {
            protected void populateItem( ListItem item ) {
                Option<Type> type = (Option<Type>) item.getModel().getObject();
                String value;
                if ( !type.hasValue() ) {
                    item.add( new LocalizedText( "type", "contribution.search.allTypes" ) );
                    value = "all";
                }
                else {
                    item.add( new LocalizedText( "type", type.getValue().getAbbreviatedTranslationKey() ) );
                    item.add( new AttributeModifier( "title", true, new ResourceModel( type.getValue().getTranslationKey() ) ) );
                    value = type.getValue().toString();
                }
                setValueAndSelection( item, value, typeStrings );
            }
        } );

        List<Level> clevels = ContributionLevel.getCurrentLevels();
        List<Option<Level>> levels = new LinkedList<Option<Level>>();
        levels.add( new Option<Level>() );
        for ( Level level : clevels ) {
            levels.add( new Option<Level>( level ) );
        }

        add( new ListView( "levels", levels ) {
            protected void populateItem( ListItem item ) {
                Option<Level> level = (Option<Level>) item.getModel().getObject();
                String value;
                if ( !level.hasValue() ) {
                    item.add( new LocalizedText( "level", "contribution.search.allLevels" ) );
                    value = "all";
                }
                else {
                    item.add( new LocalizedText( "level", level.getValue().getAbbreviatedTranslationKey() ) );
                    item.add( new AttributeModifier( "title", true, new ResourceModel( level.getValue().getTranslationKey() ) ) );
                    value = level.getValue().toString();
                }
                setValueAndSelection( item, value, levelStrings );
            }
        } );

    }

    private void setValueAndSelection( ListItem item, String value, String[] selectedValues ) {
        item.add( new AttributeModifier( "value", true, new Model( value ) ) );
        if ( selectedValues == null || selectedValues.length == 0 ) {
            if ( value.equals( "all" ) ) {
                item.add( new AttributeModifier( "selected", true, new Model( "selected" ) ) );
            }
            return;
        }
        for ( String possibleValue : selectedValues ) {
            if ( value.equals( possibleValue ) ) {
                item.add( new AttributeModifier( "selected", true, new Model( "selected" ) ) );
                return;
            }
        }
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