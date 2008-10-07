package idx3d.debug;
import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class idx3d_Texture_Inspector extends InspectorFrame
{
	public idx3d_Texture_Inspector(idx3d_Texture t, String id)
	{
		super(t, id);
		
		addEntry(new InspectorFrameEntry(this,"int","width",t.width+""));
		addEntry(new InspectorFrameEntry(this,"int","height",t.height+""));
	}
	
}