/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class LiquidSplitPaneUI extends BasicSplitPaneUI
{
    /**
     * Creates a new BasicSplitPaneUI instance
     */
    public static ComponentUI createUI(JComponent x)
    {
        return new LiquidSplitPaneUI();
    }
    
    /**
     * Creates the default divider.
     */
    public BasicSplitPaneDivider createDefaultDivider()
    {
        return new LiquidSplitPaneDivider(this);
    }
}