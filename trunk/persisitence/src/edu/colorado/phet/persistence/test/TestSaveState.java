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
public class TestSaveState extends PhetApplication {

    public TestSaveState( ApplicationModel descriptor ) {
        super( descriptor );
    }



    public static void addDebug( ApparatusPanel ap ){
        AbstractClock clock=new SwingTimerClock( 1,30);
                   RepaintDebugGraphic repaintDebugGraphic=new RepaintDebugGraphic( ap, clock,128);
        ap.addGraphic( repaintDebugGraphic,Double.POSITIVE_INFINITY );
        clock.start();
        repaintDebugGraphic.setActive( true );
    }


    public static class AppModel extends ApplicationModel {
        public AppModel() {
            super( "Save State Test", "Save State Test", "0.1" );
            AbstractClock clock = new SwingTimerClock( 1, 40 );
            setClock( clock );

            setFrameCenteredSize( 600, 500 );

            Module_A module1 = new Module_A( this );
            Module_B module2 = new Module_B( this );
            Module_C module3 = new Module_C( this );
            Module_D module4 = new Module_D( this );
            setModules( new Module[]{
                module1,
                module2,
                module3,
                module4,
            } );
            setInitialModule( module4 );
        }
    }

    public static class MyApp extends PhetApplication {
        public MyApp( ApplicationModel descriptor ) {
            super( descriptor );
        }
    }

    static void beanTest() {
        Particle p = new Particle();
        p.setPosition( 100, 50 );
        p.setVelocity( new Vector2D.Double( 1,2 ));
        XMLEncoder e = null;
        e = new XMLEncoder( System.out );
        try {
            e = new XMLEncoder( new FileOutputStream( "/temp/bt.xml"));
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }

        e.writeObject( p );
        e.close();
        Particle p2;
        XMLDecoder d = null;
        try {
            d = new XMLDecoder( new FileInputStream( "/temp/bt.xml"));
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
        p2 = (Particle)d.readObject();
        System.out.println( "p2 = " + p2.getPosition()  + "  " + p2.getVelocity());

        if( true ) {
            System.exit(0);
        }
    }

    public static XMLEncoder getTestEncoder() {
        XMLEncoder e = null;
        try {
            e = new XMLEncoder( new FileOutputStream( "/temp/bt.xml"));
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
        e.setPersistenceDelegate( Point2D.class, new Point2DPersistenceDelegate() );
        e.setPersistenceDelegate( Rectangle2D.class, new Rectangle2DPersistenceDelegate() );
        return e;
    }

    public static XMLDecoder getTestDecoder() {
        XMLDecoder d = null;
        try {
            d = new XMLDecoder( new FileInputStream( "/temp/bt.xml"));
        }
        catch( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
    return d;
    }

    static void beanTest2() {
        Object po1 = null;
        JPanel panel = new JPanel( );
//        CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
//        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 130, 30, 30, 30 ), Color.red ) );
//        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );

        CompositePhetGraphic cpg = new CompositePhetGraphic( panel );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 130, 30, 30, 30 ), Color.red ) );
        cpg.addGraphic( new PhetShapeGraphic( panel, new Ellipse2D.Double( 160, 30, 30, 30 ), Color.blue ) );
        cpg.setLocation( 100, 100 );

        po1 = cpg;

        XMLEncoder e = new XMLEncoder( System.out );
        e.writeObject( po1 );
        e.close();

        XMLEncoder e2 = getTestEncoder();
        e2.writeObject( po1 );
        e2.close();
//        XMLDecoder d2 = getTestDecoder();
//        Ellipse2D out = (Ellipse2D)d2.readObject();
//        d2.close();

        System.exit(0);
    }

    public static void main( String[] args ) {
//        beanTest();
//        beanTest2();

        PhetApplication app = new TestSaveState( new AppModel() );
        JMenuItem mi2 = new JMenuItem( "Restore state" );
        mi2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().getModuleManager().restoreState( "/temp/ttt.xml" );
            }
        } );
        app.getPhetFrame().addFileMenuItem( mi2 );

        JMenuItem mi = new JMenuItem( "Save state" );
        mi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetApplication.instance().getModuleManager().saveState( "/temp/ttt.xml" );
            }
        } );
        app.getPhetFrame().addFileMenuItem( mi );
        app.getPhetFrame().addFileMenuSeparatorAfter( mi2 );

//        app.getModuleManager().saveStateToConsole( "/temp/ttt.xml");
        app.startApplication();
    }


    public static class MyObserver implements SimpleObserver {
        public void update() {
            System.out.println( "TestSaveState$MyObserver.update" );
        }
    }
}
