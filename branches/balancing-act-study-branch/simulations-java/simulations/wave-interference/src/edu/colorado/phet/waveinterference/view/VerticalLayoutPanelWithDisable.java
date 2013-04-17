// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WISwingUtil;

/**
 * User: Sam Reid
 * Date: May 19, 2006
 * Time: 1:26:06 AM
 */

public class VerticalLayoutPanelWithDisable extends VerticalLayoutPanel {
    private boolean enabledFlag = true;

    public boolean getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabled( boolean enabled ) {
        this.enabledFlag = enabled;
        super.setEnabled( enabled );
        WISwingUtil.setChildrenEnabled( this, enabled );
    }

}
