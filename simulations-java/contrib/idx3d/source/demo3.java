import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo3 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			
			scene.addMaterial("Stone1",new idx3d_Material(new idx3d_Texture(getDocumentBase(),"stone1.jpg")));
			scene.addMaterial("Stone2",new idx3d_Material(new idx3d_Texture(getDocumentBase(),"stone2.jpg")));
			scene.addMaterial("Stone3",new idx3d_Material(new idx3d_Texture(getDocumentBase(),"stone3.jpg")));
			scene.addMaterial("Stone4",new idx3d_Material(new idx3d_Texture(getDocumentBase(),"stone4.jpg")));
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,144,120));			
			scene.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0x332211,100,40));
			scene.addLight("Light3",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0x666666,200,120));
			
			try{
				new idx3d_3ds_Importer().importFromStream(new java.net.URL(getDocumentBase(),"wobble.3ds").openStream(),scene);
				scene.rebuild();
				for (int i=0; i<scene.objects;i++)
					idx3d_TextureProjector.projectFrontal(scene.object[i]);
				
				scene.object("Sphere1").setMaterial(scene.material("Stone1"));
				scene.object("Wobble1").setMaterial(scene.material("Stone2"));
				scene.object("Wobble2").setMaterial(scene.material("Stone3"));
				scene.object("Wobble3").setMaterial(scene.material("Stone4"));
				scene.normalize();
			}
			catch(Exception e){System.out.println(e+"");}
	}

	public synchronized void paint(Graphics g)
	{
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
		if (autorotation)
		{
			float speed=1;
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000f)/20f;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/1000f)/20f;
			scene.rotate(-speed*dx,speed*dy,speed*0.04f);
		}
	
		scene.render();
		g.drawImage(scene.getImage(),0,0,this);
	}		

	public boolean imageUpdate(Image image, int a, int b, int c, int d, int e)
   	{
   	     return true;
   	}

	public boolean mouseDown(Event evt,int x,int y)
	{
		oldx=x;
		oldy=y;
		setMovingCursor();
		return true;
	}

	public boolean keyDown(Event evt,int key)
	{
		if (key==32) { System.out.println(scene.getFPS()+""); return true; }
		if (key==Event.PGUP) {scene.defaultCamera.shift(0f,0f,0.2f); return true; }
		if (key==Event.PGDN) {scene.defaultCamera.shift(0f,0f,-0.2f); return true; }
		if (key==Event.UP) {scene.defaultCamera.shift(0f,-0.2f,0f); return true; }
		if (key==Event.DOWN) {scene.defaultCamera.shift(0f,0.2f,0f); return true; }
		if (key==Event.LEFT) {scene.defaultCamera.shift(0.2f,0f,0f); return true; }
		if (key==Event.RIGHT) {scene.defaultCamera.shift(-0.2f,0f,0f); return true; }
		if ((char)key=='a') {antialias=!antialias; scene.setAntialias(antialias); return true; }
		if ((char)key=='+') {scene.scale(1.2f); return true; }
		if ((char)key=='-') {scene.scale(0.8f); return true; }
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

	public boolean mouseUp(Event evt,int x,int y)
	{
		autorotation=true;
		setNormalCursor();
		return true;
	}
	
	private void setMovingCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.MOVE_CURSOR);
	}
	
	private void setNormalCursor()
	{
		if (getFrame()==null) return;
		getFrame().setCursor(Frame.HAND_CURSOR);		
	}
	
	private Frame getFrame()
	{
		Component comp=this;
		while ((comp=comp.getParent())!=null) if(comp instanceof Frame) return (Frame)comp;
		return null;
	}
}
