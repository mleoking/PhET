package edu.colorado.phet.wickettest.admin;

import java.util.*;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.util.value.ValueMap;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.WicketApplication;
import edu.colorado.phet.wickettest.data.Keyword;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.data.Simulation;
import edu.colorado.phet.wickettest.translation.PhetLocalizer;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.StringUtils;

public class AdminSimPage extends AdminPage {
    Simulation simulation = null;

    public AdminSimPage( PageParameters parameters ) {
        super( parameters );

        int simulationId = parameters.getInt( "simulationId" );


        List<LocalizedSimulation> localizedSimulations = new LinkedList<LocalizedSimulation>();
        List<Keyword> simKeywords = new LinkedList<Keyword>();
        List<Keyword> allKeywords = new LinkedList<Keyword>();

        Session session = getHibernateSession();
        final Locale english = LocaleUtils.stringToLocale( "en" );

        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            simulation = (Simulation) session.load( Simulation.class, simulationId );

            for ( Object o : simulation.getLocalizedSimulations() ) {
                LocalizedSimulation lsim = (LocalizedSimulation) o;
                localizedSimulations.add( lsim );
            }

            for ( Object o : simulation.getKeywords() ) {
                simKeywords.add( (Keyword) o );
            }

            List allKeys = session.createQuery( "select k from Keyword as k" ).list();
            for ( Object allKey : allKeys ) {
                allKeywords.add( (Keyword) allKey );
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

        final PhetLocalizer localizer = (PhetLocalizer) getLocalizer();

        sortKeywords( allKeywords );

        HibernateUtils.orderSimulations( localizedSimulations, english );

        add( new Label( "simulation-name", simulation.getName() ) );

        add( new AddKeywordForm( "add-keyword", simKeywords, allKeywords ) );

        add( new CreateKeywordForm( "create-keyword", allKeywords ) );

        add( new ListView( "translation-list", localizedSimulations ) {
            protected void populateItem( ListItem item ) {
                LocalizedSimulation lsim = (LocalizedSimulation) item.getModel().getObject();
                item.add( new Label( "locale", LocaleUtils.localeToString( lsim.getLocale() ) ) );
                item.add( new Label( "lang-en", lsim.getLocale().getDisplayName( english ) ) );
                item.add( new Label( "lang-locale", lsim.getLocale().getDisplayName( lsim.getLocale() ) ) );
                item.add( new Label( "title", lsim.getTitle() ) );
                item.add( new Label( "description", lsim.getDescription() ) );
            }
        } );
    }

    private void sortKeywords( List<Keyword> allKeywords ) {
        final PhetLocalizer localizer = (PhetLocalizer) getLocalizer();
        Collections.sort( allKeywords, new Comparator<Keyword>() {
            public int compare( Keyword a, Keyword b ) {
                return localizer.getString( a.getKey(), AdminSimPage.this ).compareTo( localizer.getString( b.getKey(), AdminSimPage.this ) );
            }
        } );
    }

    private class AddKeywordForm extends Form {
        private AdminSimPage.AddKeywordForm.KeywordDropDownChoice dropDownChoice;
        private List<Keyword> simKeywords;

        public AddKeywordForm( String id, final List<Keyword> simKeywords, List<Keyword> allKeywords ) {
            super( id );
            this.simKeywords = simKeywords;

            add( new ListView( "keywords", simKeywords ) {
                protected void populateItem( ListItem item ) {
                    final Keyword keyword = (Keyword) item.getModel().getObject();
                    item.add( new Label( "keyword-english", new ResourceModel( keyword.getKey() ) ) );
                    item.add( new Link( "keyword-remove" ) {
                        public void onClick() {
                            Transaction tx = null;
                            try {
                                Session session = getHibernateSession();
                                tx = session.beginTransaction();

                                Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                                Keyword kword = (Keyword) session.load( Keyword.class, keyword.getId() );

                                sim.getKeywords().remove( kword );
                                simKeywords.remove( keyword );
                                session.update( sim );

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
                        }
                    } );
                }
            } );

            dropDownChoice = new KeywordDropDownChoice( "current-keywords", allKeywords );

            add( dropDownChoice );
        }

        @Override
        protected void onSubmit() {
            int keywordId = Integer.valueOf( dropDownChoice.getModelValue() );
            Transaction tx = null;
            try {
                Session session = getHibernateSession();
                tx = session.beginTransaction();

                Simulation sim = (Simulation) session.load( Simulation.class, simulation.getId() );
                Keyword kword = (Keyword) session.load( Keyword.class, keywordId );

                boolean ok = true;

                // make sure the sim doesn't already have the keyword
                for ( Object o : sim.getKeywords() ) {
                    ok = ok && ( (Keyword) o ).getId() != keywordId;
                }

                if ( ok ) {
                    sim.getKeywords().add( kword );
                    simKeywords.add( kword );

                    session.update( sim );
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
        }

        private class KeywordDropDownChoice extends DropDownChoice {
            public KeywordDropDownChoice( String id, List<Keyword> allKeywords ) {
                super( id, new Model(), allKeywords, new IChoiceRenderer() {
                    public Object getDisplayValue( Object object ) {
                        return WicketApplication.get().getResourceSettings().getLocalizer().getString( ( (Keyword) object ).getKey(), AdminSimPage.this );
                    }

                    public String getIdValue( Object object, int index ) {
                        return String.valueOf( ( (Keyword) object ).getId() );
                    }
                } );
            }
        }
    }

    private class CreateKeywordForm extends Form {

        private final ValueMap properties = new ValueMap();
        private TextField valueText;
        private TextField keyText;
        private List<Keyword> allKeywords;

        public CreateKeywordForm( String id, List<Keyword> allKeywords ) {
            super( id );
            this.allKeywords = allKeywords;

            add( valueText = new TextField( "value", new PropertyModel( properties, "value" ) ) );
            add( keyText = new TextField( "key", new PropertyModel( properties, "key" ) ) );
        }

        @Override
        protected void onSubmit() {
            String key = keyText.getModelObjectAsString();
            String value = valueText.getModelObjectAsString();
            String localizationKey = "keyword." + key;
            boolean success = StringUtils.setEnglishString( getHibernateSession(), localizationKey, value );
            if ( success ) {
                Transaction tx = null;
                try {
                    Session session = getHibernateSession();
                    tx = session.beginTransaction();

                    List sameKeywords = session.createQuery( "select k from Keyword as k where k.key = :key" ).setString( "key", key ).list();

                    if ( sameKeywords.isEmpty() ) {
                        Keyword keyword = new Keyword();
                        keyword.setKey( localizationKey );
                        session.save( keyword );

                        allKeywords.add( keyword );
                        sortKeywords( allKeywords );
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
            }
        }
    }
}
