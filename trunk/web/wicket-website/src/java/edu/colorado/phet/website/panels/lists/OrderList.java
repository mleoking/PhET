package edu.colorado.phet.website.panels.lists;

import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * A panel that displays a list of items, which can have items added, removed, and rearranged
 *
 * @param <Item>
 */
public abstract class OrderList<Item extends OrderListItem> extends PhetPanel {
    public DropDownChoice dropDownChoice;
    private List<Item> items;
    private List<Item> allItems;
    private OrderForm form;

    public abstract boolean onItemAdd( Item item );

    public abstract boolean onItemRemove( Item item, int index );

    public abstract boolean onItemSwap( Item a, Item b, int aIndex, int bIndex );

    public abstract Component getHeaderComponent( String id );

    public OrderList( String id, PageContext context, final List<Item> items, List<Item> allItems ) {
        super( id, context );
        this.items = items;
        this.allItems = allItems;

        form = new OrderForm( "form" );

        form.add( getHeaderComponent( "header-component" ) );

        form.add( new ListView( "items", items ) {
            protected void populateItem( final ListItem listItem ) {
                final Item item = (Item) listItem.getModel().getObject();
                listItem.add( item.getDisplayComponent( "item-component" ) );
                listItem.add( new Link( "remove" ) {
                    public void onClick() {
                        boolean success = onItemRemove( item, listItem.getIndex() );
                        if ( success ) {
                            items.remove( item );
                        }
                    }
                } );
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

        dropDownChoice = new ItemDropDownChoice( "options", allItems );

        form.add( dropDownChoice );

        add( form );
    }

    private void swapItemOrder( int a, int b ) {
        boolean success = onItemSwap( items.get( a ), items.get( b ), a, b );
        if ( success ) {
            Collections.swap( items, a, b );
        }
    }

    private class OrderForm extends Form {
        public OrderForm( String id ) {
            super( id );
        }

        @Override
        protected void onSubmit() {
            int itemId = Integer.valueOf( dropDownChoice.getModelValue() );
            Item item = null;
            for ( Item allItem : allItems ) {
                if ( allItem.getId() == itemId ) {
                    item = allItem;
                    break;
                }
            }
            boolean success = item != null;
            if ( success ) {
                success = onItemAdd( item );
            }
            if ( success ) {
                items.add( item );
            }
        }
    }

    public class ItemDropDownChoice extends DropDownChoice {
        public ItemDropDownChoice( String id, List<Item> allKeywords ) {
            super( id, new Model(), allKeywords, new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    if ( object instanceof OrderListItem ) {
                        return ( (OrderListItem) object ).getDisplayValue();
                    }
                    else {
                        throw new RuntimeException( "Not an OrderListItem" );
                    }
                    //return PhetWicketApplication.get().getResourceSettings().getLocalizer().getString( ( (Keyword) object ).getKey(), AdminSimPage.this );
                }

                public String getIdValue( Object object, int index ) {
                    if ( object instanceof OrderListItem ) {
                        return String.valueOf( ( (OrderListItem) object ).getId() );
                    }
                    else {
                        throw new RuntimeException( "Not an OrderListItem" );
                    }
                    //return String.valueOf( ( (Keyword) object ).getId() );
                }
            } );
        }
    }
}