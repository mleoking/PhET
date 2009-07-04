package edu.colorado.phet.wickettest.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.wickettest.SimulationModel;
import edu.colorado.phet.wickettest.WebSimulation;

public class PhetPage extends WebPage {

    private Locale myLocale;
    private ServletContext context;
    private PageParameters parameters;

    public PhetPage( PageParameters parameters ) {
        this.parameters = parameters;
        context = ( (WebApplication) getApplication() ).getServletContext();
        myLocale = LocaleUtils.stringToLocale( (String) parameters.get( "localeString" ) );
        getSession().setLocale( myLocale );
        System.out.println( "Loading " + this.getClass().getCanonicalName() + " with Locale: " + LocaleUtils.localeToString( myLocale ) );
        System.out.println( "getRequestPath() of this page is: " + getRequestPath() );
    }

    public List<WebSimulation> getOrderedSimulations() {
        List<WebSimulation> simulations = SqlUtils.getSimulationsMatching( context, null, null, myLocale );
        WebSimulation.orderSimulations( simulations, myLocale );
        return simulations;
    }

    public List<SimulationModel> getOrderedSimulationModels() {
        List<WebSimulation> simulations = getOrderedSimulations();

        List<SimulationModel> models = new LinkedList<SimulationModel>();

        for ( WebSimulation simulation : simulations ) {
            models.add( new SimulationModel( simulation ) );
        }

        return models;
    }

    public Locale getMyLocale() {
        return myLocale;
    }

    public ServletContext getContext() {
        return context;
    }

    public String getRequestPath() {
        return parameters.getString( "path" );
    }

}