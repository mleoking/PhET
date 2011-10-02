// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.FloatBuffer;

import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.platetectonics.model.PlateModel;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.util.BufferUtils;

public class CrossSectionNode extends Geometry {
    public CrossSectionNode( PlateModel model, final JMEModule module ) {
        setMesh( new Mesh() {{
//            FloatBuffer positionBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
//            FloatBuffer normalBuffer = BufferUtils.createFloatBuffer( vertexCount * 3 );
//            FloatBuffer textureBuffer = BufferUtils.createFloatBuffer( vertexCount * 2 );
        }} );
    }
}
