/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
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
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.sandwich.SandwichImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The reward that is displayed when the game is completed with a perfect score.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameRewardNode extends PhetPNode {
    
    private static final int DEFAULT_CLOCK_DELAY = 60; // ms

    private final ConstantDtClock clock;
    private final ArrayList<Image> images;
    private final Image faceImage;
    private final Image sandwichImage;

    private int population;
    private int motionDelta;
    
    public GameRewardNode() {
        this( new PBounds( 0, 0, 100, 100 ), 100, 5 );
    }

    public GameRewardNode( PBounds bounds, int population, int motionDelta ) {
        super();
        setPickable( false );
        setChildrenPickable( false );

        this.population = population;
        this.motionDelta = motionDelta;

        this.clock = new ConstantDtClock( DEFAULT_CLOCK_DELAY, 1 );
        this.clock.pause();

        // images list, includes everything by default
        {
            this.images = new ArrayList<Image>();

            // molecules
            for ( Image image : RPALImages.ALL_MOLECULES ) {
                images.add( image );
            }

            // smiley face image
            FaceNode faceNode = new FaceNode();
            faceNode.scale( 0.4 );
            faceImage = faceNode.toImage();
            images.add( faceImage );

            // sandwiches
            PImage sandwichNode = new PImage( SandwichImageFactory.createImage( new SandwichShopModel( 2, 1, 1 /* bread, meat, cheese */ ) ) );
            sandwichNode.scale( 1.25 );
            sandwichImage = sandwichNode.toImage();
            images.add( sandwichImage );
            for ( Image image : RPALImages.ALL_SANDWICHES ) {
                images.add( image );
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
    
    public void setLevel( int level, boolean perfectScore ) {
        if ( perfectScore ) {
            switch ( level ) {
            case 1:
                setClockDelay( 80 );
                setPopulation( 250 );
                setMotionDelta( 5 );
                setSmileyFacesVisible( true );
                setSandwichesVisible( false );
                break;
                
            case 2:
                setClockDelay( 60 );
                setPopulation( 550 );
                setMotionDelta( 6 );
                setSmileyFacesVisible( true );
                setSandwichesVisible( false );
                break;
                
            case 3:
                setClockDelay( 35 );
                setPopulation( 800 );
                setMotionDelta( 7 );
                setSmileyFacesVisible( true );
                setSandwichesVisible( true );
                break;
                
            default:
                throw new IllegalArgumentException( "unsupported level: " + level );
            }
        }
        else {
            setClockDelay( 200 );
            setPopulation( 50 );
            setMotionDelta( 1 );
            setSmileyFacesVisible( false );
            setSandwichesVisible( false );
        }
    }

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

    public void setClockDelay( int delay ) {
        clock.setDelay( delay );
    }

    public int getClockDelay() {
        return clock.getDelay();
    }

    public void setPopulation( int population ) {
        if ( !( population > 0 ) ) {
            throw new IllegalArgumentException( "population must be > 0: " + population );
        }
        if ( population != this.population ) {
            this.population = population;
            updateImages( false /* removeImages */);
        }
    }

    public int getPopulation() {
        return population;
    }

    public void setMotionDelta( int motionDelta ) {
        if ( !( motionDelta > 0 ) ) {
            throw new IllegalArgumentException( "motionDelta must be > 0: " + motionDelta );
        }
        if ( motionDelta != this.motionDelta ) {
            this.motionDelta = motionDelta;
        }
    }

    public int getMotionDelta() {
        return motionDelta;
    }

    public void setSmileyFacesVisible( boolean visible ) {
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

    public boolean isSmileyFacesVisible() {
        return images.contains( faceImage );
    }

    public void setSandwichesVisible( boolean visible ) {
        if ( visible != isSandwichesVisible() ) {
            if ( visible ) {
                images.add( sandwichImage );
                for ( Image image : RPALImages.ALL_SANDWICHES ) {
                    images.add( image );
                }
            }
            else {
                images.remove( sandwichImage );
                for ( Image image : RPALImages.ALL_SANDWICHES ) {
                    images.remove( image );
                }
            }
            updateImages( true /* removeImages */);
        }
    }

    public boolean isSandwichesVisible() {
        return images.contains( sandwichImage );
    }

    private void updateImages( boolean removeImages ) {
        if ( removeImages ) {
            removeAllChildren(); // assume that this node has only images as children
        }
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

    private void addRandomNode() {

        // choose a random image
        int index = (int) ( Math.random() * images.size() );
        PImage imageNode = new PImage( images.get( index ) );
        addChild( imageNode );

        // set a random location within the bounds
        PBounds bounds = getBoundsReference();
        Point2D location = getRandomPoint( bounds );
        double x = location.getX() - ( imageNode.getFullBoundsReference().getWidth() / 2 );
        double y = location.getY() - ( imageNode.getFullBoundsReference().getHeight() / 2 );
        imageNode.setOffset( x, y );
    }

    private Point2D getRandomPoint( PBounds bounds ) {
        double x = bounds.getX() + ( Math.random() * bounds.getWidth() );
        double y = bounds.getY() + ( Math.random() * bounds.getHeight() );
        return new Point2D.Double( x, y );
    }
    
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( visible ) {
            play();
        }
        else {
            stop();
        }
    }

    public void play() {
        if ( getVisible() ) {
            clock.start();
        }
    }

    public void stop() {
        clock.pause();
    }

    private void step() {
        for ( int i = 0; i < getChildrenCount(); i++ ) {
            step( getChild( i ) );
        }
    }

    private void step( PNode node ) {
        PBounds bounds = getBoundsReference();
        // walk a distance in a random direction
        double x = node.getXOffset() + ( getRandomDirection() * motionDelta );
        double y = node.getYOffset() + ( getRandomDirection() * motionDelta );
        // constrain to the bounds
        x = Math.max( bounds.getMinX(), Math.min( x, bounds.getMaxX() ) );
        y = Math.max( bounds.getMinY(), Math.min( y, bounds.getMaxY() ) );
        node.setOffset( x, y );
    }

    private int getRandomDirection() {
        return ( Math.random() > 0.5 ) ? 1 : -1;
    }

    // test harness
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

        final LinearValueControl clockDelayControl = new LinearValueControl( clockDelayRange.getMin(), clockDelayRange.getMax(), "clock speed:", "##0", "ms" );
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
        int row = 0;
        int column = 0;
        devControlPanelLayout.addComponent( clockDelayControl, row++, column );
        devControlPanelLayout.addComponent( populationControl, row++, column );
        devControlPanelLayout.addComponent( motionDeltaControl, row++, column );
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
