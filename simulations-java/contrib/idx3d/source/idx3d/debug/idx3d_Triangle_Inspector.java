package idx3d.debug;
import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public class idx3d_Triangle_Inspector extends InspectorFrame
{

	public idx3d_Triangle_Inspector(idx3d_Triangle tri, String id)
	{
		super(tri, id);
		
		addEntry(new InspectorFrameEntry(this,tri.parent,"parent"));
		addEntry(new InspectorFrameEntry(this,"int","id",tri.id+""));
		addEntry(new InspectorFrameEntry(this,tri.p1,"p1"));
		addEntry(new InspectorFrameEntry(this,tri.p2,"p2"));
		addEntry(new InspectorFrameEntry(this,tri.p3,"p3"));
		addEntry(new InspectorFrameEntry(this,tri.n,"n"));
		addEntry(new InspectorFrameEntry(this,tri.n2,"n2"));
	}
	
}