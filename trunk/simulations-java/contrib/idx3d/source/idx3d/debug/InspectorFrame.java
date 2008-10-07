package idx3d.debug;

import idx3d.*;
import java.util.Vector;
import java.util.Enumeration;
import java.awt.*;
import java.applet.*;

public abstract class InspectorFrame extends Frame
{
	private static int pos=0;
	String id;
	ScrollList list=new ScrollList(20);
	
	public InspectorFrame(Object obj, String id)
	{
		super(id +"  ["+obj.getClass().getName()+"]");
		this.id=id;
		add(list);
		pos=(pos+20)%(Toolkit.getDefaultToolkit().getScreenSize().height/2);
		move(pos,pos);
		show();
	}
	
	public void addEntry(InspectorFrameEntry comp)
	{
		list.add(comp);
		int screenheight=Toolkit.getDefaultToolkit().getScreenSize().height;
		int fullheight=20*(list.countComponents()-1)+insets().top+insets().bottom;
		int height=(fullheight<screenheight-pos)? fullheight : screenheight-pos;
		if (height<=size().height) return;
		resize(320,height);
	}
	
	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY)
		{
			hide(); dispose(); return true;
		}
		return super.handleEvent(evt);
	}
}
