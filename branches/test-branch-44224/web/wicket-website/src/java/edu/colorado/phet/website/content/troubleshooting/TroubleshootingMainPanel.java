package edu.colorado.phet.website.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.content.ForTranslatorsPanel;
import edu.colorado.phet.website.content.about.AboutLicensingPanel;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TroubleshootingMainPanel extends PhetPanel {
    public TroubleshootingMainPanel( String id, PageContext context ) {
        super( id, context );

        add( TroubleshootingJavaPanel.getLinker().getLink( "to-java", context, getPhetCycle() ) );
        add( TroubleshootingFlashPanel.getLinker().getLink( "to-flash", context, getPhetCycle() ) );
        add( TroubleshootingJavascriptPanel.getLinker().getLink( "to-javascript", context, getPhetCycle() ) );

        add( new LocalizedText( "troubleshooting-main-intro", "troubleshooting.main.intro", new Object[]{
                Linkers.getHelpLink( "Help", context, getPhetCycle() ),
                ""
        } ) );

        add( new LocalizedText( "troubleshooting-main-q1-answer", "troubleshooting.main.q1.answer", new Object[]{
                TroubleshootingJavaPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q2-answer", "troubleshooting.main.q2.answer", new Object[]{
                FullInstallPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q4-answer", "troubleshooting.main.q4.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q5-answer", "troubleshooting.main.q5.answer", new Object[]{
                Linkers.getHelpLink( "Flash Simulations", context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q6-answer", "troubleshooting.main.q6.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q7-answer", "troubleshooting.main.q7.answer", new Object[]{
                Linkers.getHelpLink( "Windows 2000 Issues", context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q8-answer", "troubleshooting.main.q8.answer", new Object[]{
                Linkers.getHelpLink( "Laptop Performance Issues", context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q9-answer", "troubleshooting.main.q9.answer", new Object[]{
                Linkers.getHelpLink( "Sound Issues", context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q10-answer", "troubleshooting.main.q10.answer", new Object[]{
                ForTranslatorsPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q11-answer", "troubleshooting.main.q11.answer", new Object[]{
                FullInstallPanel.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "troubleshooting-main-q12-answer", "troubleshooting.main.q12.answer" ) );

        add( new LocalizedText( "troubleshooting-main-q13-answer", "troubleshooting.main.q13.answer" ) );

        add( AboutLicensingPanel.getLinker().getLink( "licensing-link", context, getPhetCycle() ) );

        add( HeaderContributor.forCss( CSS.TROUBLESHOOTING ) );

    }

    public static String getKey() {
        return "troubleshooting.main";
    }

    public static String getUrl() {
        return "troubleshooting";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, TroubleshootingMainPanel.class ) ) {
                    return "http://phet.colorado.edu/tech_support/index.php";
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