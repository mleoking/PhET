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
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.tests.graphics.TestPhetGraphics;
import edu.colorado.phet.persistence.test.view.ParticleGraphic;
import edu.colorado.phet.persistence.test.view.SimpleGraphic;
import edu.colorado.phet.persistence.test.model.TestModel;
import edu.colorado.phet.persistence.test.model.TestParticle;
import edu.colorado.phet.common.util.persistence.PersistentGeneralPath;
import edu.colorado.phet.common.util.persistence.PersistentStroke;
import edu.colorado.phet.common.util.persistence.*;

import javax.swing.event.MouseInputAdapter;
import javax.swing.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.Serializable;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

/**
 * Module_A
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleGraphicModule extends Module implements Serializable {

    public SimpleGraphicModule( ApplicationModel appModel ) {
        super( "D" );

        // Create the model
        BaseModel model = new TestModel();
        appModel.getClock().addClockTickListener( model );
        setModel( model );

        // Create the apparatus panel
        ApparatusPanel2 panel = new ApparatusPanel2( model, appModel.getClock() );
//        panel.setUseOffscreenBuffer( true );
        setApparatusPanel( panel );

        TestSaveState.addDebug( panel );

        panel.addGraphicsSetup( new BasicGraphicsSetup() );

        SimpleGraphic sg = new SimpleGraphic( panel );
        sg.setLocation( 100, 50 );
        CompositePhetGraphic cpg3 = new CompositePhetGraphic( panel );
        cpg3.addGraphic( sg );
        addGraphicToPanel( cpg3, panel );

        return;
    }

    public void addGraphicToPanel( final PhetGraphic pg, ApparatusPanel panel ) {
        pg.setCursorHand();
        pg.setBoundsDirty();
        pg.setLocation( 0, 0 );
        Rectangle bounds = pg.getBounds();
        if( bounds == null ) {
            System.out.println( "error" );
        }
        Point center = RectangleUtils.getCenter( pg.getBounds() );
        pg.setRegistrationPoint( center.x, center.y );
        pg.addMouseInputListener( new Rotator( pg ) );
        pg.addTranslationListener( new Translator( pg ) );
        panel.addGraphic( pg );

    }


    //////////////////////////////
    // Inner classes
    //
    public static class Rotator extends MouseInputAdapter {
        private PhetGraphic pg;

        public Rotator() {
        }

        public Rotator( PhetGraphic pg ) {
            this.pg = pg;
        }

        public void mouseDragged( MouseEvent e ) {
            if( SwingUtilities.isRightMouseButton( e ) ) {
                pg.rotate( Math.PI / 64 );
            }
        }

        public PhetGraphic getPg() {
            return pg;
        }

        public void setPg( PhetGraphic pg ) {
            this.pg = pg;
        }
    }


    public static class Translator implements TranslationListener {
        private PhetGraphic pg;

        public Translator() {
        }

        public Translator( PhetGraphic pg ) {
            this.pg = pg;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            if( SwingUtilities.isLeftMouseButton( translationEvent.getMouseEvent() ) ) {
                pg.setLocation( translationEvent.getX(), translationEvent.getY() );
            }
        }

        public PhetGraphic getPg() {
            return pg;
        }

        public void setPg( PhetGraphic pg ) {
            this.pg = pg;
        }
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        TestSaveState.addDebug( getApparatusPanel() );
    }
}




