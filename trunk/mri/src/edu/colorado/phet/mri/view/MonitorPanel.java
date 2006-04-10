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
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.Spin;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
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

    static {
        makeImages( 0.5 );
    }

    private static void makeImages( double scale ) {
        try {
            SPIN_DOWN_IMAGE = ImageLoader.loadBufferedImage( MriConfig.DIPOLE_IMAGE );
            SPIN_UP_IMAGE = ImageLoader.loadBufferedImage( MriConfig.DIPOLE_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        SPIN_DOWN_IMAGE = BufferedImageUtils.getRotatedImage( SPIN_DOWN_IMAGE, -Math.PI / 2 );
        SPIN_UP_IMAGE = BufferedImageUtils.getRotatedImage( SPIN_UP_IMAGE, Math.PI / 2 );

        AffineTransform scaleTx = AffineTransform.getScaleInstance( scale, scale );
        AffineTransformOp scaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        SPIN_DOWN_IMAGE = scaleOp.filter( SPIN_DOWN_IMAGE, null );
        SPIN_UP_IMAGE = scaleOp.filter( SPIN_UP_IMAGE, null );

        IMAGE_WIDTH = Math.max( SPIN_DOWN_IMAGE.getWidth(), SPIN_UP_IMAGE.getWidth() );
        System.out.println( "SPIN_UP_IMAGE.getWidth() = " + SPIN_UP_IMAGE.getWidth() );
        System.out.println( "SPIN_DOWN_IMAGE.getWidth() = " + SPIN_DOWN_IMAGE.getWidth() );
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private ArrayList spinUpReps = new ArrayList();
    private ArrayList spinDownReps = new ArrayList();
    private EnergyLevel lowerLine, upperLine;
    private double fieldStrength;

    private RepresentationPolicy representationPolicy = MriConfig.InitialConditions.MONITOR_PANEL_REP_POLICY;
//    private RepresentationPolicy representationPolicy = new Transparency();

    /**
     * Constructor
     *
     * @param model
     */
    public MonitorPanel( MriModel model ) {

        lowerLine = new EnergyLevel( 200, spinUpReps, model, SPIN_UP_IMAGE );
        addWorldChild( lowerLine );
        lowerLine.setOffset( 0, 100 );

        upperLine = new EnergyLevel( 200, spinDownReps, model, SPIN_DOWN_IMAGE );
        addWorldChild( upperLine );
        upperLine.setOffset( 0, 60 );

        model.addModelElement( new DipoleRepUpdater( model ) );

        model.getLowerMagnet().addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                fieldStrength = event.getElectromagnet().getFieldStrength();
                setLinePositions();
            }
        } );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setLinePositions();
            }
        } );
    }

    /**
     * Establish the center point of the panel, and position the energy levels
     * symetrically above and below it
     */
    private void setLinePositions() {
        double heightFractionUsed = 0.8;
        double imageReserveSpace = SPIN_DOWN_IMAGE.getHeight() * 2 / 3;
        double maxOffset = getHeight() / 2 * heightFractionUsed - imageReserveSpace * 2;
        double fractionMaxField = Math.min( fieldStrength / MriConfig.MAX_FADING_COIL_FIELD, 1 );
        double offsetY = maxOffset * fractionMaxField + imageReserveSpace;
        double centerY = getHeight() / 2;
        lowerLine.setPositionY( centerY + offsetY );
        upperLine.setPositionY( centerY - offsetY );
    }

    public void setRepresentationPolicy( RepresentationPolicy representationPolicy ) {
        this.representationPolicy = representationPolicy;
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A graphic with a line and an icon for each dipole in that energy level
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
            model.addListener( new MriModel.Listener() {
                public void modelElementAdded( ModelElement modelElement ) {
                    if( modelElement instanceof Dipole ) {
                        addDipoleRep();
                    }
                }

                public void modelElementRemoved( ModelElement modelElement ) {
                    throw new RuntimeException( "not implemented" );
                }
            } );
        }

        void setLinelength( double lineLength ) {
            line.setLine( line.getX1(), line.getY1(), line.getX1() + lineLength, line.getY2() );
            lineNode.setPathTo( line );
        }

        private void addDipoleRep() {
            PNode dipoleGraphic = new PImage( dipoleRepImage );
            nucleiReps.add( dipoleGraphic );
            dipoleGraphic.setOffset( ( nucleiReps.size() - 1 ) * IMAGE_WIDTH, -dipoleGraphic.getHeight() );
            addChild( dipoleGraphic );

            // Set the size of the panel
            MonitorPanel.this.setPreferredSize( new Dimension( dipoleRepImage.getWidth() * nucleiReps.size(),
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
            representationPolicy.representSpins( dipoles, spinUpReps, spinDownReps );
//            stepInTimeA( dt );
        }

//        public void stepInTimeA( double dt ) {
//            List dipoles = model.getDipoles();
//            representationPolicy.representSpins( dipoles );
//        }
//
//        public void stepInTimeB( double dt ) {
//            List dipoles = model.getDipoles();
//            int numUp = 0;
//            int numDown = 0;
//            for( int i = 0; i < dipoles.size(); i++ ) {
//                Dipole dipole = (Dipole)dipoles.get( i );
//                if( dipole.getSpin() == Spin.UP ) {
//                    ( (PNode)spinUpReps.get( numUp ) ).setVisible( true );
//                    numUp++;
//                }
//                else if( dipole.getSpin() == Spin.DOWN ) {
//                    ( (PNode)spinDownReps.get( numDown ) ).setVisible( true );
//                    numDown++;
//                }
//            }
//
//            for( int j = numUp; j < spinUpReps.size(); j++ ) {
//                ( (PNode)spinUpReps.get( j ) ).setVisible( false );
//            }
//            for( int j = numDown; j < spinDownReps.size(); j++ ) {
//                ( (PNode)spinDownReps.get( j ) ).setVisible( false );
//            }
//        }
    }

    //----------------------------------------------------------------
    // Rpresentation policy
    //----------------------------------------------------------------
    public interface RepresentationPolicy {
        void representSpins( List dipoles, List spinUpReps, List spinDownReps );
    }

    public static class DiscretePolicy implements RepresentationPolicy {
        public void representSpins( List dipoles,  List spinUpReps, List spinDownReps ) {
            int numUp = 0;
            int numDown = 0;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                if( dipole.getSpin() == Spin.UP ) {
                    ( (PNode)spinUpReps.get( numUp ) ).setVisible( true );
                    numUp++;
                }
                else if( dipole.getSpin() == Spin.DOWN ) {
                    ( (PNode)spinDownReps.get( numDown ) ).setVisible( true );
                    numDown++;
                }
            }

            for( int j = numUp; j < spinUpReps.size(); j++ ) {
                ( (PNode)spinUpReps.get( j ) ).setVisible( false );
            }
            for( int j = numDown; j < spinDownReps.size(); j++ ) {
                ( (PNode)spinDownReps.get( j ) ).setVisible( false );
            }
        }
    }

    public static class TransparencyPolicy implements RepresentationPolicy {
        public void representSpins( List dipoles, List spinUpReps, List spinDownReps) {
            int numUp = 0;
            int numDown = 0;
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                if( dipole.getSpin() == Spin.UP ) {
                    numUp++;
                }
                else if( dipole.getSpin() == Spin.DOWN ) {
                    numDown++;
                }
            }

            float upTransparency = ((float)numUp) / dipoles.size();
            for( int j = 0; j < spinUpReps.size(); j++ ) {
                ( (PNode)spinUpReps.get( j ) ).setVisible( true );
                ( (PNode)spinUpReps.get( j ) ).setTransparency( upTransparency );
            }
            float downTransparency = ((float)numDown) / dipoles.size();
            for( int j = 0; j < spinDownReps.size(); j++ ) {
                ( (PNode)spinUpReps.get( j ) ).setVisible( true );
                ( (PNode)spinDownReps.get( j ) ).setTransparency( downTransparency );
            }
        }

    }
}