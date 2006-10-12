/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.molecularreactions.util.Resetable;

import javax.swing.*;
import java.awt.*;

/**
 * MRControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class MRControlPanel extends JPanel implements Resetable {

    public MRControlPanel( LayoutManager layout, boolean isDoubleBuffered ) {
        super( layout, isDoubleBuffered );
    }

    public MRControlPanel( LayoutManager layout ) {
        super( layout );
    }

    public MRControlPanel( boolean isDoubleBuffered ) {
        super( isDoubleBuffered );
    }

    public MRControlPanel() {
        super();
    }
}
