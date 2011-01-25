// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.dialogs.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseConstants;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseResources;
import edu.colorado.phet.eatingandexercise.control.CaloriePanel;
import edu.colorado.phet.eatingandexercise.control.ChartNode;
import edu.colorado.phet.eatingandexercise.control.HumanControlPanel;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.view.EatingAndExerciseColorScheme;
import edu.colorado.phet.eatingandexercise.view.HealthIndicator;
import edu.colorado.phet.eatingandexercise.view.HumanNode;
import edu.colorado.phet.eatingandexercise.view.ScaleNode;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

public class EatingAndExerciseCanvas extends BufferedPhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private EatingAndExerciseModel _model;

    // View
    private PNode _rootNode;

    private PSwing humanControlPanelPSwing;
    private HumanNode humanAreaNode;
    private BMIHelpButtonNode heartHealthButtonNode;
    private CaloriePanel caloriePanel;
    private BMIReadout bmiReadout;
    private TimeoutWarningMessage ageRangeMessage;

    private HumanControlPanel humanControlPanel;
    private StarvingMessage starvingMessage;
    private HeartAttackMessage heartAttackMessage;
    private HealthIndicator healthIndicator;
    private PhetPPath playAreaBackgroundNode;
    private boolean showColorChooser = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public EatingAndExerciseCanvas( final EatingAndExerciseModel model, final Frame parentFrame ) {
        super( new PDimension( 15, 15 ) );

        _model = model;

        setBackground( EatingAndExerciseConstants.BACKGROUND );
        if ( showColorChooser ) {
            showColorChooser( parentFrame );
        }
        // Root of our scene graph
        _rootNode = new PNode();


        _rootNode.addChild( new ScaleNode( model, model.getHuman() ) );
        humanAreaNode = new HumanNode( model.getHuman() );
        humanAreaNode.addListener( new HumanNode.Listener() {
            public void infoButtonPressed() {
                PhetOptionPane.showMessageDialog( EatingAndExerciseCanvas.this, EatingAndExerciseResources.getString( "heart.health.info" ) );
            }
        } );

        playAreaBackgroundNode = new PhetPPath( EatingAndExerciseColorScheme.getBackgroundColor(), new BasicStroke( 2 ), Color.gray );
        addScreenChild( playAreaBackgroundNode );

        addWorldChild( _rootNode );

        _rootNode.addChild( humanAreaNode );

        bmiReadout = new BMIReadout( model.getHuman() );
//        addScreenChild( bmiReadout );

        heartHealthButtonNode = new BMIHelpButtonNode( this, model.getHuman() );
//        addScreenChild( heartHealthButtonNode );

        updateHeartHealthButtonNodeLayout();
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateHeartHealthButtonNodeLayout();
            }
        } );
        model.getHuman().addListener( new Human.Adapter() {
            public void heightChanged() {
                updateHeartHealthButtonNodeLayout();
            }
        } );

        humanControlPanel = new HumanControlPanel( this, model, model.getHuman() );
        humanControlPanel.addListener( new HumanControlPanel.Listener() {
            public void ageManuallyChanged() {
                caloriePanel.clearAndResetDomains();
            }
        } );
//        humanControlPanel.addListener(){
        humanControlPanelPSwing = new PSwing( humanControlPanel );
        addScreenChild( humanControlPanelPSwing );

        healthIndicator = new HealthIndicator( model.getHuman() );
        addScreenChild( healthIndicator );

        caloriePanel = new CaloriePanel( model, this, parentFrame );
        addScreenChild( caloriePanel );

        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        setWorldTransformStrategy( new EatingAndExerciseRenderingSizeStrategy( this ) );

        ageRangeMessage = new AgeRangeMessage( model.getHuman() );
//        addScreenChild( ageRangeMessage );

        starvingMessage = new StarvingMessage( model.getHuman() );
        addScreenChild( starvingMessage );

        heartAttackMessage = new HeartAttackMessage( model.getHuman() );
        addScreenChild( heartAttackMessage );

