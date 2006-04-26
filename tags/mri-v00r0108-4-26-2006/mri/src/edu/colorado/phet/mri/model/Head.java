/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.mri.util.MriUtil;
import edu.colorado.phet.mri.model.Dipole;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.common.model.BaseModel;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Head
 * <p>
 * A type of Sample that can have a tumor in it
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Head extends Sample {

    private Ellipse2D shape;
    private ArrayList dipoles = new ArrayList( );

    public Head( Ellipse2D shape ) {
        this.shape = shape;
    }

    public Ellipse2D getShape() {
        return shape;
    }

    public Rectangle2D getBounds() {
        return shape.getBounds();
    }

    public void addTumor( Tumor tumor, BaseModel model ) {
        List dipoles = tumor.getDipoles();
        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            model.addModelElement( dipole );
        }
    }

    public void removeTumor( Tumor tumor, BaseModel model ) {
        List dipoles = tumor.getDipoles();
        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            model.removeModelElement( dipole );
        }
    }

    public void createDipoles( MriModel model, int numDipoles ) {
        List dl = MriUtil.createDipolesForEllipse( shape, numDipoles );
        for( int i = 0; i < dl.size(); i++ ) {
            Dipole dipole = (Dipole)dl.get( i );
            model.addModelElement( dipole );
        }
        Dipole rightEarDipole = new Dipole();
        rightEarDipole.setPosition( shape.getX() - 30 * MriConfig.SCALE_FOR_ORG, shape.getY() + shape.getHeight() * 0.5 -10);
        model.addModelElement( rightEarDipole );
        Dipole leftEarDipole = new Dipole();
        leftEarDipole.setPosition( shape.getX() + shape.getWidth() + 30 * MriConfig.SCALE_FOR_ORG, shape.getY() + shape.getHeight() * 0.5 -10 );
        model.addModelElement( leftEarDipole );
    }
}
