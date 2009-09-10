package edu.colorado.phet.wickettest.content;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

public class ResearchPanel extends PhetPanel {
    public ResearchPanel( String id, PageContext context ) {
        super( id, context );

//        add( new LocalizedText( "research-additional", "research.additional", new Object[]{
//                "href=\"/publications/PhET%20Look%20and%20Feel.pdf\""
//        } ) );

        add( HeaderContributor.forCss( "/css/research-v1.css" ) );

    }

    public static String getKey() {
        return "research";
    }

    public static String getUrl() {
        return "research";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}