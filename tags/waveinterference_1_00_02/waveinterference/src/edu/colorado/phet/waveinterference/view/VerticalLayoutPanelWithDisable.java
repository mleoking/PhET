/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WISwingUtil;

/**
 * User: Sam Reid
 * Date: May 19, 2006
 * Time: 1:26:06 AM
 * Copyright (c) May 19, 2006 by Sam Reid
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
