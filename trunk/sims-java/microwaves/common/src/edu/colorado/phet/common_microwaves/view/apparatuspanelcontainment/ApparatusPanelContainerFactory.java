/**
 * Class: ApparatusPanelContainerFactory
 * Package: edu.colorado.phet.common.examples.hellophet.application
 * Author: Another Guy
 * Date: Jun 11, 2003
 */
package edu.colorado.phet.common_microwaves.view.apparatuspanelcontainment;

import edu.colorado.phet.common_microwaves.application.ModuleManager;

public interface ApparatusPanelContainerFactory {
    ApparatusPanelContainer createApparatusPanelContainer(ModuleManager manager);
}
