//  package electron.gui;

//  import electron.utils.*;
//  import java.awt.geom.*;
//  import java.awt.*;
//  import javax.swing.*;
//  import phys2d.*;
//  import phys2d.propagators.*;
//  import electron.*;
//  import java.util.*;
//  import electron.gui.media.*;
//  import java.io.*;
//  import electron.gui.mouse.*;
//  import java.awt.image.*;

//  public class Main
//  {
//      public static void mainBAK(String[]args)
//      {
//  	//File iconMediaDir=new File("D:\\Java\\projects\\Electrons\\data\\icons\\media");
//  	//int num=30;
//  	int num=1;
//  	RandomSystemFactory randFact=new RandomSystemFactory(10,num,50,50,300,300);
//  	System2D sys=randFact.newSystem();

//  	double dt=.15;
//  	int wait=35;
//  	SystemRunner sr=new SystemRunner(sys,dt,wait);
//  	new Thread(sr).start();

//  	ParticlePanel pp=new ParticlePanel();
//  	//ParticlePainter painter=new DotPainter(Color.blue,16);
//  	BufferedImage bi=electron.utils.ResourceLoader.loadBufferedImage("images/Electron3.GIF",pp,true);
//  	bi=electron.utils.AlphaFixer.patchAlpha(bi);
//  	ParticlePainter painter=new ImagePainter(bi);

//  	randFact.updatePanel(pp,sys,painter);
//  	//ParticleGrabber pg=new ParticleGrabber(pp,sys,sr);
//  	ParticleGrabber pg=new ParticleThrower(pp,sys,sr,5,18);
//  	pp.addMouseListener(pg);
//  	pp.addMouseMotionListener(pg);

//  	JFrame jf=new JFrame("ChargePanel.");
//  	jf.setContentPane(pp);
//  	jf.setSize(new Dimension(400,400));
//  	jf.setVisible(true);
//  	jf.validate();

//  	MediaControl mc=new MediaControl(sr,randFact,pp,dt,wait,painter);
//  	JFrame controlFrame=new JFrame("Controls");

//  	controlFrame.getContentPane().add(mc.getPanel(
//  						      ResourceLoader.loadBufferedImage("icons/media/play24.gif",pp,false),
//  						      ResourceLoader.loadBufferedImage("icons/media/play24.gif",pp,false),
//  						      ResourceLoader.loadBufferedImage("icons/media/play24.gif",pp,false)));

//  	controlFrame.pack();
//  	controlFrame.setVisible(true);
//      }
//  }
