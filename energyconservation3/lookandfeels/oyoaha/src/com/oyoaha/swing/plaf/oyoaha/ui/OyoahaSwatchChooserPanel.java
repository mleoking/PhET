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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.colorchooser.*;

import com.oyoaha.swing.plaf.oyoaha.*;

public class OyoahaSwatchChooserPanel extends AbstractColorChooserPanel
{
    protected OyoahaSwatchPanel swatchPanel;
    protected MouseListener mainSwatchListener;
    protected MouseListener recentSwatchListener;
    
    protected static OyoahaRecentSwatchPanel recentSwatchPanel;

    protected static String recentStr = UIManager.getString("ColorChooser.swatchesRecentText");

    public OyoahaSwatchChooserPanel()
    {
        super();
    }

    public String getDisplayName()
    {
        return UIManager.getString("ColorChooser.swatchesNameText");
    }

    public Icon getSmallDisplayIcon()
    {
        return null;
    }

    public Icon getLargeDisplayIcon()
    {
        return null;
    }

    public void installChooserPanel(JColorChooser enclosingChooser)
    {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser()
    {
        JPanel superHolder = new JPanel(new BorderLayout());

        swatchPanel =  new OyoahaMainSwatchPanel();
        swatchPanel.getAccessibleContext().setAccessibleName(getDisplayName());

        if (recentSwatchPanel==null)
        {
            recentSwatchPanel = new OyoahaRecentSwatchPanel();
            recentSwatchPanel.getAccessibleContext().setAccessibleName(recentStr);
        }
        
        mainSwatchListener = new MainSwatchListener();
        swatchPanel.addMouseListener(mainSwatchListener);
        recentSwatchListener = new RecentSwatchListener();
        recentSwatchPanel.addMouseListener(recentSwatchListener);

        Border border = new CompoundBorder(new LineBorder(Color.black), new LineBorder(OyoahaUtilities.getScheme().getGray()));
        swatchPanel.setBorder(border);
        superHolder.add(swatchPanel, BorderLayout.CENTER);

        JPanel recentLabelHolder = new JPanel(new BorderLayout());

        JLabel l = new JLabel(recentStr);
        l.setLabelFor(recentSwatchPanel);
        l.setBorder(BorderFactory.createEmptyBorder(10,0,5,0));
        recentLabelHolder.add(l, BorderLayout.NORTH);
        recentSwatchPanel.setBorder(border);
        recentLabelHolder.add(recentSwatchPanel, BorderLayout.CENTER);

        superHolder.add(recentLabelHolder, BorderLayout.SOUTH);
        add(superHolder);
    }

    public void uninstallChooserPanel(JColorChooser enclosingChooser)
    {
        super.uninstallChooserPanel(enclosingChooser);
        swatchPanel.removeMouseListener(mainSwatchListener);
        recentSwatchPanel.removeMouseListener(recentSwatchListener);
        swatchPanel = null;
        recentSwatchPanel = null;
        mainSwatchListener = null;
        recentSwatchListener = null;
        removeAll();
    }

    public void updateChooser()
    {

    }

    class RecentSwatchListener extends MouseAdapter implements Serializable
    {
        public void mousePressed(MouseEvent e)
        {
	        Color color = recentSwatchPanel.getColorForLocation(e.getX(), e.getY());
	        getColorSelectionModel().setSelectedColor(color);
	    }
    }

    class MainSwatchListener extends MouseAdapter implements Serializable
    {
        public void mousePressed(MouseEvent e)
        {
	        Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
	        getColorSelectionModel().setSelectedColor(color);
	        recentSwatchPanel.setMostRecentColor(color);

	    }
    }
}

class OyoahaSwatchPanel extends JPanel
{
    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;

    public OyoahaSwatchPanel()
    {
      initValues();
      initColors();
      setToolTipText("");
      setOpaque(true);
      setBackground(Color.white);
      setRequestFocusEnabled(false);
    }

    public boolean isFocusTraversable()
    {
        return false;
    }

    protected void initValues()
    {

    }

