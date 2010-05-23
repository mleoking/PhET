package edu.colorado.phet.website.content.about;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.hibernate.Session;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.authentication.PhetSession;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.IndexPage;
import edu.colorado.phet.website.content.ResearchPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.data.PhetUser;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutMainPanel extends PhetPanel {
    public AboutMainPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-p1", "about.p1", new Object[]{
                ResearchPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "about-p2", "about.p2" ) );

        add( new LocalizedText( "about-p3", "about.p3", new Object[]{
                AboutLegendPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "about-p4", "about.p4", new Object[]{
                IndexPage.getLinker().getHref( context, getPhetCycle() ),
                TroubleshootingJavaPanel.getLinker().getHref( context, getPhetCycle() ),
                TroubleshootingFlashPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        final List<PhetUser> members = new LinkedList<PhetUser>();

        HibernateUtils.wrapTransaction( getHibernateSession(), new HibernateTask() {
            public boolean run( Session session ) {
                List list = session.createQuery( "select u from PhetUser as u where u.teamMember = true" ).list();
                for ( Object o : list ) {
                    members.add( (PhetUser) o );
                }
                return true;
            }
        } );

        Collections.sort( members, new Comparator<PhetUser>() {
            public int compare( PhetUser a, PhetUser b ) {
                return a.getName().compareTo( b.getName() );
            }
        } );

        final boolean isAdmin = PhetSession.get().isSignedIn() && PhetSession.get().getUser().isTeamMember();

        add( new ListView( "person", members ) {
            protected void populateItem( ListItem item ) {
                PhetUser user = (PhetUser) item.getModel().getObject();
                item.add( new Label( "name", user.getName() ) );
                item.add( new Label( "title", user.getJobTitle() ) );
                if ( isAdmin ) {
                    Label marker = new Label( "show-admin", "" );
                    marker.setRenderBodyOnly( true );
                    item.add( marker );
                    item.add( new Label( "primary-phone", user.getPhone1() ) );
                    item.add( new Label( "secondary-phone", user.getPhone2() ) );
                }
                else {
                    item.add( new InvisibleComponent( "show-admin" ) );
                    item.add( new InvisibleComponent( "primary-phone" ) );
                    item.add( new InvisibleComponent( "secondary-phone" ) );
                }
            }
        } );
    }

    public static String getKey() {
        return "about";
    }

    public static String getUrl() {
        return "about";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutMainPanel.class ) ) {
                    return "http://phet.colorado.edu/about/index.php";
                }
                else {
                    return super.getRawUrl( context, cycle );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
