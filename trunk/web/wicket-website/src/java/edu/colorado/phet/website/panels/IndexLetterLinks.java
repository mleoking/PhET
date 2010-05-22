package edu.colorado.phet.website.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;

public class IndexLetterLinks extends PhetPanel {
    public IndexLetterLinks( String id, PageContext context, List<String> letters ) {
        super( id, context );

        add( new ListView( "letter-links", letters ) {
            protected void populateItem( ListItem item ) {
                String letter = item.getModelObject().toString();
                Link link = new RawLink( "letter-link", "#" + HibernateUtils.encodeCharacterId( letter ) );
                link.add( new Label( "letter", letter ) );
                item.add( link );
            }
        } );
    }
}
