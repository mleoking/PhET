// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.StandardizedNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.common.view.barchart.BarItem;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.Ethanol;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.periodictable.PeriodicTableDialog;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerInParent;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.BeakerNodeWithTicks.volumeToHTMLString;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends SugarAndSaltSolutionsCanvas implements Module.Listener {
    private PeriodicTableDialog periodicTableDialog;
    private boolean dialogVisibleOnActivate;

    //If set to true, will highlight regions where crystals can be grown
    private boolean debugBindingSites = false;

    public MicroCanvas( final MicroModel model, final GlobalState globalState ) {
        super( model, globalState, createMicroTransform( model ), new Function1<Double, String>() {
            public String apply( Double volumeInMetersCubed ) {
                return volumeToHTMLString( volumeInMetersCubed, "0.00" );
            }
        } );

        //Show the concentration bar chart behind the shaker so the user can drag the shaker in front
        ExpandableConcentrationBarChartNode concentrationBarChart = new ExpandableConcentrationBarChartNode(
                model.showConcentrationBarChart,
                model.showConcentrationValues
        ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
            model.selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer kit ) {

                    //This is the logic for which components are present within each kit.  If kits change, this will need to be updated
                    //Put the positive ions to the left of the negative ions
                    if ( kit == 0 ) {
                        setBars( new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                                 new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ),
                                 new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Sucrose( ZERO ), model.showChargeColor ) ) ) );
                    }
                    else if ( kit == 1 ) {
                        setBars( new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                                 new BarItem( model.calciumConcentration, model.calciumColor, CALCIUM, new Some<PNode>( new SphericalParticleNode( transform, new Calcium(), model.showChargeColor ) ) ),
                                 new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ) );
                    }
                    else if ( kit == 2 ) {
                        setBars( new BarItem( model.sodiumConcentration, model.sodiumColor, SODIUM, new Some<PNode>( new SphericalParticleNode( transform, new Sodium(), model.showChargeColor ) ) ),
                                 new BarItem( model.chlorideConcentration, model.chlorideColor, CHLORIDE, new Some<PNode>( new SphericalParticleNode( transform, new Chloride(), model.showChargeColor ) ) ),
                                 new BarItem( model.nitrateConcentration, model.nitrateColor, NITRATE, new Some<PNode>( new CompositeParticleNode<Particle>( transform, new Nitrate( 0, ImmutableVector2D.ZERO ), model.showChargeColor ) ) ) );
                    }
                    else if ( kit == 3 ) {
                        setBars( new BarItem( model.sucroseConcentration, model.sucroseColor, SUCROSE, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Sucrose(), model.showChargeColor ) ) ),
                                 new BarItem( model.ethanolConcentration, model.ethanolColor, ETHANOL, new Some<PNode>( new CompositeParticleNode<SphericalParticle>( transform, new Ethanol(), model.showChargeColor ) ) ) );
                    }
                }
            } );
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

        //TODO: Why is the -10 needed in the next line?
        microKitControlNode.setOffset( concentrationBarChart.getFullBounds().getX() - microKitControlNode.getFullBounds().getWidth() - INSET - 10, concentrationBarChart.getFullBounds().getY() );
        behindShakerNode.addChild( microKitControlNode );

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

        //When any spherical particle is added in the model, add graphics for them in the view
        model.sphericalParticles.addItemAddedListener( new SphericalParticleNodeFactory( model.sphericalParticles, transform, this, model.showChargeColor ) );

        //Add clock controls for pause/play/step
        addChild( new FloatingClockControlNode( model.clockRunning, new Function1<Double, String>() {
            public String apply( Double time ) {
                return "";
            }
            //TODO: get rid of clear button if unused
        }, model.clock, "", new Property<Color>( Color.white ) ) {{
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
    //TODO: see if code can be consolidated with the macro version
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