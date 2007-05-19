package edu.colorado.phet.cck.phetgraphics_cck.circuit.components;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Sep 27, 2004
 * Time: 1:45:26 PM
 */
public interface CCKMenu {

    JPopupMenu getMenuComponent();

    void delete();

    public boolean isVisiblityRequested();

    void setVisibilityRequested( boolean b );
}
