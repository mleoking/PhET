/**
 * Class: ApparatusPanelContainer
 * Package: edu.colorado.phet.common.view.tabs
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.apparatuspanelcontainment;

import javax.swing.*;

import edu.colorado.phet.semiconductor.phetcommon.application.ModuleObserver;

public interface ApparatusPanelContainer extends ModuleObserver {
    JComponent getComponent();
}
