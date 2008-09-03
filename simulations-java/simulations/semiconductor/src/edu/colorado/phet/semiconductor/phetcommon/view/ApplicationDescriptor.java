/*, 2003.*/
package edu.colorado.phet.semiconductor.phetcommon.view;

import edu.colorado.phet.semiconductor.phetcommon.view.util.framesetup.FrameSetup;

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
    String name;

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

    public void setName( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
