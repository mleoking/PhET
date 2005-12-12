package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Class: TestApparatusPanel2
 * Package: edu.colorado.phet.common.tests
 * Author: Another Guy
 * Date: Dec 13, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class TestApparatusPanel2 {

    static class TestAppModel extends ApplicationModel {
        public TestAppModel() {
            super( "", "", "" );
            this.setClock( new SwingTimerClock( 10, 40, true ) );
            TestModule module = new TestModule( this, getClock() );
            setModule( module );
            setFrameCenteredSize( 400, 300 );
            setInitialModule( module );
        }
    }

    static class TestModule extends PhetGraphicsModule {

        protected TestModule( TestAppModel model, AbstractClock clock ) {
            super( "ApparatusPanel2 Test", clock );

//            BaseModel model = new BaseModel();
//            ApparatusPanel ap = new ApparatusPanel();
            BaseModel baseModel = new BaseModel();
            ApparatusPanel ap = new ApparatusPanel2( model.getClock() );
            setApparatusPanel( ap );
            setModel( baseModel );

            String family = "Serif";
            int style = Font.PLAIN;
            int size = 12;
            Font font = new Font( family, style, size );
            PhetGraphic pg = new TestGraphic( ap, font, "YO!", Color.blue, 100, 100 );
            pg.setCursorHand();
            ap.addGraphic( pg );
            PhetGraphic pg2 = new TestGraphic2( ap, 50, 50 );
            pg2.setCursorHand();
            ap.addGraphic( pg2 );
        }
    }

    static class TestGraphic extends PhetTextGraphic {
        public TestGraphic( Component component, Font font, String text, Color color, int x, int y ) {
            super( component, font, text, color, x, y );
            addTranslationListener( new TranslationListener() {
                public void translationOccurred( TranslationEvent translationEvent ) {
                    setLocation( (int)( getLocation().getX() + translationEvent.getDx() ),
                                 (int)( getLocation().getY() + translationEvent.getDy() ) );
                }
            } );
        }

        public void fireMouseEntered( MouseEvent e ) {
            super.fireMouseEntered( e );
        }
    }

    static class TestGraphic2 extends PhetShapeGraphic {
        public TestGraphic2( Component component, int x, int y ) {
            super( component, new Rectangle( x, y, 30, 20 ), Color.red );
        }

        public void fireMouseEntered( MouseEvent e ) {
            super.fireMouseEntered( e );
            setColor( Color.blue );
        }

        public void fireMouseExited( MouseEvent e ) {
            super.fireMouseExited( e );
            setColor( Color.red );
        }
    }

    public static void main( String[] args ) {

        PhetApplication testApp = new PhetApplication( new TestAppModel() );
        testApp.startApplication();

    }
}
