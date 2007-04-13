package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.view.piccolo.QWIScreenNode;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * User: Sam Reid
 * Date: Jul 14, 2006
 * Time: 12:17:14 PM
 * Copyright (c) Jul 14, 2006 by Sam Reid
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
