/* $RCSfile$
 * $Author: aherraez $
 * $Date: 2009-01-15 21:00:00 +0100 (Thu, 15 Jan 2009) $
 * $Revision: 7752 $

 *
 * Copyright (C) 2003-2009  The Jmol Development Team
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

/*	Based on _VrmlExporter  by rhanson
		and Help from http://x3dgraphics.com/examples/X3dForWebAuthors/index.html
*/
 
package org.jmol.export;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Font3D;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Escape;
import org.jmol.viewer.Viewer;

public class _X3dExporter extends _VrmlExporter {

  public _X3dExporter() {
    useTable = new UseTable("USE='");
  }
  

  @Override
  protected void outputHeader() {
    output("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
    output("<!DOCTYPE X3D PUBLIC \"ISO//Web3D//DTD X3D 3.1//EN\" \"http://www.web3d.org/specifications/x3d-3.1.dtd\">\n");
    output("<X3D profile='Immersive' version='3.1' "
      + "xmlns:xsd='http://www.w3.org/2001/XMLSchema-instance' "
      + "xsd:noNamespaceSchemaLocation=' http://www.web3d.org/specifications/x3d-3.1.xsd '>"
      + "\n");
    output("<head>\n");
    output("<meta name='title' content=" + Escape.escape(viewer.getModelSetName()).replace('<',' ').replace('>',' ').replace('&',' ') + "/>\n");
    output("<meta name='description' content='Jmol rendering'/>\n");
    output("<meta name='creator' content=' '/>\n");
    output("<meta name='created' content='" + getExportDate() + "'/>\n");
    output("<meta name='generator' content='Jmol "+ Viewer.getJmolVersion() +", http://www.jmol.org'/>\n");
		output("<meta name='license' content='http://www.gnu.org/licenses/licenses.html#LGPL'/>\n");
    output("</head>\n");
    output("<Scene>\n");

    output("<NavigationInfo type='EXAMINE'/>\n");
    // puts the viewer into model-rotation mode
    output("<Background skyColor='" 
      + rgbFractionalFromColix(backgroundColix, ' ') + "'/>\n");
    // next is an approximation only 
    float angle = (float) (aperatureAngle * Math.PI / 180);
    viewer.getAxisAngle(viewpoint);
    output("<Viewpoint fieldOfView='" + angle
      + "' position='" + round(cameraPosition)
      + "' orientation='" + viewpoint.x + " " + viewpoint.y + " " 
      + (viewpoint.angle == 0 ? 1 : viewpoint.z) + " " + -viewpoint.angle
      + "'\n jump='TRUE' description='v1'/>\n");
    output("\n  <!-- ");
    output(getJmolPerspective());
    output("\n  -->\n\n");

    output("<Transform translation='");
    tempP1.set(center);
    tempP1.scale(-1);
    output(tempP1);
    output("'>\n");
  }

  @Override
  protected void outputFooter() {
    useTable = null;
    output("</Transform>\n");
    output("</Scene>\n");
    output("</X3D>\n");
  }

  @Override
  protected void outputAppearance(short colix, boolean isText) {  
    String def = useTable.getDef((isText ? "T" : "") + colix);
    output("<Appearance ");
    if (def.charAt(0) == '_') {
      String color = rgbFractionalFromColix(colix, ' ');
      output("DEF='" + def + "'><Material diffuseColor='");
      if (isText)
        output("0 0 0' specularColor='0 0 0' ambientIntensity='0.0' shininess='0.0' emissiveColor='" 
            + color + "'/>");
      else
        output(color + "' transparency='" + translucencyFractionalFromColix(colix) + "'/>" );
    }
    else
      output(def +">");
    output("</Appearance>");
  }
  
  private void outputTransRot(Point3f pt1, Point3f pt2, int x, int y, int z) {    
    output(" ");
    outputTransRot(pt1, pt2, x, y, z, "='", "'");
  }
  
  @Override
  protected void outputCircle(Point3f pt1, Point3f pt2, float radius, short colix,
                              boolean doFill) {
    if (doFill) {

      // draw filled circle
      
      output("<Transform translation='");
      tempV1.set(tempP3);
      tempV1.add(pt1);
      tempV1.scale(0.5f);
      output(tempV1);
      output("'><Billboard axisOfRotation='0 0 0'><Transform rotation='1 0 0 1.5708'>");
      outputCylinderChild(pt1, tempP3, colix, Graphics3D.ENDCAPS_FLAT, radius);
      output("</Transform></Billboard>");
      output("</Transform>\n");
      
      return;
    }
    
    // draw a thin torus

    String child = useTable.getDef("C" + colix + "_" + radius);
    output("<Transform");
    outputTransRot(tempP3, pt1, 0, 0, 1);
    tempP3.set(1, 1, 1);
    tempP3.scale(radius);
    output(" scale='");
    output(tempP3);
    output("'>\n<Billboard ");
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'");
      output(" axisOfRotation='0 0 0'><Transform>");
      output("<Shape><Extrusion beginCap='FALSE' convex='FALSE' endCap='FALSE' creaseAngle='1.57'");
      output(" crossSection='");
      float rpd = 3.1415926f / 180;
      float scale = 0.02f / radius;
      for (int i = 0; i <= 360; i += 10) {
        output(round(Math.cos(i * rpd) * scale) + " ");
        output(round(Math.sin(i * rpd) * scale) + " ");
      }
      output("' spine='");
      for (int i = 0; i <= 360; i += 10) {
        output(round(Math.cos(i * rpd)) + " ");
        output(round(Math.sin(i * rpd)) + " 0 ");
      }
      output("'/>");
      outputAppearance(colix, false);
      output("</Shape></Transform>");
    } else {
      output(child + ">");
    }
    output("</Billboard>\n");
    output("</Transform>\n");
  }

