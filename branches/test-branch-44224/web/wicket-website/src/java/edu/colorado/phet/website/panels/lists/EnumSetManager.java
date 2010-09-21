package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Handles the selection of a set of enumeration values. The user can add / remove values.
 * <p/>
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
    private LinkedList<ListItem<E>> items;

    /**
     * List of all possible items
     */
    private LinkedList<ListItem<E>> allItems;

    private static final Logger logger = Logger.getLogger( EnumSetManager.class.getName() );

    /**
     * NOTE: already in transaction, so throw all exceptions!
     *
     * @param session current session
     * @return collection of values currently in set
     */
    public abstract Collection<E> getInitialValues( Session session );

    /**
     * @return collection of all possible values
     */
    public abstract Collection<E> getAllValues();

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
                for ( Object o : getInitialValues( session ) ) {
                    values.add( (E) o );
                }
                return true;
            }
        } );

        for ( E type : getAllValues() ) {
            allValues.add( type );
        }

        titleMap = new HashMap<E, String>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( E type : allValues ) {
                    String translatedString = StringUtils.getStringDirect( session, getTranslationKey( type ), locale );
                    if ( translatedString == null ) {
                        // if the string isn't translated yet, use the English version? hrmm...
                        // TODO: handle this with more grace. don't do the direct string
                        translatedString = StringUtils.getDefaultStringDirect( session, getTranslationKey( type ) );
                    }
                    if ( translatedString == null ) {
                        logger.warn( "failure to get a good string for translation key " + getTranslationKey( type ) + " for val " + type );
                    }
                    titleMap.put( type, translatedString );
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
            public boolean onItemAdd( final ListItem item ) {
                for ( ListItem oldItem : items ) {
                    if ( oldItem.getId() == item.getId() ) {
                        // already in list. don't want duplicates!
                        return false;
                    }
                }
                return true;
            }

            public boolean onItemRemove( final ListItem item, int index ) {
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

    public static class ListItem<F extends Enum> implements SortableListItem, Serializable {

        private F val;
        private String desc;

        public ListItem( F val, String desc ) {
            this.val = val;
            this.desc = desc;
        }

        public int compareTo( SortableListItem item, Locale locale ) {
            if ( item == null || getDisplayValue() == null ) {
                logger.warn( "item comparison failure, null detected" );
                logger.warn( "my class: " + this.getClass().getCanonicalName() );
                logger.warn( "my id: " + getId() );
                logger.warn( "my value: " + getValue() );
                logger.warn( "my display value: " + getDisplayValue() );
                if ( item == null ) {
                    logger.warn( "item == null" );
                }
                else {
                    logger.warn( "item id: " + item.getId() );
                    logger.warn( "item display value: " + item.getDisplayValue() );
                }
            }
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