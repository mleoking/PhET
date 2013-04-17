package org.jmol.shape;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.api.JmolRendererInterface;
import org.jmol.g3d.Graphics3D;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

public abstract class Object2d {

  public final static int POINTER_NONE = 0;
  public final static int POINTER_ON = 1;
  public final static int POINTER_BACKGROUND = 2;

  protected final static String[] hAlignNames = { "", "left", "center", "right",
      "" };

  final protected static int ALIGN_NONE = 0;
  final public static int ALIGN_LEFT = 1;
  final protected static int ALIGN_CENTER = 2;
  final protected static int ALIGN_RIGHT = 3;

  final protected static String[] vAlignNames = { "xy", "top", "bottom", "middle" };

  final public static int VALIGN_XY = 0;
  final protected static int VALIGN_TOP = 1;
  final protected static int VALIGN_BOTTOM = 2;
  final protected static int VALIGN_MIDDLE = 3;
  final public static int VALIGN_XYZ = 4;

  protected boolean isLabelOrHover;
  protected Viewer viewer;
  protected JmolRendererInterface g3d;
  public Point3f xyz;
  String target;
  protected String script;
  protected short colix;
  protected short bgcolix;
  protected int pointer;
  
  protected int align;
  public int valign;
  protected int movableX;
  protected int movableY;
  protected int movableZ;
  protected int movableXPercent = Integer.MAX_VALUE;
  protected int movableYPercent = Integer.MAX_VALUE;
  protected int movableZPercent = Integer.MAX_VALUE;
  protected int offsetX;
  protected int offsetY;
  protected int z;
  protected int zSlab; // z for slabbing purposes -- may be near an atom
  protected int windowWidth;
  protected int windowHeight;
  protected boolean adjustForWindow;
  protected float boxWidth, boxHeight, boxX, boxY;

  int modelIndex = -1;
  boolean visible = true;
  boolean hidden = false;

  protected final float[] boxXY = new float[2];
  
  protected float scalePixelsPerMicron;

  float getScalePixelsPerMicron() {
    return scalePixelsPerMicron;
  }

  public void setScalePixelsPerMicron(float scalePixelsPerMicron) {    
    this.scalePixelsPerMicron = scalePixelsPerMicron;
  }

  abstract protected void recalc();

  protected Object2d() {  
    // not used
  }
  
  protected Object2d(Viewer viewer, Graphics3D g3d, String target, 
                     short colix, int valign, int align,
                     float scalePixelsPerMicron) {
    this.viewer = viewer;
    this.g3d = g3d;
    isLabelOrHover = false;
    this.target = target;
    if (target.equals("error"))
      valign = VALIGN_TOP;
    this.colix = colix;
    this.align = align;
    this.valign = valign;
    this.z = 2;
    this.zSlab = Integer.MIN_VALUE;
    this.scalePixelsPerMicron = scalePixelsPerMicron;

  }

  void setModel(int modelIndex) {
    this.modelIndex = modelIndex;
  }

  public void setVisibility(boolean TF) {
    visible = TF;
  }

  public void setXYZ(Point3f xyz) {
    valign = (xyz == null ? VALIGN_XY : VALIGN_XYZ);
    this.xyz = xyz;
    setAdjustForWindow(xyz == null);
  }

  public void setAdjustForWindow(boolean TF) {
    adjustForWindow = TF;
  }

  void setColix(short colix) {
    this.colix = colix;
  }

  void setColix(Object value) {
    colix = Graphics3D.getColix(value);
  }

  void setTranslucent(float level, boolean isBackground) {
    if (isBackground) {
      if (bgcolix != 0)
        bgcolix = Graphics3D.getColixTranslucent(bgcolix, !Float.isNaN(level),
            level);
    } else {
      colix = Graphics3D.getColixTranslucent(colix, !Float.isNaN(level), level);
    }
  }

  void setBgColix(short colix) {
    this.bgcolix = colix;
  }

  void setBgColix(Object value) {
    bgcolix = (value == null ? (short) 0 : Graphics3D.getColix(value));
  }

  public void setMovableX(int x) {
    valign = (valign == VALIGN_XYZ ? VALIGN_XYZ : VALIGN_XY);
    movableX = x;
    movableXPercent = Integer.MAX_VALUE;
  }

  public void setMovableY(int y) {
    valign = (valign == VALIGN_XYZ ? VALIGN_XYZ : VALIGN_XY);
    movableY = y;
    movableYPercent = Integer.MAX_VALUE;
  }

  public void setMovableZ(int z) {
    if (valign != VALIGN_XYZ)
      valign = VALIGN_XY;
    movableZ = z;
    movableZPercent = Integer.MAX_VALUE;
  }

  public void setMovableXPercent(int x) {
    valign = (valign == VALIGN_XYZ ? VALIGN_XYZ : VALIGN_XY);
    movableX = Integer.MAX_VALUE;
    movableXPercent = x;
  }

  public void setMovableYPercent(int y) {
    valign = (valign == VALIGN_XYZ ? VALIGN_XYZ : VALIGN_XY);
    movableY = Integer.MAX_VALUE;
    movableYPercent = y;
  }

  public void setMovableZPercent(int z) {
    if (valign != VALIGN_XYZ)
      valign = VALIGN_XY;
    movableZ = Integer.MAX_VALUE;
    movableZPercent = z;
  }

  void setXY(int x, int y) {
    setMovableX(x);
    setMovableY(y);
  }

  void setZs(int z, int zSlab) {
    this.z = z;
    this.zSlab = zSlab;
  }

  public void setXYZs(int x, int y, int z, int zSlab) {
    setMovableX(x);
    setMovableY(y);
    setZs(z, zSlab);
  }

