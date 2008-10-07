package idx3d.debug;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class idx3d_Scene_Inspector extends InspectorFrame
{

	public idx3d_Scene_Inspector(idx3d_Scene scene, String id)
	{
		super(scene, id);
		addEntry(new InspectorFrameEntry(this,"int","width",scene.width+""));
		addEntry(new InspectorFrameEntry(this,"int","height",scene.height+""));
		addEntry(new InspectorFrameEntry(this,scene.matrix,"matrix"));
		addEntry(new InspectorFrameEntry(this,scene.normalmatrix,"normalmatrix"));
		
		addEntry(new InspectorFrameEntry(this,scene.objectData,"objectData"));
		addEntry(new InspectorFrameEntry(this,scene.lightData,"lightData"));
		addEntry(new InspectorFrameEntry(this,scene.materialData,"materialData"));
		addEntry(new InspectorFrameEntry(this,scene.cameraData,"cameraData"));
	}
	
}