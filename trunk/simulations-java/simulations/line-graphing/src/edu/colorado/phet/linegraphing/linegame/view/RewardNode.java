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
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPCanvas.CenteredStage;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.PadBoundsNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * The reward that is displayed when the game is completed with a perfect score.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RewardNode extends PhetPNode {

    private static final int DEFAULT_CLOCK_DELAY = 60; // ms

    private final ConstantDtClock clock; // clock that controls the animation

    // types of images
    private final ArrayList<Image> ones;
    private final ArrayList<Image> pointTools;
    private final ArrayList<Image> smileyFaces;

    private final ArrayList<Image> imagePool; // images currently in use by the animation

    private int population; // number of images in the animation
    private int motionDelta; // nominal motion of each image
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

    /**
     * Zero-args constructor.
     * The this(...) arguments are not really relevant, just something to get us started.
     */
    public RewardNode() {
        this( new PBounds( 0, 0, 1024, 768 ), 100, 5 );
    }

    /**
     * Constructor.
     * @param bounds images are constrained to motion within these bounds
     * @param population how many images in the animation
     * @param motionDelta nominal motion delta, may be randomly adjusted for each specific image
     */
    public RewardNode( PBounds bounds, int population, int motionDelta ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        this.population = population;
        this.motionDelta = motionDelta;
        this.motionStrategy = new FallingMotionStrategy();

        this.clock = new ConstantDtClock( DEFAULT_CLOCK_DELAY, 1 );
        this.clock.pause();

        // images
        {
            //TODO
            ones = new ArrayList<Image>();
            ones.add( new PhetPText( "1", new PhetFont( Font.BOLD, 32 ), Color.RED ).toImage() );
            ones.add( new PhetPText( "1", new PhetFont( Font.BOLD, 32 ), Color.GREEN ).toImage() );
            ones.add( new PhetPText( "1", new PhetFont( Font.BOLD, 32 ), Color.BLUE ).toImage() );
            ones.add( new PhetPText( "1", new PhetFont( Font.BOLD, 32 ), Color.ORANGE ).toImage() );
            ones.add( new PhetPText( "1", new PhetFont( Font.BOLD, 32 ), Color.MAGENTA ).toImage() );

            // point tools
            pointTools = new ArrayList<Image>();
            Color[] pointToolColors = new Color[] { Color.YELLOW, Color.RED, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.GREEN };
            for ( Color color : pointToolColors ) {
                pointTools.add( createPointToolImage( color ) );
            }

            // smiley face
            smileyFaces = new ArrayList<Image>();
            Color[] smileyFaceColors = new Color[] { Color.YELLOW, Color.RED, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.GREEN };
            for ( Color color : smileyFaceColors ) {
                smileyFaces.add( createSmileyFaceImage( color ) );
            }

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

        // initial state, everything visible
        setBounds( bounds );
        setOnesVisible( true );
        setPointToolsVisible( true );
        setSmileyFacesVisible( true );
    }

    private Image createPointToolImage( Color color ) {
        final PNode bodyNode = new PImage( Images.POINT_TOOL );
        final PPath backgroundNode = new PPath( new Rectangle2D.Double( 5, 5,
                                                                        bodyNode.getFullBoundsReference().getWidth() - 10,
                                                                        0.55 * bodyNode.getFullBoundsReference().getHeight() ) );
        backgroundNode.setPaint( color );
        PComposite parentNode = new PComposite();
        parentNode.addChild( backgroundNode );
        parentNode.addChild( bodyNode );
        parentNode.scale( 0.5 );
        return new PadBoundsNode( parentNode ).toImage();
    }

    private Image createSmileyFaceImage( Color color ) {
        return new PadBoundsNode( new FaceNode( 40, color ) ).toImage();
    }

    /**
     * Sets the animation parameters based on game difficulty level.
     * @param level
     *
     */
    public void setLevel( int level ) {

        // all levels share these settings for a perfect score
        setClockDelay( 40 );
        setPopulation( 300 );
        setMotionDelta( 10 );

        setImagesVisible( ones, level == 1 );
        setImagesVisible( pointTools, level == 2 );
        setImagesVisible( smileyFaces, level == 3 );
    }

    /**
     * When this node's bounds are changes, all images are repopulated
     * so that the images are distributed randomly throughout the entire bounds.
     * @param bounds
     */
    @Override public boolean setBounds( Rectangle2D bounds ) {
        if ( bounds.isEmpty() ) {
            throw new IllegalArgumentException( "bounds are empty" );
        }
        boolean boundsChanged = false;
        if ( !bounds.equals( getBounds() ) ) {
            boundsChanged = super.setBounds( bounds );
            updateImages( true /* removeImages */);
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
            updateImages( false /* removeImages */);
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

    private boolean isOnesVisible() {
        return isImagesVisible( ones );
    }

    private void setOnesVisible( boolean visible ) {
        setImagesVisible( ones, visible );
    }

    private boolean isTwosVisible() {
        return isImagesVisible( pointTools );
    }

    private void setPointToolsVisible( boolean visible ) {
        setImagesVisible( pointTools, visible );
    }

    private boolean isSmileyFacesVisible() {
        return isImagesVisible( smileyFaces );
    }

    private void setSmileyFacesVisible( boolean visible ) {
        setImagesVisible( smileyFaces, visible );
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

    /**
     * This node plays when it's visible, pauses when it's invisible.
     * @param visible
     */
    @Override
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            play();
        }
        else {
            pause();
        }
    }

    /**
     * Plays the animation.
     */
    public void play() {
        clock.start();
    }

    /**
     * Pauses the animation.
     */
    public void pause() {
        clock.pause();
    }

    /**
     * Is the animation running?
     * @return
     */
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
        /**
         * Moves a node within the specified bounds.
         * @param node
         * @param bounds
         */
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
        final IntegerRange clockDelayRange = new IntegerRange( 10, 200, 60 );
        final IntegerRange populationRange = new IntegerRange( 50, 1000, 500 );
        final IntegerRange motionDeltaRange = new IntegerRange( 1, 20, 5 );

        PBounds bounds = new PBounds( 0, 0, 100, 100 );
        final RewardNode rewardNode = new RewardNode( bounds, populationRange.getDefault(), motionDeltaRange.getDefault() );
        rewardNode.play();

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
        canvas.addBoundsNode( stageSize );
        canvas.addWorldChild( rewardNode );

        final IMotionStrategy jitteryMotionStrategy = new JitteryMotionStrategy();
        final IMotionStrategy fallingMotionStrategy = new FallingMotionStrategy();

        final JRadioButton jitteryMotionButton = new JRadioButton( "jittery" );
        jitteryMotionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setMotionStrategy( jitteryMotionStrategy );
            }
        });

        final JRadioButton fallingMotionButton = new JRadioButton( "falling" );
        fallingMotionButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setMotionStrategy( fallingMotionStrategy );
            }
        });

        ButtonGroup motionGroup = new ButtonGroup();
        motionGroup.add( jitteryMotionButton );
        motionGroup.add( fallingMotionButton );

        rewardNode.setMotionStrategy( fallingMotionStrategy );
        fallingMotionButton.setSelected( true );

        JPanel motionControlPanel = new VerticalLayoutPanel();
        motionControlPanel.setBorder( new TitledBorder( "Motion Strategy" ) );
        motionControlPanel.add( jitteryMotionButton );
        motionControlPanel.add( fallingMotionButton );

        final LinearValueControl clockDelayControl = new LinearValueControl( clockDelayRange.getMin(), clockDelayRange.getMax(), "clock delay:", "##0", "ms" );
        clockDelayControl.setToolTipText( "how frequently the simulation clock ticks" );
        clockDelayControl.setValue( rewardNode.getClockDelay() );
        clockDelayControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setClockDelay( (int) clockDelayControl.getValue() );
            }
        } );

        final LinearValueControl populationControl = new LinearValueControl( populationRange.getMin(), populationRange.getMax(), "population:", "##0", "" );
        populationControl.setValue( populationRange.getDefault() );
        populationControl.setToolTipText( "how many images are in the play area" );
        populationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setPopulation( (int) populationControl.getValue() );
            }
        } );

        final LinearValueControl motionDeltaControl = new LinearValueControl( motionDeltaRange.getMin(), motionDeltaRange.getMax(), "motion delta:", "##0", "pixels" );
        motionDeltaControl.setValue( motionDeltaRange.getDefault() );
        motionDeltaControl.setToolTipText( "how far images move each time the clock ticks" );
        motionDeltaControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rewardNode.setMotionDelta( (int) motionDeltaControl.getValue() );
            }
        } );

        final JCheckBox atomsCheckBox = new JCheckBox( "show 1's" );
        atomsCheckBox.setSelected( rewardNode.isOnesVisible() );
        atomsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setOnesVisible( atomsCheckBox.isSelected() );
            }
        } );

        final JCheckBox moleculesCheckBox = new JCheckBox( "show point tools" );
        moleculesCheckBox.setSelected( rewardNode.isTwosVisible() );
        moleculesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setPointToolsVisible( moleculesCheckBox.isSelected() );
            }
        } );

        final JCheckBox smileyFacesCheckBox = new JCheckBox( "show smiley faces" );
        smileyFacesCheckBox.setSelected( rewardNode.isSmileyFacesVisible() );
        smileyFacesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setSmileyFacesVisible( smileyFacesCheckBox.isSelected() );
            }
        } );

        JPanel devControlPanel = new VerticalLayoutPanel();
        devControlPanel.setBorder( new TitledBorder( "Developer Controls" ) );
        devControlPanel.add( motionControlPanel );
        devControlPanel.add( clockDelayControl );
        devControlPanel.add( populationControl );
        devControlPanel.add( motionDeltaControl );
        devControlPanel.add( atomsCheckBox );
        devControlPanel.add( moleculesCheckBox );
        devControlPanel.add( smileyFacesCheckBox );

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

