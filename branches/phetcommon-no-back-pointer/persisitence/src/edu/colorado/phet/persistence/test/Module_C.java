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
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;
import edu.colorado.phet.persistence.test.view.SimpleGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;

import javax.swing.*;
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
        SimpleGraphic simpleGraphic = new SimpleGraphic( ap );
        simpleGraphic.setLocation( 50, 50 );
        ap.addGraphic( simpleGraphic );
        if(true)return;


        SimpleGraphic sg1 = new SimpleGraphic( ap );
        sg1.setLocation( 10,10 );

        PhetImageGraphic pig = null;
        try {
            pig = new PhetImageGraphic( ap, "images/Phet-Flatirons-logo-3-small.gif");
            pig.setLocation( 10,10 );
        }
        catch( Exception e ) {
            e.printStackTrace();
        }
        CompositePhetGraphic cpg = new CompositePhetGraphic( ap );
        cpg.addGraphic( pig );
        cpg.addGraphic( sg1 );
        cpg.setLocation( 50, 50 );
//        ap.addGraphic( cpg );

        JPanel panel = ap;
        CompositePhetGraphic cpg2 = new CompositePhetGraphic( panel );
        Ellipse2D.Double elps1 = new Ellipse2D.Double( 130, 30, 30, 30 );
        PhetShapeGraphic sg2 = new PhetShapeGraphic( panel, elps1, Color.red );
        cpg2.addGraphic( sg2 );
        cpg2.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );
        cpg2.setLocation( 100, 100 );

        ap.addGraphic( cpg2 );
    }
}


