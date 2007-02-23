/*
* The Physics Education Technology (PhET) project provides 
* a suite of interactive educational simulations. 
* Copyright (C) 2007 University of Colorado.
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or 
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful, 
* but WITHOUT ANY WARRANTY; without even the implied warranty of 
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
* See the GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
* 
* For additional licensing options, please contact PhET at phethelp@colorado.edu
*/

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.piccolo.nodes;

import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * HandleNode draws a handle is somewhat C-shaped, sort of like this:
 * <p>
 * <code>
 *    --------+
 *   /        |
 *   |   /----+
 *   |   |
 *   |   |
 *   |   \----+
 *   \        |
 *    --------+
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HandleNode extends PhetPNode {
    
    public HandleNode( double width, double height, double thickness, double cornerRadius, Paint fillPaint, Paint strokePaint, Stroke stroke ) {
        super();
        
        /*
         * Create the handle shape using constructive geometry.
         * To get rounded corners on the handle, create a rounded rectangle 
         * that is twice as wide as needed, then cut it in half.
         */
        RoundRectangle2D outerRect = new RoundRectangle2D.Double( 0, 0, 2 * width, height, cornerRadius, cornerRadius );
        RoundRectangle2D innerRect = new RoundRectangle2D.Double( thickness, thickness, 2 * ( width - thickness ), height - ( 2 * thickness ), cornerRadius, cornerRadius );
        Rectangle2D boundsRect = new Rectangle2D.Double( 0, 0, width, height );
        Area handleArea = new Area( outerRect );
        handleArea.exclusiveOr( new Area( innerRect ) );
        handleArea.intersect( new Area( boundsRect ) );
        
        PPath handleNode = new PPath( handleArea );
        handleNode.setPaint( fillPaint );
        handleNode.setStrokePaint( strokePaint );
        handleNode.setStroke( stroke );
        
        addChild( handleNode );
    }

}
