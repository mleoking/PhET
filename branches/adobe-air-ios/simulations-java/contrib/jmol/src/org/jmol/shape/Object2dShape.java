package org.jmol.shape;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.jmol.g3d.Font3D;
import org.jmol.util.Logger;
import org.jmol.util.Point3fi;
import org.jmol.util.TextFormat;
import org.jmol.viewer.Viewer;

public class Object2dShape extends Shape {

  // Echo, Hover, JmolImage

  Map<String, Text> objects = new Hashtable<String, Text>();
  Object2d currentObject;
  Font3D currentFont;
  Object currentColor;
  Object currentBgColor;
  float currentTranslucentLevel;
  float currentBgTranslucentLevel;
  protected String thisID;
  
  boolean isHover;
  boolean isAll;

  @Override
  public void setProperty(String propertyName, Object value, BitSet bsSelected) {

    if ("allOff" == propertyName) {
      currentObject = null;
      isAll = true;
      objects = new Hashtable<String, Text>();
      return;
    }

    if ("delete" == propertyName) {
      if (currentObject == null) {
        if (isAll || thisID != null) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            Text text = e.next();
            if (isAll
                || TextFormat.isMatch(text.target.toUpperCase(), thisID, true,
                    true)) {
              e.remove();
            }
          }
        }
        return;
      }
      objects.remove(currentObject.target);
      currentObject = null;
      return;
    }

    if ("off" == propertyName) {
      if (isAll) {
        objects = new Hashtable<String, Text>();
        isAll = false;
        currentObject = null;
      }
      if (currentObject == null) {
        return;
      }

      objects.remove(currentObject.target);
      currentObject = null;
      return;
    }

    if ("model" == propertyName) {
      int modelIndex = ((Integer) value).intValue();
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setModel(modelIndex);
          }
        }
        return;
      }
      currentObject.setModel(modelIndex);
      return;
    }

    if ("align" == propertyName) {
      String align = (String) value;
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setAlignment(align);
          }
        }
        return;
      }
      if (!currentObject.setAlignment(align))
        Logger.error("unrecognized align:" + align);
      return;
    }

    if ("bgcolor" == propertyName) {
      currentBgColor = value;
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setBgColix(value);
          }
        }
        return;
      }
      currentObject.setBgColix(value);
      return;
    }

    if ("color" == propertyName) {
      currentColor = value;
      if (currentObject == null) {
        if (isAll || thisID != null) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            Text text = e.next();
            if (isAll
                || TextFormat.isMatch(text.target.toUpperCase(), thisID, true,
                    true)) {
              text.setColix(value);
            }
          }
        }
        return;
      }
      currentObject.setColix(value);
      return;
    }

    if ("target" == propertyName) {
      String target = (String) value;
      isAll = target.equals("all");
      if (isAll || target.equals("none")) {
        currentObject = null;
      }
      //handled by individual types -- echo or hover
      return;
    }

    boolean isBackground;
    if ((isBackground = ("bgtranslucency" == propertyName))
        || "translucency" == propertyName) {
      boolean isTranslucent = ("translucent" == value);
      if (isBackground)
        currentBgTranslucentLevel = (isTranslucent ? translucentLevel : 0);
      else
        currentTranslucentLevel = (isTranslucent ? translucentLevel : 0);
      if (currentObject == null) {
        if (isAll) {
          Iterator<Text> e = objects.values().iterator();
          while (e.hasNext()) {
            e.next().setTranslucent(translucentLevel, isBackground);
          }
        }
        return;
      }
      currentObject.setTranslucent(translucentLevel, isBackground);
      return;
    }

    if (propertyName == "deleteModelAtoms") {
      int modelIndex = ((int[]) ((Object[]) value)[2])[0];
      Iterator<Text> e = objects.values().iterator();
      while (e.hasNext()) {
        Text text = e.next();
        if (text.modelIndex == modelIndex) {
          e.remove();
        } else if (text.modelIndex > modelIndex) {
          text.modelIndex--;
        }
      }
      return;
    }

    super.setProperty(propertyName, value, bsSelected);
  }

  @Override
  protected void initModelSet() {
    currentObject = null;
    isAll = false;
  }


  @Override
  public void setVisibilityFlags(BitSet bs) {
    if (isHover) {
      return;
    }
    Iterator<Text> e = objects.values().iterator();
    while (e.hasNext()) {
      Text t = e.next();
      t.setVisibility(t.modelIndex < 0 || bs.get(t.modelIndex));
    }
  }

  @Override
  public Point3fi checkObjectClicked(int x, int y, int modifiers, BitSet bsVisible) {
    if (isHover) {
      return null;
    }
    Iterator<Text> e = objects.values().iterator();
    while (e.hasNext()) {
      Object2d obj = e.next();
      if (obj.checkObjectClicked(x, y, bsVisible)) {
        String s = obj.getScript();
        if (s != null) {
          viewer.evalStringQuiet(s);
        }
        Point3fi pt = new Point3fi();
        if (obj.xyz != null) {
          pt.set(obj.xyz);
          pt.modelIndex = (short) obj.modelIndex;
        }
        return pt; // may or may not be null
      }
    }
    return null;
  }

  @Override
  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    if (isHover)
      return false;
    Iterator<Text> e = objects.values().iterator();
    boolean haveScripts = false;
    while (e.hasNext()) {
      Object2d obj = e.next();
      String s = obj.getScript();
      if (s != null) {
        haveScripts = true;
        if (obj.checkObjectClicked(x, y, bsVisible)) {
          viewer.setCursor(Viewer.CURSOR_HAND);
          return true;
        }
      }
    }
    if (haveScripts)
      viewer.setCursor(Viewer.CURSOR_DEFAULT);
    return false;
  }


}
