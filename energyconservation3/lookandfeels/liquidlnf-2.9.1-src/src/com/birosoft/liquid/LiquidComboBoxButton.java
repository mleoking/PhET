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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

import javax.swing.ButtonModel;
import javax.swing.CellRendererPane;
import javax.swing.DefaultButtonModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;

public class LiquidComboBoxButton extends JButton
{
    protected JComboBox comboBox;
    protected JList listBox;
    protected CellRendererPane rendererPane;
    protected Icon comboIcon;
    protected boolean iconOnly = false;
    BufferedImage focusImg;
    
    public final JComboBox getComboBox()
    {
        return comboBox;
    }
    public final void setComboBox(JComboBox cb)
    {
        comboBox = cb;
    }
    
    public final Icon getComboIcon()
    {
        return comboIcon;
    }
    public final void setComboIcon(Icon i)
    {
        comboIcon = i;
    }
    
    public final boolean isIconOnly()
    {
        return iconOnly;
    }
    public final void setIconOnly(boolean isIconOnly)
    {
        iconOnly = isIconOnly;
    }
    
    LiquidComboBoxButton()
    {
        super("");
        DefaultButtonModel model = new DefaultButtonModel()
        {
            public void setArmed(boolean armed)
            {
                super.setArmed(isPressed() ? true : armed);
            }
        };
        
        setModel(model);
        setOpaque(false);
        
        // Set the background and foreground to the combobox colors.
        setBackground(UIManager.getColor("ComboBox.background"));
        setForeground(UIManager.getColor("ComboBox.foreground"));
        
        ImageIcon icon = LiquidLookAndFeel.loadIcon("comboboxfocus.png",this);
        focusImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        Graphics g3 = focusImg.getGraphics();
        icon.paintIcon(this, g3, 0, 0);
    }
    
    public LiquidComboBoxButton(JComboBox cb, Icon i, CellRendererPane pane, JList list)
    {
        this();
        comboBox = cb;
        comboIcon = i;
        rendererPane = pane;
        listBox = list;
        setEnabled(comboBox.isEnabled());
    }
    
    public LiquidComboBoxButton(JComboBox cb, Icon i, boolean onlyIcon, CellRendererPane pane, JList list)
    {
        this(cb, i, pane, list);
        iconOnly = onlyIcon;
    }
    
    SkinSimpleButtonIndexModel indexModel = new SkinSimpleButtonIndexModel();
    Skin skinArrow;
    Skin skinButton;

    public int getIndexForState()
    {
        return indexModel.getIndexForState(model.isEnabled(),model.isRollover(),model.isArmed() && model.isPressed() | model.isSelected());
    }
        
    /**
     * Mostly taken from the swing sources
     * @see javax.swing.JComponent#paintComponent(Graphics)
     * NOTE:  Moved code for painting the combobox skin to the LiquidComboBoxUI
     * class so that child components, such as the editor, wouldn't be abscured.
     */
    public void paintComponent(Graphics g)
    {
        boolean leftToRight = getComponentOrientation().isLeftToRight();
        
        int index = indexModel.getIndexForState(model.isEnabled(),model.isRollover(),model.isArmed() && model.isPressed() | model.isSelected());

// 20060213 MEV - Correction to highlight when has focus        
        index = (comboBox.hasFocus() && !iconOnly ? 1 : index);

// Paint the Button
        Skin arrowSkin = getSkinArrow();
// RtoL stuff        int arrowLoc = leftToRight ? getWidth() - arrowSkin.getHsize()-6 : 6;
        int middle = (getHeight() - arrowSkin.getVsize()) / 2;            
        arrowSkin.draw(g, index, getWidth() - arrowSkin.getHsize()-6, middle,   arrowSkin.getHsize(), arrowSkin.getVsize());
// RtoL stuff        arrowSkin.draw(g, index, arrowLoc, middle,   arrowSkin.getHsize(), arrowSkin.getVsize());
        
        
        Insets insets = new Insets(0, 12, 2, 2);
        
        int width = getWidth() - (insets.left + insets.right);
        int widthFocus = width; //- (skin.getHsize()-skin.getOffset());
        int height = getHeight() - (insets.top + insets.bottom);
        
        if (height <= 0 || width <= 0)
        {
            return;
        }
        
        int left = insets.left;
        int top = insets.top;
        int right = left + (width - 1);
        int bottom = top + (height - 1);
        
        int iconWidth = LiquidComboBoxUI.comboBoxButtonSize;
        int iconLeft = (leftToRight) ? right : left;
        
        // Let the renderer paint
        Component c = null;
        boolean mustResetOpaque = false;
        boolean savedOpaque = false;
        boolean paintFocus = comboBox.hasFocus();
        if (!iconOnly && comboBox != null)
        {
            ListCellRenderer renderer = comboBox.getRenderer();
            boolean renderPressed = getModel().isPressed();
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, renderPressed, false);
            c.setFont(rendererPane.getFont());
            
            if (model.isArmed() && model.isPressed())
            {                
                if (isOpaque())
                {
                    c.setBackground(UIManager.getColor("Button.select"));
                }
                c.setForeground(comboBox.getForeground());
            } else if (!comboBox.isEnabled())
            {
                if (isOpaque())
                {
                    c.setBackground(UIManager.getColor("ComboBox.disabledBackground"));
                }
                c.setForeground(UIManager.getColor("ComboBox.disabledForeground"));
            } else
            {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());
            }
            if (!mustResetOpaque && c instanceof JComponent)
            {
                mustResetOpaque = true;
                JComponent jc = (JComponent) c;
                savedOpaque = jc.isOpaque();
                jc.setOpaque(false);
            }
            
            int cWidth = width - (insets.right + iconWidth);
            
            // Fix for 4238829: should lay out the JPanel.
            boolean shouldValidate = false;
            if (c instanceof JPanel)
            {
                shouldValidate = true;
            }
            
            if (leftToRight)
            {
                rendererPane.paintComponent(g, c, this, left, top, cWidth, height, shouldValidate);
            } else
            {
                rendererPane.paintComponent(g, c, this, left + iconWidth, top, cWidth, height, shouldValidate);
            }
            if (paintFocus)
            {
/* 20060213 MEV - Removed old focus painting code               
                g.setColor(Color.black);
                Graphics2D g2d = (Graphics2D) g;
                //BasicStroke focusStroke=new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f, new float[] {1.0f, 1.0f}, 1.0f);
                Rectangle r = new Rectangle(left, top, 2, 2);
                TexturePaint tp = new TexturePaint(focusImg, r);
                
                g2d.setPaint(tp);
                g2d.draw(new Rectangle(left,top,cWidth, height));
*/ 
                
// 20060213 MEV - Correction to add the left-arrow when has focus        
              Graphics2D g2d = (Graphics2D) g;
              Rectangle bounds = comboBox.getBounds();
              int offset = (bounds.height / 2) - 6;
              g.setColor(new Color(196, 195, 194));
              g2d.drawLine(6, offset, 11, offset + 5);
              g.setColor(new Color(175, 174, 174));
              g2d.drawLine(6, offset + 1, 6, offset + 11);
              g2d.drawLine(6, offset + 11, 11, offset + 6);
           }
        }
        if (mustResetOpaque)
        {
            JComponent jc = (JComponent) c;
            jc.setOpaque(savedOpaque);
        }
    }

    public Skin getSkinArrow()
    {
        if (skinArrow == null)
        {
            skinArrow = new Skin("comboboxarrow.png", 4, 0);
            
        }
        return skinArrow;
    }
}
