package edu.colorado.phet.website.panels.lists;

import java.io.Serializable;
import java.util.Locale;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.data.Simulation;

/**
 * Used to display a simulation in a list where simulations can be added or removed. Sortable by localized title
 */
public class SimOrderItem implements SortableListItem, Serializable {
    private Simulation simulation;
    private String title;

    /**
     * @param simulation The simulation to represent
     * @param title      The localized title to use
     */
    public SimOrderItem( Simulation simulation, String title ) {
        this.simulation = simulation;
        this.title = title;
    }

    public String getDisplayValue() {
        return title;
    }

    public Component getDisplayComponent( String id ) {
        return new Label( id, title );
    }

    public int getId() {
        return simulation.getId();
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public int compareTo( SortableListItem item, Locale locale ) {
        return getDisplayValue().compareToIgnoreCase( item.getDisplayValue() );
    }
}
