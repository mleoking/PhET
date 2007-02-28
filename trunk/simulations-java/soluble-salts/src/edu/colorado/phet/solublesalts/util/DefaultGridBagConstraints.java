/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.util;

import java.awt.*;

/**
 * DefaultGridBagConstraints
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DefaultGridBagConstraints extends GridBagConstraints {

    public DefaultGridBagConstraints() {
        super( 0, 0,
               1, 1,
               0, 0,
               GridBagConstraints.NORTHWEST,
               GridBagConstraints.NONE,
               new Insets( 0, 0, 0, 0 ),
               0, 0 );
    }
}
