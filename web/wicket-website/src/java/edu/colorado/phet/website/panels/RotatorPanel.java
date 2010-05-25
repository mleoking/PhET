package edu.colorado.phet.website.panels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.hibernate.Session;

import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.data.LocalizedSimulation;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class RotatorPanel extends PhetPanel {

    /**
     * Used to build the flashvars
     */
    private StringBuilder builder = new StringBuilder();

    private static final Random random = new Random();

    public RotatorPanel( String id, PageContext context ) {
        super( id, context );

        final List<LocalizedSimulation> featured = new LinkedList<LocalizedSimulation>();

        // TODO: start caching these best simulations?
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "mass-spring-lab" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "circuit-construction-kit-dc" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "soluble-salts" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "my-solar-system" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "lunar-lander" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "projectile-motion" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "friction" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "wave-on-a-string" ) );
                featured.add( HibernateUtils.getBestSimulation( session, getMyLocale(), "pendulum-lab" ) );
                return true;
            }
        } );

        appendParameter( "dir", getPhetLocalizer().getBestString( getHibernateSession(), "language.dir", getMyLocale() ) );
        appendParameter( "quantity", Integer.toString( featured.size() ) );
        appendParameter( "offlineInstaller", String.valueOf( getPhetCycle().isOfflineInstaller() ) );
        appendParameter( "next", getPhetLocalizer().getString( "home.rotator.next", this ) );
        appendParameter( "previous", getPhetLocalizer().getString( "home.rotator.previous", this ) );
        appendParameter( "startIndex", String.valueOf( random.nextInt( featured.size() ) ) );
        int idx = 1;
        for ( LocalizedSimulation lsim : featured ) {
            String ids = Integer.toString( idx++ );
            appendParameter( "sim" + ids, lsim.getSimulation().getName() );
            appendParameter( "title" + ids, lsim.getTitle() );
            appendParameter( "url" + ids, SimulationPage.getLinker( lsim ).getRawUrl( context, getPhetCycle() ) );
        }

        Model flashvarsModel = new Model<String>( builder.toString() );

        Label paramLabel = new Label( "flash-param" );
        paramLabel.add( new AttributeAppender( "value", flashvarsModel, " " ) );
        add( paramLabel );

        Label paramLabel2 = new Label( "flash-param-2" );
        paramLabel2.add( new AttributeAppender( "value", flashvarsModel, " " ) );
        add( paramLabel2 );

        Link fallbackLink = SimulationPage.getLinker( "mass-spring-lab", "mass-spring-lab" ).getLink( "fallback-link", context, getPhetCycle() );
        add( fallbackLink );
        if ( getPhetCycle().isOfflineInstaller() ) {
            fallbackLink.add( new AttributeModifier( "class", true, new Model<String>( "installer" ) ) );
        }
    }

    /**
     * Append a flashvar variable to the string builder
     *
     * @param key   Key
     * @param value Value
     */
    private void appendParameter( String key, String value ) {
        try {
            builder.append( key );
            builder.append( "=" );

            // encode it
            builder.append( URLEncoder.encode( value, "UTF-8" ) );
            builder.append( "&amp;" );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
        }
    }

}