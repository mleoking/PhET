// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.view.IonGraphic;
import edu.colorado.phet.solublesalts.view.IonGraphicManager;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.NegativeSugarIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SugarIon.PositiveSugarIon;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.centerInParent;
import static edu.colorado.phet.sugarandsaltsolutions.micro.view.SucroseImage.getSucroseImage;

/**
 * Canvas for the "micro" tab of the sugar and salt solutions sim.  This shares lots of functionality with the first tab, so much of that code is reused.
 *
 * @author Sam Reid
 */
public class MicroCanvas extends SugarAndSaltSolutionsCanvas implements Module.Listener {
    private final boolean debug = false;
    private PeriodicTableDialog periodicTableDialog;
    private boolean dialogVisibleOnActivate;

    //Enable the IonGraphicManager to create graphics for sucrose molecules
    static {
        IonGraphicManager.putImage( NegativeSugarIon.class, getSucroseImage() );
        IonGraphicManager.putImage( PositiveSugarIon.class, getSucroseImage() );
    }

    public MicroCanvas( final MicroModel model, final GlobalState globalState ) {
        super( model, globalState, createMicroTransform( model ) );

        //Add graphics for each ion.  Overriden here to map from soluble salts model coordinates -> sugar and salt model coordinates -> sugar and salt canvas coordinates
        model.addIonListener( new IonGraphicManager( getRootNode() ) {
            @Override protected IonGraphic createImage( Ion ion, BufferedImage image ) {
                return new IonGraphic( ion, image ) {
                    @Override protected void updateOffset( Point2D ionPosition ) {
                        //Map the soluble salts model coordinates through sugar and salt solution model coordinates to canvas coordinates
                        Point2D sugarModelPosition = model.solubleSaltsTransform.modelToView( ionPosition );
                        Point2D viewLocation = new Point2D.Double( transform.modelToView( sugarModelPosition ).getX() - pImage.getWidth() / 2,
                                                                   transform.modelToView( sugarModelPosition ).getY() - pImage.getHeight() / 2 );
                        setOffset( viewLocation );
                    }
                };
            }
        } );

        //Show a debug path for the fluid volume, to help make sure the coordinate frames are correct
        if ( debug ) {
            addChild( new PhetPPath( new Color( 255, 0, 0, 128 ) ) {{
                final SimpleObserver updatePath = new SimpleObserver() {
                    public void update() {
                        setPathTo( transform.modelToView( model.solubleSaltsTransform.modelToView( model.getSolubleSaltsModel().getVessel().getWater().getBounds() ) ) );
                    }
                };
                //Update the path continuously in case the mode changed it for any reason that we couldn't observe
                new javax.swing.Timer( 100, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        updatePath.update();
                    }
                } ).start();
                model.waterVolume.addObserver( updatePath );
            }} );
        }

        //Add a button that shows the periodic table when pressed
        addChild( new TextButtonNode( "Show in Periodic Table" ) {{
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
        }} );

        //When sodium or chloride ions are added in the model, add graphics for them in the view
        model.sodiumList.addItemAddedListener( new ParticleNodeFactory( model.sodiumList, transform, this ) );
        model.chlorideList.addItemAddedListener( new ParticleNodeFactory( model.chlorideList, transform, this ) );
    }

    //Create a user interface element that lets the user choose solutes from a drop-down box
    @Override protected SoluteControlPanelNode createSoluteControlPanelNode( SugarAndSaltSolutionModel model, PSwingCanvas canvas, PDimension stageSize ) {
        return new ComboBoxSoluteControlPanelNode( model.dispenserType );
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