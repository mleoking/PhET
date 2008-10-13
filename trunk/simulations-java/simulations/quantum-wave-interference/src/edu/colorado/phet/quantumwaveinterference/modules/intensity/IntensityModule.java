/*  */
package edu.colorado.phet.quantumwaveinterference.modules.intensity;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.quantumwaveinterference.QWIResources;
import edu.colorado.phet.quantumwaveinterference.QuantumWaveInterferenceApplication;
import edu.colorado.phet.quantumwaveinterference.QWIModule;
import edu.colorado.phet.quantumwaveinterference.model.Detector;
import edu.colorado.phet.quantumwaveinterference.model.QWIModel;
import edu.colorado.phet.quantumwaveinterference.model.SplitModel;
import edu.colorado.phet.quantumwaveinterference.view.QWIPanel;
import edu.colorado.phet.quantumwaveinterference.view.colormaps.ColorData;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.DetectorGraphic;
import edu.colorado.phet.quantumwaveinterference.view.piccolo.RestrictedDetectorGraphic;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:06:16 AM
 */

public class IntensityModule extends QWIModule {
    private SplitModel splitModel;
    private IntensityBeamPanel intensityBeamPanel;
    private IntensityControlPanel intensityControlPanel;
    private ArrayList listeners = new ArrayList();
    private boolean rightDetectorShouldBeEnabled = false;
    private boolean leftDetectorShouldBeEnabled = false;

    public IntensityModule( QuantumWaveInterferenceApplication app, IClock clock ) {
        this( QWIResources.getString( "module.high-intensity" ), app, clock );
    }

    protected IntensityModule( String name, PhetApplication app, IClock clock ) {
        super( name, app, clock );
        splitModel = new SplitModel();
        setQWIModel( splitModel );
        intensityBeamPanel = createIntensityPanel();
        setSchrodingerPanel( intensityBeamPanel );
        intensityControlPanel = new IntensityControlPanel( this );
        setSchrodingerControlPanel( intensityControlPanel );

        synchronizeModel();

        getQWIModel().addListener( new QWIModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                if( !getQWIModel().isDoubleSlitEnabled() ) {
                    setRightDetectorEnabled( false );
                    setLeftDetectorEnabled( false );
                }
            }
        } );
        finishInit();
        getSchrodingerPanel().addListener( new QWIPanel.Adapter() {
            public void inverseSlitsChanged() {
                updateDetectors();
            }
        } );

        //•	Fixed: If I add double slits, and then add a detector on the left slit, without changing anything else first, the detector is shifted to the left, rather than directly over the slit, as it should be.
        getSchrodingerPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                splitModel.synchronizeDetectorRegions();
            }
        } );
        getSchrodingerPanel().getDetectorSheetPNode().setFadeDelay( 10 );
    }

    private boolean isInverseSlits() {
        return getSchrodingerPanel().isInverseSlits();
    }

    protected IntensityBeamPanel createIntensityPanel() {
        return new IntensityBeamPanel( this );
    }

    public SplitModel getSplitModel() {
        return splitModel;
    }

    public IntensityBeamPanel getIntensityPanel() {
        return intensityBeamPanel;
    }

    public boolean isRightDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getRightDetector() );
    }

    public boolean isLeftDetectorEnabled() {
        return splitModel.containsDetector( splitModel.getLeftDetector() );
    }

    private void updateDetectors() {
        updateLeftDetector();
        updateRightDetector();
    }

    public void setRightDetectorEnabled( boolean selected ) {
        this.rightDetectorShouldBeEnabled = selected;
        updateRightDetector();
    }

    private void updateRightDetector() {
        setRightDetectorActive( rightDetectorShouldBeEnabled && !isInverseSlits() );
    }

    private void setRightDetectorActive( boolean selected ) {
        if( !getQWIModel().isDoubleSlitEnabled() && selected ) {
            return;
        }
        if( isRightDetectorEnabled() != selected ) {
            setDetectorEnabled( splitModel.getRightDetector(), selected );
            notifyDetectorsChanged();
        }
    }

    public void setLeftDetectorEnabled( boolean selected ) {
        this.leftDetectorShouldBeEnabled = selected;
        updateLeftDetector();
    }

    private void updateLeftDetector() {
        setLeftDetectorActive( leftDetectorShouldBeEnabled && !isInverseSlits() );
    }

    private void setLeftDetectorActive( boolean selected ) {
        if( !getQWIModel().isDoubleSlitEnabled() && selected ) {
            return;
        }
        if( isLeftDetectorEnabled() != selected ) {
            setDetectorEnabled( splitModel.getLeftDetector(), selected );
            notifyDetectorsChanged();
        }
    }

    public ColorData getRootColor() {
        return intensityBeamPanel.getRootColor();
    }

    public static interface Listener {
        void detectorsChanged();
    }

    public static class Adapter implements Listener {
        public void detectorsChanged() {
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifyDetectorsChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.detectorsChanged();
        }
    }

    private void setDetectorEnabled( Detector detector, boolean selected ) {
        boolean splitMode = shouldBeSplitMode();
        if( selected ) {
//            splitModel.synchronizeDetectorRegions();//todo this is covered by a componentAdapter on the QWIPanel, see above
            splitModel.addDetector( detector );
            DetectorGraphic detectorGraphic = new RestrictedDetectorGraphic( intensityBeamPanel, detector );
            intensityBeamPanel.addDetectorGraphic( detectorGraphic );
        }
        else {
            splitModel.removeDetector( detector );
            intensityBeamPanel.removeDetectorGraphic( detector );
        }
        boolean newSplitMode = shouldBeSplitMode();
        if( newSplitMode != splitMode ) {
            synchronizeModel();
        }
    }

    private boolean shouldBeSplitMode() {
        return isLeftDetectorEnabled() || isRightDetectorEnabled();
    }

    private void synchronizeModel() {
        boolean splitMode = shouldBeSplitMode();
        splitModel.setSplitMode( splitMode );
        intensityBeamPanel.setSplitMode( splitMode );
    }


}
