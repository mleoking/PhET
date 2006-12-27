/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:38:22 AM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class ApplicationDescriptor {
    String windowTitle;
    String description;
    String version;
    FrameSetup frameSetup;

    public ApplicationDescriptor( String windowTitle, String description, String version, FrameSetup frameSetup ) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public FrameSetup getFrameSetup() {
        return frameSetup;
    }
}
