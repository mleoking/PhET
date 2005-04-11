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
import edu.colorado.phet.common.application.StateDescriptor;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;
import edu.colorado.phet.persistence.test.controller.Module_B_controlPanel;

import java.awt.geom.Ellipse2D;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;

/**
 * Module_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Module_B extends Module implements Serializable {
    private int lastX = 0;

    public Module_B( ApplicationModel appModel ) {
        super( "B" );

        // Create the model
        BaseModel model = new TestModel();
        setModel( model );

        // Create the apparatus panel
        ApparatusPanel2 ap = new ApparatusPanel2( model, appModel.getClock() );
        setApparatusPanel( ap );
        ap.setUseOffscreenBuffer( true );

        // Set the control panel
        setControlPanel( new Module_B_controlPanel( this ) );

        // Add a particle
        addParticle();
    }

    private int getNextX() {
        lastX += 100;
        return lastX;
    }

    /////////////////////////////////////////////////////////////////////
    // Add/remove elements from the system
    //
    public void addParticle() {
        TestParticle particle = new TestParticle();
        particle.setInitialPosition( getNextX(), 100 );
        getModel().addModelElement( particle );
        PhetGraphic particleGraphic = new ParticleGraphic( getApparatusPanel(), particle, Color.blue );
        getApparatusPanel().addGraphic( particleGraphic );
    }


    //////////////////////////////////////////////////////////
    // Persistence
    //
    public StateDescriptor getStateDescriptor() {
        MyStateDescriptor mySd = new MyStateDescriptor( this );
        mySd.setLastX( lastX );
        return mySd;
    }

    public void restoreState( StateDescriptor sd ) {
        super.restoreState( sd );
        lastX = ( (MyStateDescriptor)sd ).getLastX();
    }

    //////////////////////////////////////////////////////////
    // Inner classes
    //
    public static class MyStateDescriptor extends StateDescriptor {
        private int lastX;

        public MyStateDescriptor() {
        }

        protected MyStateDescriptor( Module_B module ) {
            super( module );
        }

        public int getLastX() {
            return lastX;
        }

        public void setLastX( int lastX ) {
            this.lastX = lastX;
        }
    }
}


