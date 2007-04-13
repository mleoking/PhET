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

package com.oyoaha.swing.plaf.oyoaha;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.border.*;

import com.oyoaha.swing.plaf.oyoaha.ui.*;
import com.oyoaha.swing.plaf.oyoaha.pool.*;

public class OyoahaUtilities
{
  //component state
  public final static int UNSELECTED_ENABLED = 1000;
  public final static int UNSELECTED_PRESSED = 1001;
  public final static int UNSELECTED_ROLLOVER = 1002;
  public final static int UNSELECTED_DISABLED = 1003;

  public final static int SELECTED_ENABLED = 1004;
  public final static int SELECTED_PRESSED = 1005;
  public final static int SELECTED_ROLLOVER = 1006;
  public final static int SELECTED_DISABLED = 1007;

  //invisible state
  public final static int UNVISIBLE = 1008;

  //extra state and color
  public final static int FOCUS = 100;

  //color
  public final static int WHITE = 101;
  public final static int BLACK = 102;
  public final static int GRAY = 103;

  private static OyoahaPaintUtilities paint;
  private static OyoahaSoundUtilities sound;
  private static OyoahaLookAndFeel oyoahaLookAndFeel;
  private static OyoahaImageLoader loader;
  
  //default color pool
  protected static OyoahaColorPool pool;
  
//private static Hashtable alphaClasses;

  public final static boolean isVersion(String _string)
  {
    String p = System.getProperty("java.version");

    if(p!=null)
    {
      if(p.compareTo(_string)<0)
      {
        return false;
      }
      else
      {
        return true;
      }
    }

    return false;
  }

  /*public final static boolean isThisVersion(String _string)
  {
    String p = System.getProperty("java.version");
    return p.equals(_string);
  }*/

  public final static void initialize(OyoahaLookAndFeel _oyoahaLookAndFeel)
  {
    uninitialize(oyoahaLookAndFeel);
    oyoahaLookAndFeel = _oyoahaLookAndFeel;

    //to keep information about alpha class
//alphaClasses = new Hashtable();
    //to load image with a mediatracker
    loader = new OyoahaImageLoader();
    pool = null;

    boolean java2 = isVersion("1.2");

    if(!java2)
    {
      paint = new OyoahaJava1PaintUtilities();
    }
    else
    {
      paint = new OyoahaJava2PaintUtilities();
    }
    
    sound = null;

    paint.initialize();
  }

  /**
  *
  */
  public final static void uninitialize(OyoahaLookAndFeel _oyoahaLookAndFeel)
  {
    if(oyoahaLookAndFeel==null || oyoahaLookAndFeel!=_oyoahaLookAndFeel)
    return;

    if(animation!=null)
    animation.stop();

    if(paint!=null)
    paint.uninitialize();

    if(sound!=null)
    sound.uninitialize();
  }

  //- - - - - - - - - - - - - - - -
  //
  // GENERAL ROUTINE
  //
  //- - - - - - - - - - - - - - - -

  private static OyoahaRolloverListener mouseListener;

  /**
  *
  */
  public final static OyoahaRolloverListener getOyoahaRolloverListener()
  {
    if(mouseListener==null)
    {
      mouseListener = new OyoahaRolloverListener();
    }

    return mouseListener;
  }

  /**
  *
  */
  public final static void installRolloverListener(Component c)
  {
    if(OyoahaUtilities.isRolloverEnabled(c))
    {
      if(c instanceof AbstractButton && ((AbstractButton)c).isRolloverEnabled())
      {
        return;
      }

      c.addMouseListener(getOyoahaRolloverListener());
    }
  }

  /**
  *
  */
  public final static void uninstallRolloverListener(Component c)
  {
    if(mouseListener!=null)
    {
      c.removeMouseListener(mouseListener);
    }
  }

