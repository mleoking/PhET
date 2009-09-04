package edu.colorado.phet.wickettest.admin;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.util.StringUtils;
import edu.colorado.phet.wickettest.test.LocaleTest;

// TODO: move the simulation list panel to a separate page, so if something goes bad, this page is still accessible

// TODO: add strings checks to make sure all translatable strings are contained in entities
public class AdminMainPage extends AdminPage {

    private TextField keyText;
    private TextField valueText;

    public AdminMainPage( PageParameters parameters ) {
        super( parameters );

        Session session = getHibernateSession();
        List<Simulation> simulations = new LinkedList();
        final Map<Simulation, LocalizedSimulation> englishSims = new HashMap<Simulation, LocalizedSimulation>();

        Locale english = LocaleUtils.stringToLocale( "en" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            List sims = session.createQuery( "select s from Simulation as s" ).list();

            for ( Object sim : sims ) {
                Simulation simulation = (Simulation) sim;
                simulations.add( simulation );
                for ( Object o : simulation.getLocalizedSimulations() ) {
                    LocalizedSimulation lsim = (LocalizedSimulation) o;
                    if ( lsim.getLocale().equals( english ) ) {
                        englishSims.put( simulation, lsim );
                        break;
                    }
                }
            }

            tx.commit();
        }
        catch( RuntimeException e ) {
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

        Collections.sort( simulations, new Comparator<Simulation>() {
            public int compare( Simulation a, Simulation b ) {
                return ( (Simulation) a ).getName().compareTo( ( (Simulation) b ).getName() );
            }
        } );

        ListView simulationList = new ListView( "simulation-list", simulations ) {
            protected void populateItem( ListItem item ) {
                final Simulation simulation = (Simulation) item.getModel().getObject();
                LocalizedSimulation lsim = englishSims.get( simulation );
                //Link link = new PhetLink( "simulation-link", "#" );
                Link link = new Link( "simulation-link" ) {
                    public void onClick() {
                        PageParameters params = new PageParameters();
                        params.put( "simulationId", simulation.getId() );
                        setResponsePage( AdminSimPage.class, params );
                    }
                };
                link.add( new Label( "simulation-name", simulation.getName() ) );
                item.add( link );
                item.add( new Label( "simulation-project", simulation.getProject().getName() ) );
                item.add( new Label( "simulation-title", lsim.getTitle() ) );
            }
        };

        add( simulationList );

        add( new SetStringForm( "set-string-form" ) );

        add( new Link( "debug-action" ) {
            public void onClick() {
                //LocaleTest.main( new String[]{} );
            }
        });
    }

    public final class SetStringForm extends Form {
        private static final long serialVersionUID = 1L;

        private final ValueMap properties = new ValueMap();

        public SetStringForm( final String id ) {
            super( id );

            add( keyText = new TextField( "key", new PropertyModel( properties, "key" ) ) );
            add( valueText = new TextField( "value", new PropertyModel( properties, "value" ) ) );

            // don't turn <'s and other characters into HTML/XML entities!!!
            valueText.setEscapeModelStrings( false );
        }

        public final void onSubmit() {
            String key = keyText.getModelObjectAsString();
            String value = valueText.getModelObjectAsString();
            System.out.println( "Submitted: " + key + " = " + value );
            StringUtils.setEnglishString( getHibernateSession(), key, value );
        }
    }

}
