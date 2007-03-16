/* ====================================================================
 * Copyright (c) 2001-2003 OYOAHA. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The names "OYOAHA" must not be used to endorse or promote products 
 *    derived from this software without prior written permission. 
 *    For written permission, please contact email@oyoaha.com.
 *
 * 3. Products derived from this software may not be called "OYOAHA",
 *    nor may "OYOAHA" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL OYOAHA OR ITS CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oyoaha.swing.plaf.oyoaha.ui;

import javax.swing.*;
import javax.swing.plaf.*;
import java.awt.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaColorPreviewPanel extends JPanel
{
    protected int squareSize = 25;
    protected int squareGap = 5;
    protected int innerGap = 5;

    protected int textGap = 5;
    protected Font font = new Font("Dialog", Font.PLAIN, 12);
    protected String sampleText = UIManager.getString("ColorChooser.sampleText");

    protected int swatchWidth = 50;
    protected Color oldColor = null;

    public Dimension getPreferredSize()
    {
	FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(getFont());

        if(fm==null)
        {
          return new Dimension( 200,80 );
        }

        if(sampleText==null)
        {
          sampleText = UIManager.getString("ColorChooser.sampleText");

          if(sampleText==null)
          {
            sampleText = "Sample Text";
          }
        }

	int ascent = fm.getAscent();
	int height = fm.getHeight();
	int width = fm.stringWidth(sampleText);

        int y = height*3 + textGap*3;
	int x = squareSize * 3 + squareGap*4 + swatchWidth + width;
        return new Dimension( x,y );
    }

    public void paintComponent(Graphics g)
    {
        if (oldColor == null)
	oldColor = getForeground();

        OyoahaBackgroundObject o = OyoahaUtilities.getBackgroundObject("Panel");
        Color color = this.getBackground();

        if(o!=null && color instanceof UIResource)
        {
          o.paintBackground(g, this, 0, 0, getWidth(), getHeight(), OyoahaUtilities.UNSELECTED_ENABLED);
        }
        else
        {
          OyoahaUtilities.paintColorBackground(g, this, 0, 0, getWidth(), getHeight(), color, OyoahaUtilities.UNSELECTED_ENABLED);
        }

        int squareWidth = paintSquares(g);
	int textWidth = paintText(g, squareWidth);

        paintSwatch(g, squareWidth + textWidth);
    }

    protected void paintSwatch(Graphics g, int offsetX)
    {
        int swatchX = offsetX + squareGap;
	g.setColor(oldColor);
	g.fillRect(swatchX, 0, swatchWidth, (squareSize) + (squareGap/2));
	g.setColor(getForeground());
	g.fillRect(swatchX, (squareSize) + (squareGap/2), swatchWidth, (squareSize) + (squareGap/2) );
    }

    protected int paintText(Graphics g, int offsetX)
    {
	g.setFont(getFont());
	FontMetrics fm = g.getFontMetrics();

	int ascent = fm.getAscent();
	int height = fm.getHeight();
	int width = fm.stringWidth(sampleText);

	int textXOffset = offsetX + textGap;

        Color color = getForeground();

	g.setColor(color);

	g.drawString(sampleText, textXOffset, ascent+2);

	g.fillRect(textXOffset,
		   ( height) + textGap,
		   width + (textGap),
		   height +2);

	g.setColor(Color.black);
	g.drawString(sampleText,
		     textXOffset+(textGap/2),
		     height+ascent+textGap+2);


	g.setColor(Color.white);

	g.fillRect(textXOffset,
		   ( height + textGap) * 2,
		   width + (textGap),
		   height +2);

	g.setColor(color);
	g.drawString(sampleText,
		     textXOffset+(textGap/2),
		     ((height+textGap) * 2)+ascent+2);

	return width + textGap + 4;
    }

    protected int paintSquares(Graphics g)
    {
        Color color = getForeground();

        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap,
		   innerGap,
		   squareSize - (innerGap*2),
		   squareSize - (innerGap*2));
	g.setColor(Color.white);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));

        g.setColor(color);
	g.fillRect(0,squareSize+squareGap,squareSize,squareSize);

	g.translate(squareSize+squareGap, 0);
        g.setColor(Color.black);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap,
		   innerGap,
		   squareSize - (innerGap*2),
		   squareSize - (innerGap*2));
	g.setColor(Color.white);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));
	g.translate(-(squareSize+squareGap), 0);

	g.translate(squareSize+squareGap, squareSize+squareGap);
        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap,
		   innerGap,
		   squareSize - (innerGap*2),
		   squareSize - (innerGap*2));
	g.translate(-(squareSize+squareGap), -(squareSize+squareGap));

	g.translate((squareSize+squareGap)*2, 0);
        g.setColor(Color.white);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap,
		   innerGap,
		   squareSize - (innerGap*2),
		   squareSize - (innerGap*2));
	g.setColor(Color.black);
	g.fillRect(innerGap*2,
		   innerGap*2,
		   squareSize - (innerGap*4),
		   squareSize - (innerGap*4));
	g.translate(-((squareSize+squareGap)*2), 0);

	g.translate((squareSize+squareGap)*2, (squareSize+squareGap));
        g.setColor(Color.black);
	g.fillRect(0,0,squareSize,squareSize);
	g.setColor(color);
	g.fillRect(innerGap,
		   innerGap,
		   squareSize - (innerGap*2),
		   squareSize - (innerGap*2));
	g.translate(-((squareSize+squareGap)*2), -(squareSize+squareGap));

	return ((squareSize+squareGap) *3);
    }
}