/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.PhysicsUtil;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.util.GraphicFlasher;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.quantum.model.PhotonSource;
import edu.colorado.phet.mri.model.*;
import edu.colorado.phet.mri.view.EnergySquiggle;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * MonitorPanel
 * <p/>
 * Displays icons representing the number of hydrogen nuclei at each of two energy levels
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MonitorPanel extends PhetPCanvas {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static BufferedImage SPIN_UP_IMAGE, SPIN_DOWN_IMAGE;
    private static double IMAGE_WIDTH;
    private static double BASE_SCALE = 0.5;
    private static double SCALE = 0.4;
    private static Color BACKGROUND = SampleChamberGraphic.BACKGROUND;
    private static double BASE_SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.8; // for scale = 0;5
    private static double SQUIGGLE_LENGTH_CALIBRATION_FACTOR = 1.21E8 * 2.6; // for scale = 0.4
    ;

    static {
        makeImages( SCALE );
    }

    private static void makeImages( double scale ) {
        try {
            SPIN_DOWN_IMAGE = ImageLoader.loadBufferedImage( MriConfig.DIPOLE_IMAGE );
            SPIN_UP_IMAGE = ImageLoader.loadBufferedImage( MriConfig.DIPOLE_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        SPIN_DOWN_IMAGE = BufferedImageUtils.getRotatedImage( SPIN_DOWN_IMAGE, Math.PI / 2 );
        SPIN_UP_IMAGE = BufferedImageUtils.getRotatedImage( SPIN_UP_IMAGE, -Math.PI / 2 );

        AffineTransform scaleTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        SPIN_DOWN_IMAGE = scaleOp.filter( SPIN_DOWN_IMAGE, null );
        SPIN_UP_IMAGE = scaleOp.filter( SPIN_UP_IMAGE, null );

        IMAGE_WIDTH = Math.max( SPIN_DOWN_IMAGE.getWidth(), SPIN_UP_IMAGE.getWidth() );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private ArrayList spinUpReps = new ArrayList();
    private ArrayList spinDownReps = new ArrayList();
    private EnergyLevel lowerLine, upperLine;
    private double fieldStrength;
    private EnergySquiggle energySquiggle;
    // Reserved horizontal space for the squiggle
    private double energySquiggleReserveX = 15;
    // Reserved horizontal space for the energy axis
    private double energyAxisReserveX = 30;
    // fraction of panel height that is usable for representing energy levels
    private double heightFractionUsed = 0.85;
    // Flag that tells if the energy of the radiowave source is considered close enough to the
    // energy difference between the spin levels to be considered a match. This keeps the squiggles from
    // flashing once it's matched, then moved an amount that is still within the mathing tolerancd
    private boolean matched = false;
    // There are several possibilities for how dipoles are represented. This specifies the representation du jour
    private DipoleRepresentationPolicy dipoleRepresentationPolicy = MriConfig.InitialConditions.MONITOR_PANEL_REP_POLICY_DIPOLE;

    /**
     * Constructor
     *
     * @param model
     */
    public MonitorPanel( final MriModel model ) {

        setBackground( BACKGROUND );

        // Add graphics for the energy levels
        lowerLine = new EnergyLevel( 200, spinDownReps, model, SPIN_DOWN_IMAGE );
        addWorldChild( lowerLine );
        lowerLine.setOffset( 0, 100 );

        upperLine = new EnergyLevel( 200, spinUpReps, model, SPIN_UP_IMAGE );
        addWorldChild( upperLine );
        upperLine.setOffset( 0, 60 );

        // Add a squiggle to show the energy of the radio waves
        energySquiggle = new EnergySquiggle( EnergySquiggle.VERTICAL );
        addWorldChild( energySquiggle );
        energySquiggle.setOffset( energyAxisReserveX, 0 );

        // Add a legend
        EnergyAxis energyAxis = new EnergyAxis();
        energyAxis.setOffset( 5, 50 );
        addWorldChild( energyAxis );

        // Add elements to the model that will get notified when things we are monitoring change
        model.addListener( new ModelChangeListener( model ) );
        model.addModelElement( new DipoleRepUpdater( model ) );
        model.getLowerMagnet().addChangeListener( new EnergyLevelSeparationUpdater( model ) );
        model.getRadiowaveSource().addChangeListener( new SquiggleUpdater( model ) );
        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updatePanel( model );
            }
        } );
    }

    private void updatePanel( MriModel model ) {
        setLinePositions( model );
        adjustSquiggle( model );
    }

    public void setRepresentationPolicy( DipoleRepresentationPolicy dipoleRepresentationPolicy ) {
        this.dipoleRepresentationPolicy = dipoleRepresentationPolicy;
    }


    /**
     * Establish the center point of the panel, and position the energy levels
     * symetrically above and below it
     */
    private void setLinePositions( MriModel model ) {
        double imageReserveSpace = SPIN_DOWN_IMAGE.getHeight() * 1/2;
        double maxOffset = getHeight() / 2 * heightFractionUsed - imageReserveSpace;
        double fractionMaxField = Math.min( fieldStrength / MriConfig.MAX_FADING_COIL_FIELD, 1 );
        double sampleMaterialRatio = model.getSampleMaterial().getMu() / SampleMaterial.HYDROGEN.getMu();

        double offsetY = maxOffset * fractionMaxField * sampleMaterialRatio ;
        double centerY = getHeight() / 2 + imageReserveSpace;
        lowerLine.setPositionY( centerY + offsetY );
        upperLine.setPositionY( centerY - offsetY );
    }

    /**
     * Sets the length, wavelength and location of the squiggle, and flashes it if  necessary
     *
     * @param model
     */
    private void adjustSquiggle( MriModel model ) {
        RadiowaveSource radiowaveSource = model.getRadiowaveSource();
        double frequency = radiowaveSource.getFrequency();
        double wavelength = PhysicsUtil.frequencyToWavelength( frequency );

        double transparency = radiowaveSource.getPower() / MriConfig.MAX_POWER;
        energySquiggle.setTransparency( (float)transparency );

        // TODO: the "calibration" numbers here need to be understood and made more systematic
        double length = PhysicsUtil.frequencyToEnergy( frequency ) * SQUIGGLE_LENGTH_CALIBRATION_FACTOR;
        energySquiggle.update( wavelength, 0, (int)length, 10 );
        energySquiggle.setOffset( energySquiggle.getOffset().getX(), lowerLine.getOffset().getY() - length );

        // Test to see if the squiggle should be flashed
        double bEnergy = PhysicsUtil.frequencyToEnergy( model.getLowerMagnet().getFieldStrength() * model.getSampleMaterial().getMu() );
        double rfEnergy = PhysicsUtil.frequencyToEnergy( radiowaveSource.getFrequency() );
        if( Math.abs( bEnergy - rfEnergy ) <= MriConfig.ENERGY_EPS ) {
            if( !matched ) {
                GraphicFlasher gf = new GraphicFlasher( energySquiggle );
                gf.start();
                matched = true;
            }
        }
        else {
            matched = false;
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class EnergyAxis extends PNode {
        public EnergyAxis() {
            PText axisLabel = new PText( "Energy" );
            axisLabel.rotate( -Math.PI / 2 );
            addChild( axisLabel );
            Arrow arrow = new Arrow( new Point2D.Double( 10, 40 ),
                                     new Point2D.Double( 10, 0 ),
                                     10, 8, 3,
                                     1, false );
            PPath arrowGraphic = new PPath( arrow.getShape() );
            arrowGraphic.setPaint( new Color( 80, 80, 80 ) );
            addChild( arrowGraphic );
            arrowGraphic.setOffset( 10, -40 );
        }
    }

    /**
     * A graphic with a line and an icon for each dipole in that energy level. It actually has an icon for all
     * the dipoles, but only displays the one that are currently in this energy level.
     */
    private class EnergyLevel extends PNode {
        private PPath lineNode;
        private List nucleiReps;
        private BufferedImage dipoleRepImage;
        private Line2D line;

        public EnergyLevel( double width, List nucleiReps, MriModel model, BufferedImage dipoleRepImage ) {
            this.nucleiReps = nucleiReps;
            this.dipoleRepImage = dipoleRepImage;

            line = new Line2D.Double( 0, 0, width, 0 );
            lineNode = new PPath( line );
            lineNode.setStroke( new BasicStroke( 3 ) );
            addChild( lineNode );

            MonitorPanel.this.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    setLinelength( e.getComponent().getWidth() );
                }
            } );

            // Add reps for all the dipoles that we have right now
            for( int i = 0; i < model.getDipoles().size(); i++ ) {
                addDipoleRep();
            }

            // Add a listener that will add and remove dipole reps as they go in and out of the model
            model.addListener( new MriModel.ChangeAdapter() {
                public void modelElementAdded( ModelElement modelElement ) {
                    if( modelElement instanceof Dipole ) {
                        addDipoleRep();
                    }
                }
            } );
        }

        private void setLinelength( double lineLength ) {
            line.setLine( line.getX1(), line.getY1(), line.getX1() + lineLength, line.getY2() );
            lineNode.setPathTo( line );
        }

        private void addDipoleRep() {
            PNode dipoleGraphic = new PImage( dipoleRepImage );
            nucleiReps.add( dipoleGraphic );
            dipoleGraphic.setOffset( ( nucleiReps.size() - 1 ) * IMAGE_WIDTH + energySquiggleReserveX + energyAxisReserveX,
                                     -dipoleGraphic.getHeight() );
            addChild( dipoleGraphic );

            // Set the size of the panel
            MonitorPanel.this.setPreferredSize( new Dimension( (int)( dipoleRepImage.getWidth() * ( nucleiReps.size() )
                                                                      + energySquiggleReserveX
                                                                      + energyAxisReserveX ),
                                                               getPreferredSize().height ) );
        }

        public void setPositionY( double y ) {
            setOffset( 0, y );
        }
    }

    /**
     * Tracks the number of dipoles with spin up and spin down
     */
    private class DipoleRepUpdater implements ModelElement {
        private MriModel model;

        DipoleRepUpdater( MriModel model ) {
            this.model = model;
        }

        public void stepInTime( double dt ) {
            List dipoles = model.getDipoles();
            dipoleRepresentationPolicy.representSpins( dipoles, spinUpReps, spinDownReps );
        }
    }

    //----------------------------------------------------------------
    // Representation policy
    //----------------------------------------------------------------
    public interface DipoleRepresentationPolicy {
        void representSpins( List dipoles, List spinUpReps, List spinDownReps );
    }

    public static class DiscretePolicy implements DipoleRepresentationPolicy {
        public void representSpins( List dipoles, List spinUpReps, List spinDownReps ) {

            int numUp = 0, numDown = 0;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                boolean isUp = dipole.getSpin() == Spin.UP;
                boolean isDown = !isUp;
                ( (PNode)spinUpReps.get( i ) ).setVisible( isUp );
                ( (PNode)spinDownReps.get( i ) ).setVisible( isDown );

                numUp += isUp ? 1 : 0;
                numDown += isDown ? 1 : 0;
            }
//            System.out.println( "numUp = " + numUp  + " numDown = " + numDown );
        }
    }

    public static class TransparencyPolicy implements DipoleRepresentationPolicy {
        public void representSpins( List dipoles, List spinUpReps, List spinDownReps ) {
            int numUp = 0;
            int numDown = 0;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                if( dipole.getSpin() == Spin.DOWN ) {
                    numUp++;
                }
                else if( dipole.getSpin() == Spin.UP ) {
                    numDown++;
                }
            }

            float upTransparency = ( (float)numUp ) / dipoles.size();
            for( int j = 0; j < spinUpReps.size(); j++ ) {
                ( (PNode)spinUpReps.get( j ) ).setVisible( true );
                ( (PNode)spinUpReps.get( j ) ).setTransparency( upTransparency );
            }
            float downTransparency = ( (float)numDown ) / dipoles.size();
            for( int j = 0; j < spinDownReps.size(); j++ ) {
                ( (PNode)spinDownReps.get( j ) ).setVisible( true );
                ( (PNode)spinDownReps.get( j ) ).setTransparency( downTransparency );
            }
        }
    }

    private class EnergyLevelSeparationUpdater implements Electromagnet.ChangeListener {
        private MriModel model;

        public EnergyLevelSeparationUpdater( MriModel model ) {
            this.model = model;
        }

        public void stateChanged( Electromagnet.ChangeEvent event ) {
            fieldStrength = model.getTotalFieldStrengthAt( new Point2D.Double( MriConfig.SAMPLE_CHAMBER_WIDTH / 2,
                                                                               MriConfig.SAMPLE_CHAMBER_HEIGHT / 2));
            updatePanel( model );
        }
    }

    //----------------------------------------------------------------
    // ModelChangeListener
    //----------------------------------------------------------------
    private class ModelChangeListener extends MriModel.ChangeAdapter {
        private MriModel model;

        public ModelChangeListener( MriModel model ) {
            this.model = model;
        }

        public void sampleMaterialChanged( SampleMaterial sampleMaterial ) {
            updatePanel( model );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of RadiowaveSource.ChangeListener
    //--------------------------------------------------------------------------------------------------
    private class SquiggleUpdater implements RadiowaveSource.ChangeListener {
        private MriModel model;

        public SquiggleUpdater( MriModel model ) {
            this.model = model;
        }

        public void rateChangeOccurred( PhotonSource.ChangeEvent event ) {
            adjustSquiggle( model );
        }

        public void wavelengthChanged( PhotonSource.ChangeEvent event ) {
            adjustSquiggle( model );
        }
    }
}