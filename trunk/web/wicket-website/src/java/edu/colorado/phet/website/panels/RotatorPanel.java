package edu.colorado.phet.website.panels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.content.simulations.CategoryPage;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class RotatorPanel extends PhetPanel {
    public RotatorPanel( String id, PageContext context ) {
        super( id, context );

//        setMarkupId( "rotator-holder-id" );
//        setOutputMarkupId( true );

        //String flashvars = "dir=ltr&quantity=2&project1=mass-spring-lab&sim1=mass-spring-lab&title1=Masses+%26+Springs&url1=%2Fen%2Fsimulation%2Fmass-spring-lab&project2=circuit-construction-kit&sim2=circuit-construction-kit-dc&title2=Circuit+Construction+Kit+%28DC+Only%29&url2=%2Fen%2Fsimulation%2Fcircuit-construction-kit%2Fcircuit-construction-kit-dc";
        final List<LocalizedSimulation> featured = new LinkedList<LocalizedSimulation>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "mass-spring-lab", "mass-spring-lab" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "circuit-construction-kit", "circuit-construction-kit-dc" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "soluble-salts", "soluble-salts" ) );
                return true;
            }
        } );

        StringBuffer buf = new StringBuffer();
        buf.append( "dir=" + getPhetLocalizer().getBestString( getHibernateSession(), "language.dir", getMyLocale() ) + "&" );
        buf.append( "quantity=" + Integer.toString( featured.size() ) + "&" );
        buf.append( "offlineInstaller=" + getPhetCycle().isOfflineInstaller() + "&" );
        try {
            buf.append( "next=" + URLEncoder.encode( getPhetLocalizer().getString( "home.rotator.next", this ), "UTF-8" ) + "&" );
            buf.append( "previous=" + URLEncoder.encode( getPhetLocalizer().getString( "home.rotator.previous", this ), "UTF-8" ) + "&" );

            int idx = 1;
            for ( LocalizedSimulation lsim : featured ) {
                String ids = Integer.toString( idx++ );
                buf.append( "sim" + ids + "=" + URLEncoder.encode( lsim.getSimulation().getName(), "UTF-8" ) + "&" );
                buf.append( "title" + ids + "=" + URLEncoder.encode( lsim.getTitle(), "UTF-8" ) + "&" );
                buf.append( "url" + ids + "=" + URLEncoder.encode( SimulationPage.getLinker( lsim ).getRawUrl( context, getPhetCycle() ), "UTF-8" ) + "&" );
            }
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }

        // TODO: separate out rotator panel

        Model flashvarsModel = new Model( buf.toString() );

        Label paramLabel = new Label( "flash-param" );
        paramLabel.add( new AttributeAppender( "value", flashvarsModel, " " ) );
        add( paramLabel );

        Label embedLabel = new Label( "flash-embed" );
        embedLabel.add( new AttributeAppender( "FlashVars", flashvarsModel, " " ) );
        add( embedLabel );

        add( CategoryPage.getLinker().getLink( "fallback-link", context, getPhetCycle() ) );
    }
}