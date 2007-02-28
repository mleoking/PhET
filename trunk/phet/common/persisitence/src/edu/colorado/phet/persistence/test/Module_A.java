/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.persistence.test;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;

import java.awt.*;
import java.io.Serializable;

/**
 * Module_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Module_A extends Module implements Serializable {

    public Module_A( ApplicationModel appModel ) {
        super( "A" );

        // Create the model
        BaseModel model = new TestModel();
        setModel( model );

        // Create the apparatus panel
        ApparatusPanel ap = new ApparatusPanel();
//        ApparatusPanel2 ap = new ApparatusPanel2( model, appModel.getClock() );
        setApparatusPanel( ap );
//        ap.setUseOffscreenBuffer( true );

        // Add a particle
        TestParticle particle = new TestParticle();
        particle.setInitialPosition( 200, 200 );
        model.addModelElement( particle );
        PhetGraphic particleGraphic = new ParticleGraphic( ap, particle, Color.red );
        ap.addGraphic( particleGraphic );
    }
}


