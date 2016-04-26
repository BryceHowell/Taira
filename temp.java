/*
 * Taira
 * Copyright (C) 2012 Teth Enterprises
 */

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.event.MouseEvent;

public class temp extends Applet implements Runnable {

  // keys
  private boolean[] a = new boolean[32768];

    final int VK_LEFT = 0x25;
    final int VK_RIGHT = 0x27;
    final int VK_UP = 0x26;
    final int VK_DOWN = 0x28;
    final int VK_JUMP = 0x42;
    final int VK_PAUSE = 0x50;
    final int VK_HINTS = 0x38;  
    final int VK_W = 0x57;
    final int VK_S = 0x53;
    final int VK_A = 0x41;
    final int VK_D = 0x44;
    final int VK_ESCAPE= 0x1A;

    Puzzle puzz;

  @Override
  public void start() {
    enableEvents(8);
    enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    new Thread(this).start();
  }

  public void run() {
    
    BufferedImage image = new BufferedImage(160, 192, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)image.getGraphics();
    BufferedImage buildimage = new BufferedImage(640, 512, BufferedImage.TYPE_INT_RGB);
    Graphics2D gb = (Graphics2D)buildimage.getGraphics();
    Graphics2D g2 = null;

    int invdex=0;    
    int px,py;
    int pdx=0,pdy=0;
    int i;
    int j;
    int k = 0;
    int x;
    int y;
    int z;
    Color[] colorCycle= { Color.RED, Color.ORANGE, Color.YELLOW,Color.GREEN,Color.BLUE,Color.CYAN,Color.MAGENTA};
    Color[] brightCycle= { Color.BLACK, Color.DARK_GRAY,Color.GRAY,Color.LIGHT_GRAY,Color.WHITE};
    int colorIndex=0;
    int brightIndex=0;
    boolean whichCycle=true;
    Color manColor=Color.RED;
    int timer=0;
    int colortimer=0;
    px=78;
    py=156;

    Color consoleColor=new Color(0x880000);

    puzz=new Puzzle(20,20,6);
    
  puzz.randomize();



    long nextFrameStartTime = System.nanoTime();
    long lastInventoryTick= System.nanoTime();
    while(true) {

      do {
        nextFrameStartTime += 16666667;
        
        // -- update starts ----------------------------------------------------
          // CONTROLLER LOGIC GOES HERE
          pdx=0; pdy=0;
	  if (a[VK_ESCAPE]) System.exit(0); // this is totally wrong. this would close the VM if other 
                                            // applets were running but I am lazy
					// It also does not do anything at all ...
          timer++;
      } while(nextFrameStartTime < System.nanoTime());

      // -- render starts ------------------------------------------------------

 
	
	
	  

      // clear frame
      //g.setClip(0,0,160,192);
      //g.setColor(Color.BLUE);
      //g.fillRect(0, 0, 160, 192);
      //g.setClip(0,0,160,192);
      //g.setColor(Color.LIGHT_GRAY);
      //g.fillRect(8, 17, 144, 155);



      // -- render ends --------------------------------------------------------

      // show the hidden buffer
      if (g2 == null) {
        g2 = (Graphics2D)getGraphics();
        requestFocus();
      } else {
	gb.setColor(Color.BLACK);

	gb.fillRect(0,0,640,480); 
 	puzz.draw(gb);
        //gb.drawImage(image, 0, 16, 640, 512, null);
	
	g2.drawImage(buildimage,0,0,640,512, null);
      }

      // burn off extra cycles
      while(nextFrameStartTime - System.nanoTime() > 0) {
        Thread.yield();
        }
    }
  }

 public void processMouseEvent(MouseEvent e) {
   if (e.getID()==MouseEvent.MOUSE_CLICKED)  {
     int x=e.getX()/16; 
     int y=e.getY()/12;
     if (e.getButton()==1) puzz.simplecycle(x,y); 
       else if (e.getButton()==3) puzz.simpleshatter(x,y);
     
     System.out.println(e.getX() + " " + e.getY());
     
     }

   }
  


  @Override
  public void processKeyEvent(KeyEvent keyEvent) {
    final int VK_LEFT = 0x25;
    final int VK_RIGHT = 0x27;
    final int VK_UP = 0x26;
    final int VK_DOWN = 0x28;
    final int VK_JUMP = 0x42;
    final int VK_W = 0x57;
    final int VK_S = 0x53;
    final int VK_A = 0x41;
    final int VK_D = 0x44;
    final int VK_PAUSE = 0x50; // press p for pause
    final int VK_HINTS = 0x38; // press 8 for hint map
    final int VK_ESCAPE = 0x1a; 

    int k = keyEvent.getKeyCode();
    if (k > 0) { 
    // original maps WASD to arrow keys
    //  k = k == VK_W ? VK_UP : k == VK_D ? VK_RIGHT : k == VK_A ? VK_LEFT
    //      : k == VK_S ? VK_DOWN : k;
    
      a[ (k==VK_ESCAPE || k==VK_W || k==VK_A || k==VK_S || k==VK_D) || (k >= VK_LEFT && k <= VK_DOWN) || k == VK_HINTS 
          || k == VK_PAUSE ? k : VK_JUMP] = keyEvent.getID() != 402;
    }
  }

  // to run in window, uncomment below
  public static void main(String[] args) throws Throwable {
    javax.swing.JFrame frame = new javax.swing.JFrame("Annoytris");
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    temp applet = new temp();
    applet.setPreferredSize(new java.awt.Dimension(640, 512));
    frame.add(applet, java.awt.BorderLayout.CENTER);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    Thread.sleep(250);
    applet.start();
  }
}

