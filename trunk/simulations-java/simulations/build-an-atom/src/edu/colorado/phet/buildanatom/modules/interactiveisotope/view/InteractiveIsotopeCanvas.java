// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope.view;

import static edu.colorado.phet.buildanatom.BuildAnAtomDefaults.STAGE_SIZE;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.buildanatom.BuildAnAtomConstants;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.view.MaximizeControlNode;
import edu.colorado.phet.buildanatom.view.ParticleCountLegend;
import edu.colorado.phet.buildanatom.view.PeriodicTableControlNode;
import edu.colorado.phet.buildanatom.view.StabilityIndicator;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Canvas for the tab where the user builds an atom.
 */
public class InteractiveIsotopeCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private final static Font LABEL_FONT = new PhetFont( 20 );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    // View
    private final PNode rootNode;

    // Transform.
    private final ModelViewTransform2D mvt;

    private final MaximizeControlNode symbolWindow;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

    public InteractiveIsotopeCanvas( final InteractiveIsotopeModel model ) {

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Set up the model-canvas transform.  IMPORTANT NOTES: The multiplier
        // factors for the point in the view can be adjusted to shift the
        // center right or left, and the scale factor can be adjusted to zoom
        // in or out (smaller numbers zoom out, larger ones zoom in).
        mvt = new ModelViewTransform2D(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.width * 0.32 ), (int) Math.round( STAGE_SIZE.height * 0.49 ) ),
                2.0, // "Zoom factor" - smaller zooms out, larger zooms in.
                true );

        setBackground( BuildAnAtomConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        // Create the node that contains both the atom and the neutron bucket.
        final PNode atomAndBucketNode = new InteractiveIsotopeNode(model, mvt, new BooleanProperty( true ) );

        // Create the weigh scale that sits beneath the atom.
        final PNode scaleNode = new AtomScaleNode( model.getAtom() ){{
            // The scale needs to sit just below the atom, and there are some
            // "tweak factors" needed to get it looking right.
            setOffset( mvt.modelToViewXDouble( 0 ) - getFullBoundsReference().width / 2, 530 );
        }};

        // Add the scale followed by the atom so that the layering effect is
        // correct.
        rootNode.addChild( scaleNode );
        rootNode.addChild( atomAndBucketNode );

        // Add indicator that shows whether the nucleus is stable.
        final StabilityIndicator stabilityIndicator = new StabilityIndicator( model.getAtom(), new BooleanProperty( true ) );
        model.getAtom().addObserver( new SimpleObserver() {
            public void update() {
                stabilityIndicator.setOffset( mvt.modelToViewX( 0 ) - stabilityIndicator.getFullBounds().getWidth() / 2,
                        mvt.modelToViewY( -Atom.ELECTRON_SHELL_1_RADIUS ) + 5 );
            }
        } );
        rootNode.addChild( stabilityIndicator );

        // Add the interactive periodic table that allows the user to select
        // the current element.
        final PeriodicTableControlNode periodicTableNode = new PeriodicTableControlNode( model,
                BuildAnAtomConstants.CANVAS_BACKGROUND ){
            {
                setScale( 1.2 );
                setOffset( STAGE_SIZE.width - getFullBoundsReference().width - 20, 20 );
            }
        };
        rootNode.addChild( periodicTableNode );

        // Define vars that control the position and alignment of the min/max
        // windows.
        final PDimension windowSize = new PDimension( 400, 100 );//for the 3 lower windows
        final double verticalSpacingBetweenWindows = 12;
        int indicatorWindowPosX = 600;

        // Add the symbol node that provides more detailed information about
        // the currently selected element.
        final SymbolIndicatorNode symbolNode = new SymbolIndicatorNode( model.getAtom(), false, false ) {{
            // Set location and scale.  These are empirically determined, tweak as needed.
            setScale( 1 );
            setOffset( STAGE_SIZE.width - getFullBoundsReference().width - 20,
                    periodicTableNode.getFullBoundsReference().getMaxY() + 20 );
        }};
        symbolWindow = new MaximizeControlNode( BuildAnAtomStrings.INDICATOR_SYMBOL, windowSize, symbolNode, true );
        final double insetX = 20;
        symbolNode.setOffset( insetX, windowSize.height / 2 - symbolNode.getFullBounds().getHeight() / 2 );
        symbolWindow.setOffset( indicatorWindowPosX, 250 );
        rootNode.addChild( symbolWindow );

        // Add the control that allows the user to show/hide the chemical symbol.
        BooleanProperty symbolVisibility = new BooleanProperty( true ){{
            addObserver( new SimpleObserver() {
                public void update() {
                    symbolNode.setVisible( getValue() );
                }
            });
        }};
        // TODO: i18n
        PropertyCheckBox symbolVisibilityPropertyCheckBox = new PropertyCheckBox( "Show Symbol", symbolVisibility ) {
            {
                setFont( LABEL_FONT );
                setOpaque( false );
            }
        };
        final PNode symbolVisibilityCheckBoxNode = new PSwing( symbolVisibilityPropertyCheckBox ){{
            setOffset( scaleNode.getFullBoundsReference().getMaxX() + 40, 590 );
        }};
        rootNode.addChild( symbolVisibilityCheckBoxNode );

        // Add the node that indicates the percentage abundance.
        final PNode abundanceIndicator = new AbundanceIndicator( model.getAtom() ){{
            setOffset( 730, 350 );
        }};
        rootNode.addChild( abundanceIndicator );

        // Add the control that allows the user to show/hide the abundance indicator.
        BooleanProperty abundanceVisibility = new BooleanProperty( false ){{
            addObserver( new SimpleObserver() {
                public void update() {
                    abundanceIndicator.setVisible( getValue() );
                }
            });
        }};
        // TODO: i18n
        PropertyCheckBox abundanceIndicatorVisibilityPropertyCheckBox = new PropertyCheckBox( "Show Abundance", abundanceVisibility ) {
            {
                setFont( LABEL_FONT );
                setOpaque( false );
            }
        };
        final PNode abundanceVisibilityCheckBoxNode = new PSwing( abundanceIndicatorVisibilityPropertyCheckBox ){{
            setOffset( symbolVisibilityCheckBoxNode.getFullBoundsReference().getMinX(), symbolVisibilityCheckBoxNode.getFullBoundsReference().getMaxY() );
        }};
        rootNode.addChild( abundanceVisibilityCheckBoxNode );

        // Add the legend/particle count indicator.
        ParticleCountLegend particleCountLegend = new ParticleCountLegend( model.getAtom(), Color.WHITE );
        particleCountLegend.setScale( 1.25 );
        particleCountLegend.setOffset( 20, 10 );
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

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------


    /**
     * Shows the abundance readout for a user-selected isotope.
     */
    private static class AbundanceIndicator extends PNode {

        private static DecimalFormat ABUNDANCE_FORMATTER = new DecimalFormat( "#.#####" );
        public static final double MIN_ABUNDANCE_TO_SHOW = 0.00001;//Should match the resolution of the ABUNDANCE_FORMATTER
        protected final int RECTANGLE_INSET_X = 6;

        public AbundanceIndicator( final IDynamicAtom atom ) {
            final HTMLNode title = new HTMLNode() {{
                setFont( new PhetFont( 20 ) );
                setHTML( BuildAnAtomStrings.ABUNDANCE );
            }};
            final HTMLNode value = new HTMLNode() {{
                setFont( new PhetFont( 20 ) );
                setOffset( title.getFullBounds().getWidth() + RECTANGLE_INSET_X + 10, 0 );
            }};
            final PhetPPath valueBackground = new PhetPPath( Color.white, new BasicStroke( 1 ), Color.darkGray );
            addChild( title );
            addChild( valueBackground );
            addChild( value );
            atom.addObserver( new SimpleObserver() {
                public void update() {
                    //Show the abundance value
                    final double abundancePercent = atom.getNaturalAbundance() * 100;
                    value.setHTML( abundancePercent < MIN_ABUNDANCE_TO_SHOW && abundancePercent > 0 ? BuildAnAtomStrings.VERY_SMALL : ABUNDANCE_FORMATTER.format( abundancePercent ) + "%" );

                    //Expand the white background to contain the text value
                    final Rectangle2D r = RectangleUtils.expand( value.getFullBounds(), RECTANGLE_INSET_X, 3 );
                    valueBackground.setPathTo( new RoundRectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight(), 10, 10 ) );
                }
            } );
        }
    }
}
