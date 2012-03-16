package org.jmol.api;

import java.util.BitSet;

import java.util.List;

import javax.vecmath.Point3f;


public interface MOCalculationInterface {

  public abstract void calculate(VolumeDataInterface volumeData, BitSet bsSelected,
                                 String calculationType,
                                 Point3f[] atomCoordAngstroms,
                                 int firstAtomOffset, List<int[]> shells,
                                 float[][] gaussians, int[][] dfCoefMaps,
                                 Object slaters,
                                 float[] moCoefficients, 
                                 float[] linearCombination, float[][] coefs,
                                 float[] nuclearCharges, boolean doNormalize);
  
  public abstract void calculateElectronDensity(float[] nuclearCharges);
}
