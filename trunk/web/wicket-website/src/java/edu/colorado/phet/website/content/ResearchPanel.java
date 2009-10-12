package edu.colorado.phet.website.content;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ResearchPanel extends PhetPanel {
    public ResearchPanel( String id, PageContext context ) {
        super( id, context );

//        add( new LocalizedText( "research-additional", "research.additional", new Object[]{
//                "href=\"/publications/PhET%20Look%20and%20Feel.pdf\""
//        } ) );

        add( new LocalizedText( "research-top", "research.top" ) );

        add( new LocalizedText( "research-intro", "research.intro", new Object[]{
                "href=\"/publications/PhET%20Design%20Process.pdf\"",
                "href=\"/publications/PhET%20Look%20and%20Feel.pdf\"",
                "href=\"#pub_1\""
        } ) );

        add( new LocalizedText( "research-question-1-answer", "research.question.1.answer" ) );
        add( new LocalizedText( "research-question-2-answer", "research.question.2.answer" ) );
        add( new LocalizedText( "research-question-3-answer", "research.question.3.answer" ) );

        add( new LocalizedText( "research-interest-analogy", "research.interest.analogy" ) );
        add( new LocalizedText( "research-interest-norms", "research.interest.norms" ) );
        add( new LocalizedText( "research-interest-exploration", "research.interest.exploration" ) );
        add( new LocalizedText( "research-interest-homework", "research.interest.homework" ) );
        add( new LocalizedText( "research-interest-chemistry", "research.interest.chemistry" ) );

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