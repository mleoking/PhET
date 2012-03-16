/* $RCSfile$
 * $Author: egonw $
 * $Date: 2005-11-10 09:52:44 -0600 (Thu, 10 Nov 2005) $
 * $Revision: 4255 $
 *
 * Copyright (C) 2002-2005  The Jmol Development Team
 *
 * Contact: jmol-developers@lists.sf.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.jmol.shape;

import java.awt.FontMetrics;
import java.awt.Image;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Escape;
import org.jmol.viewer.Viewer;
import org.jmol.util.TextFormat;

public class Text extends Object2d {

  @Override
  public void setScalePixelsPerMicron(float scalePixelsPerMicron) {    
    fontScale = 0;//fontScale * this.scalePixelsPerMicron / scalePixelsPerMicron;
    super.setScalePixelsPerMicron(scalePixelsPerMicron);    
  }
  
  private float fontScale;

  private String text, textUnformatted;
  private boolean doFormatText;

  private String[] lines;

  Font3D font;
  private FontMetrics fm;
  private byte fid;
  private int ascent;
  private int descent;
  private int lineHeight;

  private int textWidth;
  private int textHeight;

  private int[] widths;

  // for labels and hover
  Text(JmolRendererInterface g3d, Font3D font, String text, short colix,
      short bgcolix, int x, int y, int z, int zSlab, int textAlign,
      float scalePixelsPerMicron) {
    this.scalePixelsPerMicron = scalePixelsPerMicron;
    this.viewer = null;
    this.g3d = g3d;
    isLabelOrHover = true;
    setText(text);
    this.colix = colix;
    this.bgcolix = bgcolix;
    setXYZs(x, y, z, zSlab);
    align = textAlign;
    setFont(font);
  }

  // for echo
  Text(Viewer viewer, Graphics3D g3d, Font3D font, String target, short colix,
      int valign, int align, float scalePixelsPerMicron) {
    super(viewer, g3d, target, colix, valign, align, scalePixelsPerMicron);
    this.font = font;
    getFontMetrics();
  }

  private void getFontMetrics() {
    fm = font.fontMetrics;
    descent = fm.getDescent();
    ascent = fm.getAscent();
    lineHeight = ascent + descent;
  }

  void setFid(byte fid) { //labels only
    if (this.fid == fid)
      return;
    fontScale = 0;
    setFont(Font3D.getFont3D(fid));
  }

  void setText(String text) {
    if (image != null)
      getFontMetrics();
    image = null;
    text = fixText(text);
    if (this.text != null && this.text.equals(text))
      return;
    this.text = text;
    textUnformatted = text;
    doFormatText = (viewer != null && text != null && (text.indexOf("%{") >= 0 || text
        .indexOf("@{") >= 0));
    if (!doFormatText)
      recalc();
  }

  Image image;
  public void setImage(Image image) {
    this.image = image;
    // this.text will be file name
    recalc();
  }

  void setFont(Font3D f3d) {
    font = f3d;
    if (font == null)
      return;
    fid = font.fid;
    getFontMetrics();
    recalc();
  }

  void setFontScale(float scale) {
    if (fontScale == scale)
      return;
    fontScale = scale;
    if (fontScale != 0)
      setFont(g3d.getFont3DScaled(font, scale));
  }

  String fixText(String text) {
    if (text == null || text.length() == 0)
      return null;
    int pt;
    while ((pt = text.indexOf("\n")) >= 0)
      text = text.substring(0, pt) + "|" + text.substring(pt + 1);
    return text;
  }

  @Override
  protected void recalc() {
    if (image != null) {
      textWidth = textHeight = 0;
      boxWidth = image.getWidth(null) * fontScale;
      boxHeight = image.getHeight(null) * fontScale;
      ascent = 0;
      return;
    }
    if (text == null) {
      text = null;
      lines = null;
      widths = null;
      return;
    }
    if (fm == null)
      return;
    lines = TextFormat.split(text, '|');
    textWidth = 0;
    widths = new int[lines.length];
    for (int i = lines.length; --i >= 0;)
      textWidth = Math.max(textWidth, widths[i] = stringWidth(lines[i]));
    textHeight = lines.length * lineHeight;
    boxWidth = textWidth + (fontScale >= 2 ? 16 : 8);
    boxHeight = textHeight + (fontScale >= 2 ? 16 : 8);
  }

  private void formatText() {
    text = (viewer == null ? textUnformatted : viewer
        .formatText(textUnformatted));
    recalc();
  }

  void render(JmolRendererInterface g3d, float scalePixelsPerMicron,
              float imageFontScaling, boolean isExact) {
    if (text == null)
      return;
    setWindow(g3d, scalePixelsPerMicron);
    if (scalePixelsPerMicron != 0 && this.scalePixelsPerMicron != 0)
      setFontScale(scalePixelsPerMicron / this.scalePixelsPerMicron);
    else if (fontScale != imageFontScaling)
      setFontScale(imageFontScaling);
    if (doFormatText)
      formatText();

    if (isLabelOrHover) {
      boxXY[0] = movableX;
      boxXY[1] = movableY;
      setBoxXY(boxWidth, boxHeight, offsetX * imageFontScaling, offsetY
          * imageFontScaling, boxXY, isExact);
    } else {
      setPosition(fontScale);
    }
    boxX = boxXY[0];
    boxY = boxXY[1];

    // adjust positions if necessary

    if (adjustForWindow)
      setBoxOffsetsInWindow(/*image == null ? fontScale * 5 :*/ 0, isLabelOrHover ? 16
          * fontScale + lineHeight : 0, boxY - textHeight);

    // draw the box if necessary
    if (image == null && bgcolix != 0 && g3d.setColix(bgcolix))
      showBox(g3d, colix, (int) boxX, (int) boxY, z + 2, zSlab,
          (int) boxWidth, (int) boxHeight, fontScale, isLabelOrHover);
    if (g3d.setColix(colix)) {

      // now set x and y positions for text from (new?) box position

      if (image != null) {
        g3d.drawImage(image, (int) boxX, (int) boxY, z, zSlab, bgcolix,
            (int) boxWidth, (int) boxHeight);
      } else {
        // now write properly aligned text

        int adj = (fontScale >= 2 ? 8 : 4);
        int x0 = (int) boxX;
        switch (align) {
        case ALIGN_CENTER:
          x0 += boxWidth / 2;
          break;
        case ALIGN_RIGHT:
          x0 += boxWidth - adj;
          break;
        default:
          x0 += adj;
        }

        float x = x0;
        float y = boxY + ascent + adj;
        for (int i = 0; i < lines.length; i++) {
          switch (align) {
          case ALIGN_CENTER:
            x = x0 - widths[i] / 2;
            break;
          case ALIGN_RIGHT:
            x = x0 - widths[i];
          }
          g3d.drawString(lines[i], font, (int) x, (int) y, z, zSlab);
          y += lineHeight;
        }
      }
    }
    drawPointer(g3d);
  }

  private void setPosition(float scale) {
    float xLeft, xCenter, xRight;
    boolean is3dEcho = (xyz != null);
    if (valign == VALIGN_XY || valign == VALIGN_XYZ) {
      float x = (movableXPercent != Integer.MAX_VALUE ? movableXPercent
          * windowWidth / 100 : is3dEcho ? movableX : movableX * scale);
      float offsetX = this.offsetX * scale;
      xLeft = xRight = xCenter = x + offsetX;
    } else {
      xLeft = 5 * scale;
      xCenter = windowWidth / 2;
      xRight = windowWidth - xLeft;
    }

    // set box X from alignments

    boxXY[0] = xLeft;
    switch (align) {
    case ALIGN_CENTER:
      boxXY[0] = xCenter - boxWidth / 2;
      break;
    case ALIGN_RIGHT:
      boxXY[0] = xRight - boxWidth;
    }

    // set box Y from alignments

    boxXY[1] = 0;
    switch (valign) {
    case VALIGN_TOP:
      break;
    case VALIGN_MIDDLE:
      boxXY[1] = windowHeight / 2;
      break;
    case VALIGN_BOTTOM:
      boxXY[1] = windowHeight;
      break;
    default:
      float y = (movableYPercent != Integer.MAX_VALUE ? movableYPercent
          * windowHeight / 100 : is3dEcho ? movableY : movableY * scale);
      boxXY[1] = (is3dEcho ? y : (windowHeight - y)) + offsetY * scale;
    }

    if (align == ALIGN_CENTER)
      boxXY[1] -= (image != null ? boxHeight : xyz != null ? boxHeight 
          : ascent - boxHeight) / 2;
    else if (image != null)
      boxXY[1] -= 0;
    else if (xyz != null)
      boxXY[1] -= ascent / 2;
  }

  private static void setBoxXY(float boxWidth, float boxHeight, float xOffset,
                               float yOffset, float[] boxXY, boolean isExact) {
    float xBoxOffset, yBoxOffset;

    // these are based on a standard |_ grid, so y is reversed.
    if (xOffset > 0 || isExact) {
      xBoxOffset = xOffset;
    } else {
      xBoxOffset = -boxWidth;
      if (xOffset == 0)
        xBoxOffset /= 2;
      else
        xBoxOffset += xOffset;
    }

    if (isExact) {
      yBoxOffset = -boxHeight + yOffset;
    } else if (yOffset < 0) {
        yBoxOffset = -boxHeight + yOffset;
    } else if (yOffset == 0) {
      yBoxOffset = -boxHeight / 2; // - 2; removed in Jmol 11.7.45 06/24/2009
    } else {
      yBoxOffset = yOffset;
    }
    boxXY[0] += xBoxOffset;
    boxXY[1] += yBoxOffset;

  }
  
  private static void showBox(JmolRendererInterface g3d, short colix,
                              int x, int y, int z, int zSlab,
                              int boxWidth, int boxHeight,
                              float imageFontScaling, boolean atomBased) {
    g3d.fillRect(x, y, z, zSlab, boxWidth, boxHeight);
    g3d.setColix(colix);
    if (!atomBased)
      return;
    if (imageFontScaling >= 2) {
      g3d.drawRect(x + 3, y + 3, z - 1, zSlab, boxWidth - 6, boxHeight - 6);
      g3d.drawRect(x + 4, y + 4, z - 1, zSlab, boxWidth - 8, boxHeight - 8);
    } else {
      g3d.drawRect(x + 1, y + 1, z - 1, zSlab, boxWidth - 2, boxHeight - 2);
    }
  }

  final static void renderSimpleLabel(JmolRendererInterface g3d, Font3D font,
                                 String strLabel, short colix, short bgcolix,
                                 float[] boxXY, int z, int zSlab,
                                 int xOffset, int yOffset, float ascent,
                                 int descent, boolean doPointer,
                                 short pointerColix, boolean isExact) {

    // old static style -- quick, simple, no line breaks, odd alignment?
    // LabelsRenderer only

    float boxWidth = font.fontMetrics.stringWidth(strLabel) + 8;
    float boxHeight = ascent + descent + 8;
    
    int x0 = (int) boxXY[0];
    int y0 = (int) boxXY[1];
    
    setBoxXY(boxWidth, boxHeight, xOffset, yOffset, boxXY, isExact);

    float x = boxXY[0];
    float y = boxXY[1];
    if (bgcolix != 0 && g3d.setColix(bgcolix))
      showBox(g3d, colix, (int) x, (int) y, z, zSlab, (int) boxWidth,
          (int) boxHeight, 1, true);
    else
      g3d.setColix(colix);
    g3d.drawString(strLabel, font, (int) (x + 4),
        (int) (y + 4 + ascent), z - 1, zSlab);

    if (doPointer) {
      g3d.setColix(pointerColix);
      if (xOffset > 0)
        g3d.drawLine(x0, y0, zSlab, (int) x, (int) (y + boxHeight / 2), zSlab);
      else if (xOffset < 0)
        g3d.drawLine(x0, y0, zSlab, (int) (x + boxWidth),
            (int) (y + boxHeight / 2), zSlab);
    }
  }

  public String getState() {
    StringBuffer s = new StringBuffer();
    if (text == null || isLabelOrHover || target.equals("error"))
      return "";
    //set echo top left
    //set echo myecho x y
    //echo .....
    boolean isImage = (image != null);
//    if (isDefine) {
      String strOff = null;
      switch (valign) {
      case VALIGN_XY:
        if (movableXPercent == Integer.MAX_VALUE
            || movableYPercent == Integer.MAX_VALUE) {
          strOff = (movableXPercent == Integer.MAX_VALUE ? movableX + " "
              : movableXPercent + "% ")
          + (movableYPercent == Integer.MAX_VALUE ? movableY + ""
              : movableYPercent + "%");
        } else {
          strOff = "[" + movableXPercent + " " + movableYPercent + "%]";
        }          
      //fall through
      case VALIGN_XYZ:
        if (strOff == null)
          strOff = Escape.escape(xyz);
        s.append("  set echo ").append(target).append(" ").append(strOff);
        if (align != ALIGN_LEFT)
          s.append(";  set echo ").append(target).append(" ").append(
              hAlignNames[align]);
        break;
      default:
        s.append("  set echo ").append(vAlignNames[valign]).append(" ").append(
            hAlignNames[align]);
      }
      if (valign == VALIGN_XY && movableZPercent != Integer.MAX_VALUE)
        s.append(";  set echo ").append(target).append(" depth ")
          .append(movableZPercent);
      if (isImage)
        s.append("; set echo ").append(target).append(" IMAGE /*file*/");
      else
        s.append("; echo ");
      s.append(Escape.escape(text)); // was textUnformatted, but that is not really the STATE
      s.append(";\n");
      if (script != null)
        s.append("  set echo ").append(target).append(" script ").append(
            Escape.escape(script)).append(";\n");
      if (modelIndex >= 0)
        s.append("  set echo ").append(target).append(" model ").append(
            viewer.getModelNumberDotted(modelIndex)).append(";\n");
//    }
    //isDefine and target==top: do all
    //isDefine and target!=top: just start
    //!isDefine and target==top: do nothing
    //!isDefine and target!=top: do just this
    //fluke because top is defined with default font
    //in initShape(), so we MUST include its font def here
//    if (isDefine != target.equals("top"))
//      return s.toString();
    // these may not change much:
    s.append("  " + Shape.getFontCommand("echo", font));
    if (scalePixelsPerMicron > 0)
      s.append(" " + (10000f / scalePixelsPerMicron)); // Angstroms per pixel
    s.append("; color echo");
    if (Graphics3D.isColixTranslucent(colix))
      s.append(" translucent " + Graphics3D.getColixTranslucencyLevel(colix));
    s.append(" ").append(Graphics3D.getHexCode(colix));
    if (bgcolix != 0) {
      s.append("; color echo background");
      if (Graphics3D.isColixTranslucent(bgcolix))
        s.append(" translucent "
            + Graphics3D.getColixTranslucencyLevel(bgcolix));
      s.append(" ").append(Graphics3D.getHexCode(bgcolix));
    }
    s.append(";\n");
    return s.toString();
  }

  private int stringWidth(String str) {
    int w = 0;
    int f = 1;
    int subscale = 1; //could be something less than that
    if (str == null)
      return 0;
    if (str.indexOf("<su") < 0)
      return fm.stringWidth(str);
    int len = str.length();
    String s;
    for (int i = 0; i < len; i++) {
      if (str.charAt(i) == '<') {
        if (i + 4 < len
            && ((s = str.substring(i, i + 5)).equals("<sub>") || s
                .equals("<sup>"))) {
          i += 4;
          f = subscale;
          continue;
        }
        if (i + 5 < len
            && ((s = str.substring(i, i + 6)).equals("</sub>") || s
                .equals("</sup>"))) {
          i += 5;
          f = 1;
          continue;
        }
      }
      w += fm.stringWidth(str.substring(i, i + 1)) * f;
    }
    return w;
  }

}
