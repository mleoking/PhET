// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;

/**
 * @author Sam Reid
 */
public interface SimsharingApplication {
    public PhetFrame getPhetFrame();

    public void setTeacherMode( boolean b );

    public void setExitStrategy( VoidFunction0 exitStrategy );
}
