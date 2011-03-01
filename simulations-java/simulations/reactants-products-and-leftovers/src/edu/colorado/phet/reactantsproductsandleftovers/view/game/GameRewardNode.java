// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.model.Product;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel.SandwichReaction;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The reward that is displayed when the game is completed with a perfect score.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameRewardNode extends PhetPNode {

    private static final int DEFAULT_CLOCK_DELAY = 60; // ms
    private static final double FACE_DIAMETER = 40; // size of smiley face images

    private final ConstantDtClock clock;
    private final ArrayList<Image> images;
    private final Image faceImage;
    private final SandwichShopModel sandwichShopModel;

    private int population;
    private int motionDelta;
    private IMotionStrategy motionStrategy;

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
    public GameRewardNode() {
        this( new PBounds( 0, 0, 100, 100 ), 100, 5 );
    }

    /**
     * Constructor.
     * @param bounds images are constrained to motion within these bounds
     * @param population how many images in the animation
     * @param motionDelta nominal motion delta, may be randomly adjusted for each specific image
     */
    public GameRewardNode( PBounds bounds, int population, int motionDelta ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        this.population = population;
        this.motionDelta = motionDelta;
        this.motionStrategy = new FallingMotionStrategy();

        this.clock = new ConstantDtClock( DEFAULT_CLOCK_DELAY, 1 );
        this.clock.pause();

        // used for sandwich images
        sandwichShopModel = new SandwichShopModel();
        // select the sandwich with the most ingredients
        SandwichReaction biggestSandwich = null;
        for ( SandwichReaction reaction : sandwichShopModel.getReactions() ) {
            if ( biggestSandwich == null || reaction.getNumberOfReactants() > biggestSandwich.getNumberOfReactants() ) {
                biggestSandwich = reaction;
            }
        }
        sandwichShopModel.setReaction( biggestSandwich );
        // set all reactant coefficients to max
        for ( Reactant reactant : sandwichShopModel.getReaction().getReactants() ) {
            reactant.setCoefficient( SandwichShopModel.getCoefficientRange().getMax() );
        }

        // images list, includes everything by default
        {
            this.images = new ArrayList<Image>();

            // molecules
            for ( Image image : RPALImages.ALL_MOLECULES ) {
                images.add( image );
            }

            // smiley face image
            FaceNode faceNode = new FaceNode( FACE_DIAMETER );
            faceImage = faceNode.toImage();
            images.add( faceImage );

            // sandwich images
            for ( Reactant reactant : sandwichShopModel.getReaction().getReactants() ) {
                images.add( reactant.getImage() );
            }
            for ( Product product : sandwichShopModel.getReaction().getProducts() ) {
                images.add( product.getImage() );
            }
        }

        clock.addClockListener( new ClockAdapter() {
            @Override
            // update the animation when the clock ticks
            public void clockTicked( ClockEvent clockEvent ) {
                step();
            }
        } );

        // initial state
        setBounds( bounds );
    }

    /**
     * Sets the animation parameters based on game difficulty level
     * and whether the user got a perfect score.
     * @param level
     * @param perfectScore
     */
    public void setLevel( int level, boolean perfectScore ) {
        if ( perfectScore ) {

            // all levels share these settings for a perfect score
            setClockDelay( 40 );
            setPopulation( 200 );
            setMotionDelta( 10 );

            switch ( level ) {
            case 1:
                // show only molecules
                setMoleculesVisible( true );
                setSmileyFacesVisible( false );
                setSandwichesVisible( false );
                break;

            case 2:
                // show only smiley faces
                setMoleculesVisible( false );
                setSmileyFacesVisible( true );
                setSandwichesVisible( false );
                break;

            case 3:
                // show only sandwiches
                setMoleculesVisible( false );
                setSmileyFacesVisible( false );
                setSandwichesVisible( true );
                break;

            default:
                throw new IllegalArgumentException( "unsupported level: " + level );
            }
        }
        else {
            // show nothing
            setMoleculesVisible( false );
            setSmileyFacesVisible( false );
            setSandwichesVisible( false );
            // these settings give the feeling of slow and not exciting, included in case we ever decide to show something
            setClockDelay( 40 );
            setPopulation( 50 );
            setMotionDelta( 2 );
        }
    }

    /**
     * When this node's bounds are changes, all images are repopulated
     * so that the images are distributed randomly throughout the entire bounds.
     * @param bounds
     */
    @Override
    public boolean setBounds( Rectangle2D bounds ) {
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

    private void setSmileyFacesVisible( boolean visible ) {
        if ( visible != isSmileyFacesVisible() ) {
            if ( visible ) {
                images.add( faceImage );
            }
            else {
                images.remove( faceImage );
            }
            updateImages( true /* removeImages */);
        }
    }

    private boolean isSmileyFacesVisible() {
        return images.contains( faceImage );
    }

    private void setSandwichesVisible( boolean visible ) {
        if ( visible != isSandwichesVisible() ) {
            if ( visible ) {
                for ( Reactant reactant : sandwichShopModel.getReaction().getReactants() ) {
                    images.add( reactant.getImage() );
                }
                for ( Product product : sandwichShopModel.getReaction().getProducts() ) {
                    images.add( product.getImage() );
                }
            }
            else {
                for ( Reactant reactant : sandwichShopModel.getReaction().getReactants() ) {
                    images.remove( reactant.getImage() );
                }
                for ( Product product : sandwichShopModel.getReaction().getProducts() ) {
                    images.remove( product.getImage() );
                }
            }
            updateImages( true /* removeImages */);
        }
    }

    private boolean isSandwichesVisible() {
        return images.contains( sandwichShopModel.getReaction().getProduct(0).getImage() );
    }

    private void setMoleculesVisible( boolean visible ) {
        if ( visible != isMoleculesVisible() ) {
            if ( visible ) {
                for ( Image image : RPALImages.ALL_MOLECULES ) {
                    images.add( image );
                }
            }
            else {
                for ( Image image : RPALImages.ALL_MOLECULES ) {
                    images.remove( image );
                }
            }
            updateImages( true /* removeImages */);
        }
    }

    private boolean isMoleculesVisible() {
        return images.contains( RPALImages.ALL_MOLECULES[0] );
    }

    private void setMotionStrategy( IMotionStrategy motionStrategy ) {
        this.motionStrategy = motionStrategy;
    }

    /*
     * Updates them images to match to the current population.
     * If removeImages is true, all existing nodes are removed.
     * Otherwise, nodes are added/removed to match the population.
     * The last node added is the first one removed.
     */
    private void updateImages( boolean removeImages ) {
        if ( removeImages ) {
            removeAllChildren(); // assume that this node has only MovingImages as children
        }
        if ( images.size() > 0 ) {
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
        int index = (int) ( Math.random() * images.size() );
        MovingImageNode imageNode = new MovingImageNode( images.get( index ), delta );
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
     * During one stage of development, it was published to dev as flavor "game-rewards-prototype".
     */
    public static void main( String[] args ) {

        // parameters that control the animation
        final IntegerRange clockDelayRange = new IntegerRange( 10, 200, 60 );
        final IntegerRange populationRange = new IntegerRange( 50, 1000, 500 );
        final IntegerRange motionDeltaRange = new IntegerRange( 1, 20, 5 );

        PBounds bounds = new PBounds( 0, 0, 100, 100 );
        final GameRewardNode rewardNode = new GameRewardNode( bounds, populationRange.getDefault(), motionDeltaRange.getDefault() );
        rewardNode.play();

        final PhetPCanvas canvas = new PhetPCanvas( RPALConstants.CANVAS_RENDERING_SIZE ) {
            @Override
            // when the canvas size changes, update the reward node
            protected void updateLayout() {
                Dimension2D worldSize = getWorldSize();
                if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
                    PBounds newBounds = new PBounds( 0, 0, worldSize.getWidth(), worldSize.getHeight() );
                    rewardNode.setBounds( newBounds );
                }
            }
        };
        canvas.setBackground( RPALConstants.CANVAS_BACKGROUND );
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

        JPanel motionControlPanel = new JPanel();
        motionControlPanel.setBorder( new TitledBorder( "Motion Strategy" ) );
        EasyGridBagLayout motionControlPanelLayout = new EasyGridBagLayout( motionControlPanel );
        motionControlPanel.setLayout( motionControlPanelLayout );
        int row = 0;
        int column = 0;
        motionControlPanelLayout.addComponent( jitteryMotionButton, row, column++ );
        motionControlPanelLayout.addComponent( fallingMotionButton, row, column++ );

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

        final JCheckBox moleculesCheckBox = new JCheckBox( "show molecules" );
        moleculesCheckBox.setSelected( rewardNode.isMoleculesVisible() );
        moleculesCheckBox.setToolTipText( "determines whether molecules will be shown" );
        moleculesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setMoleculesVisible( moleculesCheckBox.isSelected() );
            }
        } );

        final JCheckBox smileyFacesCheckBox = new JCheckBox( "show smiley faces" );
        smileyFacesCheckBox.setSelected( rewardNode.isSmileyFacesVisible() );
        smileyFacesCheckBox.setToolTipText( "determines whether smiley faces will be shown" );
        smileyFacesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setSmileyFacesVisible( smileyFacesCheckBox.isSelected() );
            }
        } );

        final JCheckBox sandwichesCheckBox = new JCheckBox( "show sandwiches" );
        sandwichesCheckBox.setSelected( rewardNode.isSandwichesVisible() );
        sandwichesCheckBox.setToolTipText( "determines whether sandwiches will be shown" );
        sandwichesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rewardNode.setSandwichesVisible( sandwichesCheckBox.isSelected() );
            }
        } );

        JPanel devControlPanel = new JPanel();
        devControlPanel.setBorder( new TitledBorder( "Developer Controls" ) );
        EasyGridBagLayout devControlPanelLayout = new EasyGridBagLayout( devControlPanel );
        devControlPanel.setLayout( devControlPanelLayout );
        row = 0;
        column = 0;
        devControlPanelLayout.addComponent( motionControlPanel, row++, column );
        devControlPanelLayout.addComponent( clockDelayControl, row++, column );
        devControlPanelLayout.addComponent( populationControl, row++, column );
        devControlPanelLayout.addComponent( motionDeltaControl, row++, column );
        devControlPanelLayout.addComponent( moleculesCheckBox, row++, column );
        devControlPanelLayout.addComponent( smileyFacesCheckBox, row++, column );
        devControlPanelLayout.addComponent( sandwichesCheckBox, row++, column );

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
