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

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuBarUI;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.util.Colors;

/**
 * This class represents the UI delegate for the JMenuBar component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidMenuBarUI extends BasicMenuBarUI
{
    
    static Skin skin;
    /**
     * Creates the UI delegate for the given component.
     * Because in normal application there is usually only one menu bar, the UI
     * delegate isn't cached here.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(final JComponent c)
    {
        c.setBorder(new EmptyBorder(0,5,2,0 ));
        return new LiquidMenuBarUI();
    }
    
    /**
     * Paints the given component.
     *
     * @param g The graphics context to use.
     * @param c The component to paint.
     */
    public void paint(Graphics g, JComponent c)
    {
        int width=c.getWidth();
        int height=c.getHeight();
        getSkin().draw(g,2,width,height-2);
        if (LiquidLookAndFeel.areStipplesUsed()) {
            Colors.drawStipples(g, c, c.getBackground());
        }
    }
    
    public Skin getSkin()
    {
        if (skin == null)
        {
            skin =new Skin("menu_top.png", 3, 0);
        }
        
        return skin;
    }    
}