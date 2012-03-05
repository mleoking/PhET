// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;

import edu.colorado.phet.beerslawlab.beerslaw.model.SolidBeam;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.LightRepresentation;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Representation of light as a collection of photons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhotonBeamNode extends PhetPNode {

    private final SolidBeam beam;
    private final ModelViewTransform mvt;
    private final PClip clippingPath;

    public PhotonBeamNode( Property<BeersLawSolution> solution, final SolidBeam beam, final Property<LightRepresentation> representation, ModelViewTransform mvt ) {
        setPickable( false );
        setChildrenPickable( false );

        this.beam = beam;
        this.mvt = mvt;
        this.clippingPath = new PClip() {{
            setStroke( null );
        }};

        addChild( clippingPath );

        // for debugging, show clipping path
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            clippingPath.setStroke( new BasicStroke( 1f ) );
            clippingPath.setStrokePaint( Color.RED );
        }

        // When the solution or view representation changes, remove all photons.
        solution.addObserver( new SimpleObserver() {
            public void update() {
                removeAllPhotons();
            }
        } );

        // Make this node visible when beam is visible and light representation is "photons".
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                setVisible( beam.visible.get() && representation.get() == LightRepresentation.PHOTONS );
                if ( !getVisible() ) {
                    removeAllPhotons();
                }
            }
        };
        visibilityObserver.observe( beam.visible, representation );

        // Update the beam clipping path
        RichSimpleObserver observer = new RichSimpleObserver() {
            @Override public void update() {
                updateClippingPath();
            }
        };
        observer.observe( beam.leftShape, beam.centerShape, beam.rightShape );

        //TODO observe beam.leftPaint or light.wavelength
    }

    private void removeAllPhotons() {
        //TODO
    }

    private void updateClippingPath() {
        Area area = new Area( mvt.modelToView( beam.leftShape.get() ).toRectangle2D() );
        area.add( new Area( mvt.modelToView( beam.centerShape.get() ).toRectangle2D() ) );
        area.add( new Area( mvt.modelToView( beam.rightShape.get() ).toRectangle2D() ) );
        clippingPath.setPathTo( area );
    }
}