  /**
  *
  */
  public final static int getStatus(Component c)
  {
    int r=UNSELECTED_ENABLED;

    if(c instanceof OyoahaListCellRenderer)
    {
      r = ((OyoahaListCellRenderer)c).getStatus();
    }
    else
    if(c instanceof OyoahaTreeCellRenderer)
    {
      r = ((OyoahaTreeCellRenderer)c).getStatus();
    }
    else
    if(c instanceof AbstractButton)
    {
      AbstractButton b = (AbstractButton)c;
      ButtonModel m = b.getModel();

      Container parent = b.getParent();

      if(b.isEnabled())
      {
        if(!(b instanceof MenuElement))
        {
          if(m.isSelected())
          {
            if(m.isArmed() && m.isPressed())
            r=SELECTED_PRESSED;
            else
            if(m.isRollover() || isRollover(c))
            r=SELECTED_ROLLOVER;
            else
            r=SELECTED_ENABLED;
          }
          else
          {
            if(m.isArmed() && m.isPressed())
            r=UNSELECTED_PRESSED;
            else
            if(m.isRollover() || isRollover(c))
            r=UNSELECTED_ROLLOVER;
          }
        }
        else
        {
          if(m.isSelected())
          {
            if(isPressed(c) || m.isPressed())
            r=SELECTED_PRESSED;
            else
            if(m.isArmed() || m.isRollover() || isRollover(c))
            r=SELECTED_ROLLOVER;
            else
            r=SELECTED_ENABLED;
          }
          else
          {
            if(isPressed(c) || m.isPressed())
            r=UNSELECTED_PRESSED;
            else
            if(m.isArmed() || m.isRollover() || isRollover(c))
            r=UNSELECTED_ROLLOVER;
          }
        }
      }
      else
      {
      	if(m.isSelected())
        r=SELECTED_DISABLED;
        else
        r=UNSELECTED_DISABLED;
      }
    }
    else
    if(c instanceof JInternalFrame)
    {
      if(!c.isEnabled())
      {
      	if(((JInternalFrame)c).isSelected())
        r=SELECTED_DISABLED;
        else
        r=UNSELECTED_DISABLED;
      }
      else
      if(((JInternalFrame)c).isSelected())
      {
        if(isPressed(c))
        {
          r=SELECTED_PRESSED;
        }
        else
        if(isRollover(c))
        {
          r=SELECTED_ROLLOVER;
        }
        else
        {
          r=SELECTED_ENABLED;
        }
      }
      else
      {
        if(isPressed(c))
        {
          r=UNSELECTED_PRESSED;
        }
        else
        if(isRollover(c))
        {
          r=UNSELECTED_ROLLOVER;
        }
      }
    }
    else
    {
      if(!c.isEnabled())
      {
      	r=UNSELECTED_DISABLED;
      }
      else
      if(isPressed(c))
      {
        r=UNSELECTED_PRESSED;
      }
      else
      if(isRollover(c))
      {
        r=UNSELECTED_ROLLOVER;
      }
    }

    return r;
  }

  /**
  *
  */
  public final static boolean isPressed(Component c)
  {
    if(mouseListener!=null)
    {
      return mouseListener.isPressed(c);
    }

    return false;
  }

  /**
  *
  */
  public final static boolean isRollover(Component c)
  {
    if(mouseListener!=null)
    {
      return mouseListener.isRollover(c);
    }

    return false;
  }

  /**
  *
  */
  public final static Component getPressed()
  {
    if(mouseListener!=null)
    {
      return mouseListener.getPressed();
    }

    return null;
  }

  /**
  *
  */
  public final static Component getRollover()
  {
    if(mouseListener!=null)
    {
      return mouseListener.getRollover();
    }

    return null;
  }

  //- - - - - - - - - - - - - - - -
  //
  // SOUND ROUTINE
  //
  //- - - - - - - - - - - - - - - -

  /**
  *
  */
  public static void playStartClick()
  {
    if(sound!=null)
    sound.playStartClick();
  }

  /**
  *
  */
  public static void playStopClick()
  {
    if(sound!=null)
    sound.playStopClick();
  }

  //- - - - - - - - - - - - - - - -
  //
  // ANIMATED ROUTINE
  //
  //- - - - - - - - - - - - - - - -

  private static OyoahaUpdater animation;

  public static void initializeAnimation(boolean _focus, boolean _background)
  {
    animation = new OyoahaUpdater(_focus, _background);
    animation.start();
  }

