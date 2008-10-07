import idx3d.*;
import java.awt.*;
import java.applet.*;
import java.net.*;

public final class demo10 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;
	int oldx=0;
	int oldy=0;
	boolean antialias=false;
	boolean autorotation=true;
	boolean handCursor=false;
	
	URL link1,link2,link3;
	
	String link1URL=new String("http://www.darksim.com/html/gm_metal.html");
	String link2URL=new String("http://www.chscene.ch/index1.htm");
	String link3URL=new String("http://www.condensity.com");
	
	
	public void init()
	{		
		
		try
		{
			link1=new URL(link1URL);
			link2=new URL(link2URL);
			link3=new URL(link3URL);
		}
		catch(MalformedURLException ignored){}
			
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height-18);
			scene.useIdBuffer(true);
			
			idx3d_Material metal=new idx3d_Material();
			metal.setEnvmap(new idx3d_Texture(getImage(getDocumentBase(),"chrome.jpg")));
			metal.setReflectivity(255);
			scene.addMaterial("Metal",metal);
			
			scene.addMaterial("Flat", new idx3d_Material());
			scene.material("Flat").setFlat(true);
			
			scene.addMaterial("Link1", new idx3d_Material(new idx3d_Texture(getImage(getDocumentBase(),"link1.jpg"))));
			scene.addMaterial("Link2", new idx3d_Material(new idx3d_Texture(getImage(getDocumentBase(),"link2.jpg"))));
			scene.addMaterial("Link3", new idx3d_Material(new idx3d_Texture(getImage(getDocumentBase(),"link3.jpg"))));

			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,200,80));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,0.4f),0xAAAAAA,100,80));
					
			try{
				new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),"linkable.3ds"),scene);
				
				scene.rebuild();
				scene.normalize();
				scene.rotate(-3.14159265f/2,-2f,0.4f);
				
				for (int i=0; i<scene.objects;i++) 
					if (scene.object[i].name.startsWith("Box"))
					{
						scene.object[i].setMaterial(scene.material("Flat"));
						scene.object[i].userData=idx3d_Vector.random(0.1f);
						scene.object[i].detach();
					}

				scene.object("Blob").setMaterial(scene.material("Metal"));
					
				scene.object("Link1").setMaterial(scene.material("Link1"));
				scene.object("Link2").setMaterial(scene.material("Link2"));
				scene.object("Link3").setMaterial(scene.material("Link3"));
				scene.material("Link1").setTransparency(64);
				scene.material("Link2").setTransparency(64);
				scene.material("Link3").setTransparency(64);
				
				scene.object("Inside1").setMaterial(scene.material("Metal"));
				scene.object("Inside2").setMaterial(scene.material("Metal"));
				scene.object("Inside3").setMaterial(scene.material("Metal"));

			}
			catch(Exception e){System.out.println(e+"");}
	}

	public synchronized void paint(Graphics g)
	{
		g.setColor(Color.darkGray);
		g.fillRect(0,this.size().height-18,this.size().width,18);
	}

	public void start()
	{
		if (idx_Thread == null)
		{
			idx_Thread = new Thread(this);
			idx_Thread.start();
		}
	}
	
	public void stop()
	{
		if (idx_Thread != null)
		{
			idx_Thread.stop();
			idx_Thread = null;
		}
	}

	public void run()
	{
		while(true)
		{
			repaint();
			try
			{
				idx_Thread.sleep(10);
			}
			catch (InterruptedException e){}
		}
	}

	public synchronized void update(Graphics g)
	{
		if (autorotation) scene.rotate(0f,0.04f,0f);
		float hue=(float)((int)((System.currentTimeMillis()/48)&255))/255;
		scene.material("Flat").setColor(Color.getHSBColor(hue,1,1).getRGB());
		
		for (int i=0; i<scene.objects;i++) 
			if (scene.object[i].name.startsWith("Box"))
				scene.object[i].rotateSelf((idx3d_Vector)scene.object[i].userData);
		
		g.setColor(Color.darkGray);
		g.fillRect(0,this.size().height-18,this.size().width,18);
		
		idx3d_Object obj=scene.identifyObjectAt(oldx,oldy);
		scene.material("Link1").setTransparency(0);
		scene.material("Link2").setTransparency(0);
		scene.material("Link3").setTransparency(0);
	
		if (obj!=null)
		{
			if (obj.name.equals("Link1")||obj.name.equals("Link2")||obj.name.equals("Link3"))
			{
				setHandCursor();
				g.setColor(Color.white);
			}
			else setMoveCursor();
			
			if (obj.name.equals("Link1"))
			{
				g.drawString(link1URL,4,this.size().height-4);
				scene.material("Link1").setTransparency(88);
			}
			
			if (obj.name.equals("Link2"))
			{
				g.drawString(link2URL,4,this.size().height-4);
				scene.material("Link2").setTransparency(88);
			}
			
			if (obj.name.equals("Link3"))
			{
				g.drawString(link3URL,4,this.size().height-4);
				scene.material("Link3").setTransparency(88);
			}
		}
		else setMoveCursor();
				
		scene.render();
		g.drawImage(scene.getImage(),0,0,this);	
	}		

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}
   	
   	public boolean keyDown(Event evt,int key)
	{
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='i') {idx3d.debug.Inspector.inspect(scene); return true; }
		
		return true;
	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		autorotation=false;
		idx3d_Object obj=scene.identifyObjectAt(oldx,oldy);
		if (obj!=null)
		{
			if (obj.name.equals("Link1")) getAppletContext().showDocument(link1,"_blank");
			if (obj.name.equals("Link2")) getAppletContext().showDocument(link2,"_blank");
			if (obj.name.equals("Link3")) getAppletContext().showDocument(link3,"_blank");
		}
		
		return true;
	}
	
	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		return true;
	}
	
	public boolean mouseMove(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		autorotation=false;
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		scene.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		return true;
	}
	
	public boolean mouseExit(Event evt,int x,int y)
	{
		oldx=-1;
		oldy=-1;
		return true;
	}

	private void setHandCursor()
	{
		if (handCursor) return;
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);
		handCursor=true;
	}
	
	private void setMoveCursor()
	{
		if (!handCursor) return;
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);	
		handCursor=false;
	}
	
	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}
}
