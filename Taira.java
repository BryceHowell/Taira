/*
 * Taira
 * Copyright (C) 2012 Teth Enterprises
 */

import java.applet.Applet;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;

public class Taira extends Applet implements Runnable {

  // keys
  private boolean[] a = new boolean[32768];

    final int VK_ONE = 0x31;
    final int VK_LEFT = 0x25;
    final int VK_RIGHT = 0x27;
    final int VK_UP = 0x26;
    final int VK_DOWN = 0x28;
    final int VK_SPACE = 0x20;
    final int VK_PAUSE = 0x50;
    final int VK_HINTS = 0x38;  
    final int VK_W = 0x57;
    final int VK_S = 0x53;
    final int VK_A = 0x41;
    final int VK_D = 0x44;
	final int VK_X = 0x58;
    final int VK_ESCAPE= 0x1B;
	final int VK_F1=112;
	final int VK_F2=113;
	
    final Color[] colorCycle= { Color.RED, Color.ORANGE, Color.YELLOW,Color.GREEN,Color.BLUE,Color.CYAN,Color.MAGENTA};
    final Color[] brightCycle= { Color.BLACK, Color.DARK_GRAY,Color.GRAY,Color.LIGHT_GRAY,Color.WHITE};

  @Override
  public void start() {
    enableEvents(8);
    new Thread(this).start();
  }
  
  SpriteLibrary SL;
  int invdex,px,py,pdx,pdy,i,j,k,x,y,z;
  int lastpdx,lastpdy;
  int colorIndex,brightIndex;
  boolean dead;
  boolean whichCycle,lamplight;
  Color manColor;
  int timer,colortimer;
  Vector<Scene> world;
  Scene currentRoom;
  Item [] inventory;
  Vector<Monster> ML;
  Vector<Location> LL;
  
  BufferedImage [] swordImage;
  BufferedImage [] magnetImage;
  
  
  public void init() {
	  System.out.println("INIT");
	  
  }
  
