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
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;
import edu.colorado.phet.persistence.test.view.SimpleGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;

import java.awt.geom.Ellipse2D;
import java.awt.*;
import java.io.Serializable;

/**
 * Module_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Module_C extends Module implements Serializable {

    public Module_C( ApplicationModel appModel ) {
        super( "C" );

        // Create the model
        BaseModel model = new TestModel();
        setModel( model );

        // Create the apparatus panel
        ApparatusPanel ap = new ApparatusPanel();
        setApparatusPanel( ap );

        // Add a particle
        ap.addGraphic( new SimpleGraphic( ap ) );
    }
}


