package edu.colorado.phet.wickettest.panels;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.wickettest.components.PhetLink;
import edu.colorado.phet.wickettest.util.HibernateUtils;
import edu.colorado.phet.wickettest.util.PageContext;

public class IndexLetterLinks extends PhetPanel {
    public IndexLetterLinks( String id, PageContext context, List<String> letters ) {
        super( id, context );

        add( new ListView( "letter-links", letters ) {
            protected void populateItem( ListItem item ) {
                String letter = item.getModelObjectAsString();
                Link link = new PhetLink( "letter-link", "#" + HibernateUtils.encodeCharacterId( letter ) );
                link.add( new Label( "letter", letter ) );
                item.add( link );
            }
        } );
    }
}
