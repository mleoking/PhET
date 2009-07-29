package edu.colorado.phet.wickettest.translation;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.model.Model;
import org.hibernate.HibernateException;
import org.hibernate.Transaction;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.data.Category;
import edu.colorado.phet.wickettest.data.LocalizedSimulation;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.panels.SimulationDisplayPanel;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;

public class TranslationTestPage extends TranslationPage {

    private PhetPanel panel;
    private Model str = new Model( "Simulations" );

    public TranslationTestPage( PageParameters parameters ) {
        super( parameters, true );

        Locale testLocale = LocaleUtils.stringToLocale( "zh_CN" );

        Category category;
        List<LocalizedSimulation> simulations = null;
        PageContext context = getPageContext();

        Transaction tx = null;
        try {
            tx = getHibernateSession().beginTransaction();
            if ( parameters.containsKey( "categories" ) ) {
                category = Category.getCategoryFromPath( context.getSession(), "featured" );

                simulations = new LinkedList<LocalizedSimulation>();
                SimulationDisplay.addSimulationsFromCategory( simulations, testLocale, category );
            }
            else {
                simulations = HibernateUtils.getAllSimulationsWithLocale( context.getSession(), testLocale );
                HibernateUtils.orderSimulations( simulations, testLocale );
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

        panel = new SimulationDisplayPanel( "panel", context, simulations );
        panel.setOutputMarkupId( true );
        add( panel );

        addTitle( "Translation test page" );


        add( new AjaxEditableLabel( "translation-label", str ) {
            @Override
            protected void onSubmit( AjaxRequestTarget target ) {
                super.onSubmit( target );
                System.out.println( "Edited" );
                System.out.println( "model: " + getModel() );
                System.out.println( "Label model: " + getLabel().getModel() );
                target.addComponent( panel );
            }
        } );
    }

    public String translateString( Component component, Locale locale, String key ) {
        if ( locale == null && key.equals( "language.dir" ) ) {
            return "ltr";
        }
        if ( key.equals( "simulationDisplay.simulations" ) ) {
            return (String) str.getObject();
        }
        return null;
    }
}
