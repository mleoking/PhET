// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.acidbasesolutions.constants.ABSColors;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.ParameterKeys;
import edu.colorado.phet.acidbasesolutions.constants.ABSSimSharing.UserComponents;
import edu.colorado.phet.acidbasesolutions.model.MagnifyingGlass;
import edu.colorado.phet.acidbasesolutions.model.SolutionRepresentation.SolutionRepresentationChangeAdapter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;

/**
 * Visual representation of a magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MagnifyingGlassNode extends PhetPNode {

    private static final Stroke GLASS_STROKE = new BasicStroke( 18f );
    private static final Color GLASS_STROKE_COLOR = Color.BLACK;

    private static final Stroke HANDLE_STROKE = new BasicStroke( 2f );
    private static final Color HANDLE_STROKE_COLOR = Color.BLACK;
    private static final Color HANDLE_FILL_COLOR = new Color( 85, 55, 33 ); // brown
    private static final double HANDLE_ARC_WIDTH = 40;

    private final MagnifyingGlass magnifyingGlass;
    private final PPath handleNode;
    private final RoundRectangle2D handlePath;
    private final PPath rimNode, lensNode;
    private final Ellipse2D rimPath;
    private final MoleculesNode moleculesNode;

    public MagnifyingGlassNode( final MagnifyingGlass magnifyingGlass ) {
        super();

        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addSolutionRepresentationChangeListener( new SolutionRepresentationChangeAdapter() {

            // when the solution changes, update the color of the glass
            @Override
            public void solutionChanged() {
                updateColor();
            }

            @Override
            public void visibilityChanged() {
                setVisible( magnifyingGlass.isVisible() );
            }
        } );

        handlePath = new RoundRectangle2D.Double();
        handleNode = new PPath();
        handleNode.setStroke( HANDLE_STROKE );
        handleNode.setStrokePaint( HANDLE_STROKE_COLOR );
        handleNode.setPaint( HANDLE_FILL_COLOR );

        rimPath = new Ellipse2D.Double();
        rimNode = new PClip();
        rimNode.setStroke( GLASS_STROKE );
        rimNode.setStrokePaint( GLASS_STROKE_COLOR );
        rimNode.setPaint( null );

        /*
        * Use an opaque background node so that we can't see other things that go behind
        * the magnifying glass (eg, pH meter and other tools).  The color of this background
        * node is the same as the canvas color, so that the liquid in the magnifying glass
        * will appear to be the same color as the liquid in the beaker.  The shape of the
        * background is identical to the shape of the magnifying glass.
        */
        lensNode = new PPath();
        lensNode.setPaint( ABSColors.CANVAS_BACKGROUND );

        moleculesNode = new MoleculesNode( magnifyingGlass );

        // rendering order
        addChild( handleNode );
        addChild( lensNode );
        addChild( rimNode );
        rimNode.addChild( moleculesNode ); // clip images to circle

        updateGeometry();
        updateColor();
        setOffset( magnifyingGlass.getLocationReference() );
        setVisible( magnifyingGlass.isVisible() );

        // send sim-sharing event if user tries to interact, identify which part they clicked on
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                ParameterKeys partValue;
                if ( event.getPickedNode() == handleNode ) {
                    partValue = ParameterKeys.handle;
                }
                else if ( event.getPickedNode() == moleculesNode ) {
                    partValue = ParameterKeys.molecule;
                }
                else {
                    partValue = ParameterKeys.lens;
                }
                SimSharingManager.sendUserMessage( UserComponents.magnifyingGlass, UserComponentTypes.sprite, UserActions.pressed,
                                                   parameterSet( edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.interactive, false ).
                                                           with( edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.part, partValue.toString() ) );
            }
        } );
    }

    private void updateGeometry() {
        double diameter = magnifyingGlass.getDiameter();
        // lens
        rimPath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        rimNode.setPathTo( rimPath );
        lensNode.setPathTo( rimPath );
        // handle
        double width = diameter / 8;
        double height = diameter / 2.25;
        handlePath.setRoundRect( -width / 2, 0, width, height, HANDLE_ARC_WIDTH, HANDLE_ARC_WIDTH );
        handleNode.setPathTo( handlePath );
        handleNode.getTransform().setToIdentity();
        PAffineTransform transform = new PAffineTransform();
        transform.rotate( -Math.PI / 3 );
        transform.translate( 0, diameter / 2 );
        handleNode.setTransform( transform );
    }

    private void updateColor() {
        rimNode.setPaint( magnifyingGlass.getSolution().getColor() );
    }

}
