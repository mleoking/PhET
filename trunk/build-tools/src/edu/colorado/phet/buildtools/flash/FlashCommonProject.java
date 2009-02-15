package edu.colorado.phet.buildtools.flash;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.buildtools.util.SVNDependencyProject;

/**
 * Created by IntelliJ IDEA.
 * User: jon
 * Date: Feb 12, 2009
 * Time: 3:10:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class FlashCommonProject extends SVNDependencyProject {
    public FlashCommonProject( File projectRoot ) throws IOException {
        super( projectRoot );
    }
}
