/**
 * Class: ApparatusPanelContainer
 * Package: edu.colorado.phet.common.view.tabs
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.common_microwaves.view.apparatuspanelcontainment;

import edu.colorado.phet.common_microwaves.application.ModuleObserver;

import javax.swing.*;

public interface ApparatusPanelContainer extends ModuleObserver {
    JComponent getComponent();
}
