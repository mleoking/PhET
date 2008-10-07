import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class demo6 extends Applet implements Runnable
{
	private Thread idx_Thread;
	idx3d_Scene scene;

	int oldx=0;
	int oldy=0;
	boolean autorotation=true;
	boolean antialias=false;
	boolean useLensFlare=true;
	idx3d_FXLensFlare lensFlare;

	public void init()
	{
		setNormalCursor();
		
		// BUILD SCENE
		
			scene=new idx3d_Scene(this.size().width,this.size().height);
			scene.environment.setBackground(new idx3d_Material(getDocumentBase(),"stardust.material").getTexture());
			
			idx3d_Material metal=new idx3d_Material();
			metal.setEnvmap(new idx3d_Texture(getDocumentBase(),"chrome.jpg"));
			metal.setReflectivity(255);
			scene.addMaterial("Metal",metal);
			
			scene.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,80));			
			
						
			try{
				new idx3d_3ds_Importer().importFromURL(new java.net.URL(getDocumentBase(),"wobble.3ds"),scene);
				scene.rebuild();
				for (int i=0; i<scene.objects;i++)
				{
					scene.object[i].setMaterial(scene.material("Metal"));
					//scene.object[i].meshSmooth();
				}
				
				scene.normalize();
			}
			catch(Exception e){System.out.println(e+"");}
			
			lensFlare=new idx3d_FXLensFlare("LensFlare1", scene,false);
			lensFlare.preset1();
			scene.object("LensFlare1").scale(60f);
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
			float dx=(float)Math.sin((float)System.currentTimeMillis()/1000)/20;
			float dy=(float)Math.cos((float)System.currentTimeMillis()/1000)/20;
			scene.rotate(-speed*dx,speed*dy,speed*0.04f);
			scene.object("LensFlare1").rotate(-0.07f,0.02f,0.03f);
		}
		
		scene.render();
		if (useLensFlare) lensFlare.apply();
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
		if ((char)key=='1') {lensFlare.preset1(); return true; }
		if ((char)key=='2') {lensFlare.preset2(); return true; }
		if ((char)key=='3') {lensFlare.preset3(); return true; }
		
		if ((char)key=='l') {useLensFlare=!useLensFlare; return true; }
		
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
