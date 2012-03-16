package org.jmol.api;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.modelset.Atom;


public interface MepCalculationInterface {

  public abstract void calculate(VolumeDataInterface volumeData, BitSet bsSelected,
                                 Point3f[] atomCoordAngstroms, float[] charges, int calcType);

  public abstract void assignPotentials(Atom[] atoms, float[] potentials, BitSet bsAromatic, BitSet bsCarbonyl, BitSet bsIgnore, String data);

  public abstract float valueFor(float x, float d2, int distanceMode);

}
