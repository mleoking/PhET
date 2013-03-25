// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Class that represents a chunk of energy in the view.
 *
 * @author John Blanco
 */
public class EnergyChunkNode2 extends PNode {

    private static final double WIDTH = 24; // In screen coords, which is close to pixels.
    private static final double Z_DISTANCE_WHERE_FULLY_FADED = 0.1; // In meters.
    private static final Stroke OUTLINE_STROKE = new BasicStroke( 2 );
    private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;

    private static final Map<EnergyType, Color> mapEnergyTypeToColor = new HashMap<EnergyType, Color>() {{
        // TODO: If we end up using these, these color will need adjustment.
        put( EnergyType.THERMAL, PhetColorScheme.RED_COLORBLIND );
        put( EnergyType.ELECTRICAL, Color.YELLOW );
        put( EnergyType.MECHANICAL, Color.GRAY );
        put( EnergyType.LIGHT, Color.YELLOW );
        put( EnergyType.CHEMICAL, Color.GREEN );
    }};

    public EnergyChunkNode2( final EnergyChunk energyChunk, final ModelViewTransform mvt ) {

        // Control the overall visibility of this node.
        energyChunk.visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        // Set up updating of transparency based on Z position.
        energyChunk.zPosition.addObserver( new VoidFunction1<Double>() {
            public void apply( Double zPosition ) {
                updateTransparency( zPosition );
            }
        } );

        // Draw the energy chunks.
        final PNode body = new PhetPPath( new RoundRectangle2D.Double( -WIDTH / 2, -WIDTH / 2, WIDTH, WIDTH, WIDTH / 4, WIDTH / 4 ), OUTLINE_STROKE, OUTLINE_STROKE_COLOR );
        addChild( body );

        // Monitor the energy type and set the color appropriately.
        energyChunk.energyType.addObserver( new VoidFunction1<EnergyType>() {
            public void apply( EnergyType energyType ) {
                Color baseColor = mapEnergyTypeToColor.get( energyType );
                RoundGradientPaint paint = new RoundGradientPaint( WIDTH / 4,
                                                                   -WIDTH / 4,
                                                                   ColorUtils.brighterColor( baseColor, 0.5 ),
                                                                   new Point2D.Double( -WIDTH / 2, WIDTH / 2 ),
                                                                   baseColor );
                body.setPaint( paint );
            }
        } );

        // Add the label.
        addChild( new PhetPText( "E", new PhetFont( 20, true ) ) {{
            setOffset( -getFullBoundsReference().width / 2, -getFullBoundsReference().height / 2 );
            setTextPaint( Color.BLACK );
        }} );

        // Set this node's position when the corresponding model element moves.
        energyChunk.position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );
    }

    // Update the transparency, which is a function of several factors.
    private void updateTransparency( double zPosition ) {

        double zFadeValue = 1;
        if ( zPosition < 0 ) {
            zFadeValue = Math.max( ( Z_DISTANCE_WHERE_FULLY_FADED + zPosition ) / Z_DISTANCE_WHERE_FULLY_FADED, 0 );
        }
        setTransparency( (float)zFadeValue );
    }


    // Test harness.
    public static void main( String[] args ) {

        Dimension2D stageSize = new PDimension( 200, 100 );

        PhetPCanvas canvas = new PhetPCanvas();
        // Set up the canvas-screen transform.
        canvas.setWorldTransformStrategy( new PhetPCanvas.CenteredStage( canvas, stageSize ) );

        ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( stageSize.getWidth() * 0.5 ), (int) Math.round( stageSize.getHeight() * 0.50 ) ),
                1 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        canvas.getLayer().addChild( new EnergyChunkNode2( new EnergyChunk( EnergyType.ELECTRICAL, 0, 0, new BooleanProperty( true ) ), mvt ) );

        // Boiler plate app stuff.
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( (int) stageSize.getWidth(), (int) stageSize.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocationRelativeTo( null ); // Center.
        frame.setVisible( true );
    }

}