    public void paintComponent(Graphics g)
    {
      int w = numSwatches.width * (swatchSize.width + gap.width) -1;
      int h = numSwatches.height * (swatchSize.height + gap.height) -1;

      Dimension size = getSize();
      int xx = (size.width-w)/2;
      int yy = (size.height-h)/2;

      g.setColor(OyoahaUtilities.getScheme().getGray());
      g.fillRect(xx, yy, w, h);

      for (int row = 0; row < numSwatches.height; row++)
      {
        for (int column = 0; column < numSwatches.width; column++)
        {
          g.setColor( getColorForCell(column, row) );
          int x = column * (swatchSize.width + gap.width);
          int y = row * (swatchSize.height + gap.height);
          g.fillRect(xx+x, yy+y, swatchSize.width, swatchSize.height);
        }
      }
    }

    public Dimension getPreferredSize()
    {
      int x = numSwatches.width * (swatchSize.width + gap.width) -1;
      int y = numSwatches.height * (swatchSize.height + gap.height) -1;
      return new Dimension(x, y);
    }

    protected void initColors()
    {

    }

    public String getToolTipText(MouseEvent e)
    {
      Color color = getColorForLocation(e.getX(), e.getY());
      return color.getRed()+", "+ color.getGreen() + ", " + color.getBlue();
    }

    public Color getColorForLocation( int x, int y )
    {
      int column = x / (swatchSize.width + gap.width);
      int row = y / (swatchSize.height + gap.height);
      return getColorForCell(column, row);
    }

    private Color getColorForCell( int column, int row)
    {
      int index = (row * numSwatches.width) + column;

      if(index<colors.length)
      return colors[index];

      return colors[0];
    }
}

class OyoahaRecentSwatchPanel extends OyoahaSwatchPanel
{
    protected void initValues()
    {
        swatchSize = UIManager.getDimension("ColorChooser.swatchesRecentSwatchSize");
	    numSwatches = new Dimension(31, 2);
        gap = new Dimension(1, 1);
    }


    protected void initColors()
    {
        Color defaultRecentColor = UIManager.getColor("ColorChooser.swatchesDefaultRecentColor");
        int numColors = numSwatches.width * numSwatches.height;

	    colors = new Color[numColors];

	    for (int i = 0; i < numColors ; i++)
        {
	        colors[i] = defaultRecentColor;
	    }
    }

    public void setMostRecentColor(Color c)
    {
        System.arraycopy(colors, 0, colors, 1, colors.length-1);
        colors[0] = c;
        repaint();
    }
}

class OyoahaMainSwatchPanel extends OyoahaSwatchPanel
{
    protected void initValues()
    {
        swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize");
        numSwatches = new Dimension(31, 9);
        gap = new Dimension(1, 1);
    }

    protected void initColors()
    {
        int[] rawValues = initRawValues();
        int numColors = rawValues.length/3;

        colors = new Color[numColors];

        for (int i = 0; i < numColors ; i++)
        {
	        colors[i] = new Color( rawValues[(i*3)], rawValues[(i*3)+1], rawValues[(i*3)+2] );
        }
    }

