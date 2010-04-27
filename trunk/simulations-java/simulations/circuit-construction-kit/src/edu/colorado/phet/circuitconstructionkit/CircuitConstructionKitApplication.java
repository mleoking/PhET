package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.circuitconstructionkit.controls.OptionsMenu;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.PhetFrameWorkaround;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Circuit Construction Kit Application
 *
 * @author Sam Reid
 */
public class CircuitConstructionKitApplication extends PiccoloPhetApplication {
    private CCKModule cckPiccoloModule;

    public CircuitConstructionKitApplication(PhetApplicationConfig config, boolean ac, boolean virtualLab) {
        super(config);

        cckPiccoloModule = new CCKModule(config.getCommandLineArgs(), ac, virtualLab);
        Module[] modules = new Module[]{cckPiccoloModule};
        setModules(modules);
        if (getPhetFrame().getTabbedModulePane() != null) {
            getPhetFrame().getTabbedModulePane().setLogoVisible(false);
        }
        getPhetFrame().addMenu(new OptionsMenu(this, cckPiccoloModule));//todo options menu
    }

    protected PhetFrame createPhetFrame() {
        return new PhetFrameWorkaround(this);
    }

    public void startApplication() {
        super.startApplication();
        cckPiccoloModule.applicationStarted();
    }
}