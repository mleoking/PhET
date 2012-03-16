// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.tutorial.unit2;

import edu.colorado.phet.selfdrivenparticlemodel.tutorial.BasicTutorialCanvas;
import edu.colorado.phet.selfdrivenparticlemodel.tutorial.Page;

public class InitEmergence00 extends Page {
    public InitEmergence00( BasicTutorialCanvas page ) {
        super( page );
        setText( "Welcome to Unit II: Emergent properties of the Self Driven Particle Model." );

    }

    public void init() {
        super.init();
        artificialAdvance();
        showNextButton();

    }

    public void relayoutChildren() {
        super.relayoutChildren();
    }
}
