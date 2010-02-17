package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
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
    
    private final Image[] images;
    private int population;
    private int motionDelta;
    private boolean playing;
    
    public GameRewardNode( PBounds bounds, IClock clock, Image[] images, int population, int motionDelta ) {
        super();
        
        this.images = images;
        this.population = population;
        this.motionDelta = motionDelta;
        
        // update the animation when the clock ticks
        clock.addClockListener( new ClockAdapter() {
            @Override
            public void clockTicked( ClockEvent clockEvent ) {
                step();
            }
        } );
        
        // initial state
        playing = false;
        setBounds( bounds );
    }
    
    public boolean setBounds( Rectangle2D bounds ) {
        if ( bounds.isEmpty() ) {
            throw new IllegalArgumentException( "bounds are empty" );
        }
        boolean b = super.setBounds( bounds );
        removeAllChildren();
        updateNodes();
        return b;
    }
    
    public void setPopulation( int population ) {
        if ( !( population > 0 ) ) {
            throw new IllegalArgumentException( "population must be > 0: " + population );
        }
        if ( population != this.population ) {
            this.population = population;
            updateNodes();
        }
    }
    
    public void setMotionDelta( int motionDelta ) {
        if ( !( motionDelta > 0 ) ) {
            throw new IllegalArgumentException( "motionDelta must be > 0: " + motionDelta );
        }
        if ( motionDelta != this.motionDelta ) {
            this.motionDelta = motionDelta;
        }
    }
    
    private void updateNodes() {
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
        for ( int i = 0; i < population; i++ ) {
            addRandomNode();
        }
    }
    
    private void addRandomNode() {
        PBounds bounds = getBoundsReference();
        PImage imageNode = new PImage( images[ (int)( Math.random() * images.length ) ] );
        addChild( imageNode );
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
    
    public void play() {
        playing = true;
    }

    public void stop() {
        playing = false;
    }
    
    private void step() {
        if ( playing ) {
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                step( getChild( i ) );
            }
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
        
        // smiley face
        FaceNode faceNode = new FaceNode();
        faceNode.scale( 0.25 );
        final Image faceImage = faceNode.toImage();
        
        // sandwich
        Image sandwichImage = SandwichImageFactory.createImage( new SandwichShopModel( 2, 1, 1 ) );
        
        // parameters that control the animation
        final Image[] images = { sandwichImage, faceImage, RPALImages.H2O, RPALImages.H2, RPALImages.O2, RPALImages.N2, RPALImages.SO3, RPALImages.PCl3, RPALImages.CS2, RPALImages.BREAD, RPALImages.MEAT, RPALImages.CHEESE };
        final IntegerRange clockDelayRange = new IntegerRange( 10, 200, 60 );
        final IntegerRange populationRange = new IntegerRange( 50, 500, 200 );
        final IntegerRange motionDeltaRange = new IntegerRange( 1, 20, 5 );

        final Dimension frameSize = new Dimension( 1024, 768 );
        
        final ConstantDtClock clock = new ConstantDtClock( clockDelayRange.getDefault(), 1 );
        clock.start();
        PBounds bounds = new PBounds( 0, 0, 100, 100 );
        final GameRewardNode rewardNode = new GameRewardNode( bounds, clock, images, populationRange.getDefault(), motionDeltaRange.getDefault() );
        rewardNode.play();
        
        final PhetPCanvas canvas = new PhetPCanvas( frameSize ) {
            @Override
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
        clockDelayControl.setValue( clock.getDelay() );
        clockDelayControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                 clock.setDelay( (int) clockDelayControl.getValue() );
            }
        });
        
        final LinearValueControl populationControl = new LinearValueControl( populationRange.getMin(), populationRange.getMax(), "population:", "##0", "" );
        populationControl.setValue( populationRange.getDefault() );
        populationControl.setToolTipText( "how many images are in the play area" );
        populationControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                 rewardNode.setPopulation( (int) populationControl.getValue() );
            }
        });
        
        final LinearValueControl motionDeltaControl = new LinearValueControl( motionDeltaRange.getMin(), motionDeltaRange.getMax(), "motion delta:", "##0", "pixels" );
        motionDeltaControl.setValue( motionDeltaRange.getDefault() );
        motionDeltaControl.setToolTipText( "how far images move each time the clock ticks" );
        motionDeltaControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                 rewardNode.setMotionDelta( (int) motionDeltaControl.getValue() );
            }
        });
        
        JPanel devControlPanel = new JPanel();
        devControlPanel.setBorder( new TitledBorder( "Developer Controls" ) );
        EasyGridBagLayout devControlPanelLayout = new EasyGridBagLayout( devControlPanel );
        devControlPanel.setLayout( devControlPanelLayout );
        int row = 0;
        int column = 0;
        devControlPanelLayout.addComponent( clockDelayControl, row++, column );
        devControlPanelLayout.addComponent( populationControl, row++, column );
        devControlPanelLayout.addComponent( motionDeltaControl, row++, column );
        
        JPanel controlPanel = new JPanel();
        controlPanel.setBorder( new EmptyBorder( 10, 10, 10, 10) );
        EasyGridBagLayout controlPanelLayout = new EasyGridBagLayout( controlPanel );
        controlPanel.setLayout( controlPanelLayout );
        controlPanelLayout.addComponent( devControlPanel, 0, 0 );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.setSize( frameSize );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
    }
}