  public void run() {
    
    BufferedImage image = new BufferedImage(160, 192, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)image.getGraphics();
    BufferedImage buildimage = new BufferedImage(640, 512, BufferedImage.TYPE_INT_RGB);
    Graphics2D gb = (Graphics2D)buildimage.getGraphics();
    Graphics2D g2 = null;

    Color consoleColor=new Color(0x880000);

	  SL=new SpriteLibrary();
	  SL.readFile("sprites.txt");
	  invdex=0; pdx=0; pdy=0; colorIndex=0; brightIndex=0;
	  whichCycle=true;
	  manColor=Color.RED;
	  timer=0;
      colortimer=0;
	  px=78;
	  py=156;
	  dead=false;
	  world=RoomParser.fileRead("labyrinth.txt", SL); 
      Scene startroom=null;
	  for (int s=0; s<world.size(); s++) {
		if (world.get(s).getName().equalsIgnoreCase("bluecastle")) { startroom=world.get(s); break; }
		}
	  currentRoom=startroom;
	  ML=new Vector<Monster>(32);
	  LL=new Vector<Location>(64);
	  ItemParser.fileRead("itemlist.txt",SL,world,ML,LL);	  
	  inventory=new Item[24];
	  lamplight=false;

	swordImage=new BufferedImage[4];
	magnetImage=new BufferedImage[4];	
	// ASSIGN ITEM USAGE SPRITES HERE
	Sprite horzSword=SL.nameSearch("broadsword-hrz");
	Sprite vertSword=SL.nameSearch("broadsword-vrt");
	//if (horzSword.pix==null) System.out.println("hpNULL");
	//if (vertSword.pix==null) System.out.println("vpNULL");
	swordImage[0]=horzSword.pix[0];
	swordImage[1]=vertSword.pix[0];
	swordImage[2]=horzSword.pix[1];
	swordImage[3]=vertSword.pix[1];
	
	
    Force force=new Force();
    force.inside=false;
    BoundBox bound=new BoundBox();

    long nextFrameStartTime = System.nanoTime();
    long lastInventoryTick= System.nanoTime();
	
	long lastBroadswordTime=System.nanoTime()-50000000;
	
    while(true) {

      do {
        nextFrameStartTime += 16666667;
        
        // -- update starts ----------------------------------------------------
          // CONTROLLER LOGIC GOES HERE
          pdx=0; pdy=0;
		  if (a[VK_PAUSE]) {
				System.out.println("("+px+","+py+") "+currentRoom.getName());
				} // prints position and room for builders to use

		  if (a[VK_F1]) {  // this routine depends on there being at least as many locations as items+monsters
				// shuffle items and monsters into location slots
				// randomize the location list first
				for (int i=0; i<LL.size(); i++) {
					int j=(int)Math.floor((LL.size()-i)*Math.random())+i;
					Location temp=LL.get(i);
					LL.set(i,LL.get(j));
					LL.set(j,temp);
					}	
				//   go through inventory items 
				Vector <Item> tempitemlist=new Vector<Item>(64);
				for (int i=0; i<24; i++)
					if (inventory[i]!=null) {
						Item itemtemp=inventory[i];
						if (itemtemp.type.equalsIgnoreCase("light")) lamplight=false;
						inventory[i]=null;
						tempitemlist.add(itemtemp);						
						}
				// remove all items from rooms, place in temp list
				Scene iterate;
				for (int i=0; i<world.size(); i++) {
					iterate=world.get(i);
					for (int j=0; j<iterate.itemList.size(); j++) {
						tempitemlist.add(iterate.itemList.get(j));
						}
					iterate.itemList.clear();						
					}
				
				
				// iterate temp list and place items at locations in order
				int locindex=0;
				for (int i=0; i<tempitemlist.size(); i++) {
						Item itemtemp=tempitemlist.get(i);
						Location locale=LL.get(locindex); locindex++;
						// place the item using locale
						itemtemp.x=locale.x;
						itemtemp.y=locale.y;
						itemtemp.z=locale.z;
						locale.room.addItem(itemtemp);					
					}
				//   go through monsterlist and relocate according to LL as above
				// resetting monsters to alive
				for (int i=0; i<ML.size(); i++) {
					Monster m=ML.get(i);
					m.dead=false;
					m.where.removeMonster(ML.get(i));
					Location locale=LL.get(locindex); locindex++;
					m.where=locale.room;
					m.where.addMonster(m);
					m.x=locale.x;
					m.y=locale.y;
					m.z=locale.z;
					}				
				// return player to start
				// return to the startroom (outside bluecastle)
			    currentRoom=startroom;
			    px=78;
				py=156;
				// make alive again (just in case)
				dead=false;
				 
		  }

				
		  if (a[VK_F2]) {
				//drop all inventory items in place, centered on current position
				for (int i=0; i<24; i++)
					if (inventory[i]!=null) {
						Item itemtemp=inventory[i];
						if (itemtemp.type.equalsIgnoreCase("light")) lamplight=false;
						inventory[i]=null;
						itemtemp.x=px+2-itemtemp.width/2;
						itemtemp.y=py+4-itemtemp.height/2;
						currentRoom.addItem(itemtemp);	
						}
				// return to the startroom (outside bluecastle)
			    currentRoom=startroom;
			    px=78;
				py=156;
				// make alive again
				dead=false;
				// set monsters to alive again...
				for (int i=0; i<ML.size(); i++ ) ML.get(i).dead=false;
				}			
          if (!dead && a[VK_UP]) { pdy=-2; lastpdy=-2; lastpdx=0; }
          if (!dead && a[VK_DOWN]) { pdy=2; lastpdy=2; lastpdx=0; }
          if (!dead && a[VK_LEFT]) { pdx=-1; lastpdx=-1; lastpdy=0; }
          if (!dead && a[VK_RIGHT]) { pdx=1; lastpdx=1; lastpdy=0; }
		  
		  if (!dead && a[VK_X] && inventory[invdex]!=null) {
			//remove item from inv
			Item itemtemp=inventory[invdex];
			if (itemtemp.type.equalsIgnoreCase("light")) lamplight=false;
			inventory[invdex]=null;
			// place in currentRoom's itemList, calculating x and y properly (not overlapping player)
			if (lastpdx!=0) {
				itemtemp.y=py-(itemtemp.height-8)/2;
				if (lastpdx<0) itemtemp.x=px-2-itemtemp.width; else itemtemp.x=px+2+4;					
				}
			if (lastpdy!=0) {
				itemtemp.x=px-(itemtemp.width-4)/2;
				if (lastpdy<0) itemtemp.y=py-4-itemtemp.height; else itemtemp.y=py+2+8;					
				}					
			currentRoom.addItem(itemtemp);	
			}

		  if (!dead && a[VK_SPACE] && inventory[invdex]!=null) {
				//System.out.println("SWOOSH");
			// broadsword and foil... thrust out for a fract. second -draw it in the render section
			//   kills dragons and smaller creatures correspondingly
			if (inventory[invdex].name.equalsIgnoreCase("broadsword")) {
				// set a timer for the sword to be out
				//System.out.println("THRUST");
				lastBroadswordTime=System.nanoTime();
				}
			// magnet attracts ... all objects in line with your direction.. or attracts all in room
			//   not sure which i prefer yet ... 
			
			}
		
		  
          if (System.nanoTime()-lastInventoryTick>100000000l) {
          	if (a[VK_W] || a[VK_S]) invdex^=1;
          	if (a[VK_A]) { invdex+=22; invdex%=24; } 
          	if (a[VK_D]) { invdex+=2;  invdex%=24; }
		lastInventoryTick=System.nanoTime();
		}

          px+=pdx; py+=pdy;
          // update timer
          timer++;
      } while(nextFrameStartTime < System.nanoTime());
	  // -- monster movement ---------------------------------------------------
	  for (int i=0; i<ML.size(); i++) {
		Monster moveM = ML.get(i);
		moveM.move(px,py,currentRoom);
		}
	  // -- monster collisions check here
	  // several things can occur depending on the monster's type.
	  // return the type
	  Monster m=currentRoom.monsterCheck(bound);
	  if (m!=null && !m.dead) {
		  if (m.name.equalsIgnoreCase("spider") || m.name.equalsIgnoreCase("specter")) { dead=true; }
		  // dragons and aliens will need their own cases
		}
      // -- render starts ------------------------------------------------------

 
      bound.x=px; bound.y=py; bound.z=0;
      bound.width=4; bound.height=8;
	
	  currentRoom=currentRoom.boundaryCheck(bound);
	
	  currentRoom.draw(g,bound,lamplight);
	  px=bound.x; py=bound.y;
	  
      force=currentRoom.collision(bound);
	  px+=force.dx; py+=force.dy;
      if (force.inside) {
		 
        if (force.scene==null) {
          // move man back
          px-=pdx; py-=pdy; } else { 
		    //if (force.brush.type==Brush.GATE) System.out.println("annoying");
	        currentRoom=force.scene;
	        px=bound.x; py=bound.y;
			
	      }
        }
	  if (force.brush!=null) { 
	        //System.out.println("collision");
			Item holding=inventory[invdex];
			// check if brush is GATE or a trigger
			if (force.brush.type==Brush.GATE && holding!=null) {
				    //System.out.println("should happen1");
					//if (holding==null) System.out.println("holding is null"); else System.out.println("holding not null");
					if (force.brush.keyname==null) System.out.println("force.brush.keyname");
					if (holding.type==null) System.out.println("holding.type");
					if (holding.name==null) System.out.println("holding.name");
					if (holding.type.equalsIgnoreCase("key") && holding.name.equalsIgnoreCase(force.brush.keyname) ) {
						// now toggle unlocked
						//System.out.println("should happen2");
						force.brush.unlocked=!force.brush.unlocked;
					}					
				
				// check if holding a key. if so, toggle unlocked
				}
			if (force.brush.trigger!=Brush.TRIGGER_NONE) {
				if (force.brush.trigger==Brush.TRIGGER_ON) {
					// switch passable and drawn to on for target
					force.brush.target.passable=true;
					force.brush.target.drawn=true;
					} else if (force.brush.trigger==Brush.TRIGGER_OFF) {
					// switch passable and drawn off for target
					force.brush.target.passable=false;
					force.brush.target.drawn=false;
					} else { // force.brush.trigger==Brush.TRIGGER_TOGGLE
					// toggle passable and drawn
					//if (force.brush.target==null) System.out.println("force.brush.target is null");
					force.brush.target.passable=!force.brush.target.passable;
					force.brush.target.drawn=!force.brush.target.drawn;
					}
				}
			}

		if (System.nanoTime()-lastBroadswordTime<100000000) {
			//System.out.println("SWORD");
			// find out direction, draw sword there
			int dir=0;
			if (lastpdx!=0) if (lastpdx>0) dir=0; else dir=2;
			if (lastpdy!=0) if (lastpdy<0) dir=1; else dir=3;
			bound.width=swordImage[dir].getWidth(); bound.height=swordImage[dir].getHeight();
			// come up with a bounding box, pass that into a monsterCheck routine
			if (dir==0) { bound.x=px+4; bound.y=py+4-swordImage[0].getHeight()/2; } 
			if (dir==2) { bound.x=px-swordImage[2].getWidth(); bound.y=py+4-swordImage[2].getHeight()/2; } 
			if (dir==1) { bound.x=px+2-swordImage[1].getWidth()/2; bound.y=py-swordImage[1].getHeight(); } 
			if (dir==3) { bound.x=px+2-swordImage[3].getWidth()/2; bound.y=py+8; }
			Monster mon=currentRoom.monsterCheck(bound);
			if (mon!=null) {
				if (mon.name.equalsIgnoreCase("spider") || mon.type.equalsIgnoreCase("dragon")) { mon.dead=true; }
				}	
			// draw sword
			g.setClip(bound.x,bound.y,bound.width,bound.height);
			g.drawImage(swordImage[dir],bound.x,bound.y,null);
			}
		
		
		bound.x=px; bound.y=py; bound.z=0;
		bound.width=4; bound.height=8;
		Item pickup=currentRoom.itemCheck(bound);
		if (pickup != null) {
		  int firstEmpty;
		  for (firstEmpty=0; firstEmpty<24; firstEmpty++) if (inventory[firstEmpty]==null) break;
		  if (firstEmpty<24) {
			// add Item to inventory
			inventory[firstEmpty]=pickup;
			// remove Item from currentRoom
			currentRoom.itemList.remove(pickup);
		    }
			
		  if (pickup.type.equalsIgnoreCase("light")) lamplight=true;
		  }
	   
      // apply new forces
      pdx+=force.dx; pdy+=force.dy;

      // clear frame
      //g.setClip(0,0,160,192);
      //g.setColor(Color.BLUE);
      //g.fillRect(0, 0, 160, 192);
      //g.setClip(0,0,160,192);
      //g.setColor(Color.LIGHT_GRAY);
      //g.fillRect(8, 17, 144, 155);

      // draw "the man"
      colortimer++;
      colortimer%=5;
      whichCycle=false;
      if (colortimer==0 && !dead) 
      if (whichCycle) {
          manColor=colorCycle[colorIndex];
          colorIndex++;
          colorIndex%=7;
          whichCycle=false;
          } else {
          manColor=brightCycle[brightIndex];
          brightIndex++;
          brightIndex%=5;
          whichCycle=true;
          }	
	  g.setClip(px,py,4,8);
      g.setColor(manColor);
      g.fillRect(px, py, 4,8);



      // -- render ends --------------------------------------------------------

      // show the hidden buffer
      if (g2 == null) {
        g2 = (Graphics2D)getGraphics();
        requestFocus();
      } else {
	gb.setColor(Color.BLACK);
	gb.fillRect(0,0,640,16); 
        gb.drawImage(image, 0, 16, 640, 384, null);
	gb.setColor(Color.BLACK);
	gb.fillRect(0,400,640,16); 
	gb.setColor(consoleColor);
	gb.fillRect(0,416,640,96);
        // I ought to have a separate buffer for the inventory, draw it first, then draw it onto g2 
	gb.setColor(AtariColor.translate("LOR"));
	int invx=invdex/2; int invy=invdex%2;
	gb.drawRect(4+46*invx,4+416+46*invy,42,42);
	gb.drawRect(5+46*invx,5+416+46*invy,40,40);
	for (int iv=0; iv<24; iv++) {
		int ix=iv/2; int iy=iv%2;
		if (inventory[iv]!=null) gb.drawImage(inventory[iv].iconview,6+46*ix,416+6+46*iy,null);
	}
	
	g2.drawImage(buildimage,0,0,640,512, null);
      }

      // burn off extra cycles
      while(nextFrameStartTime - System.nanoTime() > 0) {
        Thread.yield();
        }
    }
  }

