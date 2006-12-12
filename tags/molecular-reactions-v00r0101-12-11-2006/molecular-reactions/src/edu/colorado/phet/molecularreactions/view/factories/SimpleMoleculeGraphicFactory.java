/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view.factories;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.reactions.Profiles;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.molecularreactions.view.SpatialSimpleMoleculeGraphic;
import edu.umd.cs.piccolo.PNode;

/**
 * SimpleMoleculeGraphicFactory
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimpleMoleculeGraphicFactory extends ModelElementGraphicManager.GraphicFactory
        implements MRModel.ModelListener {

    private EnergyProfile profile;

    public SimpleMoleculeGraphicFactory( MRModel model, PNode moleculeLayer ) {
        super( SimpleMolecule.class, moleculeLayer );
        model.addListener( this );
        profile = model.getEnergyProfile();
    }

    public PNode createGraphic( ModelElement modelElement ) {
        PNode newNode = new SpatialSimpleMoleculeGraphic( (SimpleMolecule)modelElement,
                                                          profile );

        // If we're not using the default profile, change the color of the image
        if( profile != Profiles.DEFAULT ) {

        }
        return newNode;
    }

    public void energyProfileChanged( EnergyProfile profile ) {
        this.profile = profile;
    }
}
