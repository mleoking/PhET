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
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.persistence.test.model.TestParticle;
import edu.colorado.phet.common.util.persistence.PersistentPoint2D;
import edu.colorado.phet.common.util.persistence.*;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.util.Vector;

/**
 * TestSaveState
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestSimpleGraphic extends PhetApplication {

    public TestSimpleGraphic( ApplicationModel descriptor ) {
        super( descriptor );
    }


    public static void addDebug( ApparatusPanel ap ) {
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        RepaintDebugGraphic repaintDebugGraphic = new RepaintDebugGraphic( ap, clock, 128 );
        ap.addGraphic( repaintDebugGraphic, Double.POSITIVE_INFINITY );
        clock.start();
        repaintDebugGraphic.setActive( true );
    }


    public static class AppModel extends ApplicationModel {
        public AppModel() {
            super( "Save State Test", "Save State Test", "0.1" );
            AbstractClock clock = new SwingTimerClock( 1, 40 );
            setClock( clock );

            setFrameCenteredSize( 600, 500 );
            Module module = new SimpleGraphicModule( this );
            setModules( new Module[]{
                module,
            } );
            setInitialModule( module );
        }
    }

    public static class MyApp extends PhetApplication {
        public MyApp( ApplicationModel descriptor ) {
            super( descriptor );
        }
    }

    /**
     * Utility to create an XMLEncoder for a file
     *
     * @return
     */
    public static XMLEncoder getTestEncoder() {
        XMLEncoder e = null;
        try {
            e = new XMLEncoder( new FileOutputStream( "/temp/bt.xml" ) );
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        e.setPersistenceDelegate( AffineTransform.class, new AffineTransformPersistenceDelegate() );
        e.setPersistenceDelegate( BasicStroke.class, new BasicStrokePersistenceDelegate() );
        e.setPersistenceDelegate( Ellipse2D.Double.class, new Ellipse2DPersistenceDelegate() );
        e.setPersistenceDelegate( Ellipse2D.Float.class, new Ellipse2DPersistenceDelegate() );
        e.setPersistenceDelegate( GeneralPath.class, new GeneralPathPersistenceDelegate() );
        e.setPersistenceDelegate( GradientPaint.class, new GradientPaintPersistenceDelegate() );
        e.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
        e.setPersistenceDelegate( Point2D.Float.class, new Point2DPersistenceDelegate() );
        e.setPersistenceDelegate( Rectangle2D.Double.class, new Rectangle2DPersistenceDelegate() );
        e.setPersistenceDelegate( Rectangle2D.Float.class, new Rectangle2DPersistenceDelegate() );
        return e;
    }

    /**
     * Utility to create an XMLDecoder for a file
     *
     * @return
     */
    public static XMLDecoder getTestDecoder() {
        XMLDecoder d = null;
        try {
            d = new XMLDecoder( new FileInputStream( "/temp/bt.xml" ) );
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        return d;
    }


    /**
     * Utility test for simple beans
     */
    static void beanTest2() {
        Object po1 = null;

        JPanel p = new JPanel();

        String family = "Serif";
        int style = Font.PLAIN;
        int size = 12;
        Font font = new Font( family, style, size );
        PhetTextGraphic ptg = new PhetTextGraphic( p, font, "FOO", Color.red, 10, 20 );

        po1 = ptg;

        XMLEncoder e = new XMLEncoder( System.out );
        e.writeObject( po1 );
        e.close();

        XMLEncoder e2 = getTestEncoder();
        e2.writeObject( po1 );
        e2.close();
        XMLDecoder d2 = getTestDecoder();
        Object out = d2.readObject();
        d2.close();

        System.out.println( "po1 = " + po1 );
        System.out.println( "out = " + out );
        System.exit( 0 );
    }

    public static void main( String[] args ) {
        ModuleManager.USE_GZIP_STREAMS = false;

//        beanTest2();

        PhetApplication app = new TestSimpleGraphic( new AppModel() );
        PersistenceUtil.addMenuItems( app );
        app.startApplication();
    }
}
