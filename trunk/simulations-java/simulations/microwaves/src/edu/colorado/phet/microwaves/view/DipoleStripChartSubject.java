/**
 * Class: DipoleStripChartSubject
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 18, 2003
 */
package edu.colorado.phet.microwaves.view;

import edu.colorado.phet.microwaves.coreadditions.chart.StripChartSubject;
import edu.colorado.phet.microwaves.model.PolarBody;

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
