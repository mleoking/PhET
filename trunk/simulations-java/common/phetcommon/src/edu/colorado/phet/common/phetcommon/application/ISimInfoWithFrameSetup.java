package edu.colorado.phet.common.phetcommon.application;

import javax.security.auth.spi.LoginModule;

import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 16, 2008
 * Time: 11:26:32 AM
 */
public interface ISimInfoWithFrameSetup extends ISimInfo{
    FrameSetup getFrameSetup();
}
