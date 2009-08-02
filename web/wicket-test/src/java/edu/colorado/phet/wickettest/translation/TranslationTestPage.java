package edu.colorado.phet.wickettest.translation;

import java.util.Locale;

import org.apache.wicket.PageParameters;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.data.Translation;
import edu.colorado.phet.wickettest.panels.PanelHolder;
import edu.colorado.phet.wickettest.translation.entities.CommonEntity;
import edu.colorado.phet.wickettest.util.PhetPage;

public class TranslationTestPage extends PhetPage {
    private int translationId;
    private PanelHolder panelHolder;
    private TranslateEntityPanel subPanel;
    private Locale testLocale;
    private String selectedEntityName = null;

    public TranslationTestPage( PageParameters parameters ) {
        super( parameters, true );

        testLocale = LocaleUtils.stringToLocale( "zh_CN" );
        Session session = getHibernateSession();
        Translation translation = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            translation = new Translation();
            translation.setLocale( testLocale );

            session.save( translation );

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

        if ( translation != null ) {
            translationId = translation.getId();
        }

        addTitle( "Translation test page" );

        panelHolder = new PanelHolder( "translation-panel", getPageContext() );
        add( panelHolder );
        CommonEntity commonEntity = new CommonEntity();
        selectedEntityName = commonEntity.getDisplayName();
        subPanel = new TranslateEntityPanel( panelHolder.getWicketId(), getPageContext(), commonEntity, translationId, testLocale );
        panelHolder.add( subPanel );

        add( new TranslationEntityListPanel( "entity-list-panel", getPageContext(), this ) );
    }

    public int getTranslationId() {
        return translationId;
    }

    public PanelHolder getPanelHolder() {
        return panelHolder;
    }

    public TranslateEntityPanel getSubPanel() {
        return subPanel;
    }

    public Locale getTestLocale() {
        return testLocale;
    }

    public String getSelectedEntityName() {
        return selectedEntityName;
    }

    public void setSelectedEntityName( String selectedEntityName ) {
        this.selectedEntityName = selectedEntityName;
    }

    /*
    private int translationId;
    private PanelHolder panel;
    private Component subPanel;
    private Model modelDir = new Model( "ltr" );
    private Model modelPrincipalSponsors = new Model( "Principal Sponsors" );
    private Model modelHewlett = new Model( "Makes grants to address the most serious social and environmental problems facing society, where risk capital, responsibly invested, may make a difference over time." );
    private Model modelNsf = new Model( "An independent federal agency created by Congress in 1950 to promote the progress of science." );
    private Model modelKsu = new Model( "King Saud University seeks to become a leader in educational and technological innovation, scientific discovery and creativity through fostering an atmosphere of intellectual inspiration and partnership for the prosperity of society." );

    public TranslationTestPage( PageParameters parameters ) {
        super( parameters, true );

        final Locale testLocale = LocaleUtils.stringToLocale( "zh_CN" );
        Session session = getHibernateSession();
        Translation translation = null;
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            translation = new Translation();
            translation.setLocale( testLocale );

            session.save( translation );

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

        if ( translation != null ) {
            translationId = translation.getId();
            add( new Label( "translation-id", String.valueOf( translationId ) ) );
        }

        panel = new PanelHolder( "panel", new PageContext( testLocale, this ) );
        subPanel = new SponsorsPanel( panel.getWicketId(), new PageContext( testLocale, this ) );
        panel.add( subPanel );
        add( panel );

        addTitle( "Translation test page" );

        Form form = new Form( "test-form" );
        form.add( new AjaxButton( "test-button" ) {
            protected void onSubmit( AjaxRequestTarget target, Form form ) {
                panel.remove( subPanel );
                //subPanel = new Label( panel.getWicketId(), "Buahahaha!" );
                //subPanel = new AboutPhetPanel( panel.getWicketId(), new PageContext( testLocale, TranslationTestPage.this ) );

                LocalizedSimulation simulation = null;
                Session session = ( (PhetRequestCycle) getRequestCycle() ).getHibernateSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();

                    Query query = session.createQuery( "select ls from LocalizedSimulation as ls, Simulation as s where (ls.simulation = s and s.name = 'pendulum-lab' and ls.locale = :locale)" );
                    
                    simulation = (LocalizedSimulation) query.setLocale( "locale", LocaleUtils.stringToLocale( "en" ) ).uniqueResult();

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

                subPanel = new SimulationMainPanel( panel.getWicketId(), simulation, new PageContext( testLocale, TranslationTestPage.this ) );
                panel.add( subPanel );
                target.addComponent( panel );
            }
        } );
        add( form );


        add( new AjaxEditableMultiLineLabel( "translation-dir", modelDir ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                setString( "language.dir", (String) modelDir.getObject() );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-principalSponsors", modelPrincipalSponsors ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                setString( "sponsors.principalSponsors", (String) modelPrincipalSponsors.getObject() );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-hewlett", modelHewlett ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                setString( "sponsors.hewlett", (String) modelHewlett.getObject() );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-nsf", modelNsf ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                setString( "sponsors.nsf", (String) modelNsf.getObject() );
                target.addComponent( panel );
            }
        } );

        add( new AjaxEditableMultiLineLabel( "translation-ksu", modelKsu ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                setString( "sponsors.ksu", (String) modelKsu.getObject() );
                target.addComponent( panel );
            }
        } );

    }

    public void setString( String key, String value ) {
        Session session = getHibernateSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();

            Translation translation = (Translation) session.load( Translation.class, translationId );
            TranslatedString tString = null;
            for ( Object o : translation.getTranslatedStrings() ) {
                TranslatedString ts = (TranslatedString) o;
                if ( ts.getKey().equals( key ) ) {
                    tString = ts;
                    break;
                }
            }
            if ( tString == null ) {
                tString = new TranslatedString();
                tString.setKey( key );
                tString.setValue( value );
                tString.setTranslation( translation );
                translation.getTranslatedStrings().add( tString );
                session.save( tString );
            }
            else {
                tString.setValue( value );
                session.update( tString );
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

    @Override
    public String getVariation() {
        return new Integer( translationId ).toString();
    }

    */
}
