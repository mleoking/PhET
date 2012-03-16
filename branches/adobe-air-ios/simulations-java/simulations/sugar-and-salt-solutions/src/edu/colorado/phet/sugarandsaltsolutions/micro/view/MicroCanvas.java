// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerAndShakerCanvas;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable.PeriodicTableDialog;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused by extending BeakerAndShakerCanvas.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends BeakerAndShakerCanvas implements Module.Listener {
    private PeriodicTableDialog periodicTableDialog;
    private boolean dialogVisibleOnActivate;

    //If set to true, will highlight regions where crystals can be grown
    private final boolean debugBindingSites = false;

    private final PNode microKitControlNode;

    //Keep track of the global state to access the PhetFrame to position the Periodic Table Dialog
    private final GlobalState globalState;

    public MicroCanvas( final MicroModel model, final GlobalState globalState ) {
        super( model, globalState, createMicroTransform( model ), true, false );
        this.globalState = globalState;

        //List of the kits the user can choose from, for showing the appropriate bar charts + controls
        final MicroSoluteKitList kitList = new MicroSoluteKitList( model, transform );

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
        addChild( new RemoveSolutesButton( REMOVE_SOLUTE, model.numberSoluteTypes.valueEquals( 1.0 ), model ) {{
            setOffset( evaporationSlider.getFullBounds().getMaxX() + INSET, evaporationSlider.getFullBounds().getY() );
        }} );
        addChild( new RemoveSolutesButton( REMOVE_SOLUTES, model.numberSoluteTypes.greaterThan( 1 ), model ) {{
            setOffset( evaporationSlider.getFullBounds().getMaxX() + INSET, evaporationSlider.getFullBounds().getY() );
        }} );

        //A button that shows the periodic table when pressed, shown inside the kit selection node since the selected item controls what is highlighted in the periodic table
        final TextButtonNode periodicTableButton = new TextButtonNode( PERIODIC_TABLE, CONTROL_FONT, Color.yellow ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    //Only create the periodic table dialog once
                    if ( periodicTableDialog == null ) {
                        periodicTableDialog = new PeriodicTableDialog( model.dispenserType, globalState.colorScheme, globalState.frame ) {{

                            //Show the periodic table dialog to the left side of the window and underneath the solute control
                            Rectangle parentBounds = globalState.frame.getBounds();

                            //Setting the X to be the same as the parent X actually has the incorrect effect of moving it about 15 pixels to the left of the main frame, so translate to compensate for this bug
                            Rectangle dialogBounds = new Rectangle( (int) ( parentBounds.getMinX() + 15 ),
                                                                    (int) ( parentBounds.getMinY() + getKitControlNodeY() + INSET * 2 ),
                                                                    getWidth(), getHeight() );
                            setLocation( dialogBounds.x, dialogBounds.y );
                        }};
                    }

                    //Show the dialog window with the periodic table
                    periodicTableDialog.setVisible( true );
                }
            } );
        }};

        //Show the kit control node that allows the user to scroll through different kits
        microKitControlNode = new ZeroOffsetNode( new MicroKitControlNode( model.selectedKit, model.dispenserType, periodicTableButton, globalState.singleMicroKit ) {{
            model.addResetListener( new VoidFunction0() {
                public void apply() {
                    kitSelectionNode.selectedKit.set( 0 );
                }
            } );
        }} );

        microKitControlNode.setOffset( concentrationBarChart.getFullBounds().getX() - microKitControlNode.getFullBounds().getWidth() - INSET - 10, concentrationBarChart.getFullBounds().getY() );
        behindShakerNode.addChild( microKitControlNode );

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
        addChild( new FloatingClockControlNode( model.clockRunning, NO_READOUT, model.clock, "", new Property<Color>( Color.white ) ) {{
            setOffset( 0, stageSize.getHeight() - getFullBounds().getHeight() );
        }} );

        //For debugging, show the location of binding sites
        if ( debugBindingSites ) {
            addChild( new BindingSiteDebugger( transform, model ) );
        }
    }

    //Get the bottom of solute kit control node for purposes of showing the periodic table beneath it
    private double getKitControlNodeY() {
        return microKitControlNode.getFullBounds().getMaxY() + SwingUtilities.convertPoint( this, 0, 0, globalState.frame ).getY();
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