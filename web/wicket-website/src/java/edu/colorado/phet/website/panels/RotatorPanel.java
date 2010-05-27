package edu.colorado.phet.website.panels;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
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

        final List<Entry> featured = new LinkedList<Entry>();

        // TODO: start caching these best simulations?
        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "mass-spring-lab" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "circuit-construction-kit-dc" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "soluble-salts" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "my-solar-system" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "wave-on-a-string" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "glaciers" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "lunar-lander" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "curve-fitting" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "projectile-motion" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "equation-grapher" ), 1 ) );
                featured.add( new Entry( HibernateUtils.getBestSimulation( session, getMyLocale(), "friction" ), 1 ) );
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
        for ( Entry entry : featured ) {
            LocalizedSimulation lsim = entry.getLocalizedSimulation();
            String ids = Integer.toString( idx++ );
            appendParameter( "sim" + ids, lsim.getSimulation().getName() );
            appendParameter( "title" + ids, lsim.getTitle() );
            appendParameter( "url" + ids, SimulationPage.getLinker( lsim ).getRawUrl( context, getPhetCycle() ) );
            appendParameter( "v" + ids, Integer.toString( entry.getVersion() ) );
        }

        Model flashvarsModel = new Model<String>( builder.toString() );

        Label paramLabel = new Label( "flash-param" );
        paramLabel.add( new AttributeAppender( "value", flashvarsModel, " " ) );
        add( paramLabel );

        Label paramLabel2 = new Label( "flash-param-2" );
        paramLabel2.add( new AttributeAppender( "value", flashvarsModel, " " ) );
        add( paramLabel2 );

        add( new RotatorFallbackPanel( "rotator-fallback-panel", context ) );
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

    private static class Entry {
        private LocalizedSimulation localizedSimulation;
        private int version;

        private Entry( LocalizedSimulation localizedSimulation, int version ) {
            this.localizedSimulation = localizedSimulation;
            this.version = version;
        }

        public LocalizedSimulation getLocalizedSimulation() {
            return localizedSimulation;
        }

        public int getVersion() {
            return version;
        }
    }

}