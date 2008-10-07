package idx3d.debug;

import java.awt.*;
import java.util.Vector;


public class ScrollList extends Panel
{
	int cellheight;
	Scrollbar scrollbar;
	Vector entry=new Vector();
		
	// C O N S T R U C T O R S
	
		private ScrollList() {}
	
		public ScrollList(int cellheight)
		{
			this.cellheight=cellheight;
			scrollbar=new Scrollbar(Scrollbar.VERTICAL);
			super.add(scrollbar);
			setBackground(Color.lightGray);
		}
	
	// C O N T A I N E R  M E T H O D S
			
		public Component add(Component comp)
		{
			entry.addElement(comp);
			return super.add(comp);
		}
		
		public void remove(Component comp)
		{
			entry.removeElement(comp);
			super.remove(comp);
		}
		
		public void layout()
		{
			int width=size().width;
			int height=size().height;
			if (height<0) return;
			
			int components=entry.size();
			int maxComponents=height/cellheight;
			
			if (maxComponents>components) maxComponents=components;
			
			int invisibleComponents=components-maxComponents;
			
			scrollbar.enable(invisibleComponents>0);
			int firstElement=scrollbar.getValue();
			if (firstElement>invisibleComponents) firstElement=invisibleComponents;
			scrollbar.setValues(firstElement,1,0,invisibleComponents+1);		
			
			for(int i=0;i<firstElement;i++)
				((Component)entry.elementAt(i)).hide();
			for(int i=maxComponents;i<components;i++)
				((Component)entry.elementAt(i)).hide();
						
			int offset=0;
			for(int i=firstElement;i<firstElement+maxComponents;i++)
			{
				((Component)entry.elementAt(i)).show();
				((Component)entry.elementAt(i)).reshape(0,offset,width-18,cellheight);
			 	offset+=cellheight;
			}
			scrollbar.reshape(width-18,0,18,height);
		}
		
		public boolean handleEvent(Event evt)
		{
			if (evt.target==scrollbar) layout();
			return super.handleEvent(evt);
		}
		
		
}
