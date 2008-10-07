import idx3d.*;
import java.awt.*;
import java.applet.*;

public final class TextureLab extends Dialog
{
	idx3d_Screen textureView;
	idx3d_Scene objectView;
	Image doubleBuffer=null;
	boolean initialized=false;
	boolean viewObject=false;
	int oldx=0;
	int oldy=0;
	idx3d_TextureSettings settings=null;
	
	idx3d_Texture texture;
		
	Button 		random,param,randomColor,returnTexture;
	Label		label1,label2,label3,label4,label5,label6,label7;
	Scrollbar		persistency,density,samples,numColors;
	Label[]		colorLabel;
	TextField[] 	color;
	TextField 		width,height;
	CheckboxGroup	mode;
	Checkbox		perlin,wave,grain,autoupdate;
	
	Font normal;
	idx3dMaterialLab parent=null;
	
	boolean textureId=true;  //true=Texture, false=Envmap
	
	public TextureLab(idx3dMaterialLab parent,idx3d_TextureSettings settings, boolean id)
	{
		super(parent,"Texture Lab",true);
		this.parent=parent;
		this.textureId=id;
		this.settings=settings;
		
		resize(522+insets().left+insets().right,350+insets().top+insets().bottom);
		setResizable(false);
		
		setBackground(Color.lightGray);
		normal=new Font("Helvetica",0,11);
				
		textureView=new idx3d_Screen(256,256);
		objectView=new idx3d_Scene(256,256);
		
		objectView.setAmbient(0x333333);
		objectView.addLight("Light1",new idx3d_Light(new idx3d_Vector(0.2f,0.2f,1f),0xFFFFFF,320,120));			
		objectView.addLight("Light2",new idx3d_Light(new idx3d_Vector(-1f,-1f,1f),0xFFFFFF,160,200));
		
		idx3d_Object trefoil=idx3d_ObjectFactory.TORUSKNOT(2f,3f,0.4f,1.2f,0.48f,1.2f,120,8);
		objectView.addObject("Trefoil",trefoil);
		objectView.object("Trefoil").rotate(0.2f,3.5f,-0.5f);
		objectView.object("Trefoil").setMaterial(new idx3d_Material(texture));
		objectView.object("Trefoil").scale(0.4f);
		objectView.object("Trefoil").removeDuplicateVertices();
		idx3d_TextureProjector.projectTop(objectView.object("Trefoil"));
		
		initGUI();
		show();
	}

	public void paint(Graphics g)
	{
		repaint();
	}
	
	private void initGUI()
	{
		if (initialized) return;
		initialized=true;
		texture=new idx3d_Texture(256,256);

		random=new Button("Create new random texture");
		param=new Button("Create texture from parameters");
		randomColor=new Button("Randomize colors only");
		returnTexture=new Button("Return to Material Lab");
		label1=new Label("Persistency:");
		label2=new Label("Density:");
		label3=new Label("Samples:");
		label4=new Label("Number of colors:");
		label5=new Label("Click & Drag on Texture to view it in 3d");
		label6=new Label("Width:");
		label7=new Label("Height:");
		persistency=new Scrollbar(0,20,7,20,90);
		density=new Scrollbar(0,50,45,50,500);
		samples=new Scrollbar(0,0,1,1,7);
		numColors=new Scrollbar(0,0,1,2,6);
		colorLabel=new Label[5];
		color=new TextField[5];
		for (int i=0;i<5;i++) 
		{
			colorLabel[i]=new Label("Color #"+i);
			color[i]=new TextField();
		}
		width=new TextField();
		height=new TextField();
		mode=new CheckboxGroup();
		perlin=new Checkbox("Perlin",mode,true);
		wave=new Checkbox("Wave",mode,false);
		grain=new Checkbox("Grain",mode,false);
		autoupdate=new Checkbox("Auto Update Texture (for fast computers)");
		autoupdate.setState(true);
		
		random.setFont(normal);
		param.setFont(normal);
		randomColor.setFont(normal);
		returnTexture.setFont(normal);
		label1.setFont(normal);
		label2.setFont(normal);
		label3.setFont(normal);
		label4.setFont(normal);
		label5.setFont(normal);
		perlin.setFont(normal);
		wave.setFont(normal);
		grain.setFont(normal);
		autoupdate.setFont(normal);
		label6.setFont(normal);
		label7.setFont(normal);
		width.setFont(normal);
		height.setFont(normal);
		
		add(random);
		add(param);
		add(randomColor);
		add(returnTexture);
		add(persistency);
		add(density);
		add(samples);
		add(numColors);
		add(label1);
		add(label2);
		add(label3);
		add(label4);
		add(label5);
		add(perlin);
		add(wave);
		add(grain);
		add(autoupdate);	
		add(label6);
		add(label7);
		add(width);
		add(height);
		
		for (int i=0;i<5;i++)
		{ 
			colorLabel[i].setFont(normal);
			add(colorLabel[i]);
			add(color[i]);
		}
		
		applySettings(settings);
		repaint();
	}

