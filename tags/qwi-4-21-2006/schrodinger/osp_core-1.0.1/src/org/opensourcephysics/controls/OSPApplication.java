/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see:
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.controls;

/**
 *  An OSPApplication defines a model and a control.
 *
 * @author Douglas Brown
 * @version 1.0
 */
public class OSPApplication {
  Control control;
  Object model;

  /**
   * Constructs an OSPApplication.
   *
   * @param  control
   * @param  model
   */
  public OSPApplication(Control control, Object model) {
    this.control = control;
    this.model = model;
  }

  /**
   * Returns an XML.ObjectLoader to save and load data for this object.
   *
   * @return the object loader
   */
  public static XML.ObjectLoader getLoader() {
    return new OSPAppLoader();
  }

  /**
   * A class to save and load data for OSPControls.
   */
  static class OSPAppLoader implements XML.ObjectLoader {

    /**
     * Saves object data to an XMLControl.
     *
     * @param xmlControl the xml control to save to
     * @param obj the object to save
     */
    public void saveObject(XMLControl xmlControl, Object obj) {
      OSPApplication app = (OSPApplication) obj;
      xmlControl.setValue("control", app.control);
      xmlControl.setValue("model", app.model);
    }

    /**
     * Creates an object using data from an XMLControl.
     *
     * @param xmlControl the xml control
     * @return the newly created object
     */
    public Object createObject(XMLControl xmlControl) {
      Object model = xmlControl.getObject("model");
      Control control = (Control) xmlControl.getObject("control");
      return new OSPApplication(control, model);
    }

    /**
     * Loads an object with data from an XMLControl.
     *
     * @param xmlControl the control
     * @param obj the object
     * @return the loaded object
     */
    public Object loadObject(XMLControl xmlControl, Object obj) {
      OSPApplication app = (OSPApplication) obj;
      XMLControlElement cControl = (XMLControlElement) xmlControl.getChildControl("control");
      if(cControl!=null) {
        // autoimport data from cControl even if mismatched classes
        cControl.loadObject(app.control, true, true);
      }
      XMLControl modelControl = xmlControl.getChildControl("model");
      if(modelControl!=null) {
        modelControl.loadObject(app.model);
      }
      return app;
    }
  }
}

/*
 * Open Source Physics software is free software; you can redistribute
 * it and/or modify it under the terms of the GNU General Public License (GPL) as
 * published by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.

 * Code that uses any portion of the code in the org.opensourcephysics package
 * or any subpackage (subdirectory) of this package must must also be be released
 * under the GNU GPL license.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston MA 02111-1307 USA
 * or view the license online at http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2007  The Open Source Physics project
 *                     http://www.opensourcephysics.org
 */
