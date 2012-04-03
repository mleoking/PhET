// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import static edu.colorado.phet.common.phetcommon.util.Pair.pair;

/**
 * Radio button strip that lets the user choose between different scenes in the pressure tab.
 *
 * @author Sam Reid
 */
public class FluidPressureRadioButtonStripControlPanelNode extends PNode {
    public FluidPressureRadioButtonStripControlPanelNode( final FluidPressureCanvas canvas, final FluidPressureModel model ) {
        final List<Pair<PNode, ? extends IPool>> elements = createButtons( canvas, model );

        //Copied code from RadioButtonStripControlPanelNode so we could make non-square
        final HBox representationLayer = new HBox( 5 ) {{
            for ( final Pair<PNode, ? extends IPool> element : elements ) {

                double delta = 10;
                PNode button = new PhetPPath( new RoundRectangle2D.Double( -delta / 2, -delta / 2, element._1.getFullBounds().getWidth() + delta, element._1.getFullBounds().getHeight() + delta, 20, 20 ), null ) {{

                    final ZeroOffsetNode theNode = new ZeroOffsetNode( element._1 ) {{
                        final Point2D.Double origOffset = new Point2D.Double( element._1.getFullBounds().getWidth() / 2 - getFullWidth() / 2, element._1.getFullBounds().getHeight() / 2 - getFullHeight() / 2 );
                        setOffset( origOffset );
                    }};
                    addChild( theNode );
                }};

                addChild( new ToggleButtonNode( button, model.pool.valueEquals( element._2 ), new VoidFunction0() {
                    public void apply() {
                        model.pool.set( element._2 );
                    }
                } ) );
            }
        }};
        addChild( representationLayer );
    }

    private static List<Pair<PNode, ? extends IPool>> createButtons( final FluidPressureCanvas canvas, final FluidPressureModel model ) {
        return Arrays.asList( pair( icon( canvas, model.pool, model.squarePool ), model.squarePool ),
                              pair( icon( canvas, model.pool, model.trapezoidPool ), model.trapezoidPool ),
                              pair( icon( canvas, model.pool, model.chamberPool ), model.chamberPool ) );
    }

    //Creates an icon that displays the track.
    //Copied from code in EnergySkatePark's TrackButton
    private static PNode icon( final FluidPressureCanvas canvas, SettableProperty<IPool> property, IPool pool ) {
        IPool origPool = property.get();

        //Set the property that we wish to display
        property.set( pool );

        //To create the icon, load the module and render it to an image
        BufferedImage icon = new BufferedImage( 1024, 768, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = icon.createGraphics();
        canvas.setSize( 1024, 768 );

        //Render into the image
        canvas.getRootNode().fullPaint( new PPaintContext( g2 ) );

        //Restore
        property.set( origPool );

        //Resize to a small icon size using multi-scaling so the quality will be high
        return new PImage( BufferedImageUtils.multiScaleToWidth( icon, 100 ) );
    }
}