//        setZoomEventHandler( new PZoomEventHandler() );

        updateLayout();
    }

    private void showColorChooser( final Frame parentFrame ) {
        getCamera().addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent aEvent ) {
                if ( aEvent.isLeftMouseButton() && aEvent.getPickedNode() instanceof PCamera ) {
                    ColorChooserFactory.showDialog( "background color", parentFrame, getBackground(), new ColorChooserFactory.Listener() {
                        public void colorChanged( Color color ) {
                            setBackground( color );
                        }

                        public void ok( Color color ) {
                            setBackground( color );
                        }

                        public void cancelled( Color originalColor ) {
                            setBackground( originalColor );
                        }
                    }, true );
                }
            }
        } );
    }

    private void updateHeartHealthButtonNodeLayout() {
        Point2D pt = humanAreaNode.getHeartNode().getGlobalFullBounds().getOrigin();
        pt.setLocation( pt.getX() + humanAreaNode.getHeartNode().getGlobalFullBounds().getWidth(), pt.getY() + 5 );
        bmiReadout.setOffset( pt );
        heartHealthButtonNode.setOffset( bmiReadout.getFullBounds().getX(), bmiReadout.getFullBounds().getMaxY() );
    }

    public HumanNode getHumanAreaNode() {
        return humanAreaNode;
    }
//----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------

    public PNode getEditDietButton() {
        return caloriePanel.getEditDietButton();
    }

    /*
     * Updates the layout of stuff on the canvas.
     */

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( EatingAndExerciseConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        humanControlPanelPSwing.setOffset( 0, getHeight() - humanControlPanelPSwing.getFullBounds().getHeight() );
        ageRangeMessage.setOffset( humanControlPanelPSwing.getFullBounds().getMaxX(), humanControlPanelPSwing.getFullBounds().getY() + humanControlPanel.getAgeSliderY() );
        starvingMessage.setOffset( humanAreaNode.getGlobalFullBounds().getCenterX() - starvingMessage.getFullBounds().getWidth() / 2, humanAreaNode.getGlobalFullBounds().getCenterY() );
        heartAttackMessage.setOffset( starvingMessage.getFullBounds().getX(), starvingMessage.getFullBounds().getMaxY() );
        updateHealthIndicatorLocation();

        caloriePanel.setOffset( humanControlPanelPSwing.getFullBounds().getWidth(), 0 );
        playAreaBackgroundNode.setPathToRectangle( 0, 0, (float) humanControlPanelPSwing.getFullBounds().getWidth(), getHeight() );
    }

    private void updateHealthIndicatorLocation() {
        healthIndicator.setOffset( 5, humanControlPanelPSwing.getFullBounds().getMinY() - healthIndicator.getFullBounds().getHeight() );
        Point2D center = humanAreaNode.getGlobalFullBounds().getCenter2D();
        healthIndicator.setOffset( 5, center.getY() );
    }

    public double getControlPanelWidth() {
        return humanControlPanelPSwing.getFullBounds().getWidth();
    }

    //reset any view settings
    public void resetAll() {
        caloriePanel.resetAll();
    }

    public double getHumanControlPanelHeight() {
        return humanControlPanelPSwing == null ? 0 : humanControlPanelPSwing.getFullBounds().getHeight();
    }

    public void applicationStarted() {
        caloriePanel.applicationStarted();
    }

    public void addEditorClosedListener( ActionListener actionListener ) {
        caloriePanel.addEditorClosedListener( actionListener );
    }

    public double getAvailableWorldHeight() {
        return getHeight() - humanControlPanelPSwing.getFullBounds().getHeight();
    }

    public double getAvailableWorldWidth() {
        return humanControlPanelPSwing.getFullBounds().getWidth() * 1.1;//okay to overlap by 10%
    }

    public double getControlPanelY() {
        return humanControlPanelPSwing.getFullBounds().getY();
    }

    public void addFoodPressedListener( ActionListener actionListener ) {
        caloriePanel.addFoodPressedListener( actionListener );
    }

    public PNode getPlateNode() {
        return caloriePanel.getPlateNode();
    }

    public void addExerciseDraggedListener( ActionListener actionListener ) {
        caloriePanel.addExerciseDraggedListener( actionListener );
    }

    public PNode getDiaryNode() {
        return caloriePanel.getDiaryNode();
    }

    public ChartNode getChartNode() {
        return caloriePanel.getChartNode();
    }
}