  @Override
  public void processKeyEvent(KeyEvent keyEvent) {
    final int VK_LEFT = 0x25;
    final int VK_RIGHT = 0x27;
    final int VK_UP = 0x26;
    final int VK_DOWN = 0x28;
    final int VK_SPACE = 0x20;
    final int VK_W = 0x57;
    final int VK_S = 0x53;
    final int VK_A = 0x41;
    final int VK_D = 0x44;
	final int VK_X = 0x58;
    final int VK_PAUSE = 0x50; // press p for pause
    final int VK_HINTS = 0x38; // press 8 for hint map
    final int VK_ESCAPE = 0x1B; 
    final int VK_ONE = 0x31; 
	final int VK_F1=112;
	final int VK_F2=113; // resets (in case of player death)

	
    int k = keyEvent.getKeyCode();
	
    if (k > 0) {
			//System.out.println("k="+k);
    // original maps WASD to arrow keys
    //  k = k == VK_W ? VK_UP : k == VK_D ? VK_RIGHT : k == VK_A ? VK_LEFT
    //      : k == VK_S ? VK_DOWN : k;
	a[k]=keyEvent.getID()!=402; 
	//System.out.println("a="+k); // uncomment to troubleshoot and discover key values
      //a[ (k==VK_ONE || k==VK_ESCAPE || k==VK_W || k==VK_A || k==VK_S || k==VK_D || k==VK_X ) || (k >= VK_LEFT && k <= VK_DOWN) || k == VK_HINTS 
      //    || k == VK_PAUSE ? k : VK_SPACE] = keyEvent.getID() != 402;
    }
  }

  // to run in window, uncomment below
  public static void main(String[] args) throws Throwable {
    javax.swing.JFrame frame = new javax.swing.JFrame("Taira");
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    Taira applet = new Taira();
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

