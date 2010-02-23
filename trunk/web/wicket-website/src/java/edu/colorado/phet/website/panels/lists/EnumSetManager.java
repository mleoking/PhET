package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Handles the selection of a set of enumeration values. The user can add / remove values.
 *
 * Can use getComponent() to return the necessary component.
 */
public abstract class EnumSetManager<E extends Enum> implements Serializable {

    /**
     * List of the INITIAL values
     */
    private List<E> values;

    /**
     * List of all possible values
     */
    private List<E> allValues;

    /**
     * Locale-specific mapping of display strings
     */
    private Map<E, String> titleMap;

    /**
     * Current list of selected items
     */
    private List<ListItem<E>> items;

    /**
     * List of all possible items
     */
    private List<ListItem<E>> allItems;

    /**
     * NOTE: already in transaction, so throw all exceptions!
     *
     * @param session current session
     * @return collection of values currently in set
     */
    public abstract Collection<E> getInitialTypes( Session session );

    /**
     * @return collection of all possible values
     */
    public abstract Collection<E> getAllTypes();

    /**
     * @param val value
     * @return the translation key that should be displayed
     */
    public abstract String getTranslationKey( E val );

    /**
     * @param session A session that we can use to wrap transactions with
     * @param locale  The locale to compute the item display values with
     */
    public EnumSetManager( Session session, final Locale locale ) {
        values = new LinkedList<E>();
        allValues = new LinkedList<E>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( Object o : getInitialTypes( session ) ) {
                    values.add( (E) o );
                }
                return true;
            }
        } );

        for ( E type : getAllTypes() ) {
            allValues.add( type );
        }

        titleMap = new HashMap<E, String>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( E type : allValues ) {
                    titleMap.put( type, StringUtils.getString( session, getTranslationKey( type ), locale ) );
                }
                return true;
            }
        } );

        items = new LinkedList<ListItem<E>>();
        allItems = new LinkedList<ListItem<E>>();

        for ( E type : values ) {
            items.add( new ListItem( type, titleMap.get( type ) ) );
        }

        for ( E type : allValues ) {
            allItems.add( new ListItem( type, titleMap.get( type ) ) );
        }
    }


    public SortedList<ListItem<E>> getComponent( String id, PageContext context ) {
        return new SortedList<ListItem<E>>( id, context, items, allItems ) {
            public boolean onAdd( final ListItem item ) {
                for ( ListItem oldItem : items ) {
                    if ( oldItem.getId() == item.getId() ) {
                        // already in list. don't want duplicates!
                        return false;
                    }
                }
                return true;
            }

            public boolean onRemove( final ListItem item, int index ) {
                return true;
            }

        };
    }

    public List<ListItem<E>> getItems() {
        return items;
    }

    public List<E> getValues() {
        List<E> ret = new LinkedList<E>();
        for ( ListItem<E> item : getItems() ) {
            ret.add( item.getValue() );
        }
        return ret;
    }

    private static class ListItem<F extends Enum> implements SortableListItem, Serializable {

        private F val;
        private String desc;

        public ListItem( F val, String desc ) {
            this.val = val;
            this.desc = desc;
        }

        public int compareTo( SortableListItem item, Locale locale ) {
            return getDisplayValue().compareToIgnoreCase( item.getDisplayValue() );
        }

        public String getDisplayValue() {
            return desc;
        }

        public Component getDisplayComponent( String id ) {
            return new Label( id, desc );
        }

        public int getId() {
            return val.ordinal();
        }

        public F getValue() {
            return val;
        }
    }

}