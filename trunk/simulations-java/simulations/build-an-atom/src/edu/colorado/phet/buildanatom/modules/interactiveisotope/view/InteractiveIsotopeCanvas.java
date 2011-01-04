/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.view.ParticleCountLegend;
import edu.colorado.phet.buildanatom.view.StabilityIndicator;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class InteractiveIsotopeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public InteractiveIsotopeCanvas( final InteractiveIsotopeModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, BuildAnAtomDefaults.STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.width * 0.50 ), (int) Math.round( BuildAnAtomDefaults.STAGE_SIZE.height * 0.55 ) ),
                2.0,
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Add the node that contains both the atom and the neutron bucket.
        rootNode.addChild( new InteractiveIsotopeNode(model, mvt, new BooleanProperty( true ) ));

        // Show whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), new BooleanProperty( true ) );
        // Position the stability indicator under the nucleus
        model.getAtom().addObserver( new SimpleObserver() {
            public void update() {
                stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2, mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) - stabilityIndicator.getFullBounds().getHeight() );
            }
        } );
        rootNode.addChild( stabilityIndicator );

        // TODO: These buttons are for unit test purposes and should
        // eventually be removed.
        ButtonNode testButton1 = new ButtonNode("Helium");
        testButton1.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setAtomConfiguration( new AtomValue(2, 2, 2) );
            }
        });
        testButton1.setOffset( 100, 50 );
        addWorldChild( testButton1 );
        ButtonNode testButton2 = new ButtonNode("Lithium");
        testButton2.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setAtomConfiguration( new AtomValue(3, 4, 3) );
            }
        });
        testButton2.setOffset( 100, 100 );
        addWorldChild( testButton2 );
        ButtonNode testButton3 = new ButtonNode("Clear");
        testButton3.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                model.setAtomConfiguration( new AtomValue(0, 0, 0) );
            }
        });
        testButton3.setOffset( 100, 150 );
        addWorldChild( testButton3 );

        /*
         * TODO: Commented out while details of atom presentation are sorted through.

        rootNode.addChild( new InteractiveSchematicAtomNode(model, mvt, new BooleanProperty( true ) ));

        // Show the name of the element.
        ElementNameIndicator elementNameIndicator = new ElementNameIndicator( model.getAtom(), new BooleanProperty( true ) );
        // Position the name indicator above the nucleus
        elementNameIndicator.setOffset( mvt.modelToViewX( 0 ), mvt.modelToViewY( Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) + elementNameIndicator.getFullBounds().getHeight() / 2 );
        rootNode.addChild( elementNameIndicator );

        // Show whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), new BooleanProperty( true ) );
        // Position the stability indicator under the nucleus
        model.getAtom().addObserver( new SimpleObserver() {
            public void update() {
                stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2, mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS * 3.0 / 4.0 ) - stabilityIndicator.getFullBounds().getHeight() );
            }
        } );
        rootNode.addChild( stabilityIndicator );
                 */


        // Add the legend/particle count indicator.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom() );
        particleCountLegend.setOffset( 700, 10 );
        rootNode.addChild( particleCountLegend );
    }

    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /*
     * Updates the layout of stuff on the canvas.
     */
    @Override
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( BuildAnAtomConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
