/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 27, 2004
 * Time: 1:45:26 PM
 * Copyright (c) Sep 27, 2004 by Sam Reid
 */
public interface CCKMenu {

    JPopupMenu getMenuComponent();

    void delete();

    public boolean isVisiblityRequested();

    void setVisibilityRequested( boolean b );
}
