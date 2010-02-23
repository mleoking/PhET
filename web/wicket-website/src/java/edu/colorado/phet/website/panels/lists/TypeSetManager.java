package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.*;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.hibernate.Session;

import edu.colorado.phet.website.data.contribution.Type;
import edu.colorado.phet.website.util.HibernateTask;
import edu.colorado.phet.website.util.HibernateUtils;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.StringUtils;

/**
 * Handles a simulation set control where the user can select or remove simulations
 */
public abstract class TypeSetManager implements Serializable {

    // TODO: generify!

    private List<Type> types;
    private List<Type> allTypes;
    private Map<Type, String> titleMap;
    private List<TypeListItem> items;
    private List<TypeListItem> allItems;

    /**
     * NOTE: already in transaction, so throw all exceptions!
     *
     * @param session current session
     * @return list of simulations currently in set
     */
    public abstract Set getInitialTypes( Session session );

    public TypeSetManager( Session session, final Locale locale ) {
        types = new LinkedList<Type>();
        allTypes = new LinkedList<Type>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( Object o : getInitialTypes( session ) ) {
                    types.add( (Type) o );
                }
                return true;
            }
        } );

        for ( Type type : Type.values() ) {
            allTypes.add( type );
        }

        titleMap = new HashMap<Type, String>();

        HibernateUtils.wrapTransaction( session, new HibernateTask() {
            public boolean run( Session session ) {
                for ( Type type : allTypes ) {
                    titleMap.put( type, StringUtils.getString( session, type.getTranslationKey(), locale ) );
                }
                return true;
            }
        } );

        items = new LinkedList<TypeListItem>();
        allItems = new LinkedList<TypeListItem>();

        for ( Type type : types ) {
            items.add( new TypeListItem( type, titleMap.get( type ) ) );
        }

        for ( Type type : allTypes ) {
            allItems.add( new TypeListItem( type, titleMap.get( type ) ) );
        }
    }

    public SortedList<TypeListItem> getComponent( String id, PageContext context ) {
        return new SortedList<TypeListItem>( id, context, items, allItems ) {
            public boolean onAdd( final TypeListItem item ) {
                for ( TypeListItem oldItem : items ) {
                    if ( oldItem.getId() == item.getId() ) {
                        // already in list. don't want duplicates!
                        return false;
                    }
                }
                return true;
            }

            public boolean onRemove( final TypeListItem item, int index ) {
                return true;
            }

        };
    }

    public List<TypeListItem> getItems() {
        return items;
    }

    public List<Type> getTypes() {
        List<Type> ret = new LinkedList<Type>();
        for ( TypeListItem item : getItems() ) {
            ret.add( item.getType() );
        }
        return ret;
    }

    public static class TypeListItem implements SortableListItem, Serializable {

        private Type type;
        private String desc;

        public TypeListItem( Type type, String desc ) {
            this.type = type;
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
            return type.ordinal();
        }

        public Type getType() {
            return type;
        }
    }

}