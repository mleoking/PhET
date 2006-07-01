package org.cove.jade.composites;

import org.cove.jade.DynamicsEngine;
import org.cove.jade.constraints.SpringConstraint;
import org.cove.jade.primitives.RectangleParticle;
/**
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * SpringBox class
 * Copyright 2005 Raymond Sheh
 *   A Java port of Flade - Flash Dynamics Engine, 
 *   Copyright 2004, 2005 Alec Cove
 * 
 * This file is part of JADE. The JAva Dynamics Engine. 
 *
 * JADE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * JADE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JADE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * ======== Original file header appears below ========
 * Flade - Flash Dynamics Engine
 * Release 0.6 alpha 
 * SpringBox class
 * Copyright 2004, 2005 Alec Cove
 * 
 * This file is part of Flade. The Flash Dynamics Engine. 
 *	
 * Flade is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Flade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Flade; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Flash is a registered trademark of Macromedia
 * ======== Original file header appears above ========
 *
 *
 */

/**
 * Class implementing a sprung box. This is a box consisting of
 * 4 corner primitive particles, each linked to the other by spring constraints.
 * <p/>
 * <pre>
 * JADE - JAva Dynamics Engine
 * Release 0.6.1 alpha 2005-12-28
 * SpringBox class
 * Copyright 2005 Raymond Sheh
 *   A Java port of Flade - Flash Dynamics Engine,
 *   Copyright 2004, 2005 Alec Cove
 * <p/>
 * This file is part of JADE. The JAva Dynamics Engine.
 * <p/>
 * JADE is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p/>
 * JADE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with JADE; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * </pre>
 * <p/>
 * Please see the documentation for the main class, org.cove.jade.DynamicsEngine, for
 * more details about JADE.
 */

public class SpringBox {

    /**
     * Top left corner particle
     */
    public RectangleParticle p0;
    /**
     * Top right corner particle
     */
    public RectangleParticle p1;
    /**
     * Bottom right corner particle
     */
    public RectangleParticle p2;
    /**
     * Bottom left corner particle
     */
    public RectangleParticle p3;

    /**
     * Instantiate a SpringBox. (px,py) = center of box, (w,h) = width and height, all dimensions in pixels.
     */
    public SpringBox(
            double px,
            double py,
            double w,
            double h,
            DynamicsEngine engine ) {

        // top left
        p0 = new RectangleParticle( px - w / 2, py - h / 2, 1, 1 );
        // top right
        p1 = new RectangleParticle( px + w / 2, py - h / 2, 1, 1 );
        // bottom right
        p2 = new RectangleParticle( px + w / 2, py + h / 2, 1, 1 );
        // bottom left
        p3 = new RectangleParticle( px - w / 2, py + h / 2, 1, 1 );

        p0.setVisible( false );
        p1.setVisible( false );
        p2.setVisible( false );
        p3.setVisible( false );

        engine.addPrimitive( p0 );
        engine.addPrimitive( p1 );
        engine.addPrimitive( p2 );
        engine.addPrimitive( p3 );

        // edges
        engine.addConstraint( new SpringConstraint( p0, p1 ) );
        engine.addConstraint( new SpringConstraint( p1, p2 ) );
        engine.addConstraint( new SpringConstraint( p2, p3 ) );
        engine.addConstraint( new SpringConstraint( p3, p0 ) );

        // crossing braces
        engine.addConstraint( new SpringConstraint( p0, p2 ) );
        engine.addConstraint( new SpringConstraint( p1, p3 ) );
    }
}
