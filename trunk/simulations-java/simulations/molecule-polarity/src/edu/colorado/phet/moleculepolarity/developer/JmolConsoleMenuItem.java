// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.developer;

import java.awt.Container;
import java.awt.Frame;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.menu.DialogCheckBoxMenuItem;

/**
 * Menu item that opens a Jmol Console in a dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolConsoleMenuItem extends DialogCheckBoxMenuItem {

    public JmolConsoleMenuItem( final Frame owner, final JmolViewer viewer ) {
        super( "Jmol Console...", "Jmol Console", owner, new Function0<Container>() {
            public Container apply() {
                return new JmolConsole( viewer );
            }
        } );
    }
}
