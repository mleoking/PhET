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

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;

/**
 * This DesktopManager extends the standard DesktopManager in order to allow
 * rounded windows. It's job is to set dragMode to DEFAULT_DRAG_MODE.
 */

public class LiquidDesktopManager extends DefaultDesktopManager
{
    
    public void beginDraggingFrame(JComponent f)
    {
    }
    
    public void dragFrame(JComponent f, int newX, int newY)
    {
        
        setBoundsForFrame(f, newX, newY, f.getWidth(), f.getHeight());
    }
    
    public void endDraggingFrame(JComponent f)
    {
    }
    
    public void resizeFrame(JComponent f, int newX, int newY, int newWidth, int newHeight)
    {
        
        setBoundsForFrame(f, newX, newY, newWidth, newHeight);
    }
    
    public void endResizingFrame(JComponent f)
    {
    }
    
}