	public void update(Graphics g)
	{
		int xoffset=insets().left;
		int yoffset=insets().top;
		
		if (viewObject)
		{ 
			objectView.render();
			g.drawImage(objectView.getImage(),xoffset,yoffset,this);
			return; 
		}
		
		textureView.draw(texture,0,0,256,256);
		doubleBuffer=textureView.getImage();
		g.drawImage(doubleBuffer,xoffset,yoffset,this);
		
		randomColor.reshape(xoffset+256,yoffset+196,256,20);
		param.reshape(xoffset+256,yoffset+216,256,20);
		random.reshape(xoffset+256,yoffset+236,256,20);
		returnTexture.reshape(xoffset+256,yoffset+296,256,20);
		
		label1.reshape(xoffset+260,yoffset+0,92,20);
		label2.reshape(xoffset+260,yoffset+20,92,20);
		label3.reshape(xoffset+260,yoffset+40,92,20);
		label4.reshape(xoffset+260,yoffset+60,92,20);
		persistency.reshape(xoffset+352,yoffset+0,160,20);
		density.reshape(xoffset+352,yoffset+20,160,20);
		samples.reshape(xoffset+352,yoffset+40,160,20);
		numColors.reshape(xoffset+352,yoffset+60,160,20);
		label5.reshape(xoffset+4,yoffset+256,252,20);
		
		perlin.reshape(xoffset,yoffset+276,80,20);
		wave.reshape(xoffset+80,yoffset+276,80,20);
		grain.reshape(xoffset+160,yoffset+276,80,20);
		autoupdate.reshape(xoffset+256,yoffset+256,256,20);
		
		label6.reshape(xoffset,yoffset+296,48,20);
		width.reshape(xoffset+48,yoffset+296,70,20);
		label7.reshape(xoffset+128,yoffset+296,48,20);
		height.reshape(xoffset+176,yoffset+296,70,20);
		
		for (int i=0;i<numColors.getValue();i++)
		{
			colorLabel[i].reshape(xoffset+260,yoffset+90+i*20,60,20);
			try{
				g.setColor(new Color(0xFF000000|(int)Integer.parseInt(color[i].getText(),16)));
			}
			catch(Exception e) {g.setColor(Color.black);}
			g.fillRect(xoffset+330,yoffset+90+i*20,20,20);
			color[i].reshape(xoffset+350,yoffset+90+i*20,160,20);
		}
		for (int i=numColors.getValue();i<5;i++)
		{
			colorLabel[i].reshape(0,0,0,0);
			color[i].reshape(0,0,0,0);
			g.setColor(Color.lightGray);
			g.fillRect(xoffset+330,yoffset+90+i*20,20,20);
		}
	}
	
	public boolean action(Event evt, Object obj)
	{
		if (evt.target==random) { random(); return true; }
		if (evt.target==returnTexture) { returnTexture(); return true; }
		
		if (evt.target==param) { generateTexture(); return true; }
		if (evt.target==randomColor) { randomColor(); return true; }
		return super.action(evt,obj);
	}
	
