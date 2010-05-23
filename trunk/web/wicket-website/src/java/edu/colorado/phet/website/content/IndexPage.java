package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.RawLinkable;

public class IndexPage extends PhetPage {
    public IndexPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( new ResourceModel( "home.title" ) );

        add( new IndexPanel( "index-panel", getPageContext() ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^$", IndexPage.class );
    }

    public static RawLink createLink( String id, PageContext context ) {
        if ( context.getPrefix().equals( "/en/" ) ) {
            return new RawLink( id, "/" );
        }
        else {
            return new RawLink( id, context.getPrefix() );
        }
    }

    public static RawLinkable getLinker() {
        return new RawLinkable() {
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( context.getPrefix().equals( "/en/" ) ) {
                    return "/";
                }
                else {
                    return context.getPrefix();
                }
            }

            public String getHref( PageContext context, PhetRequestCycle cycle ) {
                return "href=\"" + getRawUrl(context, cycle ) + "\"";
            }

            public String getDefaultRawUrl() {
                return "/";
            }

            public Link getLink( String id, PageContext context, PhetRequestCycle cycle ) {
                return new RawLink( id, getRawUrl( context, cycle ));
            }
        };
    }
}