// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.solublesalts.util;

import java.awt.*;

/**
 * DefaultGridBagConstraints
 *
 * @author Ron LeMaster
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
