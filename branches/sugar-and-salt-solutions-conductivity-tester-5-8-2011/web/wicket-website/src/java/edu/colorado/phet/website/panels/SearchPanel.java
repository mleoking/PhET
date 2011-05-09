package edu.colorado.phet.website.panels;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainerWithAssociatedMarkup;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.util.PageContext;

public class SearchPanel extends PhetPanel {
    public SearchPanel( String id, PageContext context ) {
        super( id, context );

        WebMarkupContainerWithAssociatedMarkup form = new WebMarkupContainerWithAssociatedMarkup( "search-form" );
        form.add( new AttributeAppender( "action", true, new Model( context.getPrefix() + "search" ), " " ) );
        add( form );
    }
}