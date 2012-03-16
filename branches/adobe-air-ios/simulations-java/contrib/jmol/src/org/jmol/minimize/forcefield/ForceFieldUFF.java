/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2007-11-23 12:49:25 -0600 (Fri, 23 Nov 2007) $
 * $Revision: 8655 $
 *
 * Copyright (C) 2003-2005  The Jmol Development Team
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

package org.jmol.minimize.forcefield;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jmol.minimize.Minimizer;
import org.jmol.util.Logger;
import org.jmol.util.Parser;


public class ForceFieldUFF extends ForceField {

  
  @Override
  public void setModel(Minimizer m) {
    super.setModel(m);
    calc = new CalculationsUFF(this, m.minAtoms, m.minBonds, 
        m.angles, m.torsions, m.partialCharges, m.constraints);
  }

  @Override
  protected Map<String, FFParam> getFFParameters() {
    FFParam ffParam;

    Map<String, FFParam> temp = new Hashtable<String, FFParam>();

    // open UFF.txt
    URL url = null;
    String fileName = "UFF.txt";
    BufferedReader br = null;
    try {
      if ((url = this.getClass().getResource(fileName)) == null) {
        System.err.println("Couldn't find file: " + fileName);
        throw new NullPointerException();
      }
      //turns out from the Jar file
      // it's a sun.net.www.protocol.jar.JarURLConnection$JarURLInputStream
      // and within Eclipse it's a BufferedInputStream

      br = new BufferedReader(new InputStreamReader(
          (InputStream) url.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
        String[] vs = Parser.getTokens(line);
        if (vs.length < 13)
          continue;
        if (Logger.debugging)
          Logger.info(line);
        if (line.substring(0, 5).equals("param")) {
          // set up all params from this
          ffParam = new FFParam();
          temp.put(vs[1], ffParam);
          ffParam.dVal = new double[11];
          ffParam.sVal = new String[1];
          ffParam.sVal[0] = vs[1]; // atom type
          
          ffParam.dVal[CalculationsUFF.PAR_R] = Parser.parseFloat(vs[2]); // r1
          ffParam.dVal[CalculationsUFF.PAR_THETA] = Parser.parseFloat(vs[3]) 
             * Calculations.DEG_TO_RAD; // theta0(radians)
          ffParam.dVal[CalculationsUFF.PAR_X] = Parser.parseFloat(vs[4]); // x1
          ffParam.dVal[CalculationsUFF.PAR_D] = Parser.parseFloat(vs[5]); // D1
          ffParam.dVal[CalculationsUFF.PAR_ZETA] = Parser.parseFloat(vs[6]); // zeta
          ffParam.dVal[CalculationsUFF.PAR_Z] = Parser.parseFloat(vs[7]); // Z1
          ffParam.dVal[CalculationsUFF.PAR_V] = Parser.parseFloat(vs[8]); // Vi
          ffParam.dVal[CalculationsUFF.PAR_U] = Parser.parseFloat(vs[9]); // Uj
          ffParam.dVal[CalculationsUFF.PAR_XI] = Parser.parseFloat(vs[10]); // Xi
          ffParam.dVal[CalculationsUFF.PAR_HARD] = Parser.parseFloat(vs[11]); // Hard
          ffParam.dVal[CalculationsUFF.PAR_RADIUS] = Parser.parseFloat(vs[12]); // Radius
          
          ffParam.iVal = new int[1];

          char coord = (vs[1].length() > 2 ? vs[1].charAt(2) : '1'); // 3rd character of atom type

          switch (coord) {
          case 'R':
            coord = '2';
            break;
          default: // general case (unknown coordination)
            // These atoms appear to generally be linear coordination like Cl
            coord = '1';
            break;
          case '1': // linear
          case '2': // trigonal planar (sp2)
          case '3': // tetrahedral (sp3)
          case '4': // square planar
          case '5': // trigonal bipyramidal -- not actually in parameterization
          case '6': // octahedral
            break;
          }
          ffParam.iVal[0] = coord - '0';
        }
      }
      br.close();
    } catch (Exception e) {
      System.err.println("Exception " + e.getMessage() + " in getResource "
          + fileName);
      try{
        br.close();
      } catch (Exception ee) {
        
      }
      return null;
    }
    Logger.info(temp.size() + " atom types read from " + fileName);
    return temp;
  }

  @Override
  public List<String[]> getAtomTypes() {
    List<String[]> types = new ArrayList<String[]>(); //!< external atom type rules
    URL url = null;
    String fileName = "UFF.txt";
    try {
      if ((url = this.getClass().getResource(fileName)) == null) {
        System.err.println("Couldn't find file: " + fileName);
        throw new NullPointerException();
      }

      //turns out from the Jar file
      // it's a sun.net.www.protocol.jar.JarURLConnection$JarURLInputStream
      // and within Eclipse it's a BufferedInputStream

      BufferedReader br = new BufferedReader(new InputStreamReader(
          (InputStream) url.getContent()));
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() > 4 && line.substring(0, 4).equals("atom")) {
          String[] vs = Parser.getTokens(line);
          String[] info = new String[] { vs[1], vs[2] };
          types.add(info);
        }
      }

      br.close();
    } catch (Exception e) {
      System.err.println("Exception " + e.getMessage() + " in getResource "
          + fileName);

    }
    Logger.info(types.size() + " force field parameters read");
    return (types.size() > 0 ? types : null);
  }
}
