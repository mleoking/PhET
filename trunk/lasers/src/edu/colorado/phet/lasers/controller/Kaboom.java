/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.image.BufferedImage;

/**
 * Kaboom
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Kaboom implements ModelElement {
    private MultipleAtomModule module;
    private LaserModel model;

    public Kaboom( MultipleAtomModule multipleAtomModule ) {
        this.module = multipleAtomModule;
        model = multipleAtomModule.getLaserModel();
    }

    public void stepInTime( double dt ) {
        int numPhotons = model.getNumPhotons();
        if( numPhotons > 100 ) {
            System.out.println( "KABOOM!!!" );
            kaboom();
        }
    }

    private void kaboom() {
        ApparatusPanel2 ap = (ApparatusPanel2)module.getApparatusPanel();
        BufferedImage snapshot = BufferedImageUtils.toBufferedImage( ap.getSnapshot() );
        ap.getGraphic().clear();
        ap.addGraphic( new PhetImageGraphic( ap, snapshot ) );
    }
}