  public static void initializeSound(String pressed, String scrolled)
  {
    if(pressed==null && scrolled==null)
    return;
    
    if(!isVersion("1.3"))
    {
      sound = new OyoahaJava1SoundUtilities();
    }
    else
    {
      sound = new OyoahaJava2SoundUtilities();
    }
    
    sound.initialize(pressed, scrolled);
  }

  //- - - - - - - - - - - - - - - -
  //
  // GET SOME INFORMATION ABOUT A COMPONENT
  //
  //- - - - - - - - - - - - - - - -

  /**
   *
   */
  public final static boolean hasFocus(Component c)
  {
    return paint.hasFocus(c);
  }

  /**
   *
   */
  public final static boolean isDefaultButton(Component c)
  {
    return paint.isDefaultButton(c);
  }

  /**
   *
   */
  public final static boolean isLeftToRight(Component c)
  {
    return paint.isLeftToRight(c);
  }

  /**
   *
   */
  public final static Rectangle getFullRect(Component c)
  {
    Rectangle r  = c.getBounds();
    r.x=0; r.y=0;
    return r;
  }

  /**
   *
   */
  public final static Rectangle getViewRect2(Component c)
  {
    Rectangle r = getFullRect(c);

    if (c instanceof JComponent)
    {
        Insets i = ((Container)c).getInsets();
        r.x += i.left;
        r.y += i.top;
        r.width -= i.right+r.x;
        r.height -= i.bottom+r.y;
    }
    else
    if(c instanceof Container)
    {
      Insets i = ((Container)c).getInsets();
      r.x += i.left;
      r.y += i.top;
      r.width -= i.right+r.x;
      r.height -= i.bottom+r.y;
    }

    return r;
  }  

  /**
   *
   */
  public final static Rectangle getViewRect(Component c)
  {
    Rectangle r = getFullRect(c);
    Insets i = getRealInsets(c);

    if(i!=null)
    {
      r.x += i.left;
      r.y += i.top;
      r.width -= i.right+r.x;
      r.height -= i.bottom+r.y;
    }
    else
    {
        if (c instanceof JComponent)
        {
            Border border = ((JComponent)c).getBorder();

            if (border instanceof OyoahaButtonBorderLike)
            {
                OyoahaButtonBorderLike bbl = (OyoahaButtonBorderLike)border;
    
                r.x += bbl.getLeftInsets();
                r.y += bbl.getTopInsets();
                r.width -= bbl.getRightInsets()+r.x;
                r.height -= bbl.getBottomInsets()+r.y;
            }
            else
            {
                i = ((Container)c).getInsets();
                r.x += i.left;
                r.y += i.top;
                r.width -= i.right+r.x;
                r.height -= i.bottom+r.y;
            }
        }
        else
        if(c instanceof Container)
        {
          i = ((Container)c).getInsets();
          r.x += i.left;
          r.y += i.top;
          r.width -= i.right+r.x;
          r.height -= i.bottom+r.y;
        }
    }

    return r;
  }

  public final static Insets getRealInsets(Component c)
  {
    return getRealInsets(getUIClassID(c));
  }

  /**
  *
  */
  public final static Insets getRealInsets(String ui)
  {
    Object o = UIManager.get(ui+".realInsets");

    if(o!=null && o instanceof Insets)
    {
      return (Insets)o;
    }

    return null;
  }

  /**
  *
  */
  public final static Point getAPosition(Component c)
  {
    return paint.getAPosition(c);
  }

  /**
  *
  */
  public final static boolean intersects(int x, int y, int width, int height, int x1, int y1, int width1, int height1)
  {
    return paint.intersects(x, y, width, height, x1, y1, width1, height1);
  }

  /**
  *
  */
  public final static Shape normalizeClip(Graphics g, int x, int y, int width, int height)
  {
    return paint.normalizeClip(g, x, y, width, height);
  }

  /**
   *
   */
  public final static boolean isComponentOpaque(Component c)
  {
    return paint.isOpaque(c);
  }

