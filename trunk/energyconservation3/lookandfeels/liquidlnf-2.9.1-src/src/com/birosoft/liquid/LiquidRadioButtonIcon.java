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

import com.birosoft.liquid.skin.Skin;

/*
 * This class extends LiquidCheckBoxIcon and is responsible for painting
 * a radio button.
 */
class LiquidRadioButtonIcon extends LiquidCheckBoxIcon
{
    static Skin skin;
    public Skin getSkin()
    {
        if (skin==null)
            skin=new Skin("radiobutton.png", 8, 0);
        return skin;
    }   
}

