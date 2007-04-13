/**
 * Class: ModuleObserver
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: Jun 9, 2003
 */
package edu.colorado.phet.common.conductivity.application;


public interface ModuleObserver {
    public void moduleAdded( Module m );

    public void activeModuleChanged( Module m );
}