  @Override
  protected void outputCone(Point3f ptBase, Point3f ptTip, float radius,
                            short colix) {
    float height = ptBase.distance(ptTip);
    output("<Transform");
    outputTransRot(ptBase, ptTip, 0, 1, 0);
    output(">\n<Shape ");
    String cone = "o" + (int) (height * 100) + "_" + (int) (radius * 100);
    String child = useTable.getDef("c" + cone + "_" + colix);
    if (child.charAt(0) == '_') {
      output("DEF='" + child +  "'>");
      cone = useTable.getDef(cone);
      output("<Cone ");
      if (cone.charAt(0) == '_') {
        output("DEF='"+ cone + "' height='" + round(height) 
          + "' bottomRadius='" + round(radius) + "'/>");
      } else {
        output(cone + "/>");
      }
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  @Override
  protected boolean outputCylinder(Point3f ptCenter, Point3f pt1, Point3f pt2,
                                short colix, byte endcaps, float radius, Point3f ptX, Point3f ptY) {
    output("<Transform");
    if (ptX == null) {
      outputTransRot(pt1, pt2, 0, 1, 0);
    } else {
      output(" translation='");
      output(ptCenter);
      output("'");
      outputQuaternionFrame(ptCenter, ptY, pt1, ptX, 2, "='", "'");
      pt1.set(0, 0, -1);
      pt2.set(0, 0, 1);
    }
    output(">\n");
    outputCylinderChild(pt1, pt2, colix, endcaps, radius);
    output("\n</Transform>\n");
    if (endcaps == Graphics3D.ENDCAPS_SPHERICAL) {
      outputSphere(pt1, radius * 1.01f, colix);
      outputSphere(pt2, radius * 1.01f, colix);
    }
    return true;
  }

  private void outputCylinderChild(Point3f pt1, Point3f pt2, short colix,
                                   byte endcaps, float radius) {
    float length = pt1.distance(pt2);
    String child = useTable.getDef("C" + colix + "_" + (int) (length * 100) + "_"
        + radius + "_" + endcaps);
    output("<Shape ");
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Cylinder ");
      String cyl = useTable.getDef("c" + round(length) + "_" + endcaps + "_" + radius);
      if (cyl.charAt(0) == '_') {
        output("DEF='"
            + cyl
            + "' height='"
            + round(length)
            + "' radius='"
            + radius
            + "'"
            + (endcaps == Graphics3D.ENDCAPS_FLAT ? ""
                : " top='FALSE' bottom='FALSE'") + "/>");
      } else {
        output(cyl + "/>");
      }
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>");
  }
  
  @Override
  protected void outputEllipsoid(Point3f center, Point3f[] points, short colix) {
    output("<Transform translation='");
    output(center);
    output("'");
    outputQuaternionFrame(center, points[1], points[3], points[5], 1, "='", "'");
    output(">");
    tempP3.set(0, 0, 0);
    outputSphereChild(tempP3, 1.0f, colix);
    output("</Transform>\n");
  }

  @Override
  protected void outputSphereChild(Point3f center, float radius, short colix) {
    output("<Transform translation='");
    output(center);
    output("'>\n<Shape ");
    String child = useTable.getDef("S" + colix + "_" + (int) (radius * 100));
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Sphere radius='" + radius + "'/>");
      outputAppearance(colix, false);
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  @Override
  protected void outputSurface(Point3f[] vertices, Vector3f[] normals,
                               short[] colixes, int[][] indices,
                               short[] polygonColixes,
                               int nVertices, int nPolygons, int nFaces, BitSet bsPolygons,
                               int faceVertexMax, short colix,
                               List<Short> colorList, Map<Short, Integer> htColixes, Point3f offset) {
    output("<Shape>\n");
    outputAppearance(colix, false);
    output("<IndexedFaceSet \n");

    if (polygonColixes != null)
      output(" colorPerVertex='FALSE'\n");

    // coordinates, part 1

    output("coordIndex='\n");
    int[] map = new int[nVertices];
    getCoordinateMap(vertices, map, null);
    outputIndices(indices, map, nPolygons, bsPolygons, faceVertexMax);
    output("'\n");

    // normals, part 1  
    
    List<String> vNormals = null;
    if (normals != null) {
      vNormals = new ArrayList<String>();
      map = getNormalMap(normals, nVertices, null, vNormals);
      output("  solid='FALSE'\n  normalPerVertex='TRUE'\n  normalIndex='\n");
      outputIndices(indices, map, nPolygons, bsPolygons, faceVertexMax);
      output("'\n");
    }      
    
    map = null;
    
    // colors, part 1
        
    if (colorList != null) {
      output("  colorIndex='\n");
      outputColorIndices(indices, nPolygons, bsPolygons, faceVertexMax, htColixes, colixes, polygonColixes);
      output("'\n");
    }    

    output(">\n");  // closes IndexedFaceSet opening tag
    
    // coordinates, part 2
    
    output("<Coordinate point='\n");
    outputVertices(vertices, nVertices, offset);
    output("'/>\n");

    // normals, part 2

    if (normals != null) {
      output("<Normal vector='\n");
      outputNormals(vNormals);
      vNormals = null;
      output("'/>\n");
    }

    // colors, part 2

    if (colorList != null) {
      output("<Color color='\n");
      outputColors(colorList);
      output("'/>\n");
    }
   
    output("</IndexedFaceSet>\n");
    output("</Shape>\n");
    
  }

  @Override
  protected void outputTriangle(Point3f pt1, Point3f pt2, Point3f pt3, short colix) {
    // nucleic base
    // cartoons
    output("<Shape>\n");
    output("<IndexedFaceSet solid='FALSE' ");
    output("coordIndex='0 1 2 -1'>");
    output("<Coordinate point='");
    output(pt1);
    output(" ");
    output(pt2);
    output(" ");
    output(pt3);
    output("'/>");
    output("</IndexedFaceSet>\n");
    outputAppearance(colix, false);
    output("\n</Shape>\n");
  }

  @Override
  protected void outputTextPixel(Point3f pt, int argb) {
    // text only
    String color = rgbFractionalFromArgb(argb, ' ');
    output("<Transform translation='");
    output(pt);
    output("'>\n<Shape ");
    String child = useTable.getDef("p" + argb);
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "'>");
      output("<Sphere radius='0.01'/>");
      output("<Appearance><Material diffuseColor='0 0 0' specularColor='0 0 0'"
        + " ambientIntensity='0.0' shininess='0.0' emissiveColor='" 
        + color + "'/></Appearance>'");
    } else {
      output(child + ">");
    }
    output("</Shape>\n");
    output("</Transform>\n");
  }

  @Override
  void plotText(int x, int y, int z, short colix, String text, Font3D font3d) {
    if (z < 3)
      z = viewer.getFrontPlane();
    String useFontStyle = font3d.fontStyle.toUpperCase();
    String preFontFace = font3d.fontFace.toUpperCase();
    String useFontFace = (preFontFace.equals("MONOSPACED") ? "TYPEWRITER"
        : preFontFace.equals("SERIF") ? "SERIF" : "SANS");
    output("<Transform translation='");
    tempP3.set(x, y, z);
    viewer.unTransformPoint(tempP3, tempP1);
    output(tempP1);
    output("'>");
    // These x y z are 3D coordinates of echo or the atom the label is attached
    // to.
    output("<Billboard ");
    String child = useTable.getDef("T" + colix + useFontFace + useFontStyle + "_" + text);
    if (child.charAt(0) == '_') {
      output("DEF='" + child + "' axisOfRotation='0 0 0'>"
        + "<Transform translation='0.0 0.0 0.0'>"
        + "<Shape>");
      outputAppearance(colix, true);
      output("<Text string=" + Escape.escape(text) + ">");
      output("<FontStyle ");
      String fontstyle = useTable.getDef("F" + useFontFace + useFontStyle);
      if (fontstyle.charAt(0) == '_') {
        output("DEF='" + fontstyle + "' size='0.4' family='" + useFontFace
            + "' style='" + useFontStyle + "'/>");      
      } else {
        output(fontstyle + "/>");
      }
      output("</Text>");
      output("</Shape>");
      output("</Transform>");
    } else {
      output(child + ">");
    }
    output("</Billboard>\n");
    output("</Transform>\n");

    /*
     * Unsolved issues: # Non-label texts: echos, measurements :: need to get
     * space coordinates, not screen coord. # Font size: not implemented; 0.4A
     * is hardcoded (resizes with zoom) Java VRML font3d.fontSize = 13.0 size
     * (numeric), but in angstroms, not pixels font3d.fontSizeNominal = 13.0 #
     * Label offsets: not implemented; hardcoded to 0.25A in each x,y,z #
     * Multi-line labels: only the first line is received # Sub/superscripts not
     * interpreted
     */
  }


}
