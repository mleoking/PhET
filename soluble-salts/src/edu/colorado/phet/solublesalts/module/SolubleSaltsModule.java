/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.module;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.MouseTracker;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.util.PiccoloUtils;
import edu.colorado.phet.piccolo.util.PMouseTracker;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.*;
import edu.colorado.phet.solublesalts.view.*;
import edu.colorado.phet.solublesalts.view.charts.Concentrations;
import edu.colorado.phet.solublesalts.view.charts.ConcentrationsSgt;
import edu.colorado.phet.solublesalts.view.charts.ConcentrationsPTPlot2;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PCanvas;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * SolubleSaltsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModule extends PiccoloModule {

    Random random = new Random( System.currentTimeMillis() );
    private PhetPCanvas simPanel;

    public SolubleSaltsModule( AbstractClock clock ) {
        super( SimStrings.get( "Module.title" ), clock );

        // Set up the basics
        final SolubleSaltsModel model = new SolubleSaltsModel();
        setModel( model );
        simPanel = new PhetPCanvas( new Dimension( 900, 600 ) );
        setPhetPCanvas( simPanel );

        // Make a graphic for the un-zoomed setup, and add it to the canvax
        PNode fullScaleCanvas = new WorldCanvas( model, simPanel );
        simPanel.addWorldChild( fullScaleCanvas );

        // Add another apparatus panel for the magnifier
        {
            final WorldCanvas magnifierCanvas = new WorldCanvas( model, simPanel );
            magnifierCanvas.addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    double dx = event.getDelta().getWidth();
                    double dy = event.getDelta().getHeight();
                    magnifierCanvas.translate( dx, dy );
                }
            } );

            double scale = 0.5;
//        magnifierCanvas.setScale( 0.5 );
            PNode magnifier = new PNode();
            PPath background = new PPath( new Rectangle2D.Double( 0,
                                                                  0,
                                                                  simPanel.getSize().getWidth() * scale,
                                                                  simPanel.getSize().getHeight() * scale ) );
            background.setPaint( Color.white );
            magnifier.addChild( background );
            magnifier.addChild( magnifierCanvas );
            RegisterablePNode magnifierNode = new RegisterablePNode( magnifierCanvas );
            magnifierNode.setOffset( 450, 300 );
            magnifierNode.setScale( 0.5 );
            magnifierNode.setRegistrationPoint( magnifierNode.getFullBounds().getWidth() / 2, magnifierNode.getFullBounds().getHeight() / 2 );
//        simPanel.addWorldChild( magnifierNode );
        }

        // Add a graphic manager to the model that will create and remove IonGraphics
        // when Ions are added to and removed from the model
        model.addIonListener( new IonGraphicManager2( fullScaleCanvas ) );
//        model.addIonListener( new IonGraphicManager2( magnifierCanvas ) );

        Ion ion = null;

        ion = new Chloride();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 130, 200 );
        ion.setVelocity( 0, 5 );
//        model.addModelElement( ion );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 70, 230 );
        ion.setVelocity( 5, 0 );
//        model.addModelElement( ion );

        ion = new Sodium();
        IonInitializer.initialize( ion, model );
        ion.setPosition( 600, 435 );
        ion.setVelocity( 5, 0 );
//        model.addModelElement( ion );

//        ion = new Chloride();
//        IonInitializer.initialize( ion, model );
//        ion.setPosition( 280, 200 );
//        ion.setVelocity( 0, 5 );
//        model.addModelElement( ion );


        // Set up the control panel
        setControlPanel( new SolubleSaltsControlPanel( this ) );


//        TestGraphic tg = new TestGraphic();
//        tg.setScale( 2 );
//        tg.setOffset( 500, 500 );
////        tg.setScale( 2 );
//        simPanel.addWorldChild( tg );
//
//        PPath pp = new PPath( new Ellipse2D.Double(-2,-2,4,4));
//        pp.setOffset( tg.getOffset() );
//        pp.setPaint( Color.red );
//        simPanel.addWorldChild( pp );
//        PPath pp2 = new PPath( new Ellipse2D.Double(-2,-2,4,4));
//        pp2.setOffset( 500, 500 );
//        pp2.setPaint( Color.green );
//        simPanel.addWorldChild( pp2 );

    }

    private void test() {
        // A test graphic
        Rectangle r = new Rectangle( 100, 150, 20, 70 );
        PPath pp = new PPath( r );
        pp.setPaint( Color.red );
        simPanel.addWorldChild( pp );
        pp.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        pp.addInputEventListener( new PDragEventHandler() );
    }


    class TestGraphic extends RegisterablePNode {

        public TestGraphic() {

            PPath pPath = new PPath( new Rectangle2D.Double( 0, 0, 60, 30 ) );
            pPath.setPaint( Color.cyan );
            setRegistrationPoint( pPath.getWidth() / 3, pPath.getHeight() / 3 );
            addChild( pPath );

        }
    }

}
