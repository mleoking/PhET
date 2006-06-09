/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * SimulationList
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimulationList extends JList {

    public SimulationList( final List simulations ) {
        super( getSimEntries( simulations ) );
    }

    private static Object[] getSimEntries( List simulations ) {
        JPanel[] entries = new JPanel[simulations.size()];

        for( int i = 0; i < simulations.size(); i++ ) {
            Simulation simulation = (Simulation)simulations.get( i );
            JPanel panel = new JPanel( new GridLayout( 1, 2 ) );
            panel.add( new JLabel( simulation.getName() ) );
            panel.add( new JLabel( simulation.getThumbnail() ) );
            entries[i] = panel;
        }
        return entries;
    }

    private static Object[] getSimEntries( Object[] simulations ) {
        JPanel[] entries = new JPanel[simulations.length];

        for( int i = 0; i < simulations.length; i++ ) {
            Simulation simulation = (Simulation)simulations[i];
            JPanel panel = new JPanel( new GridLayout( 1, 2 ) );
            panel.add( new JLabel( simulation.getName() ) );
            panel.add( new JLabel( simulation.getThumbnail() ) );
            entries[i] = panel;
        }
        return entries;
    }

    public void setListData( final Object[] listData ) {
        this.removeAll();
        super.setListData( getSimEntries( listData ) );
    }
}
