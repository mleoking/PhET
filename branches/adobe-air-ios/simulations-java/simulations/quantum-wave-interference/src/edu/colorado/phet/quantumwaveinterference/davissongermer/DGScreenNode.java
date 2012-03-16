// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.QWIScreenNode;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.WavefunctionGraphic;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:17:14 PM
 */

public class DGScreenNode extends QWIScreenNode {
    public DGScreenNode( QWIModule module, DGSchrodingerPanel dgSchrodingerPanel ) {
        super( module, dgSchrodingerPanel );
    }

    protected WavefunctionGraphic createWavefunctionGraphic() {
        return new DGWavefunctionGraphic( getDiscreteModel(), super.getQWIModel().getWavefunction() );
    }

    protected void showZoom( final PImage child, String text ) {
    }
}
