/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Options;
import edu.colorado.phet.simlauncher.SimulationTable;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * MostRecentlyUsedSortAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SetInstalledSimulatonSortAction extends AbstractAction {
    private SimulationTable.SimulationComparator sortType;

    public SetInstalledSimulatonSortAction( SimulationTable.SimulationComparator sortType ) {
        this.sortType = sortType;
    }

    public void actionPerformed( ActionEvent e ) {
        Options.instance().setInstalledSimulationsSortType( sortType );
    }
}
