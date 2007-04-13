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

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;

import com.birosoft.liquid.LiquidButtonUI;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;

/**
 * A button placed in the title frame of a internal frame to enable
 * closing, iconifying and maximizing of the internal frame.
 */
public class LiquidSpinnerButtonUI extends LiquidButtonUI
{
    
    int type;
    
    /** The only instance for this UI */
//    private static final String[] files =
//    {"spinnerarrowborder.png","spinnerarrowborder.png"};
    private static final String[] arrowfiles =
    {"spinneruparrows.png","spinnerdownarrows.png"};
    /** the index model for the window buttons */
    private static SkinSimpleButtonIndexModel indexModel=new SkinSimpleButtonIndexModel(0,1,2,3);
    
    static Skin skin = null;//new Skin[2];
    static Skin arrowSkins[]=new Skin[2];
    
    public static ComponentUI createUI(JComponent c)
    {
        throw new IllegalStateException("Must not be used this way.");
    }
    
    /**
     * Creates a new Spinner Button. Use either SwingConstants.SOUTH or SwingConstants.NORTH
     * for a SpinnerButton of Type up or a down.
     * @param type
     */
    LiquidSpinnerButtonUI(int type)
    {
        this.type=type;
    }
    
    public void paint(Graphics g, JComponent c)
    {
        
        AbstractButton button = (AbstractButton) c;
        
        indexModel.setButton(button);
        int index=indexModel.getIndexForState();
        // Paint the spinner button
        getSkin().draw(g, index,  button.getWidth(),  button.getHeight());
        getArrowSkin(type).drawCentered(g, index, button.getWidth(), button.getHeight());
        
    }
    
    /**
     * returns the spinner button
     * @param type
     * @return Skin
     */
    public static Skin getSkin()
    {
        if( skin == null) 
        {
            skin = new Skin("spinnerarrowborder.png", 4, 2);
        }
        
        return skin;
    }
    
    /**
     * returns the spinner button's arrow
     * @param type
     * @return Skin
     */
    protected Skin getArrowSkin(int type)
    {
        switch (type)
        {
            case SwingConstants.NORTH:
                arrowSkins[0]=new Skin(arrowfiles[0], 4, 2);
                return arrowSkins[0];
            case SwingConstants.SOUTH:
                arrowSkins[1]=new Skin(arrowfiles[1], 4, 2);
                return arrowSkins[1];
            default:
                throw new IllegalStateException("type must be either SwingConstants.SOUTH or SwingConstants.NORTH for XPSpinnerButton");
        }
    }
    
    /**
     * @see javax.swing.plaf.basic.BasicButtonUI#getPreferredSize(javax.swing.JComponent)
     */
    public Dimension getPreferredSize(JComponent c)
    {
        return new Dimension( getSkin().getHsize(), getSkin().getVsize() );
    }
    
    
}
