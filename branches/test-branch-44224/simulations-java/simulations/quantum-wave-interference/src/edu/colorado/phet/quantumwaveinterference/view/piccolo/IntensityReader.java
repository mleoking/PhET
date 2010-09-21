/*  */
package edu.colorado.phet.quantumwaveinterference.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.model.math.Complex;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Dec 18, 2005
 * Time: 11:28:29 PM
 */

public class IntensityReader extends PComposite {
    private SimpleWavefunctionGraphic simpleWavefunctionGraphic;
    private PComposite crosshairs;
    private PText readout;
    private PPath textBackground;
    private StripChartJFCNode stripChartJFCNode;
    private int time = 100;

    public IntensityReader( SimpleWavefunctionGraphic simpleWavefunctionGraphic ) {
        this.simpleWavefunctionGraphic = simpleWavefunctionGraphic;
        textBackground = new PPath();
        textBackground.setPaint( new Color( 255, 255, 255, 235 ) );
        addChild( textBackground );
        double radius = 8;
        crosshairs = new PComposite();
        Ellipse2D.Double aShape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        PPath circle = new PPath( aShape );
        circle.setStrokePaint( Color.red );
        circle.setStroke( new BasicStroke( 3 ) );
        crosshairs.setPaint( new Color( 0, 0, 0, 0 ) );//to make it grabbable inside
        crosshairs.addChild( circle );
        PPath vertical = new PPath( new Line2D.Double( 0, -radius * 3, 0, radius * 3 ) );
        vertical.setStrokePaint( Color.green );
        crosshairs.addChild( vertical );
        PPath horizontalLine = new PPath( new Line2D.Double( -radius * 3, 0, radius * 3, 0 ) );
        horizontalLine.setStrokePaint( Color.green );
        crosshairs.addChild( horizontalLine );
//        crosshairs.setFont( new PhetDefaultFont( Font.BOLD, 24 ) );
//        crosshairs.setTextPaint( new GradientPaint( new Point2D.Double(), Color.blue, new Point2D.Double( 0, crosshairs.getHeight() ), Color.red ) );
        addInputEventListener( new PDragEventHandler() );
        addChild( crosshairs );

        readout = new PText( QWIResources.getString( "value" ) );
        readout.setFont( new PhetFont( Font.BOLD, 14 ) );
        readout.setTextPaint( Color.blue );
        addChild( readout );
        readout.setOffset( 0, crosshairs.getHeight() + 5 );

        stripChartJFCNode = new StripChartJFCNode( 200, 200 );
        addChild( stripChartJFCNode );

        updateTextBackground();
        Timer timer = new Timer( 300, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                update();
            }
        } );
        timer.start();

        crosshairs.translate( 0, textBackground.getFullBounds().getHeight() + crosshairs.getFullBounds().getHeight() / 2 );
        stripChartJFCNode.translate( crosshairs.getFullBounds().getMaxX() + 5, textBackground.getFullBounds().getMaxY() );
        CursorHandler cursorHandler = new CursorHandler( Cursor.HAND_CURSOR );
        addInputEventListener( cursorHandler );


        update();
    }

    private void update() {
        //get the coordinate in the wavefunctiongraphic.
        Point2D location = crosshairs.getGlobalTranslation();
        location.setLocation( location.getX() + 1, location.getY() + 1 );//todo this line seems necessary because we are off somewhere by 1 pixel
//        crosshairs.localToGlobal( location );
        simpleWavefunctionGraphic.globalToLocal( location );

//        simpleWavefunctionGraphic.localToParent( location );
//        Point gc = simpleWavefunctionGraphic.getGridCoordinates( location );
//        Dimension d = simpleWavefunctionGraphic.getCellDimensions();
        double fracDistX = location.getX() / simpleWavefunctionGraphic.getFullBounds().getWidth();
        double fracDistY = location.getY() / simpleWavefunctionGraphic.getFullBounds().getHeight();

        Point2D fracLoc = new Point2D.Double( fracDistX, fracDistY );

        Point cellLocation = new Point( (int)( fracLoc.getX() * simpleWavefunctionGraphic.getWavefunction().getWidth() ),
                                        (int)( fracLoc.getY() * simpleWavefunctionGraphic.getWavefunction().getHeight() ) );

        if( simpleWavefunctionGraphic.getWavefunction().containsLocation( cellLocation.x, cellLocation.y ) ) {
            Complex value = simpleWavefunctionGraphic.getWavefunction().valueAt( cellLocation.x, cellLocation.y );
//        readout.setText( "Location=" + location + ", bounds=" + simpleWavefunctionGraphic.getFullBounds() );
            readout.setText( MessageFormat.format( QWIResources.getString( "magnitude.0" ), new Object[]{new DecimalFormat( "0.00" ).format( value.abs() )} ) );
            stripChartJFCNode.addValue( time++, value.abs() );
        }
        else {
            readout.setText( MessageFormat.format( QWIResources.getString( "location.0" ), new Object[]{cellLocation} ) );
        }

//        textBackground.setPathTo( new Rectangle2D.Double( 0,0,readout.getFullBounds().getWidth(), readout.getFullBounds().getHeight() ) );
        updateTextBackground();
//        System.out.println( "readout.getFullBounds() = " + readout.getFullBounds() );

//        double valueAtCursor = simpleWavefunctionGraphic.getWavefunction().
    }

    private void updateTextBackground() {
        textBackground.setPathTo( RectangleUtils.expand( readout.getFullBounds(), 10, 10 ) );
    }
}
