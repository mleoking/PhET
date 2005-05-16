/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests;

import javax.swing.*;

/**
 * ITestControlPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public abstract class ITestContentPanel extends JPanel {
    abstract public void setControlPanel( JComponent panel );
}
