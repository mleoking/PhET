/**
 * Class: TabDescriptor
 * Package: edu.colorado.phet.common.view.tabs
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.view.apparatuspanelcontainment;

import javax.swing.*;

public class TabDescriptor {
    String tabname;
    String tooltip;
    Icon icon;

    public TabDescriptor(String tabname) {
        this.tabname = tabname;
    }

    public TabDescriptor(String tabname, String tooltip) {
        this.tabname = tabname;
        this.tooltip = tooltip;
    }

    public TabDescriptor(String tabname, String tooltip, Icon icon) {
        this.tabname = tabname;
        this.tooltip = tooltip;
        this.icon = icon;
    }

    public String getName() {
        return tabname;
    }

    public String getTooltip() {
        return tooltip;
    }

    public Icon getIcon() {
        return icon;
    }
}
