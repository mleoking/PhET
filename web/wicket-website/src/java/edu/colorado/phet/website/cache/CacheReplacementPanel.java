package edu.colorado.phet.website.cache;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

public class CacheReplacementPanel extends PhetPanel {

    private CharSequence header;
    private CharSequence body;

    private static Logger logger = Logger.getLogger( CacheReplacementPanel.class.getName() );

    public CacheReplacementPanel( String id, PageContext context, CharSequence header, CharSequence body ) {
        super( id, context );

        this.header = header;
        this.body = body;

        //logger.debug( "init header " + header );
        //logger.debug( "init body " + body );
    }

    @Override
    public void renderHead( HtmlHeaderContainer container ) {
        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();
        response.write( header );
    }

    @Override
    protected void onRender( MarkupStream markupStream ) {
        markupStream.skipComponent();
        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();
        response.write( body );
    }
}
