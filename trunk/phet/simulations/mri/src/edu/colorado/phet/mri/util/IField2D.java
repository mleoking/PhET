/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.util;

import java.awt.geom.Point2D;

/**
 * IField2D
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface IField2D {
    double getValueAt( Point2D p );
}