  /**
   *
   */
  public final static boolean isOpaque(Component c)
  {
    /*if (c instanceof OyoahaButtonLikeComponent)
    {
        //return ((OyoahaButtonLikeComponent)c).isOpaque();
        return true;
    }
    
    ComponentUI cui = (ComponentUI)UIManager.getUI((JComponent)c);
    
    if (cui!=null && cui instanceof OyoahaButtonLikeComponent)
    {
        if(!((OyoahaButtonLikeComponent)cui).isBorderPainted(c))
        return false;
        
        return true; //((OyoahaButtonLikeComponent)c).isAlphaClasses(); 
    }*/
  
    /*String ui = OyoahaUtilities.getUIClassID(c);

    if(alphaClasses.containsKey(ui))
    {
      if(((Boolean)alphaClasses.get(ui)).booleanValue())
      {
        return true;
      }
    }*/
    
    OyoahaButtonLikeComponent blc = getOyoahaButtonLikeComponent(c);
    
    if (blc!=null)
    {
        return true;
    }

    return paint.isOpaque(c);
  }

  /**
  *
  */
  public final static Color getBackground(Component c)
  {
    return paint.getBackground(c);
  }

  /**
  *
  */
  public final static Color getForeground(Component c)
  {
    return c.getForeground();
  }

  //- - - - - - - - - - - - - - - -
  //
  // GET GENERAL STATE
  //
  //- - - - - - - - - - - - - - - -

  public static boolean getBoolean(String _s)
  {
    return getBoolean(_s, false);
  }

  public static boolean getBoolean(String _s, boolean _default)
  {
    Object  o=UIManager.get(_s);

    if(o instanceof Boolean)
    {
      return ((Boolean)o).booleanValue();
    }

    return _default;
  }

  public final static void loadImage(Image image)
  {
    loader.loadImage(image);
  }

  public final static Image loadImage(int[] source, int w, int h)
  {
    Image image = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(w, h, source, 0, w));
    loader.loadImage(image);

    return image;
  }

  public final static OyoahaBackgroundObject getBackgroundObject(Component c)
  {
    return getBackgroundObject(getUIClassID(c));
  }

  public final static OyoahaBackgroundObject getBackgroundObject(String ui)
  {
    Object o = UIManager.get(ui+".backgroundObject");

    if(o!=null && o instanceof OyoahaBackgroundObject)
    {
      return (OyoahaBackgroundObject)o;
    }

    return null;
  }

  public final static OyoahaFlash getFlash(Component c)
  {
    return getFlash(getUIClassID(c));
  }

  public final static OyoahaFlash getFlash(String ui)
  {
    OyoahaBackgroundObject o = null;

    if(ui.equals("Slider")) //SliderTrack.backgroundObject
    {
      o = getBackgroundObject("SliderTrack");
    }
    else
    if(ui.equals("ScrollBar")) //ScrollBarTrack.backgroundObject
    {
      o = getBackgroundObject("ScrollBarTrack");
    }
    else
    {
      o = getBackgroundObject(ui);
    }

    if(o!=null && o instanceof OyoahaFlash)
    {
      return (OyoahaFlash)o;
    }

    return null;
  }

  public final static OyoahaFlash getFlashBorder(Component c)
  {
    if(c instanceof JComponent)
    {
      Border b = ((JComponent)c).getBorder();

      if(b!=null && b instanceof OyoahaFlash)
      {
        return (OyoahaFlash)b;
      }
    }

    return null;
  }

  public final static OyoahaFlash getFlashIcon(Component c)
  {
    if(c instanceof AbstractButton)
    {
      Icon i = ((AbstractButton)c).getIcon();

      if(i!=null && i instanceof OyoahaFlash)
      {
        return (OyoahaFlash)i;
      }
    }

    return null;
  }

  public final static OyoahaBumpObject getOyoahaBumpObject()
  {
    Object o = UIManager.get("oyoahabump");

    if(o!=null && o instanceof OyoahaBumpObject)
    {
      return (OyoahaBumpObject)o;
    }

    return null;
  }

