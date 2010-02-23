package edu.colorado.phet.website.panels.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;

/**
 * Like the OrderList, however items are automatically sorted (and thus cannot be rearranged). Applicable for use with sets
 *
 * @param <Item>
 */
public abstract class SortedList<Item extends SortableListItem> extends PhetPanel {
    public DropDownChoice dropDownChoice;
    private List<Item> items;
    private List<Item> allItems;

    public abstract boolean onAdd( Item item );

    public abstract boolean onRemove( Item item, int index );

    public SortedList( String id, PageContext context, final List<Item> items, final List<Item> allItems ) {
        super( id, context );
        this.items = items;
        this.allItems = allItems;

        // output a markup ID so this component can be updated within ajax
        setOutputMarkupId( true );

        sortItems( items );
        sortItems( allItems );

        add( new ListView( "items", items ) {
            protected void populateItem( final ListItem listItem ) {
                final Item item = (Item) listItem.getModel().getObject();
                listItem.add( item.getDisplayComponent( "item-component" ) );
                listItem.add( new Link( "remove" ) {
                    public void onClick() {
                        boolean success = onRemove( item, listItem.getIndex() );
                        if ( success ) {
                            items.remove( item );
                        }
                    }
                } );
            }
        } );

        dropDownChoice = new ItemDropDownChoice( "options", allItems );

        add( dropDownChoice );

        add( new AjaxButton( "button" ) {
            protected void onSubmit( AjaxRequestTarget target, Form form ) {
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
                    success = onAdd( item );
                }
                if ( success ) {
                    items.add( item );
                    sortItems( items );
                }

                // redraw the whole list, but nothing else
                target.addComponent( SortedList.this );
            }
        } );
    }

    private void sortItems( List<Item> list ) {
        Collections.sort( list, new Comparator<Item>() {
            public int compare( Item a, Item b ) {
                return a.compareTo( b, getLocale() );
            }
        } );
    }

    public class ItemDropDownChoice extends DropDownChoice {
        public ItemDropDownChoice( String id, List<Item> allKeywords ) {
            super( id, new Model(), allKeywords, new IChoiceRenderer() {
                public Object getDisplayValue( Object object ) {
                    if ( object instanceof SortableListItem ) {
                        return ( (SortableListItem) object ).getDisplayValue();
                    }
                    else {
                        throw new RuntimeException( "Not an SortableListItem" );
                    }
                }

                public String getIdValue( Object object, int index ) {
                    if ( object instanceof SortableListItem ) {
                        return String.valueOf( ( (SortableListItem) object ).getId() );
                    }
                    else {
                        throw new RuntimeException( "Not an SortableListItem" );
                    }
                }
            } );
        }
    }
}