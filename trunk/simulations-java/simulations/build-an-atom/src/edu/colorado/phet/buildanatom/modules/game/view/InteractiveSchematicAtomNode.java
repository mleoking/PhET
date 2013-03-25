// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.model.Electron;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.model.Neutron;
import edu.colorado.phet.buildanatom.model.Proton;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.view.OrbitalView;
import edu.colorado.phet.buildanatom.view.ParticleBucketView;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo Node that adds interactivity to a node that represents and atom.
 * The type of interactivity that it adds is the ability to grab and move
 * the subatomic particles in and out of the atom.  This extension also add
 * buckets for holding subatomic particles that are outside of the atom.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class InteractiveSchematicAtomNode extends SchematicAtomNode {

    private final BuildAnAtomModel model;

    /**
     * Constructor.
     */
    public InteractiveSchematicAtomNode( final BuildAnAtomModel model, ModelViewTransform mvt, PhetPCanvas canvas, final Property<OrbitalView> orbitalView ) {
        super( model.getAtom(), mvt, orbitalView );

        this.model = model;

        // Add a marker that depicts the center of the atom's nucleus.
        electronShellLayer.addChild( new CenterMarkerNode( model, mvt ) );

        // Add the buckets that hold the sub-atomic particles.
        BucketView electronBucketNode = new ParticleBucketView( model.getElectronBucket(), mvt, canvas );
        electronShellLayer.addChild( electronBucketNode.getHoleNode() );
        frontLayer.addChild( electronBucketNode.getFrontNode() );
        for ( SphericalParticle electron : model.getElectronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addElectronNode( (Electron) electron );
        }
        BucketView protonBucketNode = new ParticleBucketView( model.getProtonBucket(), mvt, canvas );
        electronShellLayer.addChild( protonBucketNode.getHoleNode() );
        frontLayer.addChild( protonBucketNode.getFrontNode() );
        for ( SphericalParticle proton : model.getProtonBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addProtonNode( (Proton) proton );
        }
        BucketView neutronBucketNode = new ParticleBucketView( model.getNeutronBucket(), mvt, canvas );
        electronShellLayer.addChild( neutronBucketNode.getHoleNode() );
        frontLayer.addChild( neutronBucketNode.getFrontNode() );
        for ( SphericalParticle neutron : model.getNeutronBucket().getParticleList() ) {
            // Add these particles to the atom representation even though they
            // are outside of the atom, since they may well be added to the
            // atom later.
            addNeutronNode( (Neutron) neutron );
        }

        // Add a circular node that sits behind all the interactive particles
        // and makes it easier for the user to grab particles from the atom.
        {
            double radius = 0;
            for ( ElectronShell electronShell : atom.getElectronShells() ){
                if ( mvt.modelToViewDeltaX( electronShell.getRadius() ) > radius ){
                    radius = mvt.modelToViewDeltaX( electronShell.getRadius() );
                }
            }
            radius = radius * 1.2; // Make this node slightly larger than the largest electron shell.
            Point2D center = mvt.modelToView( atom.getPosition() );
            PNode particleGrabHelper = new PhetPPath( new Ellipse2D.Double( center.getX() - radius,
                                                                            center.getY() - radius,
                                                                            radius * 2,
                                                                            radius * 2 ),
                                                      Color.PINK );
            particleGrabHelper.setOffset( electronShellLayer.getOffset() );
            addChild( particleGrabHelper );
            particleGrabHelper.moveInBackOf( electronShellLayer );
        }

    }

    public ImmutableAtom getAtomValue() {
        return model.getAtom().toImmutableAtom();
    }

    public void setAtomValue( ImmutableAtom answer ) {
        model.setState( answer, false );
    }

    private static class CenterMarkerNode extends PNode {
        public CenterMarkerNode( final BuildAnAtomModel model, ModelViewTransform mvt ) {
            setPickable( false );
            setChildrenPickable( false );

            model.getAtom().addAtomListener( new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    // Only visible when there are no nucleons in the atom.
                    setVisible( model.getAtom().getMassNumber() == 0 );
                }
            } );

            // Create the marker.
            DoubleGeneralPath nucleusXMarkerModelCoords = new DoubleGeneralPath();
            double xMarkerSize = Proton.RADIUS; // Arbitrary, adjust as desired.
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                                              model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                                              model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.moveTo( model.getAtom().getPosition().getX() - xMarkerSize / 2,
                                              model.getAtom().getPosition().getY() + xMarkerSize / 2 );
            nucleusXMarkerModelCoords.lineTo( model.getAtom().getPosition().getX() + xMarkerSize / 2,
                                              model.getAtom().getPosition().getY() - xMarkerSize / 2 );
            Shape nucleusXMarkerShape = mvt.modelToView( nucleusXMarkerModelCoords.getGeneralPath() );
            PNode nucleusXMarkerNode = new PhetPPath( nucleusXMarkerShape, new BasicStroke( 4f ), new Color( 255, 0, 0, 75 ) );
            addChild( nucleusXMarkerNode );
        }
    }
}
