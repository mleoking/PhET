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

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import com.birosoft.liquid.skin.Skin;

/**
 * The standard editor for a combo box
 */
public class LiquidComboBoxEditor extends BasicComboBoxEditor
{
    
    static Skin skin;
    
    public LiquidComboBoxEditor()
    {
        super();
        editor.setBorder( new AbstractBorder()
        {
            /**
             * @see javax.swing.border.Border#getBorderInsets(java.awt.Component)
             */
            public Insets getBorderInsets(Component c)
            {
                return new Insets(0, 3, 0, 3);
            }
            
            /**
             * @see javax.swing.border.Border#paintBorder(java.awt.Component, java.awt.Graphics, int, int, int, int)
             */
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
            {                
                //int index = c.isEnabled() ? 0 : 1;
            }
        });
    }
    
    /**
     * A subclass of BasicComboBoxEditor that implements UIResource.
     * BasicComboBoxEditor doesn't implement UIResource
     * directly so that applications can safely override the
     * cellRenderer property with BasicListCellRenderer subclasses.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases. The current serialization support is
     * appropriate for short term storage or RMI between applications running
     * the same version of Swing.  As of 1.4, support for long term storage
     * of all JavaBeans<sup><font size="-2">TM</font></sup>
     * has been added to the <code>java.beans</code> package.
     * Please see {@link java.beans.XMLEncoder}.
     */
    public static class UIResource extends LiquidComboBoxEditor implements javax.swing.plaf.UIResource
    {
    }
}