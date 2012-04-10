// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.radiobuttonstrip.ToggleButtonNode;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.pressure.model.FluidPressureModel;
import edu.colorado.phet.fluidpressureandflow.pressure.model.IPool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.button;
import static edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.sendMessage;

/**
 * Radio button strip that lets the user choose between different scenes in the pressure tab.
 *
 * @author Sam Reid
 */
public class FluidPressureRadioButtonStripControlPanelNode extends PNode {
    public FluidPressureRadioButtonStripControlPanelNode( final FluidPressureCanvas canvas, final FluidPressureModel model ) {
        final List<Element> elements = createButtons( canvas, model );

        //Copied code from RadioButtonStripControlPanelNode so we could make non-square
        final HBox representationLayer = new HBox( 5 ) {{
            for ( final Element element : elements ) {

                double delta = 10;
                PNode buttonNode = new PhetPPath( new RoundRectangle2D.Double( -delta / 2, -delta / 2, element.node.getFullBounds().getWidth() + delta, element.node.getFullBounds().getHeight() + delta, 20, 20 ), null ) {{

                    final ZeroOffsetNode theNode = new ZeroOffsetNode( element.node ) {{
                        final Point2D.Double origOffset = new Point2D.Double( element.node.getFullBounds().getWidth() / 2 - getFullWidth() / 2, element.node.getFullBounds().getHeight() / 2 - getFullHeight() / 2 );
                        setOffset( origOffset );
                    }};
                    addChild( theNode );
                }};

                addChild( new ToggleButtonNode( buttonNode, model.pool.valueEquals( element.pool ), sendMessage( element.component, button, pressed ).andThen( model.setPool_( element.pool ) ) ) );
            }
        }};
        addChild( representationLayer );
    }

    private static List<Element> createButtons( final FluidPressureCanvas canvas, final FluidPressureModel model ) {
        return Arrays.asList( new Element( icon( canvas, model.pool, model.squarePool ), model.squarePool, UserComponents.squarePoolButton ),
                              new Element( icon( canvas, model.pool, model.trapezoidPool ), model.trapezoidPool, UserComponents.trapezoidPoolButton ),
                              new Element( icon( canvas, model.pool, model.chamberPool ), model.chamberPool, UserComponents.massesPoolButton ) );
    }

    public static class Element {
        public final PNode node;
        public final IPool pool;
        public final IUserComponent component;

        public Element( final PNode node, final IPool pool, final IUserComponent component ) {
            this.node = node;
            this.pool = pool;
            this.component = component;
        }
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