  public void setScript(String script) {
    this.script = (script == null || script.length() == 0 ? null : script);
  }

  public String getScript() {
    return script;
  }

  void setOffset(int offset) {
    //Labels only
    offsetX = getXOffset(offset);
    offsetY = getYOffset(offset);
    valign = VALIGN_XY;
  }

  static int getXOffset(int offset) {
    switch (offset) {
    case 0:
      return JmolConstants.LABEL_DEFAULT_X_OFFSET;
    case Short.MAX_VALUE:
      return 0;
    default:
      return - - (int) (byte) ((offset >> 8) & 0xFF);
    }
  }

  static int getYOffset(int offset) {
    switch (offset) {
    case 0:
      return -JmolConstants.LABEL_DEFAULT_Y_OFFSET;
    case Short.MAX_VALUE:
      return 0;
    default:
      return -(int) (byte) (offset & 0xFF);
    }
  }

  boolean setAlignment(String align) {
    if ("left".equals(align))
      return setAlignment(ALIGN_LEFT);
    if ("center".equals(align))
      return setAlignment(ALIGN_CENTER);
    if ("right".equals(align))
      return setAlignment(ALIGN_RIGHT);
    return false;
  }

  static String getAlignment(int align) {
    return hAlignNames[align & 3];
  }

  boolean setAlignment(int align) {
    if (this.align != align) {
      this.align = align;
      recalc();
    }
    return true;
  }

  void setPointer(int pointer) {
    this.pointer = pointer;
  }

  static String getPointer(int pointer) {
    return ((pointer & POINTER_ON) == 0 ? ""
        : (pointer & POINTER_BACKGROUND) > 0 ? "background" : "on");
  }

  protected void drawPointer(JmolRendererInterface g3d) {
    // now draw the pointer, if requested

    if ((pointer & POINTER_ON) != 0) {
      if (!g3d.setColix((pointer & POINTER_BACKGROUND) != 0 && bgcolix != 0 ? bgcolix
              : colix))
        return;
      if (boxX > movableX)
        g3d.drawLine(movableX, movableY, zSlab, (int) boxX, (int) (boxY + boxHeight / 2),
            zSlab);
      else if (boxX + boxWidth < movableX)
        g3d.drawLine(movableX, movableY, zSlab, (int) (boxX + boxWidth), 
            (int) (boxY + boxHeight / 2), zSlab);
    }
  }

  protected void setBoxOffsetsInWindow(float margin, float vMargin, float vTop) {
    // not labels

    // these coordinates are (0,0) in top left
    // (user coordinates are (0,0) in bottom left)
    float bw = boxWidth + margin;
    float x = boxX;
    if (x + bw > windowWidth)
      x = windowWidth - bw;
    if (x < margin)
      x = margin;
    boxX = x;

    float bh = boxHeight;
    float y = vTop;
    if (y + bh > windowHeight)
      y = windowHeight - bh;
    if (y < vMargin)
      y = vMargin;
    boxY = y;
  }

  protected void setWindow(JmolRendererInterface g3d, float scalePixelsPerMicron) {
    windowWidth = g3d.getRenderWidth();
    windowHeight = g3d.getRenderHeight();
    if (this.scalePixelsPerMicron < 0 && scalePixelsPerMicron != 0)
      this.scalePixelsPerMicron = scalePixelsPerMicron;
  }

  public boolean checkObjectClicked(int x, int y, BitSet bsVisible) {
    if (modelIndex >= 0 && !bsVisible.get(modelIndex) || hidden)
      return false;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
    }
    return (script != null 
        && x >= boxX && x <= boxX + boxWidth 
        && y >= boxY && y <= boxY + boxHeight);
  }

  public static boolean setProperty(String propertyName, Object value, Object2d currentObject) {
    
    if ("script" == propertyName) {
      if (currentObject != null)
        currentObject.setScript((String) value);
      return true;
    }

    if ("xpos" == propertyName) {
      if (currentObject != null)
        currentObject.setMovableX(((Integer) value).intValue());
      return true;
    }

    if ("ypos" == propertyName) {
      if (currentObject != null)
        currentObject.setMovableY(((Integer) value).intValue());
      return true;
    }

    if ("%xpos" == propertyName) {
      if (currentObject != null)
        currentObject.setMovableXPercent(((Integer) value).intValue());
      return true;
    }

    if ("%ypos" == propertyName) {
      if (currentObject != null)
        currentObject.setMovableYPercent(((Integer) value).intValue());
      return true;
    }
    
    if ("%zpos" == propertyName) {
      if (currentObject != null)
        currentObject.setMovableZPercent(((Integer) value).intValue());
      return true;
    }
    
    if ("xypos" == propertyName) {
      if (currentObject == null)
        return true;
      Point3f pt = (Point3f) value;
      currentObject.setXYZ(null);
      if (pt.z == Float.MAX_VALUE) {
        currentObject.setMovableX((int) pt.x);        
        currentObject.setMovableY((int) pt.y);        
      } else {
        currentObject.setMovableXPercent((int) pt.x);        
        currentObject.setMovableYPercent((int) pt.y);        
      }
      return true;
    }
    
    if ("xyz" == propertyName) {
      if (currentObject != null) {
        currentObject.setXYZ((Point3f) value);
      }
      return true;
    }
    
    return false;
  }

  public static int getOffset(int xOffset, int yOffset) {
    xOffset = Math.min(Math.max(xOffset, -127), 127);
    yOffset = Math.min(Math.max(yOffset, -127), 127);
    return ((xOffset & 0xFF) << 8) | (yOffset & 0xFF);
  }
  
}
