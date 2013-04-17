// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.PointToolNode;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * The reward that is displayed when a game is completed with a perfect score.
 * Various images (based on game level) move from top to bottom in the play area.
 * See the "Developer->Test game reward" menu item for testing and configuration of this node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RewardNode extends PhetPNode {

    private final ConstantDtClock clock; // clock that controls the animation

    // types of images
    private final ArrayList<Image> graphImages;
    private final ArrayList<Image> equationImages;
    private final ArrayList<Image> pointToolImages;
    private final ArrayList<Image> smileyFaceImages;
    private final ArrayList<Image> phetLogoImages;

    private final ArrayList<Image> imagePool; // images currently in use by the animation

    private int population; // number of images in the animation
    private int motionDelta; // maximum motion of each image
    private IMotionStrategy motionStrategy; // type of motion

    /**
     * An image node that has an associated motion delta.
     * The motion delta is used by an IMotionStrategy to determine
     * how far an MovingImageNode moves on each animation step.
     */
    private static class MovingImageNode extends PImage {

        private int motionDelta;

        public MovingImageNode( Image image, int motionDelta ) {
            super( image );
            this.motionDelta = motionDelta;
        }

        public int getMotionDelta() {
            return motionDelta;
        }

        public void setMotionDelta( int motionDelta ) {
            this.motionDelta = motionDelta;
        }
    }

    // Zero-args constructor, with defaults.
    public RewardNode() {
        this( new PBounds( 0, 0, 1024, 768 ), 150, 10, 40 );
    }

    /**
     * Constructor.
     *
     * @param bounds      images are constrained to motion within these bounds
     * @param population  how many images in the animation
     * @param motionDelta nominal motion delta, may be randomly adjusted for each specific image
     * @param clockDelay  large value causes slower animation
     */
    private RewardNode( PBounds bounds, int population, int motionDelta, int clockDelay ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        this.population = population;
        this.motionDelta = motionDelta;
        this.motionStrategy = new FallingMotionStrategy();

        this.clock = new ConstantDtClock( clockDelay, 1 );
        this.clock.pause();

        // images
        {
            graphImages = new ArrayList<Image>();
            equationImages = new ArrayList<Image>();
            pointToolImages = new ArrayList<Image>();
            smileyFaceImages = new ArrayList<Image>();
            phetLogoImages = new ArrayList<Image>();

            Color[] colors = new Color[] { Color.YELLOW, Color.RED, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.GREEN };
            for ( Color color : colors ) {
                graphImages.add( BufferedImageUtils.toImage( GraphNode.createYEqualsXIcon( 50, color ) ) );
                graphImages.add( BufferedImageUtils.toImage( GraphNode.createYEqualsNegativeXIcon( 50, color ) ) );
                pointToolImages.add( createPointToolImage( color ) );
                smileyFaceImages.add( createSmileyFaceImage( color ) );
                // add additional variations of equations
                for ( int i = 0; i < 5; i++ ) {
                    equationImages.add( createEquationImage( color ) );
                }
            }

            // PhET logo
            PNode phetLogoNode = new PImage( PhetCommonResources.getImage( "logos/phet-logo-120x50.jpg" ));
            phetLogoNode.scale( 1.0 );
            phetLogoImages.add( phetLogoNode.toImage() );

            // image pool, images that are in use by the animation
            imagePool = new ArrayList<Image>();
        }

        clock.addClockListener( new ClockAdapter() {
            @Override
            // update the animation when the clock ticks
            public void clockTicked( ClockEvent clockEvent ) {
                step();
            }
        } );

        // initial state, all image types visible
        setBounds( bounds );
        setGraphsVisible( true );
        setEquationsVisible( true );
        setPointToolsVisible( true );
        setPhETLogosVisible( true );
        setSmileyFacesVisible( true );
    }

    // Creates a random equation is the specified color.
    private Image createEquationImage( Color color ) {
        boolean useSlopeInterceptForm = ( Math.random() < 0.5 );
        final PhetFont font = new PhetFont( Font.BOLD, 24 );
        PNode node;
        if ( useSlopeInterceptForm ) {
            node = new SlopeInterceptEquationNode(
                    Line.createSlopeIntercept( getRandomNonZeroInteger( -20, 20 ),
                                               getRandomNonZeroInteger( -20, 20 ),
                                               getRandomNonZeroInteger( -20, 20 ) ),
                    font, color );
        }
        else {
            node = new PointSlopeEquationNode(
                    Line.createPointSlope( getRandomNonZeroInteger( -20, 20 ),
                                           getRandomNonZeroInteger( -20, 20 ),
                                           getRandomNonZeroInteger( -20, 20 ),
                                           getRandomNonZeroInteger( -20, 20 ) ),
                    font, color );
        }
        return new PadBoundsNode( node ).toImage();
    }

    private static int getRandomNonZeroInteger( int min, int max ) {
        int i = (int) ( min + ( Math.random() * ( max - min ) ) );
        if ( i == 0 ) {
            i = 1;
        }
        return i;
    }

    // Creates a point tool with a random point and the specified color.
    private Image createPointToolImage( Color color ) {
        Vector2D point = new Vector2D( Math.random() * 20, Math.random() * 20 );
        final PNode pointToolNode = new PointToolNode( point, Orientation.DOWN, color );
        pointToolNode.scale( 0.75 );
        return new PadBoundsNode( pointToolNode ).toImage();
    }

    // Creates a smiley face with the specified color.
    private Image createSmileyFaceImage( Color color ) {
        return new PadBoundsNode( new FaceNode( 40, color ) ).toImage();
    }

    /**
     * Sets the animation parameters based on game difficulty level.
     * Levels 1-5 each have one image; level 6 is all images.
     *
     * @param level the game level
     */
    public void setLevel( int level ) {
        assert( level >= 1 && level <= 6 );
        setImagesVisible( equationImages, level == 1 || level == 6 );
        setImagesVisible( graphImages, level == 2 || level == 6 );
        setImagesVisible( pointToolImages, level == 3 || level == 6 );
        setImagesVisible( smileyFaceImages, level == 4 || level == 6 );
        setImagesVisible( phetLogoImages, level == 5 || level == 6 );
    }

    /**
     * When this node's bounds are changes, all images are repopulated
     * so that the images are distributed randomly throughout the entire bounds.
     *
     * @param bounds the bounds that the reward must fill
     */
    @Override public boolean setBounds( Rectangle2D bounds ) {
        if ( bounds.isEmpty() ) {
            throw new IllegalArgumentException( "bounds are empty" );
        }
        boolean boundsChanged = false;
        if ( !bounds.equals( getBounds() ) ) {
            boundsChanged = super.setBounds( bounds );
            updateImages( true /* removeImages */ );
        }
        return boundsChanged;
    }

    private void setClockDelay( int delay ) {
        clock.setDelay( delay );
    }

    private int getClockDelay() {
        return clock.getDelay();
    }

    private void setPopulation( int population ) {
        if ( !( population > 0 ) ) {
            throw new IllegalArgumentException( "population must be > 0: " + population );
        }
        if ( population != getPopulation() ) {
            this.population = population;
            updateImages( false /* removeImages */ );
        }
    }

    private int getPopulation() {
        return population;
    }

    private void setMotionDelta( int motionDelta ) {
        if ( !( motionDelta > 0 ) ) {
            throw new IllegalArgumentException( "motionDelta must be > 0: " + motionDelta );
        }
        if ( motionDelta != getMotionDelta() ) {
            this.motionDelta = motionDelta;
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                if ( getChild( i ) instanceof MovingImageNode ) {
                    int delta = (int) Math.max( 1, Math.random() * motionDelta );
                    ( (MovingImageNode) getChild( i ) ).setMotionDelta( delta );
                }
            }
        }
    }

    private int getMotionDelta() {
        return motionDelta;
    }

    private boolean isGraphsVisible() {
        return isImagesVisible( graphImages );
    }

    private void setGraphsVisible( boolean visible ) {
        setImagesVisible( graphImages, visible );
    }

    private boolean isEquationsVisible() {
        return isImagesVisible( equationImages );
    }

    private void setEquationsVisible( boolean visible ) {
        setImagesVisible( equationImages, visible );
    }

    private boolean isPointToolsVisible() {
        return isImagesVisible( pointToolImages );
    }

    private void setPointToolsVisible( boolean visible ) {
        setImagesVisible( pointToolImages, visible );
    }

    private boolean isPhETLogosVisible() {
        return isImagesVisible( phetLogoImages );
    }

    private void setPhETLogosVisible( boolean visible ) {
        setImagesVisible( phetLogoImages, visible );
    }

    private boolean isSmileyFacesVisible() {
        return isImagesVisible( smileyFaceImages );
    }

    private void setSmileyFacesVisible( boolean visible ) {
        setImagesVisible( smileyFaceImages, visible );
    }

    private void setImagesVisible( ArrayList<Image> images, boolean visible ) {
        if ( visible ) {
            imagePool.addAll( images );
        }
        else {
            imagePool.removeAll( images );
        }
        updateImages( true /* removeImages */ );
    }

    private boolean isImagesVisible( ArrayList<Image> images ) {
        return imagePool.contains( images.get( 0 ) );
    }

    private void setMotionStrategy( IMotionStrategy motionStrategy ) {
        this.motionStrategy = motionStrategy;
    }

    /*
     * Updates the image nodes to match the current population.
     * If removeImages is true, all existing nodes are removed.
     * Otherwise, nodes are added/removed to match the population.
     * The last node added is the first one removed.
     */
    private void updateImages( boolean removeImages ) {
        if ( removeImages ) {
            removeAllChildren(); // assume that this node has only MovingImages as children
        }
        if ( imagePool.size() > 0 ) {
            if ( getChildrenCount() > population ) {
                // remove some nodes
                while ( getChildrenCount() > population ) {
                    removeChild( getChildrenCount() - 1 );
                }
            }
            else {
                // add some nodes
                while ( getChildrenCount() < population ) {
                    addRandomNode();
                }
            }
        }
    }

    /*
     * Adds a random node, with a random motion delta, at a random location within this node's bounds.
     */
    private void addRandomNode() {

        // choose a random motion delta
        int delta = (int) Math.max( 1, Math.random() * motionDelta );

        // choose a random image
        int index = (int) ( Math.random() * imagePool.size() );
        MovingImageNode imageNode = new MovingImageNode( imagePool.get( index ), delta );
        addChild( imageNode );

        // set a random location within the bounds
        PBounds bounds = getBoundsReference();
        Point2D location = getRandomPoint( bounds );
        double x = location.getX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        double y = location.getY() - ( imageNode.getFullBoundsReference().getHeight() / 2 );
        imageNode.setOffset( x, y );
    }

    /*
     * Gets a random point within a specified bounds.
     */
    private Point2D getRandomPoint( PBounds bounds ) {
        double x = bounds.getX() + ( Math.random() * bounds.getWidth() );
        double y = bounds.getY() + ( Math.random() * bounds.getHeight() );
        return new Point2D.Double( x, y );
    }

    // This node plays when it's visible, pauses when it's invisible.
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        setRunning( visible );
    }

    // Sets whether the animation is running.
    public void setRunning( boolean running ) {
        if ( running ) {
            clock.start();
        }
        else {
            clock.pause();
        }
    }

    // Is the animation running?
    public boolean isRunning() {
        return clock.isRunning();
    }

    /*
     * Performs one animation step for all MovingImageNodes.
     */
    private void step() {
        PBounds bounds = getBoundsReference();
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            if ( getChild( i ) instanceof MovingImageNode ) {
                motionStrategy.step( (MovingImageNode) getChild( i ), bounds );
            }
        }
    }

    /**
     * Interface implemented by all motion strategies.
     */
    public interface IMotionStrategy {
        // Moves a node within the specified bounds.
        public void step( MovingImageNode node, PBounds bounds );
    }

    /**
     * Node jitters around, doing a simple random walk.
     */
    public static class JitteryMotionStrategy implements IMotionStrategy {

        public void step( MovingImageNode node, PBounds bounds ) {
            // walk a distance in a random direction
            double x = node.getXOffset() + ( getRandomDirection() * node.getMotionDelta() );
            double y = node.getYOffset() + ( getRandomDirection() * node.getMotionDelta() );
            // constrain to the bounds
            x = Math.max( bounds.getMinX(), Math.min( x, bounds.getMaxX() ) );
            y = Math.max( bounds.getMinY(), Math.min( y, bounds.getMaxY() ) );
            node.setOffset( x, y );
        }

        private int getRandomDirection() {
            return ( Math.random() > 0.5 ) ? 1 : -1;
        }
    }

    /**
     * Node falls vertically.
     */
    public static class FallingMotionStrategy implements IMotionStrategy {
        public void step( MovingImageNode node, PBounds bounds ) {
            double y = node.getYOffset() + node.getMotionDelta();
            if ( y > bounds.getMaxY() ) {
                y = bounds.getMinY() - node.getFullBoundsReference().getHeight();
            }
            node.setOffset( node.getXOffset(), y );
        }
    }

    /**
     * This application was provided to team members so that they could experiment with settings.
     * During one stage of development, it was published to dev as flavor "test-game-reward".
     */
    public static void main( String[] args ) {

        // parameters that control the animation
        final IntegerRange populationRange = new IntegerRange( 50, 1000, 500 );
        final IntegerRange motionDeltaRange = new IntegerRange( 1, 20, 5 );
        final IntegerRange clockDelayRange = new IntegerRange( 10, 200, 60 );

        PBounds bounds = new PBounds( 0, 0, 100, 100 );
        final RewardNode rewardNode = new RewardNode( bounds, populationRange.getDefault(), motionDeltaRange.getDefault(), clockDelayRange.getDefault() );
        rewardNode.setRunning( true );

        final PhetPCanvas canvas = new PhetPCanvas() {
            // when the canvas size changes, update the reward node's bounds
            @Override protected void updateLayout() {
                PBounds worldBounds = getWorldBounds();
                if ( worldBounds.getWidth() > 0 && worldBounds.getHeight() > 0 ) {
                    rewardNode.setBounds( worldBounds );
                }
            }
        };
        final PDimension stageSize = new PDimension( 1008, 679 );
        canvas.setWorldTransformStrategy( new CenteredStage( canvas, stageSize ) );
        canvas.setBackground( LGColors.CANVAS );
        canvas.addWorldChild( rewardNode );

        final IMotionStrategy jitteryMotionStrategy = new JitteryMotionStrategy();
        final IMotionStrategy fallingMotionStrategy = new FallingMotionStrategy();

        final JRadioButton jitteryMotionButton = new JRadioButton( "jittery" );
        jitteryMotionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setMotionStrategy( jitteryMotionStrategy );
            }
        } );

        final JRadioButton fallingMotionButton = new JRadioButton( "falling" );
        fallingMotionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setMotionStrategy( fallingMotionStrategy );
            }
        } );

        ButtonGroup motionGroup = new ButtonGroup();
        motionGroup.add( jitteryMotionButton );
        motionGroup.add( fallingMotionButton );

        rewardNode.setMotionStrategy( fallingMotionStrategy );
        fallingMotionButton.setSelected( true );

        JPanel motionControlPanel = new VerticalLayoutPanel();
        motionControlPanel.setBorder( new TitledBorder( "Motion Strategy" ) );
        motionControlPanel.add( jitteryMotionButton );
        motionControlPanel.add( fallingMotionButton );

        final JCheckBox graphsCheckBox = new JCheckBox( "graphs" );
        graphsCheckBox.setSelected( rewardNode.isGraphsVisible() );
        graphsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setGraphsVisible( graphsCheckBox.isSelected() );
            }
        } );

        final JCheckBox equationsCheckBox = new JCheckBox( "equations" );
        equationsCheckBox.setSelected( rewardNode.isEquationsVisible() );
        equationsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setEquationsVisible( equationsCheckBox.isSelected() );
            }
        } );

        final JCheckBox pointToolsCheckBox = new JCheckBox( "point tools" );
        pointToolsCheckBox.setSelected( rewardNode.isPointToolsVisible() );
        pointToolsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setPointToolsVisible( pointToolsCheckBox.isSelected() );
            }
        } );

        final JCheckBox phetLogosCheckBox = new JCheckBox( "PhET logos" );
        phetLogosCheckBox.setSelected( rewardNode.isPhETLogosVisible() );
        phetLogosCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setPhETLogosVisible( phetLogosCheckBox.isSelected() );
            }
        } );

        final JCheckBox smileyFacesCheckBox = new JCheckBox( "smiley faces" );
        smileyFacesCheckBox.setSelected( rewardNode.isSmileyFacesVisible() );
        smileyFacesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setSmileyFacesVisible( smileyFacesCheckBox.isSelected() );
            }
        } );

        JPanel objectsControlPanel = new VerticalLayoutPanel();
        objectsControlPanel.setBorder( new TitledBorder( "Objects" ) );
        objectsControlPanel.add( graphsCheckBox );
        objectsControlPanel.add( equationsCheckBox );
        objectsControlPanel.add( pointToolsCheckBox );
        objectsControlPanel.add( phetLogosCheckBox );
        objectsControlPanel.add( smileyFacesCheckBox );

        final LinearValueControl populationControl = new LinearValueControl( populationRange.getMin(), populationRange.getMax(), "population:", "##0", "" );
        populationControl.setBorder( BorderFactory.createEtchedBorder() );
        populationControl.setValue( populationRange.getDefault() );
        populationControl.setToolTipText( "how many images are in the play area" );
        populationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setPopulation( (int) populationControl.getValue() );
            }
        } );

        final LinearValueControl motionDeltaControl = new LinearValueControl( motionDeltaRange.getMin(), motionDeltaRange.getMax(), "motion delta:", "##0", "pixels" );
        motionDeltaControl.setBorder( BorderFactory.createEtchedBorder() );
        motionDeltaControl.setValue( motionDeltaRange.getDefault() );
        motionDeltaControl.setToolTipText( "how far images move each time the clock ticks" );
        motionDeltaControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setMotionDelta( (int) motionDeltaControl.getValue() );
            }
        } );

        final LinearValueControl clockDelayControl = new LinearValueControl( clockDelayRange.getMin(), clockDelayRange.getMax(), "clock delay:", "##0", "ms" );
        clockDelayControl.setBorder( BorderFactory.createEtchedBorder() );
        clockDelayControl.setToolTipText( "how frequently the simulation clock ticks" );
        clockDelayControl.setValue( rewardNode.getClockDelay() );
        clockDelayControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setClockDelay( (int) clockDelayControl.getValue() );
            }
        } );

        JPanel devControlPanel = new VerticalLayoutPanel();
        devControlPanel.add( motionControlPanel );
        devControlPanel.add( objectsControlPanel );
        devControlPanel.add( populationControl );
        devControlPanel.add( motionDeltaControl );
        devControlPanel.add( clockDelayControl );

        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        controlPanel.add( devControlPanel );

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );

        JFrame frame = new JFrame( "Game Reward test" );
        frame.setContentPane( panel );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        PhetApplicationConfig.DEFAULT_FRAME_SETUP.initialize( frame ); // ala PhET sims
        frame.setVisible( true );
    }
}