    private int[] initRawValues()
    {
        int[] rawValues = 
        {     
255, 255, 255, // first row.
204, 255, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
204, 204, 255,
255, 204, 255,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 204, 204,
255, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
204, 255, 204,
224, 224, 224,  // second row.
153, 255, 255,
153, 204, 255,
153, 153, 255,
153, 153, 255,
153, 153, 255,
153, 153, 255,
153, 153, 255,
153, 153, 255,
153, 153, 255,
204, 153, 255,
255, 153, 255,
255, 153, 204,
255, 153, 153,
255, 153, 153,
255, 153, 153,
255, 153, 153,
255, 153, 153,
255, 153, 153,
255, 153, 153,
255, 204, 153,
255, 255, 153,
204, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 153,
153, 255, 204,
204, 204, 204,  // third row
102, 255, 255,
102, 204, 255,
102, 153, 255,
102, 102, 255,
102, 102, 255,
102, 102, 255,
102, 102, 255,
102, 102, 255,
153, 102, 255,
204, 102, 255,
255, 102, 255,
255, 102, 204,
255, 102, 153,
255, 102, 102,
255, 102, 102,
255, 102, 102,
255, 102, 102,
255, 102, 102,
255, 153, 102,
255, 204, 102,
255, 255, 102,
204, 255, 102,
153, 255, 102,
102, 255, 102,
102, 255, 102,
102, 255, 102,
102, 255, 102,
102, 255, 102,
102, 255, 153,
102, 255, 204,
173, 173, 173, // fourth row
51, 255, 255,
51, 204, 255,
51, 153, 255,
51, 102, 255,
51, 51, 255,
51, 51, 255,
51, 51, 255,
102, 51, 255,
153, 51, 255,
204, 51, 255,
255, 51, 255,
255, 51, 204,
255, 51, 153,
255, 51, 102,
255, 51, 51,
255, 51, 51,
255, 51, 51,
255, 102, 51,
255, 153, 51,
255, 204, 51,
255, 255, 51,
204, 255, 51,
153, 244, 51,
102, 255, 51,
51, 255, 51,
51, 255, 51,
51, 255, 51,
51, 255, 102,
51, 255, 153,
51, 255, 204,
153, 153, 153, // Fifth row
0, 255, 255,
0, 204, 255,
0, 153, 255,
0, 102, 255,
0, 51, 255,
0, 0, 255,
51, 0, 255,
102, 0, 255,
153, 0, 255,
204, 0, 255,
255, 0, 255,
255, 0, 204,
255, 0, 153,
255, 0, 102,
255, 0, 51,
255, 0 , 0,
255, 51, 0,
255, 102, 0,
255, 153, 0,
255, 204, 0,
255, 255, 0,
204, 255, 0,
153, 255, 0,
102, 255, 0,
51, 255, 0,
0, 255, 0,
0, 255, 51,
0, 255, 102,
0, 255, 153,
0, 255, 204,
122, 122, 122, // sixth row
0, 204, 204,
0, 204, 204,
0, 153, 204,
0, 102, 204,
0, 51, 204,
0, 0, 204,
51, 0, 204,
102, 0, 204,
153, 0, 204,
204, 0, 204,
204, 0, 204,
204, 0, 204,
204, 0, 153,
204, 0, 102,
204, 0, 51,
204, 0, 0,
204, 51, 0,
204, 102, 0,
204, 153, 0,
204, 204, 0,
204, 204, 0,
204, 204, 0,
153, 204, 0,
102, 204, 0,
51, 204, 0,
0, 204, 0,
0, 204, 51,
0, 204, 102,
0, 204, 153,
0, 204, 204, 
102, 102, 102, // seventh row
0, 153, 153,
0, 153, 153,
0, 153, 153,
0, 102, 153,
0, 51, 153,
0, 0, 153,
51, 0, 153,
102, 0, 153,
153, 0, 153,
153, 0, 153,
153, 0, 153,
153, 0, 153,
153, 0, 153,
153, 0, 102,
153, 0, 51,
153, 0, 0,
153, 51, 0,
153, 102, 0,
153, 153, 0,
153, 153, 0,
153, 153, 0,
153, 153, 0,
153, 153, 0,
102, 153, 0,
51, 153, 0,
0, 153, 0,
0, 153, 51,
0, 153, 102,
0, 153, 153,
0, 153, 153,
71, 71, 71, // eigth row
0, 102, 102,
0, 102, 102,
0, 102, 102,
0, 102, 102,
0, 51, 102,
0, 0, 102,
51, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 102,
102, 0, 51,
102, 0, 0,
102, 51, 0,
102, 102, 0,
102, 102, 0,
102, 102, 0,
102, 102, 0,
102, 102, 0,
102, 102, 0,
102, 102, 0,
51, 102, 0,
0, 102, 0,
0, 102, 51,
0, 102, 102,
0, 102, 102,
0, 102, 102,
0, 0, 0, // ninth row
0, 51, 51,
0, 51, 51,
0, 51, 51,
0, 51, 51,
0, 51, 51,
0, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 51,
51, 0, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
51, 51, 0,
0, 51, 0,
0, 51, 51,
0, 51, 51,
0, 51, 51,
0, 51, 51,
51, 51, 51 
};
	return rawValues;
    
    }
}