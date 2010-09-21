package edu.colorado.phet.website.panels.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxFallbackLink;
import org.apache.wicket.ajax.markup.html.AjaxLink;
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

    public SortedList( String id, PageContext context, final LinkedList<Item> items, final LinkedList<Item> allItems ) {
        super( id, context );
        this.items = items;
        this.allItems = allItems;

        // output a markup ID so this component can be updated within ajax
        setOutputMarkupId( true );

        sortItems( items );
        sortItems( allItems );

        Form form = new Form( "form" );
        add( form );

        listEmpty = new Label( "list-empty", new Model<String>( getPhetLocalizer().getString( "list.empty", this ) ) );
        form.add( listEmpty );

        Model<LinkedList<Item>> itemsModel = new Model<LinkedList<Item>>( items );

        form.add( new ListView<Item>( "items", itemsModel ) {
            @Override
            protected void populateItem( final ListItem<Item> listItem ) {
                final Item item = listItem.getModelObject();
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

        form.add( new AjaxLink( "button" ) {
            public void onClick( AjaxRequestTarget target ) {
                //dropDownChoice.updateModel();
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
        } );

        // when the drop down choice selection is changed, update the model
        dropDownChoice.add( new AjaxFormComponentUpdatingBehavior( "onchange" ) {
            @Override
            protected void onUpdate( AjaxRequestTarget target ) {
                dropDownChoice.updateModel();
            }
        } );
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