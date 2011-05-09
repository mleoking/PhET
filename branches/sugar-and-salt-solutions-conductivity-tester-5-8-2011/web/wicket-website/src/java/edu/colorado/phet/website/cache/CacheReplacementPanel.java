package edu.colorado.phet.website.cache;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.internal.HtmlHeaderContainer;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * A lightweight PhetPanel that can have an arbitrary inserted header and body text. Useful for injecting cached
 * versions of other panels
 */
public class CacheReplacementPanel extends PhetPanel {

    private CharSequence header;
    private CharSequence body;

    private static final Logger logger = Logger.getLogger( CacheReplacementPanel.class.getName() );

    /**
     * @param id      The Wicket id for the panel to use
     * @param context The page context
     * @param header  The text to use for the header part (inserted into the HTML head element)
     * @param body    The text to use for the main part (inserted where the original panel is placed)
     */
    public CacheReplacementPanel( String id, PageContext context, CharSequence header, CharSequence body ) {
        super( id, context );

        this.header = header;
        this.body = body;
    }

    @Override
    public void renderHead( HtmlHeaderContainer container ) {
        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();
        response.write( header );
    }

    @Override
    protected void onRender( MarkupStream markupStream ) {
        // skip the component area in the markup, so it appears to have been rendered
        markupStream.skipComponent();

        RequestCycle cycle = getRequestCycle();
        Response response = cycle.getResponse();
        response.write( body );
    }
}
