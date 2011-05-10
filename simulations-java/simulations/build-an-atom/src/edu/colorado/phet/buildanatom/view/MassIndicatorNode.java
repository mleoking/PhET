// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomResources;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.AtomListener;
import edu.colorado.phet.buildanatom.model.ElectronShell;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetColorScheme;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Shows a scale with a numeric readout of the atom weight.  Origin is the top left of the scale body (not the platform).
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MassIndicatorNode extends PNode {
    private static final double WIDTH = 90; // The image will define the aspect ratio and therefore the height.

    public MassIndicatorNode( final Atom atom, final OrbitalViewProperty orbitalViewProperty ) {

        final PImage weighScaleImageNode = new PImage( BuildAnAtomResources.getImage( "atom_builder_scale.png" ) );
        weighScaleImageNode.setScale( WIDTH / weighScaleImageNode.getFullBoundsReference().width );
        final double HEIGHT = weighScaleImageNode.getHeight();
        addChild ( weighScaleImageNode );

        final PPath readout = new PhetPPath(Color.WHITE, new BasicStroke(1f), Color.LIGHT_GRAY){{
            Shape readoutBackgroundShape = new RoundRectangle2D.Double(0, 0,
                    WIDTH * 0.4,
                    HEIGHT * 0.31, 4, 4);
            setPathTo( readoutBackgroundShape );
        }};
        addChild( readout );

        final PText readoutPText = new PText() {{
            setFont( BuildAnAtomConstants.WINDOW_TITLE_FONT );
            setTextPaint( Color.BLACK );
        }};

        //from 9/30/2010 meeting
        //will students think the atom on the scale is an electron?
        //use small icon of orbits/cloud instead of cloud

        final PNode atomNode = new PNode();
        //Make it small enough so it looks to scale, but also so we don't have to indicate atomic substructure
        double scale = 1.0 / 5;
        ModelViewTransform mvt = ModelViewTransform.createRectangleMapping( new Rectangle2D.Double( 0, 0, 1, 1 ), new Rectangle2D.Double( 0, 0, scale, scale ) );
        for ( ElectronShell electronShell : atom.getElectronShells() ) {
            atomNode.addChild( new ElectronOrbitalNode( mvt, orbitalViewProperty, atom, electronShell, false ) );
        }

        atomNode.addChild( new ResizingElectronCloudNode( mvt, orbitalViewProperty, atom ) );

        double nucleusWidth=1;
        atomNode.addChild( new PhetPPath( new Ellipse2D.Double( -nucleusWidth / 2, -nucleusWidth / 2, nucleusWidth, nucleusWidth ), Color.red ) {{
            setOffset( atomNode.getFullBounds().getCenter2D() );
            final AtomListener updateNucleusNode = new AtomListener.Adapter() {
                @Override
                public void configurationChanged() {
                    setVisible( atom.getNumProtons() + atom.getNumNeutrons() > 0 );
                    if ( atom.getNumProtons() > 0 ) {
                        setPaint( PhetColorScheme.RED_COLORBLIND );//if any protons, it should look red
                    }
                    else {
                        setPaint( Color.gray );
                    } //if no protons, but some neutrons, should look neutron colored
                }
            };
            atom.addAtomListener( updateNucleusNode );
            updateNucleusNode.configurationChanged(); // Initial update.
        }} );
        addChild( atomNode );

        final AtomListener updateAtomOffset = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                // Position the atom and the scale such that the (0,0) position is the
                // upper left corner of the whole assembly.
                double y = 0;
                //This logic makes sure the atom is centered on the scale, whether it has orbital rings or 1 or 2 levels of fuzziness
                if ( orbitalViewProperty.get() != OrbitalView.RESIZING_CLOUD ) {
                    y = atomNode.getFullBoundsReference().height / 2;
                }
                else {
                    if ( atom.getElectronShells().get( 1 ).getNumElectrons() == 0 ) {
                        y = atomNode.getFullBoundsReference().height*0.9;
                    }
                    else {
                        y = atomNode.getFullBoundsReference().height *0.6;
                    }
                }
                atomNode.setOffset( weighScaleImageNode.getFullBoundsReference().getCenterX(), y );
            }
        };
        atom.addAtomListener( updateAtomOffset );//update when number of e- changes in outer shell
        orbitalViewProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateAtomOffset.configurationChanged();
            }
        });

        // There is a tweak factor here to set the vertical relationship between
        // the atom and scale.
        weighScaleImageNode.setOffset( 0, atomNode.getFullBoundsReference().height * 0.75 );

        // Now that the weigh scale is positioned we can set the offset of the
        // readout.
        readout.setOffset(
                weighScaleImageNode.getFullBoundsReference().getCenterX() - readout.getFullBoundsReference().width / 2,
                weighScaleImageNode.getFullBoundsReference().getMaxX() - readout.getFullBoundsReference().height - 2.5);

        // Add the test to the readout.
        final AtomListener readoutUpdater = new AtomListener.Adapter() {
            @Override
            public void configurationChanged() {
                readoutPText.setText( atom.getMassNumber() + "" );
                readoutPText.setOffset( readout.getFullBounds().getCenterX() - readoutPText.getFullBounds().getWidth() / 2, readout.getFullBounds().getCenterY()-readoutPText.getFullBounds().getHeight()/2);
            }
        };
        atom.addAtomListener( readoutUpdater );
        readoutUpdater.configurationChanged(); // Initial update.
        addChild( readoutPText );

        // Prevent user from interacting with this readout node.
        setPickable( false );
        setChildrenPickable( false );
    }
}
