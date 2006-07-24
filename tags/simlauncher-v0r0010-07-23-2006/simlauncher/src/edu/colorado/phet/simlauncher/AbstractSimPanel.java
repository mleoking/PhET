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

/**
 * ISimPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class AbstractSimPanel extends JPanel implements Catalog.ChangeListener, SimContainer {
    public AbstractSimPanel( LayoutManager layout ) {
        super( layout );
    }

    protected abstract void updateSimTable();
}
