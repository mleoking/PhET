/**
 * Class: DipoleStripChartSubject
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwave.view;

import edu.colorado.phet.coreadditions.chart.StripChartSubject;
import edu.colorado.phet.microwave.model.PolarBody;

public class DipoleStripChartSubject extends StripChartSubject {

    PolarBody dipole;

    public DipoleStripChartSubject( PolarBody dipole ) {
        this.dipole = dipole;
        dipole.addObserver( this );
    }

    public double getDatum() {
        return dipole.getDipoleOrientation();
    }
}