//-----------

  /**
  * by default rollover is enabled in OyoahaLookAndFeel
  */
  public final static boolean isRolloverEnabled(Component c)
  {
    return isRolloverEnabled(c, oyoahaLookAndFeel.getDefaultRolloverPolicy(c));
  }

  public final static boolean isRolloverEnabled(Component c, boolean d)
  {
    return getBoolean(getUIClassID(c)+".rollover",d);
  }

  //- - - - - - - - - - - - - - - -
  //
  // SOME USEFULL PAINT ROUTINE
  //
  //- - - - - - - - - - - - - - - -

  public final static void paintBump(Graphics g, Component c, int x, int y, int w, int h, int state, int nx, int ny)
  {
    //getbump for the this state
    OyoahaBumpObject bump = getOyoahaBumpObject();

    if(bump==null || nx==0 || ny==0)
    return;

    int posx = x;
    int posy = y;

    //check if there is enough space
    if(nx>0)
    {
      int size = nx*bump.getWidth()+(nx-1);

      if(size>w)
      return;

      posx += (w-size)/2;
    }
    else
    {
      //check if at least one bump can be painted
      nx = 1;
      int size = nx*bump.getWidth();

      if(size>w)
      return;

      while(size<w)
      {
        nx++;
        size = nx*bump.getWidth()+(nx-1);
      }

      nx--;
      size = nx*bump.getWidth()+(nx-1);

      posx += (w-size)/2;
    }

    if(ny>0)
    {
      int size = ny*bump.getHeight()+(ny-1);

      if(size>h)
      return;

      posy += (h-size)/2;
    }
    else
    {
      //check if at least one bump can be painted
      ny = 1;
      int size = ny*bump.getHeight();

      if(size>h)
      return;

      while(size<h)
      {
        ny++;
        size = ny*bump.getHeight()+(ny-1);
      }

      ny--;
      size = ny*bump.getHeight()+(ny-1);

      posy += (h-size)/2;
    }

    //paint bump
    for(int yy=0;yy<ny;yy++)
    {
      for(int xx=0;xx<nx;xx++)
      {
        bump.paint(g, c, posx+(xx*bump.getWidth())+xx, posy+(yy*bump.getHeight())+yy, state);
      }
    }
  }

  public final static void setAlphaChannel(Graphics g, Component c, float f)
  {
    paint.setAlphaChannel(g, c, f);
  }

  public final static void paintBackground(Graphics g, Component c)
  {
    Rectangle r = OyoahaUtilities.getFullRect(c);
    paint.paintBackground(g, c, r.x, r.y, r.width, r.height, OyoahaUtilities.getBackground(c), OyoahaUtilities.getStatus(c));
  }

  public final static void paintBackground(Graphics g, Component c, int x, int y, int width, int height, int status)
  {
    paint.paintBackground(g, c, x, y, width, height, null, status);
  }

  public final static void paintBackground(Graphics g, Component c, int x, int y, int width, int height, Color color, int status)
  {
    paint.paintBackground(g, c, x, y, width, height, color, status);
  }

  public final static void paintColorBackground(Graphics g, Component c, int x, int y, int width, int height, Color bg, int status)
  {
    paint.paintColorBackground(g, c, x, y, width, height, bg, status);
  }

  public final static boolean isAlphaClasses(Component c)
  {
    /*if (c instanceof AbstractButton && !((AbstractButton)c).isBorderPainted())
    {
        return false;
    }*/
    
    /*if (c instanceof OyoahaButtonLikeComponent)
    {
        if(!((OyoahaButtonLikeComponent)c).isBorderPainted(c))
        return false;
    
        return true; //((OyoahaButtonLikeComponent)c).isAlphaClasses();
    }
    
    //ComponentUI cui = (ComponentUI)UIManager.get(OyoahaUtilities.getUIClassID(c));
    ComponentUI cui = (ComponentUI)UIManager.getUI((JComponent)c);
    
    if (cui!=null && cui instanceof OyoahaButtonLikeComponent)
    {
        if(!((OyoahaButtonLikeComponent)cui).isBorderPainted(c))
        return false;
        
        return true; //((OyoahaButtonLikeComponent)c).isAlphaClasses(); 
    }
    
    /*String ui = OyoahaUtilities.getUIClassID(c);

    if (alphaClasses.containsKey(ui))
    {
      return ((Boolean)alphaClasses.get(ui)).booleanValue();
    }*/

    //return false;
    
    OyoahaButtonLikeComponent blc = getOyoahaButtonLikeComponent(c);
    
    if (blc!=null)
    {
        return blc.isBorderPainted(c);
    }
    
    return false;
  }

  /*public final static void forceOpaque(Component c)
  {
    if(!(c instanceof JComponent))
    {
      return;
    }

    /*String ui = OyoahaUtilities.getUIClassID(c);

    if(alphaClasses.containsKey(ui))
    {
      ((JComponent)c).setOpaque(!((Boolean)alphaClasses.get(ui)).booleanValue());
      return;
    }

    alphaClasses.put(ui, new Boolean(true));
    ((JComponent)c).setOpaque(false);*/
    
    //((JComponent)c).setOpaque(false);
  //}
  
  /**
   *
   */
  public final static OyoahaButtonLikeComponent getOyoahaButtonLikeComponent(Component c)
  {
    if (c instanceof OyoahaButtonLikeComponent)
    {
        return (OyoahaButtonLikeComponent)c;
    }
    
    if (c instanceof JComponent)
    {
        String ui = OyoahaUtilities.getUIClassID2(c);
        
        if(ui==null)
        return null;
        
        Object o = UIManager.get(ui);
        
        if(o==null || !o.toString().startsWith("com.oyoaha."))
        return null;
        
        o = UIManager.getUI((JComponent)c);
        
        if (o!=null && o instanceof OyoahaButtonLikeComponent)
        {
            return (OyoahaButtonLikeComponent)o;
        }
    }

    return null;
  }

  public final static void setOpaque(Component c)
  {
    if(!(c instanceof JComponent))
    {
      return;
    }

    //String ui = OyoahaUtilities.getUIClassID(c);

    /*if(alphaClasses.containsKey(ui))
    {
      ((JComponent)c).setOpaque(!((Boolean)alphaClasses.get(ui)).booleanValue());
      return;
    }*/

    //Boolean bool = null;
    
    /*Border border = ((JComponent)c).getBorder();

    if(border==null || border.isBorderOpaque())
    {
      OyoahaBackgroundObject back = OyoahaUtilities.getBackgroundObject(c);

      if(back!=null)
      bool = new Boolean(!back.isOpaque());
      else
      bool = new Boolean(false);
    }
    else
    if(border!=null)
    {
      bool = new Boolean(true);
    }
    else
    {
      bool = new Boolean(true);
    }*/
    
    /*bool = new Boolean(true);

    if (!alphaClasses.containsKey(ui))
    {
        alphaClasses.put(ui, bool);
    }*/
    
    /*if (c instanceof OyoahaButtonLikeComponent)
    {
        ((JComponent)c).setOpaque(false);
    }
    else
    {
        //ComponentUI cui = (ComponentUI)UIManager.get(OyoahaUtilities.getUIClassID(c));
        ComponentUI cui = (ComponentUI)UIManager.getUI((JComponent)c);
        
        if (cui!=null && cui instanceof OyoahaButtonLikeComponent)
        {    
            ((JComponent)c).setOpaque(false);
        }
    }*/
    
    OyoahaButtonLikeComponent blc = getOyoahaButtonLikeComponent(c);
    
    if (blc!=null)
    {
        ((JComponent)c).setOpaque(false);
    }
  }

  /**
  *
  */
  public final static void unsetOpaque(Component c)
  {
    if(c instanceof JComponent)
    {
      //((JComponent)c).setOpaque(isAlphaClasses(c));
      ((JComponent)c).setOpaque(isOpaque(c));
    }
  }

  /**
  *
  */
  public final static OyoahaThemeScheme getScheme()
  {
    return oyoahaLookAndFeel.getOyoahaThemeScheme();
  }

