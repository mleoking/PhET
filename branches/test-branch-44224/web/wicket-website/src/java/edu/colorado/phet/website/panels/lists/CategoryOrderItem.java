package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.data.Category;

/**
 * Used to display a category in a list where categories can be added or removed. Sortable by localized title
 */
public class CategoryOrderItem implements SortableListItem, Serializable {
    private Category category;
    private String title;

    /**
     * @param category The category to represent
     * @param title    The localized title to use
     */
    public CategoryOrderItem( Category category, String title ) {
        this.category = category;
        this.title = title;
    }

    public String getDisplayValue() {
        return title;
    }

    public Component getDisplayComponent( String id ) {
        return new Label( id, title );
    }

    public int getId() {
        return category.getId();
    }

    public Category getCategory() {
        return category;
    }

    public int compareTo( SortableListItem item, Locale locale ) {
        return getDisplayValue().compareToIgnoreCase( item.getDisplayValue() );
    }
}