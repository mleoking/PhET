// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.mechanics.DefaultBody;
import edu.colorado.phet.common.phetcommon.util.EventChannel;

import java.awt.geom.Point2D;
import java.util.EventListener;
import java.util.List;

/**
 * TemperatureControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TemperatureControl extends DefaultBody {
    private MRModel model;
    private double setting;

    public TemperatureControl( MRModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        if( setting != 0 ) {
            double de = 0;
            List modelElements = model.getModelElements();
            double scaleFactor = 1 + setting / ( 10000 );

            for( int i = 0; i < modelElements.size(); i++ ) {
                Object o = modelElements.get( i );
                if( o instanceof SimpleMolecule && !( (SimpleMolecule)o ).isPartOfComposite() ) {
                    SimpleMolecule molecule = (SimpleMolecule)o;
                    double ke0 = molecule.getKineticEnergy();
                    molecule.setVelocity( molecule.getVelocity().scale( scaleFactor ) );
                    double ke1 = molecule.getKineticEnergy();
                    de += ( ke1 - ke0 );
                }
                if( o instanceof CompositeMolecule ) {
                    CompositeMolecule compositeMolecule = (CompositeMolecule)o;
                    double ke0 = compositeMolecule.getKineticEnergy();
                    compositeMolecule.setOmega( compositeMolecule.getOmega() * scaleFactor );
                    compositeMolecule.setVelocity( compositeMolecule.getVelocity().scale( scaleFactor ) );
                    double ke1 = compositeMolecule.getKineticEnergy();
                    de += ( ke1 - ke0 );
                }
            }
            // todo: this comes out with the new heat model
            model.addEnergy( de );
        }
    }

    public void setPosition( double x, double y ) {
        super.setPosition( x, y );
        changeListenerProxy.positionChanged( getPosition() );
    }

    public void setPosition( Point2D position ) {
        super.setPosition( position );
        changeListenerProxy.positionChanged( getPosition() );
    }

    public double getSetting() {
        return setting;
    }

    public void setSetting( double setting ) {
        this.setting = setting;
        changeListenerProxy.settingChanged( setting );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    public interface ChangeListener extends EventListener {
        void settingChanged( double setting );

        void positionChanged( Point2D newPosition );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }
}
