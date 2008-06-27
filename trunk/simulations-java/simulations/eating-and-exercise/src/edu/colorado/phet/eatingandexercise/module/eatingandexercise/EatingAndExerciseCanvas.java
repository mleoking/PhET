/* Copyright 2007, University of Colorado */

package edu.colorado.phet.eatingandexercise.module.eatingandexercise;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseConstants;
import edu.colorado.phet.eatingandexercise.control.CaloriePanel;
import edu.colorado.phet.eatingandexercise.control.HumanControlPanel;
import edu.colorado.phet.eatingandexercise.model.Human;
import edu.colorado.phet.eatingandexercise.view.HumanAreaNode;
import edu.colorado.phet.eatingandexercise.view.ScaleNode;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

public class EatingAndExerciseCanvas extends BufferedPhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private EatingAndExerciseModel _model;

    // View
    private PNode _rootNode;

    public static final double CANVAS_WIDTH = 4.6;
    public static final double CANVAS_HEIGHT = CANVAS_WIDTH * ( 3.0d / 4.0d );

    // Translation factors, used to set origin of canvas area.
    private RulerNode rulerNode;
    private PSwing humanControlPanelPSwing;
    private HumanAreaNode humanAreaNode;
    private BMIHelpButtonNode heartHealthButtonNode;
    private CaloriePanel caloriePanel;
    private BMIReadout bmiReadout;
    private TimeoutWarningMessage ageRangeMessage;

    private HumanControlPanel humanControlPanel;
    private StarvingMessage starvingMessage;
    private HeartAttackMessage heartAttackMessage;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public EatingAndExerciseCanvas( final EatingAndExerciseModel model, final Frame parentFrame ) {
        super( new PDimension( 15, 15 ) );
//        super( new PDimension( 10, 10 ) );

        // Set the transform strategy in such a way that the center of the
        // visible canvas will be at 0,0.

        _model = model;

        setBackground( EatingAndExerciseConstants.CANVAS_BACKGROUND );
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
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        _rootNode.addChild( new ScaleNode( model, model.getHuman() ) );
        humanAreaNode = new HumanAreaNode( model.getHuman() );

        _rootNode.addChild( humanAreaNode );

        bmiReadout = new BMIReadout( model.getHuman() );
//        addScreenChild( bmiReadout );

        heartHealthButtonNode = new BMIHelpButtonNode( this, model.getHuman() );
        addScreenChild( heartHealthButtonNode );

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

        rulerNode = createRulerNode();
//        addWorldChild( rulerNode );

        humanControlPanel = new HumanControlPanel( model, model.getHuman() );
        humanControlPanel.addListener( new HumanControlPanel.Listener() {
            public void ageManuallyChanged() {
                caloriePanel.clearAndResetDomains();
            }
        } );
//        humanControlPanel.addListener(){
        humanControlPanelPSwing = new PSwing( humanControlPanel );
        addScreenChild( humanControlPanelPSwing );

        caloriePanel = new CaloriePanel( model, this, parentFrame );
        caloriePanel.setOffset( humanControlPanelPSwing.getFullBounds().getWidth(), 0 );
        addScreenChild( caloriePanel );

        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        setWorldTransformStrategy( new EatingAndExerciseRenderingSizeStrategy( this, CANVAS_WIDTH, CANVAS_HEIGHT ) );

        ageRangeMessage = new AgeRangeMessage( model.getHuman() );
        addScreenChild( ageRangeMessage );

        starvingMessage = new StarvingMessage( model.getHuman() );
        addScreenChild( starvingMessage );

        heartAttackMessage=new HeartAttackMessage(model.getHuman());
        addScreenChild( heartAttackMessage );
    }

    private void updateHeartHealthButtonNodeLayout() {
        Point2D pt = humanAreaNode.getHeartNode().getGlobalFullBounds().getOrigin();
        pt.setLocation( pt.getX() + humanAreaNode.getHeartNode().getGlobalFullBounds().getWidth(), pt.getY() + 5 );
        bmiReadout.setOffset( pt );
        heartHealthButtonNode.setOffset( bmiReadout.getFullBounds().getX(), bmiReadout.getFullBounds().getMaxY() );
    }

    private RulerNode createRulerNode() {
        final RulerNode rulerNode = new RulerNode( 1, 0.1, 0.1, new String[]{"0.0", "0.25", "0.5", "0.75", "1.0"}, new PhetFont(), "m", new PhetFont(), 4, 0.03, 0.01 );
        rulerNode.rotate( Math.PI * 3 / 2 );
        rulerNode.addInputEventListener( new PDragEventHandler() );

        rulerNode.addInputEventListener( new CursorHandler() );
        rulerNode.setBackgroundStroke( new BasicStroke( 0.005f ) );
        rulerNode.setFontScale( 0.005 );
        rulerNode.setUnitsSpacing( 0.001 );
        rulerNode.setTickStroke( new BasicStroke( 0.005f ) );
        rulerNode.setOffset( -0.6653250773993821, 0.1145510835913312 );
        return rulerNode;
    }

    public HumanAreaNode getHumanAreaNode() {
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
        starvingMessage.setOffset( humanAreaNode.getGlobalFullBounds().getMaxX(), humanAreaNode.getGlobalFullBounds().getCenterY() );
        heartAttackMessage.setOffset( starvingMessage.getFullBounds().getX(), starvingMessage.getFullBounds().getMaxY() );
        //XXX lay out nodes

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
}