/****************************************/
/****************************************/
/****************************************/

  /**
  *
  */
  protected final static OyoahaColorPool getDefaultOyoahaColorPool()
  {
    if(pool==null)
    {
      //fist try to load the specified ColorPool...
      Object o = UIManager.get("OyoahaUtilities.defaultOyoahaColorPool");

      if(o!=null && o instanceof OyoahaColorPool)
      {
        pool = (OyoahaColorPool)o;
      }
      else
      {
        pool = new OyoahaDefaultColorPool(oyoahaLookAndFeel);
      }
    }

    return pool;
  }

  public final static void updateOyoahaThemeScheme(OyoahaThemeSchemeChanged changed)
  {
    //nothing todo
  }

  public final static void updateOyoahaTheme()
  {
    pool = null;
  }

  /**
  *
  */
  public final static Color getColor(Component c)
  {
    return getColor(getBackground(c), getStatus(c));
  }

  /**
  *
  */
  public final static Color getColor(Component c, int status)
  {
    return getColor(getBackground(c), status);
  }

  /**
  *
  */
  public final static Color getColor(Color color, int status)
  {
    return getDefaultOyoahaColorPool().getColor(color, status);
  }

  /**
  *
  */
  public final static Color getColor(int status)
  {
    if(status==BLACK)
    return oyoahaLookAndFeel.getOyoahaThemeScheme().getBlack();

    if(status==WHITE)
    return oyoahaLookAndFeel.getOyoahaThemeScheme().getWhite();

    return getDefaultOyoahaColorPool().getColor(status);
  }

  /**
  *
  */
  public final static Color getColorFromScheme(int status)
  {
    return oyoahaLookAndFeel.getOyoahaThemeScheme().get(status);
  }

  /**
  *
  */
  public final static Color getColor(Component c, Color bg)
  {
    return getColor(bg, getStatus(c));
  }

