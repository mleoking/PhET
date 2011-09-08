// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable.PeriodicTableDialog;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerInParent;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SHOW_IN_PERIODIC_TABLE;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends SugarAndSaltSolutionsCanvas implements Module.Listener, ICanvas {
    private PeriodicTableDialog periodicTableDialog;
    private boolean dialogVisibleOnActivate;

    //If set to true, will highlight regions where crystals can be grown
    private final boolean debugBindingSites = false;

    //Function that is used in the floating clock control panel to hide the time readout
    public static final Function1<Double, String> NO_READOUT = new Function1<Double, String>() {
        public String apply( Double aDouble ) {
            return "";
        }
    };

    public MicroCanvas( final MicroModel model, final GlobalState globalState ) {
        super( model, globalState, createMicroTransform( model ), true, false );

        //List of the kits the user can choose from, for showing the appropriate bar charts + controls
        final KitList kitList = new KitList( model, transform );

        //Show the concentration bar chart behind the shaker so the user can drag the shaker in front
        ExpandableConcentrationBarChartNode concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.showConcentrationValues ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
            model.selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer kit ) {
                    setBars( kitList.getKit( kit ).barItems );
                }
            } );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Control that shows the Remove Solute button, which appears when any solutes of any type are in solution
        //Show it to the right of the evaporation slider, below the beaker so it doesn't overlap (or get overlapped by) any solutes
        //Reads "remove solute" if one solute type, "remove solutes" if two solute types
        addChild( new RemoveSolutesButton( "Remove solute", model.numberSoluteTypes.valueEquals( 1.0 ), model ) {{
            setOffset( evaporationSlider.getFullBounds().getMaxX() + INSET, evaporationSlider.getFullBounds().getY() );
        }} );
        addChild( new RemoveSolutesButton( "Remove solutes", model.numberSoluteTypes.greaterThan( 1 ), model ) {{
            setOffset( evaporationSlider.getFullBounds().getMaxX() + INSET, evaporationSlider.getFullBounds().getY() );
        }} );

        //Show the kit control node that allows the user to scroll through different kits
        final PNode microKitControlNode = new ZeroOffsetNode( new MicroKitControlNode( model.selectedKit, model.dispenserType ) {{
            model.addResetListener( new VoidFunction0() {
                public void apply() {
                    kitSelectionNode.selectedKit.set( 0 );
                }
            } );
        }} );

        microKitControlNode.setOffset( concentrationBarChart.getFullBounds().getX() - microKitControlNode.getFullBounds().getWidth() - INSET - 10, concentrationBarChart.getFullBounds().getY() );
        behindShakerNode.addChild( microKitControlNode );

        //Add a button that shows the periodic table when pressed
        final TextButtonNode periodicTableButton = new TextButtonNode( SHOW_IN_PERIODIC_TABLE ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //Only create the periodic table dialog once
                    if ( periodicTableDialog == null ) {
                        periodicTableDialog = new PeriodicTableDialog( model.dispenserType, globalState.colorScheme, globalState.frame ) {{
                            centerInParent( this );
                        }};
                    }

                    //Show the dialog window with the periodic table
                    periodicTableDialog.setVisible( true );
                }
            } );

            //Put the button near the solute selection node, since it controls what is highlighted in the periodic table
            setOffset( microKitControlNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, microKitControlNode.getFullBounds().getMaxY() + INSET );
        }};
        behindShakerNode.addChild( periodicTableButton );

        //Hide the periodic table on reset, and set it to null so it will come up in the default location next time
        model.addResetListener( new VoidFunction0() {
            public void apply() {
                if ( periodicTableDialog != null ) {
                    periodicTableDialog.dispose();
                    periodicTableDialog = null;
                }
            }
        } );

        //When any spherical particle is added in the model, add graphics for them in the view
        model.sphericalParticles.addElementAddedObserver( new SphericalParticleNodeFactory( model.sphericalParticles, transform, this, model.showChargeColor ) );

        //Add clock controls for pause/play/step
        addChild( new FloatingClockControlNode( model.playButtonPressed, NO_READOUT, model.clock, "", new Property<Color>( Color.white ) ) {{
            setOffset( 0, stageSize.getHeight() - getFullBounds().getHeight() );
        }} );

        //For debugging, show the location of binding sites
        if ( debugBindingSites ) {
            addChild( new BindingSiteDebugger( transform, model ) );
        }
    }

    //If the periodic table dialog was showing when the user switched away from this tab, restore it
    public void activated() {
        if ( periodicTableDialog != null && dialogVisibleOnActivate ) {
            periodicTableDialog.setVisible( true );
        }
    }

    //When the user switches to another tab, remember whether the periodic table dialog was showing so it can be restored if necessary.
    public void deactivated() {
        if ( periodicTableDialog != null ) {
            dialogVisibleOnActivate = periodicTableDialog.isVisible();
            periodicTableDialog.setVisible( false );
        }
    }

    //See docs in MacroCanvas.createMacroTransform, this variant is used to create the transform for the micro tab
    public static ModelViewTransform createMicroTransform( SugarAndSaltSolutionModel model ) {
        double modelScale = 0.75;
        return createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                //Manually tuned so that the model part shows up in the left side of the canvas,
                                                // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                //Must be further to the right than the Macro canvas transform since the beaker labels take up more horizontal space
                                                new Rectangle2D.Double(

                                                        //X position.  This number is hand tuned when the font, size location or style of the tick labels on the left of the beaker are changed
                                                        42,

                                                        //y-position: increasing this number moves down the beaker
                                                        135,
                                                        canvasSize.width * modelScale, canvasSize.height * modelScale ) );
    }
}