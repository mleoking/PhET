/**
 * Class: HelpItemManager
 * Package: edu.colorado.phet.common.view.help
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.common.view.help;

import java.util.ArrayList;

public class HelpItemManager {

    private ArrayList helpItems = new ArrayList();
    private boolean helpVisible;

    /**
     * Adds an on-screen help item to the PhetFrame. This is typically called
     * in the activate() method of an application-specific subclass of
     * ApparatusPanel.
     * @see HelpItem
     * @param helpText
     */
    public void addHelpItem(HelpItem helpText) {
//        this.getLayeredPane().add( helpText, JLayeredPane.POPUP_LAYER );
        helpItems.add(helpText);
    }

    public void setHelpVisible(boolean isVisible) {
        helpVisible = isVisible;
        for (int i = 0; i < helpItems.size(); i++) {
            HelpItem helpItem = (HelpItem) helpItems.get(i);
            helpItem.setVisible(isVisible);
        }
    }

    /**
     * Removes all OnScreenHelpItems from the PhetFrame
     */
    public void clearHelp() {
        setHelpVisible(false);
        helpItems.clear();
        setHelpVisible(helpVisible);
    }

    /**
     * Displays all on-screen help
     */
    public void showHelp() {
        this.setHelpVisible(true);
    }

    /**
     * Hides all on-screen help
     */
    public void hideHelp() {
        this.setHelpVisible(false);
    }
}
