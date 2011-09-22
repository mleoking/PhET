// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.ModulePanel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.TabbedModulePanePiccolo;

/**
 * The JME version of the PhET application. Used to override the module-panel handling, so that we
 * never hide the JME3 version.
 * <p/>
 * NOT to be confused with the PhetJMEApplication, which is the JME-application variant for PhET. Yeah.
 * <p/>
 * TODO: make it so that we only override the behavior when switching from the JME3 canvas to itself?
 */
public class JMEPhetApplication extends PiccoloPhetApplication {
    public JMEPhetApplication( PhetApplicationConfig config ) {
        this( config, new TabbedModulePanePiccolo() {
            private ModulePanel panel = null;

            // don't add anything after the initial module panel
            @Override public void add( Component comp, Object constraints ) {
                if ( panel == null || !( comp instanceof ModulePanel ) ) {
                    super.add( comp, constraints );
                    if ( comp instanceof ModulePanel ) {
                        panel = (ModulePanel) comp;
                    }
                }
            }

            @Override public void remove( Component comp ) {
                // don't allow removal of our primary module panel
            }
        } );
    }

    public JMEPhetApplication( PhetApplicationConfig config, TabbedModulePanePiccolo tabbedModulePane ) {
        super( config, tabbedModulePane );
    }
}
