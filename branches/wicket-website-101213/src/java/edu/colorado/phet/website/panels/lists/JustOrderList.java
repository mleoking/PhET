package edu.colorado.phet.website.panels.lists;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * A panel that displays a list of items, where they can only be rearranged.
 *
 * @param <Item>
 */
public abstract class JustOrderList<Item extends OrderListItem> extends PhetPanel {
    public DropDownChoice dropDownChoice;
    private List<Item> items;
    private Form form;

    public abstract boolean onSwap( Item a, Item b, int aIndex, int bIndex );

    public abstract Component getHeaderComponent( String id );

    public JustOrderList( String id, PageContext context, final List<Item> items ) {
        super( id, context );
        this.items = items;

        form = new Form( "form" );

        form.add( getHeaderComponent( "header-component" ) );

        form.add( new ListView( "items", items ) {
            protected void populateItem( final ListItem listItem ) {
                final Item item = (Item) listItem.getModel().getObject();
                listItem.add( item.getDisplayComponent( "item-component" ) );
                if ( listItem.getIndex() != 0 ) {
                    listItem.add( new Link( "move-up" ) {
                        public void onClick() {
                            swapItemOrder( listItem.getIndex() - 1, listItem.getIndex() );
                        }
                    } );
                }
                else {
                    listItem.add( new InvisibleComponent( "move-up" ) );
                }
                if ( listItem.getIndex() < items.size() - 1 ) {
                    listItem.add( new Link( "move-down" ) {
                        public void onClick() {
                            swapItemOrder( listItem.getIndex(), listItem.getIndex() + 1 );
                        }
                    } );
                }
                else {
                    listItem.add( new InvisibleComponent( "move-down" ) );
                }
            }
        } );

        add( form );
    }

    private void swapItemOrder( int a, int b ) {
        boolean success = onSwap( items.get( a ), items.get( b ), a, b );
        if ( success ) {
            Collections.swap( items, a, b );
        }
    }

}