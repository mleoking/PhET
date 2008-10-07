package idx3d.debug;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class idx3d_Material_Inspector extends InspectorFrame
{
	public idx3d_Material_Inspector(idx3d_Material m, String id)
	{
		super(m, id);
		addEntry(new InspectorFrameEntry(this,m.getTexture(),"texture"));
		addEntry(new InspectorFrameEntry(this,m.getEnvmap(),"envmap"));
		addEntry(new InspectorFrameEntry(this,"int","color","0x"+Integer.toHexString(m.getColor())));
		addEntry(new InspectorFrameEntry(this,"int","transparency",""+m.getTransparency()));
		addEntry(new InspectorFrameEntry(this,"int","reflectivity",""+m.getReflectivity()));
	}
}