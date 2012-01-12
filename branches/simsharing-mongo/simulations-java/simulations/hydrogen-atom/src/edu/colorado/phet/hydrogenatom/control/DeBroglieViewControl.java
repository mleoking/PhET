// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.enums.DeBroglieView;
import edu.umd.cs.piccolox.pswing.PComboBox;


/**
 * DeBroglieViewControl is a JComboBox for selecting the view type for the deBroglie atomic model.
 * This class extends PComboBox so that it can be put on the Piccolo canvas using PSwing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeBroglieViewControl extends PComboBox {

    /**
     * Constructor.
     */
    public DeBroglieViewControl() {
        super();
        setOpaque( false );
        addItem( new Choice( HAResources.getString( "menu.deBroglieView.radialDistance" ), DeBroglieView.RADIAL_DISTANCE ) );
        addItem( new Choice( HAResources.getString( "menu.deBroglieView.height3D" ), DeBroglieView.HEIGHT_3D ) );
        addItem( new Choice( HAResources.getString( "menu.deBroglieView.brightness" ), DeBroglieView.BRIGHTNESS ) );
    }
   
    /**
     * Gets the selected view type.
     * 
     * @return DeBroglieView
     */
    public DeBroglieView getSelectedView() {
        return ((Choice)getSelectedItem()).getView();
    }
    
    /**
     * Sets the selected view type.
     * 
     * @param view
     */
    public void setSelectedView( DeBroglieView view ) {
        
        boolean found = false;
        
        int itemCount = getItemCount();
        for ( int i = 0; i < itemCount && !found; i++ ) {
            Choice option = (Choice) getItemAt( i );
            if ( option.getView() == view ) {
                setSelectedIndex( i );
                found = true;
            }
        }
        
        if ( !found ) {
            throw new IllegalArgumentException( "unsupported DeBroglieView: " + view );
        }
    }
    
    /**
     * Choice is a deBroglie view choice.
     */
    private static class Choice {
        
        private String _key;
        private DeBroglieView _view;
        
        public Choice( String key, DeBroglieView view ) {
            _key = key;
            _view = view;
        }
        
        public String toString() {
            return _key;
        }
        
        public DeBroglieView getView() {
            return _view;
        }
    }
}