	public boolean handleEvent(Event evt)
	{
		if (evt.id==Event.WINDOW_DESTROY)
		{
			hide(); dispose(); return true;
		}
		if (autoupdate!=null && autoupdate.getState())
		{
			if (evt.target==numColors) { generateTexture(); return true; }
			if (evt.target==persistency) { generateTexture(); return true; }
			if (evt.target==density) { generateTexture(); return true; }
			if (evt.target==samples) { generateTexture(); return true; }
		}
		else
		{
			if (evt.target==numColors) { repaint(); return true; }
		}
		if (evt.target==perlin) { generateTexture(); return true; }
		if (evt.target==wave) { generateTexture(); return true; }
		if (evt.target==grain) { generateTexture(); return true; }
		return super.handleEvent(evt);
	}
	
	public boolean mouseDown(Event evt,int x,int y)
	{
		viewObject=true;
		objectView.object("Trefoil").material.setTexture(texture);
		oldx=x;
		oldy=y;
		repaint();
		return true;
	}
	
	public boolean mouseUp(Event evt, int x, int y)
	{
		viewObject=false;
		repaint();
		return true;
	}
	
	public boolean mouseDrag(Event evt,int x,int y)
	{
		float dx=(float)(y-oldy)/50;
		float dy=(float)(oldx-x)/50;
		objectView.rotate(dx,dy,0);
		oldx=x;
		oldy=y;
		repaint();
		return true;
	}
	
	private void applySettings(idx3d_TextureSettings settings)
	{
		if (settings==null) { random(); return; }
		persistency.setValue((int)(100*settings.persistency));
		density.setValue((int)(100*settings.density));
		samples.setValue(settings.samples);
		if (settings.type==1) perlin.setState(true);
		if (settings.type==2) wave.setState(true);
		if (settings.type==3) grain.setState(true);
		numColors.setValue(settings.colors.length);
		width.setText(settings.width+"");
		height.setText(settings.height+"");
		for (int i=0;i<numColors.getValue();i++)
			color[i].setText(Integer.toHexString(settings.colors[i]).toUpperCase());

		generateTexture();	
	}		
		
	private void random()
	{
		persistency.setValue((int)(100*idx3d_Math.random(0.2f,0.9f)));
		density.setValue((int)(100*idx3d_Math.random(0.5f,5f)));
		samples.setValue((int)(idx3d_Math.random(1,8)));
		numColors.setValue((int)(idx3d_Math.random(2,6)));
		width.setText("256");
		height.setText("256");
		
		randomColor();
	}
	
	private void returnTexture()
	{
		generateTexture();
		if (parent!=null && settings!=null) parent.setSettings(settings,textureId);
		hide(); dispose();
	}
		
	private void randomColor()
	{
		for (int i=0;i<numColors.getValue();i++)
			color[i].setText(Integer.toHexString(idx3d_Color.random()).toUpperCase());

		generateTexture();
	}

	private void generateTexture()
	{
		float p=(float)persistency.getValue()/100f;
		float d=(float)density.getValue()/100f;
		int s=samples.getValue();
		int nc=numColors.getValue();
		int[] colors=new int[nc];
		for (int i=0;i<nc;i++)
		{
			try{
				colors[i]=(int)Integer.parseInt(color[i].getText(),16);
			}
			catch(Exception e) {colors[i]=0;}
		}
		int modeId=0;
		int w,h;
		try{ w=(int)Integer.parseInt(width.getText()); }
		catch(Exception e) {w=256;}
		try{ h=(int)Integer.parseInt(height.getText()); }
		catch(Exception e) {h=256;}
		
		if (w<=0) w=256;
		if (h<=0) h=256;
		
		if (perlin.getState())
		{
			modeId=1;
			texture=idx3d_TextureFactory.PERLIN(w,h, p, d, s, 1024).colorize(
				idx3d_Color.makeGradient(colors,1024));
		}
		if (wave.getState())
		{
			modeId=2;
			texture=idx3d_TextureFactory.WAVE(w,h, p, d, s, 1024).colorize(
				idx3d_Color.makeGradient(colors,1024));
		}
		if (grain.getState())
		{
			modeId=3;
			texture=idx3d_TextureFactory.GRAIN(w,h, p, d, s,8,1024).colorize(
				idx3d_Color.makeGradient(colors,1024));
		}
		
		settings=new idx3d_TextureSettings(texture,w,h,modeId,p,d,s,colors);
		
		repaint();
	}
	
}
