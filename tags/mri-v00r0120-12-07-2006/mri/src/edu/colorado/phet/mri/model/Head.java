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

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.mri.util.MriUtil;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Head
 * <p/>
 * A type of Sample that can have a tumor in it
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Head extends Sample {

    private Ellipse2D skull;
    private Ellipse2D[] ears;
    private Shape shape;

    public Head( Ellipse2D skull, Ellipse2D[] ears ) {
        this.skull = skull;
        this.ears = ears;
        Area area = new Area( skull );
        area.add( new Area( ears[0] ) );
        area.add( new Area( ears[1] ) );
        shape = area;
    }

    public Shape getShape() {
        return skull;
    }

    public Rectangle2D getBounds() {
        return shape.getBounds();
//        return skull.getBounds();
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
        for( int i = dipoles.size() - 1; i >= 0; i-- ) {
//        for( int i = 0; i < dipoles.size(); i++ ) {
            Dipole dipole = (Dipole)dipoles.get( i );
            model.removeModelElement( dipole );
        }
    }

    public void createDipoles( MriModel model, double spacing ) {
        List dl = MriUtil.createDipolesForEllipse( skull, spacing );
        for( int i = 0; i < dl.size(); i++ ) {
            Dipole dipole = (Dipole)dl.get( i );
            model.addModelElement( dipole );
        }
        Dipole rightEarDipole = new Dipole();
        rightEarDipole.setPosition( ears[1].getCenterX(), ears[1].getCenterY() );
        model.addModelElement( rightEarDipole );
        Dipole leftEarDipole = new Dipole();
        leftEarDipole.setPosition( ears[0].getCenterX(), ears[0].getCenterY() );
        model.addModelElement( leftEarDipole );
    }
}
