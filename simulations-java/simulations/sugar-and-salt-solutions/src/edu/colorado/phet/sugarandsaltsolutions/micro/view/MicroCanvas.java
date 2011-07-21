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
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.StandardizedNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.SettableNot.not;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerInParent;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends SugarAndSaltSolutionsCanvas implements Module.Listener {
    private PeriodicTableDialog periodicTableDialog;
    private boolean dialogVisibleOnActivate;
    private ExpandableConcentrationBarChartNode concentrationBarChart;

    public MicroCanvas( final MicroModel model, final GlobalState globalState ) {
        super( model, globalState, createMicroTransform( model ) );

        //Show the concentration bar chart behind the shaker so the user can drag the shaker in front
        concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart,
                                                                         model.sodiumConcentration,
                                                                         model.sodiumColor,
                                                                         model.chlorideConcentration,
                                                                         model.chlorideColor,
                                                                         model.showConcentrationValues,
                                                                         transform,
                                                                         model.showChargeColor ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Show the kit control node that allows the user to scroll through different kits
        PNode microKitControlNode = new StandardizedNode( new MicroKitControlNode( model.selectedKit, model.dispenserType ) {{
            model.addResetListener( new VoidFunction0() {
                public void apply() {
                    kitSelectionNode.selectedKit.set( 0 );
                }
            } );
        }} );
        microKitControlNode.setOffset( concentrationBarChart.getFullBounds().getX() - microKitControlNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );
        addChild( microKitControlNode );

        //Add a button that shows the periodic table when pressed
        final TextButtonNode periodicTableButton = new TextButtonNode( "Show in Periodic Table" ) {{
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
            //Put the button near the other controls, on the right side of the screen
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), stageSize.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( periodicTableButton );

        //Hide the periodic table on reset, and set it to null so it will come up in the default location next time
        model.addResetListener( new VoidFunction0() {
            public void apply() {
                if ( periodicTableDialog != null ) {
                    periodicTableDialog.dispose();
                    periodicTableDialog = null;
                }
            }
        } );

        //Checkbox to toggle whether the color shown is based on charge or atom identity
        addChild( new PSwing( new PropertyCheckBox( "Show Charge", model.showChargeColor ) {{
            setBackground( new Color( 0, 0, 0, 0 ) );
            setFont( CONTROL_FONT );
            setForeground( Color.white );
        }} ) {{
            setOffset( periodicTableButton.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, periodicTableButton.getFullBounds().getMaxY() + INSET );
        }} );

        //When sodium or chloride ions are added in the model, add graphics for them in the view
        model.sphericalParticles.addItemAddedListener( new SphericalParticleNodeFactory( model.sphericalParticles, transform, this, model.showChargeColor ) );

        //Add clock controls for pause/play/step
        addChild( new FloatingClockControlNode( not( model.clockPausedProperty ), new Function1<Double, String>() {
            public String apply( Double time ) {
                return "";
            }
            //TODO: get rid of clear button if unused
        }, model.clock, "", new Property<Color>( Color.white ) ) {{
            setOffset( 0, stageSize.getHeight() - getFullBounds().getHeight() );
        }} );
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
    //TODO: see if code can be consolidated with the macro version
    public static ModelViewTransform createMicroTransform( SugarAndSaltSolutionModel model ) {
        double modelScale = 0.75;
        System.out.println( "model.visibleRegion.toRectangle2D() = " + model.visibleRegion.toRectangle2D() );
        final ModelViewTransform transform = createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                                              //Manually tuned so that the model part shows up in the left side of the canvas,
                                                                              // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                                              new Rectangle2D.Double( 20,
                                                                                                      //y-position: increasing this number moves down the beaker
                                                                                                      135,
                                                                                                      canvasSize.width * modelScale, canvasSize.height * modelScale ) );
        System.out.println( "transform = " + transform );
        return transform;
    }
}