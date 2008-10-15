/*, 2003.*/
package edu.colorado.phet.common_microwaves.view;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:38:22 AM
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
