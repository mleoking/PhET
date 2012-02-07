// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

/**
 * Detects both absorbance and transmittance.
 * If place in the path of the beam between the light and cuvette, measures 0 absorbance and 100% transmittance.
 * Inside the cuvette, there is no measurement.
 * <p>
 * Absorbance (A) is unitless. A = abC.
 * <p>
 * a = molar absorptivity.  Units = M-1cm-1  (Also called e or molar extinction coefficient)
 * This is a measure of the amount of light absorbed per unit concentration at a given wavelength.
 * It is different constant value for each chemical substance.  The relationship between absorbance
 * and concentration is linear (below the detection limit).
 * <p>
 * b = pathlength. Units = cm.
 * The container of solution used in spectrophotometers is called a cuvette.
 * The dimension or cross section of the cuvette that the light passes through is the pathlength.
 * <p>
 * C = concentration. Units = M = mol/L (moles of solute per Liter of solution)
 * <p>
 * A = 2 - log10 %T, where %T is percent transmittance.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AbsorbanceTransmittanceDetector {
}
