package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.Linkable;

public class IndexPage extends PhetPage {
    public IndexPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( new ResourceModel( "home.title" ) );

        add( new IndexPanel( "index-panel", getPageContext() ) );

    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^$", IndexPage.class );
    }

    public static PhetLink createLink( String id, PageContext context ) {
        if ( context.getPrefix().equals( "/en/" ) ) {
            return new PhetLink( id, "/" );
        }
        else {
            return new PhetLink( id, context.getPrefix() );
        }
    }

    public static Linkable getLinker() {
        return new Linkable() {
            public Link getLink( String id, PageContext context ) {
                return createLink( id, context );
            }
        };
    }
}