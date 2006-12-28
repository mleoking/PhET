/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.controller;

import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.MriModel;
import edu.colorado.phet.mri.model.RadiowaveSource;
import edu.colorado.phet.mri.model.SampleChamber;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.colorado.phet.quantum.model.PhotonSource;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * MriModuleA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class NmrModule extends AbstractMriModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static String name = SimStrings.get( "Module.NmrTitle" );

    //----------------------------------------------------------------
    // Instance methods and fields
    //----------------------------------------------------------------

    static SampleChamber sampleChamber = new SampleChamber( new Rectangle2D.Double( MriConfig.SAMPLE_CHAMBER_LOCATION.getX(),
                                                                                    MriConfig.SAMPLE_CHAMBER_LOCATION.getY(),
                                                                                    MriConfig.SAMPLE_CHAMBER_WIDTH,
                                                                                    MriConfig.SAMPLE_CHAMBER_HEIGHT ) );

    /**
     * Constructor
     */
    public NmrModule() {
        super( name, new SwingClock( delay, dt ), sampleChamber );
        setLogoPanelVisible( false );
    }

    public boolean auxiliarySquiggleVisible() {
        return true;
    }

    protected void init() {
        super.init();

        // Control panel
        setControlPanel( new NmrControlPanel( this ) );

        // Sample Chamber
        sampleChamber.createDipoles( (MriModel)getModel(), 20 );

        // Set the initial view
        setEmRep( NmrModule.WAVE_VIEW );

        // Put up a wiggle-me
        createWiggleMe();

//
//        final JSlider js = new JSlider( 0, 100, 50 );
//        js.setOpaque( false );
//        final PImage pi = PImageFactory.create( "images/background.png");
//        PSwing pSwing = new PSwing( getPhetPCanvas(), js );
//        final PNode pNode = new PNode();
//        pNode.addChild( pi );
//        pNode.addChild( pSwing );
//        pNode.setOffset( 50, 50);
//        js.addChangeListener( new ChangeListener() {
//            public void stateChanged( ChangeEvent e ) {
//                js.revalidate();
//            }
//        } );
//        getPhetPCanvas().addScreenChild( pNode );
    }

    private void createWiggleMe() {
        Point2D radiowaveSourceLocation = ( (MriModel)getModel() ).getRadiowaveSource().getPosition();
//        final WiggleMe wiggleMe = new WiggleMe( SimStrings.get( "Application.WiggleMe" ),
//                                                (int)radiowaveSourceLocation.getX() - 100,
//                                                (int)radiowaveSourceLocation.getY() );
        final MotionHelpBalloon wiggleMe = new MotionHelpBalloon( getPhetPCanvas(), SimStrings.get( "Application.WiggleMe" ) );
        wiggleMe.setOffset( 50, 100 );
        wiggleMe.setBalloonFillPaint( new Color( 255, 255, 100 ) );
        wiggleMe.setBalloonVisible( true );
        wiggleMe.setBalloonStroke( new BasicStroke( 1 ) );
        getPhetPCanvas().addScreenChild( wiggleMe );
        wiggleMe.setVisible( true );
        wiggleMe.animateTo( ( (MriModel)getModel() ).getRadiowaveSource().getPosition().getX() - 120,
                            ( (MriModel)getModel() ).getRadiowaveSource().getPosition().getY() - 20 );

        PhotonSourceListener photonSourceListener = new PhotonSourceListener( wiggleMe );
        ( (MriModel)getModel() ).getRadiowaveSource().addRateChangeListener( photonSourceListener );
        ( (MriModel)getModel() ).getRadiowaveSource().addWavelengthChangeListener( photonSourceListener );
//        ( (MriModel)getModel() ).getRadiowaveSource().addChangeListener( new PhotonSource.ChangeListener() {
//            public void rateChangeOccurred( PhotonSource.ChangeEvent event ) {
//                removeWiggleMe( event );
//            }
//
//            public void wavelengthChanged( PhotonSource.ChangeEvent event ) {
//                removeWiggleMe( event );
//            }
//
//            private void removeWiggleMe( PhotonSource.ChangeEvent event ) {
//                ( (RadiowaveSource)event.getPhotonSource() ).removeChangeListener( this );
//                getPhetPCanvas().removeScreenChild( wiggleMe );
//            }
//        } );
    }

//    class MagnifierPanel extends JPanel {
//        public MagnifierPanel( final Magnifier magnifier ) {
//            super( new GridBagLayout() );
//            setBorder( ControlBorderFactory.createPrimaryBorder( "Magnifier " ) );
//
//            final JSpinner magSpinner = new JSpinner( new SpinnerNumberModel( 2, 0.5, 5, 0.1 ) );
//            magSpinner.addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    double mag = ( (Double)magSpinner.getValue() ).doubleValue();
//                    magnifier.setZoom( mag );
//                }
//            } );
//
//            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
//                                                             GridBagConstraints.EAST,
//                                                             GridBagConstraints.NONE,
//                                                             new Insets( 0, 10, 0, 10 ), 0, 0 );
//            gbc.gridheight = 2;
//            add( magnifier, gbc );
//            gbc.gridheight = 1;
//            gbc.gridx = 1;
//            gbc.anchor = GridBagConstraints.SOUTHWEST;
//            add( new JLabel( "Magnification" ), gbc );
//            gbc.anchor = GridBagConstraints.NORTHWEST;
//            gbc.gridy = 1;
//            add( magSpinner, gbc );
//        }
//    }

    private class PhotonSourceListener implements PhotonSource.RateChangeListener,
                                                  PhotonSource.WavelengthChangeListener {
        private final MotionHelpBalloon wiggleMe;

        public PhotonSourceListener( MotionHelpBalloon wiggleMe ) {
            this.wiggleMe = wiggleMe;
        }

        public void rateChangeOccurred( PhotonSource.RateChangeEvent event ) {
            ( (RadiowaveSource)event.getSource() ).removeListener( this );
            getPhetPCanvas().removeScreenChild( wiggleMe );
        }

        public void wavelengthChanged( PhotonSource.WavelengthChangeEvent event ) {
            ( (RadiowaveSource)event.getSource() ).removeListener( this );
            getPhetPCanvas().removeScreenChild( wiggleMe );
        }
    }
}
