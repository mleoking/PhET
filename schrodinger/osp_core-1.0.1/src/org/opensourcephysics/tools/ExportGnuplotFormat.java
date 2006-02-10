/*
 * Open Source Physics software is free software as described near the bottom of this code file.
 *
 * For additional information and documentation on Open Source Physics please see: 
 * <http://www.opensourcephysics.org/>
 */

package org.opensourcephysics.tools;
import java.io.IOException;
import java.io.FileWriter;
import javax.swing.JOptionPane;
import org.opensourcephysics.display.Dataset;
import java.io.File;
import java.util.List;
import org.opensourcephysics.display2d.GridData;
import java.util.Iterator;
import java.io.PrintWriter;

/**
* Text format, compatible with Octave and Gnuplot
*
* @author Kipton Barros
* @version 1.0
*/
public class ExportGnuplotFormat implements ExportFormat {
  public String description() {
    return "Text";
  }

  public String extension() {
    return "txt";
  }

  /*
   * Writes indexed x, y data as a (n, 2) matrix, in text format.
   *
   * @param  file
   */
  void exportDataset(PrintWriter pw, Dataset data, int index) throws IOException {
    double[] x = data.getXPoints();
    double[] y = data.getYPoints();
    pw.print("\n# name: data"+index+"\n"+"# type: matrix\n"+"# rows: "+x.length+"\n"+"# columns: "+2+"\n");
    for(int j = 0;j<x.length;j++) {
      pw.println(x[j]+" "+y[j]);
    }
  }

  void exportGridData(PrintWriter pw, GridData gridData, int index) throws IOException {
    // double[][] data = gridData.getData()[0];
    int nx = gridData.getNx(); // data.length;
    int ny = gridData.getNy(); // data[0].length;
    double x0 = gridData.getLeft();
    // double x1 = gridData.getRight();
    double dx = gridData.getDx();
    pw.println("\n# name: col_range"+index+"\n"+"# type: matrix\n"+"# rows: 1\n"+"# columns: "+nx);
    for(int i = 0;i<nx;i++) {
      pw.print((x0+i*dx)+" ");
    }
    pw.println("\n");
    double y0 = gridData.getTop();
    // double y1 = gridData.getBottom();
    double dy = gridData.getDy();
    pw.println("# name: row_range"+index+"\n"+"# type: matrix\n"+"# rows: 1\n"+"# columns: "+ny);
    for(int i = 0;i<ny;i++) {
      pw.print((y0+i*dy)+" ");
    }
    pw.println("\n");
    int nc = gridData.getComponentCount(); // number of components
    // added by W. Christian
    for(int c = 0;c<nc;c++) { // iterate over the number of data components in the grid
      String cname = gridData.getComponentName(c);
      pw.println("# name: grid_"+index+'_'+cname+'\n'+"# type: matrix\n"+"# rows: "+ny+'\n'+"# columns: "+nx);
      for(int i = 0;i<ny;i++) {
        for(int j = 0;j<nx;j++) {
          // pw.print(data[j][i] + " ");  // removed by W. Christian
          // the getValue method works for all types of grid data
          pw.print(gridData.getValue(j, i, c)+" ");
        }
        pw.println();
      }
    }
  }

  public void export(File file, List data) {
    try {
      FileWriter fw = new FileWriter(file);
      PrintWriter pw = pw = new PrintWriter(fw);
      pw.println("# Created by the Open Source Physics library");
      Iterator it = data.iterator();
      for(int i = 0;it.hasNext();i++) {
        Object o = it.next();
        if(o instanceof Dataset) {
          exportDataset(pw, (Dataset) o, i);
        } else if(o instanceof GridData) {
          exportGridData(pw, (GridData) o, i);
        }
      }
      pw.close();
    } catch(IOException e) {
      JOptionPane.showMessageDialog(null, "An error occurred while saving your file. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
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
