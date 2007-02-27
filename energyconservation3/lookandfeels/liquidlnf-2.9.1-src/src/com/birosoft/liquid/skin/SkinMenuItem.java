/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	Liquid Look and Feel                                                   *
*                                                                              *
*  Author, Miroslav Lazarevic                                                  *
*                                                                              *
*   For licensing information and credits, please refer to the                 *
*   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
*                                                                              *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid.skin;

import java.awt.Graphics;


/**
 * The only case the Skin currently can't handle is a skin for a JMenuItem.
 * @see com.stefankrause.xplookandfeel.skin.Skin
 */
public class SkinMenuItem extends SkinElement
{
    private int leftOffset, leftRolloverOffset, rightOffset, rightRolloverOffset;
    
    private int hsize, vsize;
    private int roundedSize;
    private boolean useDefaultButton = false;
    private boolean useSelectedButton = false;
    private boolean doneAllCalculations = false;
    
    public SkinMenuItem(String fileName, int leftOffset, int leftRolloverOffset, int rightOffset, int rightRolloverOffset, int roundedSize)
    {
        super(fileName,false);
        this.leftOffset = leftOffset;
        this.leftRolloverOffset = leftRolloverOffset;
        this.rightOffset = rightOffset;
        this.rightRolloverOffset = rightRolloverOffset;
        this.roundedSize = roundedSize;
        calculateSizes();
    }
    
    public void draw(Graphics g, boolean isEnabled, boolean isSelected, boolean isPushed, boolean isRollover, int pSizeX, int leftSize, int sizeY)
    {
        int offsetL = 0, offsetR = 0;
        
        if (isSelected)
        {
            offsetL = leftRolloverOffset;
            offsetR = rightRolloverOffset;
        } else
        {
            offsetL = leftOffset;
            offsetR = rightOffset;
        }
        
        offsetL = hsize * offsetL;
        offsetR = hsize * offsetR;
        
        // Left Side
        
        {
            int offset=offsetL;
            int sizeX=leftSize;
            if (roundedSize>0)
            {
                // lo
                g.drawImage(getImage(), 0, 0, roundedSize, roundedSize, offset + 0, 0, offset + roundedSize, roundedSize, null);
                
                // mo
                g.drawImage(getImage(), roundedSize, 0, sizeX - roundedSize, roundedSize, offset + roundedSize, 0, offset + hsize - roundedSize, roundedSize, null);
                
                // ro
                g.drawImage(getImage(), sizeX - roundedSize, 0, sizeX, roundedSize, offset + hsize - roundedSize, 0, offset + hsize, roundedSize, null);
                
                // lm
                g.drawImage(getImage(), 0, roundedSize, roundedSize, sizeY - roundedSize, offset + 0, roundedSize, offset + roundedSize, vsize - roundedSize, null);
                
                // rm
                g.drawImage(getImage(), sizeX - roundedSize, roundedSize, sizeX, sizeY - roundedSize, offset + hsize - roundedSize, roundedSize, offset + hsize, vsize - roundedSize, null);
                
                // lu
                g.drawImage(getImage(), 0, sizeY - roundedSize, roundedSize, sizeY, offset + 0, vsize - roundedSize, offset + roundedSize, vsize, null);
                
                // mu
                g.drawImage(getImage(), roundedSize, sizeY - roundedSize, sizeX - roundedSize, sizeY, offset + roundedSize, vsize - roundedSize, offset + hsize - roundedSize, vsize, null);
                
                // ru
                g.drawImage(getImage(), sizeX - roundedSize, sizeY - roundedSize, sizeX, sizeY, offset + hsize - roundedSize, vsize - roundedSize, offset + hsize, vsize, null);
            }
            g.drawImage(getImage(), roundedSize, roundedSize, sizeX - roundedSize, sizeY - roundedSize, offset + roundedSize, roundedSize, offset + hsize - roundedSize, vsize - roundedSize, null);
        }
        // Right Side
        
        {
            int offset=offsetR;
            int sizeX=pSizeX-leftSize;
            g.translate(leftSize,0);
            if (roundedSize>0)
            {
                // lo
                g.drawImage(getImage(), 0, 0, roundedSize, roundedSize, offset + 0, 0, offset + roundedSize, roundedSize, null);
                
                // mo
                g.drawImage(getImage(), roundedSize, 0, sizeX - roundedSize, roundedSize, offset + roundedSize, 0, offset + hsize - roundedSize, roundedSize, null);
                
                // ro
                g.drawImage(getImage(), sizeX - roundedSize, 0, sizeX, roundedSize, offset + hsize - roundedSize, 0, offset + hsize, roundedSize, null);
                
                // lm
                g.drawImage(getImage(), 0, roundedSize, roundedSize, sizeY - roundedSize, offset + 0, roundedSize, offset + roundedSize, vsize - roundedSize, null);
                
                // rm
                g.drawImage(getImage(), sizeX - roundedSize, roundedSize, sizeX, sizeY - roundedSize, offset + hsize - roundedSize, roundedSize, offset + hsize, vsize - roundedSize, null);
                
                // lu
                g.drawImage(getImage(), 0, sizeY - roundedSize, roundedSize, sizeY, offset + 0, vsize - roundedSize, offset + roundedSize, vsize, null);
                
                // mu
                g.drawImage(getImage(), roundedSize, sizeY - roundedSize, sizeX - roundedSize, sizeY, offset + roundedSize, vsize - roundedSize, offset + hsize - roundedSize, vsize, null);
                
                // ru
                g.drawImage(getImage(), sizeX - roundedSize, sizeY - roundedSize, sizeX, sizeY, offset + hsize - roundedSize, vsize - roundedSize, offset + hsize, vsize, null);
            }
            g.drawImage(getImage(), roundedSize, roundedSize, sizeX - roundedSize, sizeY - roundedSize, offset + roundedSize, roundedSize, offset + hsize - roundedSize, vsize - roundedSize, null);
            
            g.translate(-leftSize,0);
        }
    }
    
    public int getHsize()
    {
        if (!doneAllCalculations)
        {
            calculateSizes();
            doneAllCalculations=true;
        }
        return hsize;
    }
    
    public int getVsize()
    {
        if (!doneAllCalculations)
        {
            calculateSizes();
            doneAllCalculations=true;
        }
        return vsize;
    }
    
    protected void calculateSizes()
    {
        int maxOffset = 0;
        if (leftOffset > maxOffset)
            maxOffset = leftOffset;
        if (leftRolloverOffset > maxOffset)
            maxOffset = leftRolloverOffset;
        if (rightOffset > maxOffset)
            maxOffset = rightOffset;
        if (rightRolloverOffset > maxOffset)
            maxOffset = rightRolloverOffset;
        
        hsize = (getImage().getWidth(null)) / (maxOffset + 1);
        vsize = getImage().getHeight(null);
    }
}
