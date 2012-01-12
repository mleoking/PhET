/* $RCSfile$
 * $Author: hansonr $
 * $Date: 2006-09-26 01:48:23 -0500 (Tue, 26 Sep 2006) $
 * $Revision: 5729 $
 *
 * Copyright (C) 2005  Miguel, Jmol Development, www.jmol.org
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

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Escape;
import org.jmol.util.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import javax.vecmath.Point3f;

/**
 * DCD binary trajectory file reader.
 * see http://www.ks.uiuc.edu/Research/vmd/plugins/molfile/dcdplugin.html
 * and http://www.ks.uiuc.edu/Research/namd/mailing_list/namd-l/5651.html
 * Bob Hanson 2/18/2011
 * 
 * requires PDB file
 * 
 *  load trajectory "c:/temp/t.pdb" coord "c:/temp/t.dcd"
 * 
 */

public class BinaryDcdReader extends BinaryReader {

  private List<Point3f[]> trajectorySteps;

  @SuppressWarnings("unchecked")
  @Override
  protected void initializeReader() {
    // add a dummy atom, just so not "no atoms found"
    atomSetCollection.addAtom(new Atom());
    trajectorySteps = (List<Point3f[]>) htParams.get("trajectorySteps");
    if (trajectorySteps == null)
      htParams.put("trajectorySteps", trajectorySteps = new ArrayList<Point3f[]>());
  }


  /*
  There's a description of the X-PLOR variation of the DCD format here:

  The DCD format is structured as follows (FORTRAN UNFORMATTED, with Fortran data type descriptions):

  HDR     NSET    ISTRT   NSAVC   5-ZEROS NATOM-NFREAT    DELTA   9-ZEROS
  `CORD'  #files  step 1  step    zeroes  (zero)          timestep  (zeroes)
                          interval
  C*4     INT     INT     INT     5INT    INT             DOUBLE  9INT
  ==========================================================================
  NTITLE          TITLE
  INT (=2)        C*MAXTITL
                  (=32)
  ==========================================================================
  NATOM
  #atoms
  INT
  ==========================================================================
  X(I), I=1,NATOM         (DOUBLE)
  Y(I), I=1,NATOM         
  Z(I), I=1,NATOM         
  ==========================================================================
 T\0 \0\0 
5400 0000 4-byte HEADER
C O  R D  
434F 5244 

 Where HDR = `CORD' or `VELD' for coordinates and velocities, respectively:

ICNTRL(1)=NFILE ! number of frames in trajectory file
ICNTRL(2)=NPRIV ! number of steps in previous run
ICNTRL(3)=NSAVC ! frequency of saving
ICNTRL(4)=NSTEP ! total number of steps
NFILE=NSTEP/NSAVC
ICNTRL(8)=NDEGF ! number of degrees of freedom
ICNTRL(9)=NATOM-NFREAT ! number of fixed atoms
ICNTRL(10)=DELTA4 ! coded time step
ICNTRL(11)=stoi(XTLTYP,ALPHABET) ! coded crystallographic
! group (or zero)
ICNTRL(20)=VERNUM ! version number 
   */

  private int nModels;
  private int nAtoms;
  private int nFree;
  private BitSet bsFree;
  private float[] xAll, yAll, zAll;
  

  @Override
  protected void readDocument() throws Exception {
    byte[] bytes = new byte[40];
    
    // read DCD header
    
    int n = doc.readInt(); 
    doc.setIsBigEndian(n != 0x54);
    n = doc.readInt(); // "CORD"
    nModels = doc.readInt();
    /* int nPriv = */ doc.readInt();
    /* int nSaveC = */ doc.readInt();
    /* int nStep = */ doc.readInt();
    doc.readInt();
    doc.readInt();
    doc.readInt();
    int ndegf = doc.readInt();
    nFree = ndegf / 3;
    int nFixed = doc.readInt();
    /* int delta4 = */ doc.readInt();
    doc.readByteArray(bytes, 0, 36);
    /* int nTitle = */ doc.readInt();
    n = doc.readInt();  // TRAILER
    
    // read titles
    
    n = doc.readInt();  // HEADER
    n = doc.readInt();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < n; i++)
      sb.append(doc.readString(80).trim()).append('\n');
    n = doc.readInt(); // TRAILER
    Logger.info("BinaryDcdReadaer:\n" + sb);

    // read number of atoms and free-atom list
    
    n = doc.readInt(); // HEADER
    nAtoms = doc.readInt();
    n = doc.readInt(); // TRAILER
    nFree = nAtoms - nFixed;
    if (nFixed != 0) {
      // read list of free atoms
      doc.readInt(); // HEADER
      bsFree = new BitSet(nFree);
      for (int i = 0; i < nFree; i++)
        bsFree.set(doc.readInt() - 1);
      n = doc.readInt() / 4; // TRAILER
      Logger.info("free: " + bsFree.cardinality() + " " + Escape.escape(bsFree));
    }
    
    readCoordinates();
    
    Logger.info("Total number of trajectory steps=" + trajectorySteps.size());
  }

  private float[] readFloatArray() throws Exception {
    int n = doc.readInt() / 4; // HEADER
    float[] data = new float[n];
    for (int i = 0; i < n; i++)
      data[i] = doc.readFloat();
    n = doc.readInt() / 4; // TRAILER
    if (Logger.debugging)
      System.out.println(modelNumber + " " + doc.getPosition() + ": " + n + " " + data[0]+ "\t" + data[1]+ "\t" + data[2]);
    return data;
  }

  private void readCoordinates() throws Exception {
    int atomCount = (bsFilter == null ? templateAtomCount : ((Integer) htParams
        .get("filteredAtomCount")).intValue());
    for (int i = 0; i < nModels; i++)
      if (doGetModel(++modelNumber)) {
        Point3f[] trajectoryStep = new Point3f[atomCount];
        if (!getTrajectoryStep(trajectoryStep))
          return;
        trajectorySteps.add(trajectoryStep);
        if (isLastModel(modelNumber))
          return;
      } else {
        readFloatArray();
        readFloatArray();
        readFloatArray();
      }
  }

  private boolean getTrajectoryStep(Point3f[] trajectoryStep)
      throws Exception {
    try {
    int atomCount = trajectoryStep.length;
    int n = -1;
    float[] x = readFloatArray();
    float[] y = readFloatArray();
    float[] z = readFloatArray();
    BitSet bs = (xAll == null ? null : bsFree);
    if (bs == null) {
      xAll = x;
      yAll = y;
      zAll = z;
    }
    for (int i = 0, vpt = 0; i < nAtoms; i++) {
      Point3f pt = new Point3f();
      if (bs == null || bs.get(i)) {
        pt.set(x[vpt], y[vpt], z[vpt]);
        vpt++;
      } else {
        pt.set(xAll[i], yAll[i], zAll[i]);
      }
      if (bsFilter == null || bsFilter.get(i)) {
        if (++n == atomCount)
          return true;
        trajectoryStep[n] = pt;
      }
    }
    return true;
    } catch (Exception e) {
      return false;
    }
  }

}