/****************************************/
/****************************************/
/****************************************/

  /**
  *
  */
  public final static void paintAScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY)
  {
    paint.paintAScrollMosaic(g, c, x, y, width, height, i, decalX, decalY);
  }
  
  public final static void paintAGradient(Graphics g, Component c, int x, int y, int width, int height, Color color1, Color color2, boolean horizontal, boolean vertical, int state)
  {
    paint.paintAGradient(g, c, x, y, width, height, color1, color2, horizontal, vertical, state);
  }

  /**
  *
  */
  public final static void paintRScrollMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i, int decalX, int decalY)
  {
    paint.paintAScrollMosaic(g, c, x, y, width, height, i, decalX, decalY);
  }

  /**
  *
  */
  public final static void paintAMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    paint.paintAMosaic(g, c, x, y, width, height, i);
  }

  /**
  *
  */
  public final static void paintRMosaic(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    paint.paintRMosaic(g, c, x, y, width, height, i);
  }

  public final static int CENTER = 0;
  public final static int TOP = 1;
  public final static int TOPLEFT = 2;
  public final static int LEFT = 3;
  public final static int LEFTBOTTOM = 4;
  public final static int BOTTOM = 5;
  public final static int BOTTOMRIGHT = 6;
  public final static int RIGHT = 7;
  public final static int RIGHTTOP = 8;

  /**
  *
  */
  public final static void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos)
  {
    paint.paintAImageAt(g, c, x, y, width, height, i, pos);
  }

  /**
  *
  */
  public final static void paintAImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos, int w, int h)
  {
    paint.paintAImageAt(g, c, x, y, width, height, i, pos, w, h);
  }

  /**
  *
  */
  public final static void paintRImageAt(Graphics g, Component c, int x, int y, int width, int height, Image i, int pos)
  {
    paint.paintRImageAt(g, c, x, y, width, height, i, pos);
  }

  /**
  *
  */
  public final static void paintRScaling(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    paint.paintRScaling(g, c, x, y, width, height, i);
  }

  /**
  *
  */
  public final static void paintAScaling(Graphics g, Component c, int x, int y, int width, int height, Image i)
  {
    paint.paintAScaling(g, c, x, y, width, height, i);
  }

  //- - - - - - - - -

  public final static String getUIClassID2(Component c)
  {
    if(c instanceof JComponent)
    {
      String ui=((JComponent)c).getUIClassID();

      if(ui==null || ui=="")
      return null;

      return ui;
    }

    return null;
  }

  public final static String getUIClassID(Component c)
  {
    if(c instanceof JComponent)
    {
      String ui=((JComponent)c).getUIClassID();

      if(ui==null || ui=="")
      return "Panel";

      if(ui.endsWith("UI"))
      ui=ui.substring(0,ui.length()-2);

      return ui;
    }

    return "Panel";
  }
}