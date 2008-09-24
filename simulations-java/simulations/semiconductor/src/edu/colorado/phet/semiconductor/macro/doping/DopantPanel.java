package edu.colorado.phet.semiconductor.macro.doping;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.common.BorderGraphic;
import edu.colorado.phet.semiconductor.common.ModelLocation;
import edu.colorado.phet.semiconductor.common.TextGraphic;
import edu.colorado.phet.semiconductor.phetcommon.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.semiconductor.phetcommon.view.CompositeInteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.InteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.ShapeGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.DragToCreate;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.InteractiveGraphicCreator;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.TransformListener;

/**
 * User: Sam Reid
 * Date: Jan 27, 2004
 * Time: 1:54:01 AM
 */
public class DopantPanel extends CompositeInteractiveGraphic {
    Rectangle viewRect;
    private BorderGraphic border;
    private ApparatusPanel apparatusPanel;
    private ModelViewTransform2D transform;
    private DopantGraphic posDopGraphic;
    private DopantGraphic negDopGraphic;
    ShapeGraphic background;
    private boolean visible = true;
    ArrayList dopantListeners = new ArrayList();
    private Hashtable map = new Hashtable();

    public DopantPanel( final ApparatusPanel apparatusPanel, final ModelViewTransform2D transform, final BufferedImage nDopantImage, BufferedImage pDopantImage, final Rectangle2D.Double modelRect ) throws IOException {
        this.apparatusPanel = apparatusPanel;

        this.transform = transform;

        this.viewRect = transform.modelToView( modelRect );
        background = new ShapeGraphic( viewRect, new Color( 200, 200, 245 ) );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                viewRect = transform.modelToView( modelRect );
                border.setRectangle( viewRect );
                background.setShape( viewRect );
            }
        } );
        Border init = BorderFactory.createLineBorder( Color.blue, 2 );
        Font font = new PhetFont( Font.BOLD, 16 );
        Border bo = BorderFactory.createTitledBorder( init, SimStrings.get( "DopantPanel.DopantBorder" ), 0, 0, font, Color.black );
        border = new BorderGraphic( bo, apparatusPanel, viewRect );

        PhetVector leftCell = getCenter( modelRect ).getAddedInstance( 0, -modelRect.getHeight() / 5.5 );
        PhetVector rightCell = getCenter( modelRect ).getAddedInstance( 0, modelRect.getHeight() / 5.5 );

        Dopant negativeDopant = new Dopant( leftCell, DopantType.N );
        Dopant positiveDopant = new Dopant( rightCell, DopantType.P );

        CompositeInteractiveGraphic positive = new CompositeInteractiveGraphic();
        CompositeInteractiveGraphic negative = new CompositeInteractiveGraphic();

        posDopGraphic = new DopantGraphic( positiveDopant, transform, pDopantImage, 5 );
        negDopGraphic = new DopantGraphic( negativeDopant, transform, nDopantImage, 5 );

        positive.addGraphic( posDopGraphic );
        negative.addGraphic( negDopGraphic );

        addText( SimStrings.get( "DopantPanel.PositiveTypeText" ), positive, rightCell, transform );
        addText( SimStrings.get( "DopantPanel.NegativeTypeText" ), negative, leftCell, transform );

        InteractiveGraphicCreator nsource = makeSource( negativeDopant, nDopantImage, false, "N" );
        InteractiveGraphicCreator psource = makeSource( positiveDopant, pDopantImage, true, "P" );

        DragToCreate dragToCreateNeg = new DragToCreate( nsource, this, 10000 );
        DragToCreate dragToCreatePos = new DragToCreate( psource, this, 10000 );

        DefaultInteractiveGraphic createN = new DefaultInteractiveGraphic( negative, negDopGraphic );
        createN.addMouseInputListener( dragToCreateNeg );
        createN.addCursorHandBehavior();
        addGraphic( createN, 0 );

        DefaultInteractiveGraphic createP = new DefaultInteractiveGraphic( positive, posDopGraphic );
        createP.addMouseInputListener( dragToCreatePos );
        createP.addCursorHandBehavior();
        addGraphic( createP, 0 );
    }

    private void addText( String s, CompositeInteractiveGraphic graphic, PhetVector cell, ModelViewTransform2D transform ) {
        final TextGraphic pText = new TextGraphic( 100, 100, new PhetFont( Font.PLAIN, 14 ), Color.black, s );
        ViewChangeListener vcl = new ViewChangeListener() {
            public void viewCoordinateChanged( int x, int y ) {
                pText.setPosition( x - 25, y - 23 );
            }
        };
        graphic.addGraphic( pText );


        ModelLocation ml = new ModelLocation( cell.getX(), cell.getY(), transform );
        ml.addViewChangeListener( vcl );
    }

    public void addDopantDropListener( DopantDropListener ddl ) {
        dopantListeners.add( ddl );
    }

    class DopantSourceGraphic {
        DefaultInteractiveGraphic graphic;

        public DopantSourceGraphic( final Dopant source, final BufferedImage image, final boolean positive, String name ) {

            Dopant dopant = new Dopant( source.getPosition(), positive ? DopantType.P : DopantType.N );
            final DopantGraphic dragDopantGraphic = new DopantGraphic( dopant, transform, image, 5 );
            CompositeInteractiveGraphic cg = new CompositeInteractiveGraphic();
            cg.addGraphic( dragDopantGraphic );

            graphic = new DefaultInteractiveGraphic( cg, dragDopantGraphic );
            map.put( dragDopantGraphic, graphic );
            Translatable t = new Translatable() {
                public void translate( double dx, double dy ) {
                    Point2D.Double trf = transform.viewToModelDifferential( (int) dx, (int) dy );
                    trf.x += dragDopantGraphic.getDopant().getPosition().getX();
                    trf.y += dragDopantGraphic.getDopant().getPosition().getY();
                    if ( trf.x < 5.7 ) {
                        dx = 0;
                    }
//                        PhetVector pv=dragDopantGraphic.getDestination(dx,dy);
                    dragDopantGraphic.translate( dx, dy );
                    apparatusPanel.repaint();
//                        System.out.println("Calling repaint, time="+System.currentTimeMillis());
                }
            };
            graphic.addCursorHandBehavior();
            graphic.addTranslationBehavior( t );
            MouseInputAdapter mia = new MouseInputAdapter() {
                // implements java.awt.event.MouseListener
                public void mouseReleased( MouseEvent e ) {
                    //dropping the dopant here.
                    for ( int i = 0; i < dopantListeners.size(); i++ ) {
                        DopantDropListener dopantDropListener = (DopantDropListener) dopantListeners.get( i );
                        dopantDropListener.dopantDropped( dragDopantGraphic );
                    }
                }
            };
            graphic.addMouseInputListener( mia );
        }

        public InteractiveGraphic getGraphic() {
            return graphic;
        }
    }

    InteractiveGraphicCreator makeSource( final Dopant source, final BufferedImage image, final boolean positive, final String name ) {
        InteractiveGraphicCreator igc = new InteractiveGraphicCreator() {
            public InteractiveGraphic newInstance() {
                return new DopantSourceGraphic( source, image, positive, name ).getGraphic();
            }
        };
        return igc;
    }

    private PhetVector getCenter( Rectangle2D.Double modelRect ) {
        return new PhetVector( modelRect.x + modelRect.width / 2, modelRect.y + modelRect.height / 2 );
    }

    public void paint( Graphics2D graphics2D ) {
        if ( !visible ) {
            return;
        }

        background.paint( graphics2D );
        border.paint( graphics2D );
        super.paint( graphics2D );
    }

    public void removeDopant( DopantGraphic dg ) {
        Graphic g = (Graphic) map.get( dg );
        super.removeGraphic( g );
    }
}
