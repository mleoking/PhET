/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.ContainmentGraphic;
import edu.colorado.phet.nuclearphysics.view.LegendPanel;
import edu.colorado.phet.nuclearphysics.view.ExplodingContainmentGraphic;
import edu.colorado.phet.nuclearphysics.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImageOp;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Presents a number of U235 and U238 nuclei, and an optional containment vessel, and a gun that fires a
 * single neutron at the central U235 nucleus. It's supposed to represent a bomb.
 */
public class MultipleNucleusFissionModule extends ChainReactionModule implements Containment.ResizeListener {

    private double containmentGraphicLayer = .9;
    private double gunGraphicLayer = containmentGraphicLayer + 1000;
    private Containment containment;
    private ContainmentGraphic containmentGraphic;

    private Point gunMuzzelLocation = new Point( -40, 0 );
    private Point2D neutronLaunchPt = new Point2D.Double( -550, 0 );
    private ExplodingContainmentGraphic explodingContainmentGraphic;
    private List explodingGraphics;
    private FireButton fireButton;

    /**
     * Constructor
     *
     * @param clock
     */
    public MultipleNucleusFissionModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleNucleusFissionModule" ), clock );
    }

    protected void init() {
        super.init();

        // set the SCALE of the physical panel so we can fit more nuclei in it
        getPhysicalPanel().setPhysicalScale( 0.5 );
        super.addControlPanelElement( new MultipleNucleusFissionControlPanel( this ) );

        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( MultipleNucleusFissionModule.this.neutronToAdd != null ) {
                    MultipleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    MultipleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );

        // Add a model element that watches for collisions between neutrons and
        // nuclei
        getModel().addModelElement( new FissionDetector() );

        // Ray gun
        PhetImageGraphic gunGraphic = new PhetImageGraphic( getPhysicalPanel(), "images/gun-8A.png" );
        gunGraphic.setTransform( AffineTransform.getScaleInstance( 2, 2 ) );
        gunGraphic.setRegistrationPoint( gunGraphic.getWidth() - 15, 25 );
        gunGraphic.setLocation( gunMuzzelLocation );
        getPhysicalPanel().addGraphic( gunGraphic );

        // Add a fire button to the play area, on top of the gun
        fireButton = new FireButton( getPhysicalPanel() );
        getPhysicalPanel().addGraphic( fireButton, 1E6 );
        fireButton.setLocation( (int)( 40 ), 293 );
        fireButton.addActionListener( new FireButton.ActionListener() {
            public void actionPerformed( FireButton.ActionEvent event ) {
                fireNeutron();
            }
        } );

        // Add a button to enable/disable the containment vessel
        ContainmentButton containmentButton = new ContainmentButton( getPhysicalPanel() );
        getPhysicalPanel().addGraphic( containmentButton, 1E6 );
        containmentButton.setLocation( 600, 100 );
        containmentButton.addActionListener( new PhetGraphicsButton.ActionListener() {
            public void actionPerformed( PhetGraphicsButton.ActionEvent event ) {
                if( containment == null ) {
                    addContainment();
                }
                else {
                    removeContainment();
                }
            }
        } );

        // Add a listener that will detect when the containment vessel has blown up

        // Start it up
        start();
    }

    protected List getLegendClasses() {
        LegendPanel.LegendItem[] legendClasses = new LegendPanel.LegendItem[]{
                LegendPanel.NEUTRON,
                LegendPanel.PROTON,
                LegendPanel.U235,
                LegendPanel.U238,
                LegendPanel.U239
        };
        return Arrays.asList( legendClasses );
    }

    public void stop() {
        super.stop();
        for( int i = 0; explodingGraphics != null && i < explodingGraphics.size(); i++ ) {
            ExplodingContainmentGraphic graphic = (ExplodingContainmentGraphic)explodingGraphics.get( i );
            graphic.clearGraphics();
        }
    }

    public void start() {
        // Add a bunch of nuclei, including one in the middle that we can fire a neutron at
        Uranium235 centralNucleus = new Uranium235( new Point2D.Double(), (NuclearPhysicsModel)getModel() );
        getModel().addModelElement( centralNucleus );
        getU235Nuclei().add( centralNucleus );
        addNucleus( centralNucleus );

        // If the containment is enabled, recreate it, so it will be fully
        // displayed
//        if( containment != null ) {
        setContainmentEnabled( false );
//            setContainmentEnabled( true );
//        }
        computeNeutronLaunchParams();
    }

    protected void computeNeutronLaunchParams() {
        neutronLaunchAngle = 0;
        setNeutronLaunchPoint( neutronLaunchPt );
        neutronPath = new Line2D.Double( getNeutronLaunchPoint(), new Point2D.Double( 0, 0 ) );
    }

    public void fission( FissionProducts products ) {
        super.fission( products );
        // If the conatinment vessel is being used, make it dissovle
        if( containment != null ) {
            containment.dissolve();
        }
    }

    private void addContainment() {
        containment = new Containment( new Point2D.Double( 0, 0 ), 400, (NuclearPhysicsModel)getModel() );
        getModel().addModelElement( containment );
        containment.addResizeListener( this );
        // To remove any nuclei that are outside the containment
        containementResized( containment );
        containmentGraphic = new ContainmentGraphic( containment, getPhysicalPanel(), getPhysicalPanel().getNucleonTx() );
        getPhysicalPanel().addGraphic( containmentGraphic, containmentGraphicLayer );

        // Add a listener that will add a graphic showing the containment blowing up when the
        // containment explodes
        containment.addChangeListener( new Containment.ChangeListener() {
            public void containmentExploded( Containment.ChangeEvent event ) {
                // The following constructor Creates a graphic AND ADDS IT TO THE APPARATUS PANEL
                explodingGraphics = new ArrayList();
                explodingGraphics.add( new ExplodingContainmentGraphic( MultipleNucleusFissionModule.this, containmentGraphic ) );
                getPhysicalPanel().removeGraphic( containmentGraphic );
                setContainmentEnabled( false );

                // Add a mushroom cloud
                PhetImageGraphic mushroomCloudGraphic = new PhetImageGraphic( getPhysicalPanel(), "images/mc-10.jpg" );
                getPhysicalPanel().addGraphic( mushroomCloudGraphic );
                Dimension targetSize = new Dimension( (int)(getApparatusPanel().getSize( ).width / getPhysicalPanel().getScale()),
                                                      (int)(getApparatusPanel().getSize( ).height / getPhysicalPanel().getScale()));
                ImageGraphicExpander ige = new ImageGraphicExpander( mushroomCloudGraphic, targetSize );
                getClock().addClockListener( ige );

                // Display a message
                Font font = new Font( "Lucida sans", Font.ITALIC, 72 );
                PhetTextGraphic2 textGraphic = new PhetTextGraphic2( getPhysicalPanel(), font, "You have made an atomic bomb", Color.red );
                getPhysicalPanel().addGraphic( textGraphic );
                getApparatusPanel().getFontMetrics( font );
                int width = getApparatusPanel().getFontMetrics( font ).stringWidth( textGraphic.getText() );
                textGraphic.setLocation( -width / 2, 0 );
            }
        } );
    }

    private class ImageGraphicExpander implements ClockListener {
        private PhetImageGraphic pig;
        private Dimension targetSize;
        private AffineTransform atx = AffineTransform.getScaleInstance( 1.1, 1.1 );
        private AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );

        public ImageGraphicExpander( PhetImageGraphic pig, Dimension targetSize ) {
            this.pig = pig;
            this.targetSize = targetSize;
        }

        public void clockTicked( ClockEvent clockEvent ) {
            if( pig.getWidth() < targetSize.getWidth() || pig.getHeight() < targetSize.getHeight() ) {
                BufferedImage bi = pig.getImage();
                bi = atxOp.filter( bi, null );
                pig.setImage( bi );
                pig.repaint();
                pig.setLocation( -bi.getWidth() / 2, -bi.getHeight() / 2 );
            }
        }

        public void clockStarted( ClockEvent clockEvent ) {

        }

        public void clockPaused( ClockEvent clockEvent ) {

        }

        public void simulationTimeChanged( ClockEvent clockEvent ) {

        }

        public void simulationTimeReset( ClockEvent clockEvent ) {

        }
    }

    private void removeContainment() {
        getModel().removeModelElement( containment );
        containment = null;
        getPhysicalPanel().removeGraphic( containmentGraphic );
    }

    public void setContainmentEnabled( boolean enabled ) {
        if( enabled ) {
            addContainment();
            // This call will cause any nuclei that are outside the containment
            // to be removed
            containementResized( containment );
        }
        else {
            removeContainment();
        }
        computeNeutronLaunchParams();
    }

    /**
     * Finds a place to put a new nucleus. If it can't find one, returns null
     *
     * @return the location where a nucleus can be put, or null if one can't be found
     */
    protected Point2D.Double findLocationForNewNucleus() {

        // Determine the model bounds represented by the current size of the apparatus panel
        Rectangle2D r = getPhysicalPanel().getBounds();
        AffineTransform atx = new AffineTransform( getPhysicalPanel().getNucleonTx() );
        AffineTransform gtx = getPhysicalPanel().getGraphicTx();
        atx.preConcatenate( gtx );
        Rectangle2D modelBounds = new Rectangle2D.Double();
        try {
            modelBounds.setFrameFromDiagonal( atx.inverseTransform( new Point2D.Double( r.getMinX(), r.getMinY() ), null ),
                                              atx.inverseTransform( new Point2D.Double( r.getMinX() + r.getWidth(), r.getMinY() + r.getHeight() ), null ) );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }

        Shape bounds = null;
        if( containment != null ) {
            bounds = containment.getInteriorArea();
        }
        else {
            bounds = modelBounds;
        }
        boolean overlapping = false;
        Point2D.Double location = new Point2D.Double();
        int attempts = 0;
        do {
            // If there is already a nucleus at (0,0), then generate a random location
            boolean centralNucleusExists = false;
            for( int i = 0; i < getNuclei().size() && !centralNucleusExists; i++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( i );
                if( testNucleus.getPosition().getX() == 0 && testNucleus.getPosition().getY() == 0 ) {
                    centralNucleusExists = true;
                }
            }

            double x = centralNucleusExists ? random.nextDouble() * ( modelBounds.getWidth() - 50 ) + modelBounds.getMinX() + 25 : 0;
            double y = centralNucleusExists ? random.nextDouble() * ( modelBounds.getHeight() - 50 ) + modelBounds.getMinY() + 25 : 0;
            location.setLocation( x, y );

            overlapping = false;
            for( int j = 0; j < getNuclei().size() && !overlapping; j++ ) {
                Nucleus testNucleus = (Nucleus)getNuclei().get( j );
                if( testNucleus.getPosition().distance( location ) < testNucleus.getRadius() * 3 ) {
                    overlapping = true;
                }
            }

            // todo: the hard-coded 50 here should be replaced with the radius of a Uranium nucleus
            if( location.getX() != 0 && location.getY() != 0 ) {
                overlapping = overlapping
                              || getNeutronPath().ptSegDist( location ) < 50
                              || location.distance( 0, 0 ) + 50 > bounds.getBounds2D().getWidth() / 2;
            }
            attempts++;
        } while( overlapping && attempts < s_maxPlacementAttempts );

        if( overlapping ) {
            location = null;
        }
        return location;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Containment.ResizeListener
    //--------------------------------------------------------------------------------------------------

    public void containementResized( Containment containment ) {
        Shape bounds = containment.getArea();

        // Recompute the spot from which neutrons are fired
        computeNeutronLaunchParams();

        ArrayList removeList = new ArrayList();
        double nucleusRadius = 120;
        for( int i = 0; i < getNuclei().size(); i++ ) {
            Nucleus nucleus = (Nucleus)getNuclei().get( i );
            if( nucleus.getPosition().distance( 0, 0 ) + nucleusRadius > bounds.getBounds2D().getWidth() / 2 ) {
                removeList.add( nucleus );
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            Nucleus nucleus = (Nucleus)removeList.get( i );
            getModel().removeModelElement( nucleus );
            // This is lazy and crude, but let's just remove from all the lists, so
            // we don't have to check types, use logic, etc.
            getNuclei().remove( nucleus );
            getU235Nuclei().remove( nucleus );
            getU238Nuclei().remove( nucleus );
            getU239Nuclei().remove( nucleus );
        }
    }

    public FireButton getFireNeutronButton() {
        return fireButton;
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    /**
     * Detects fission caused by collisions between neutrons and U235 or U238 nuclei
     */
    private class FissionDetector implements ModelElement {
        private Line2D utilLine = new Line2D.Double();

        public void stepInTime( double dt ) {
            List neutrons = getNeutrons();
            List u235Nuclei = getU235Nuclei();
            List u238Nuclei = getU238Nuclei();
            for( int i = neutrons.size() - 1; i >= 0; i-- ) {
                Neutron neutron = (Neutron)neutrons.get( i );
                utilLine.setLine( neutron.getPosition(), neutron.getPositionPrev() );
                // Check U235 nuclei
                for( int j = 0; j < u235Nuclei.size(); j++ ) {
                    Uranium235 u235 = (Uranium235)u235Nuclei.get( j );
                    double perpDist = utilLine.ptSegDistSq( u235.getPosition() );
                    if( perpDist <= u235.getRadius() * u235.getRadius() ) {
                        u235.fission( neutron );
                    }
                }
                // Check U238 nuclei
                for( int j = 0; j < u238Nuclei.size(); j++ ) {
                    Uranium238 u238 = (Uranium238)u238Nuclei.get( j );
                    double perpDist = utilLine.ptSegDistSq( u238.getPosition() );
                    if( perpDist <= u238.getRadius() * u238.getRadius() ) {
                        u238.fission( neutron );
                    }
                }
            }
        }
    }
}