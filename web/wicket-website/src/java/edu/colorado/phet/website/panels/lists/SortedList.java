package edu.colorado.phet.website.panels.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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
    private Label listEmpty;

    public abstract boolean onItemAdd( Item item );

    public abstract boolean onItemRemove( Item item, int index );

    private static final Logger logger = Logger.getLogger( SortedList.class.getName() );

    public SortedList( String id, PageContext context, final List<Item> items, final List<Item> allItems ) {
        super( id, context );
        this.items = items;
        this.allItems = allItems;

        // output a markup ID so this component can be updated within ajax
        setOutputMarkupId( true );

        sortItems( items );
        sortItems( allItems );

        Form form = new Form( "form" );
        add( form );

        listEmpty = new Label( "list-empty", new Model( getPhetLocalizer().getString( "list.empty", this ) ) );
        form.add( listEmpty );

        form.add( new ListView( "items", items ) {
            protected void populateItem( final ListItem listItem ) {
                final Item item = (Item) listItem.getModel().getObject();
                listItem.add( item.getDisplayComponent( "item-component" ) );
                listItem.add( new AjaxFallbackLink( "remove" ) {
                    public void onClick( AjaxRequestTarget target ) {
                        boolean success = onItemRemove( item, listItem.getIndex() );
                        if ( success ) {
                            items.remove( item );
                            updateEmpty();
                            target.addComponent( SortedList.this );
                        }
                    }
                } );
            }
        } );

        dropDownChoice = new ItemDropDownChoice( "options", allItems );

        form.add( dropDownChoice );

        AjaxButton link = new AjaxButton( "button" ) {
            protected void onSubmit( AjaxRequestTarget target, Form form ) {
                dropDownChoice.updateModel();
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
                else {
                    logger.warn( "item was null. itemId=" + itemId );
                }
                if ( success ) {
                    items.add( item );
                    sortItems( items );
                }

                updateEmpty();

                // redraw the whole list, but nothing else
                target.addComponent( SortedList.this );
            }
        };
        form.add( link );
    }

    private void updateEmpty() {
        listEmpty.setVisible( items.isEmpty() );
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

    public DropDownChoice getFormComponent() {
        return dropDownChoice;
    }
}