import java.util.Vector;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;

class Exit {

	static int EAST=0;
	static int NORTH=1;
	static int WEST=2;
	static int SOUTH=3;



	Scene from;
	int fromType;
	Scene to;
	int toType;
	
	int tX,tY,tW,tH;
	};

class Brush {

  static int BLOCK=0;
  static int GATE=1;     // a specialized nice porticullis/gate that needs a key
  //static int TELEPORT=2; // touching this brush sends you to the exit point
  
  static int TRIGGER_NONE=0;
  static int TRIGGER_ON=1;
  static int TRIGGER_OFF=2;
  static int TRIGGER_TOGGLE=3;
  
  Scene exitScene;
  int exitX,exitY;
  
  String name;
  String keyname;
  
  int trigger;
  String targetstring;
  Brush target;
  
  int x,y,z; // I am almost thinking I should start doing a pseudo-3D system now
             // but we'll see
  int width,height;
  // I need an AtariColor class
  boolean drawn; // do not draw... for detectors and such
  Color color; // I could just include a color member
  boolean teleport; // brush teleports player on touch... implemented differently for gates
  boolean unlocked;
  // color transition shading just to look nice
  boolean passable; // allows being covered by trees, and secret passages
  int force_type; // this member allows me to decide if a passable brush 
                  // exerts a force. only meaningful if passable is true
  // I want doors. I want traps. I want walls that move for puzzles.
  int type;
  BufferedImage decal;
  String imagename;
  public Brush(String col, int a,int b, int c, int w, int h) {
    color=AtariColor.translate(col);
    x=a; y=b; z=c;
    width=w; height=h;
    drawn=true;
	force_type=-1; // -1 means none
    type=BLOCK;
    decal=null;
    unlocked=false;
	passable=false;
	teleport=false;
    name=new String("");
	trigger=TRIGGER_NONE;
    }

	
 
  Scene exit(BoundBox agent) {
	if (type==Brush.GATE && unlocked && teleport) {
			//System.out.println("supposed to happen: GATE TELEPORT");
            agent.x=exitX;
            agent.y=exitY;
			//if (exitScene==null) System.out.println("exitScene is null"); else System.out.println("room is "+exitScene.getName());
            return exitScene; 
            } else
	if (type!=Brush.GATE && teleport) {
			//System.out.println("supposed to happen:BLOCK TELEPORT");
            agent.x=exitX;
            agent.y=exitY;
            return exitScene;
			}
	return null;
	}
  };

// How to manage the render loop
// 


// scenes should be more complicated
// they should have doors that can open and shut (gates)
// traps that deal damage wouldn't be a bad thing (walls that slam into you)
// the top of the castles should lead to a stairwell and perhaps should be protected from monsters?

// scenes should also specify their exits
// this is turning out to be a complicated class

public class Scene {
  private String name; // yes, I am going to name these goddamn things and start binding them.
  // I am going to start over. (hahahahahaha, no.)
  // There is going to be a master World class
  // it will have the master list of objects and creatures. 
  private String group;
  Vector<Brush> brushList;
  Color foreground;
  Color background;
  boolean darkness;

  Vector<Item> itemList;
  Vector<Monster> monsterList;
  
  int high,wide;
  final int exitListSize=16;
  Scene [] exitList;

  Monster monsterCheck(BoundBox check) {
	for (int i=0; i<monsterList.size(); i++) {
		Monster current=monsterList.get(i);
		if (check.x+check.width<=current.x || current.x+current.width<=check.x) continue;
		if (check.y+check.height<=current.y || current.y+current.height<=check.y) continue;
		return current;
		}
	return null;
	}
  
  boolean hasItem(Item I) {
	return itemList.contains(I);  
	}

  void addItem(Item I) {
	itemList.add(I);
	}

  void removeItem(Item I) {
	itemList.remove(I);
	}

  void addMonster(Monster M) {
	monsterList.add(M);
	}

  void removeMonster(Monster M) {
	monsterList.remove(M);
	}
	
  void setDarkness(boolean noLight) {
	darkness=noLight;
	}
  
  void setExit(int index, Scene destination) {
	exitList[index]=destination;
	}

  void setName(String nomen) {
	name=nomen;
	}

  void setGroup(String nomen) {
	group=nomen;
	}

  String getName() {
	return name;
	}

  String getGroup() {
	return group;
	}

  Brush nameSearch(String brushname) {
	for (int i=0; i<brushList.size(); i++) {
		String fetch=brushList.get(i).name;
		if (fetch!=null && fetch.equalsIgnoreCase(brushname)) return brushList.get(i);
		} 
	return null;
	}
    

  // this has to be redone. I need either a full item/agent object OR a (Scene,x,y,z) vector
  // also, this member is unnecessary to a degree... unless I do something weird at the edges
  Scene boundaryCheck(BoundBox agent) {
    Scene nextScene;
    // the rule for Adventure on the 2600 seems to be that if any part of the sprite is off the screen, 
	// the object is actually in the next screen 
	int arx=0,ary=0,index;
	if (agent.x<0) arx=-1; else if (agent.x+agent.width>=wide) arx=1;
	if (agent.y<0) ary=-1; else if (agent.y+agent.height>=high) ary=1;
	agent.arx=arx; agent.ary=ary;
	if (arx==0 && ary==0) return this;
	index=(ary+1)*3+arx+1;
	nextScene=exitList[index];
	// correcting agent position onto the next scene... or for staying on the current
	if (nextScene==null) {
		if (arx==-1) agent.x=0;
		if (arx==1) agent.x=this.wide-agent.width;
		if (ary==-1) agent.y=0;	
		if (ary==1) agent.y=this.high-agent.height;	
		return this; 
		} else { 
		if (arx==-1) agent.x=nextScene.wide-agent.width;
		if (arx==1) agent.x=0;
		if (ary==-1) agent.y=nextScene.high-agent.height;	
		if (ary==1) agent.y=0;
		return nextScene;
		}
	// the above does not take account of the delta for the coordinates 
	// so I probably need to fix that
	}
  
  // a scene has 4 natural exits (N,S,W,E)
  // walking off the diagonals should be handled, especially for flyers
  // some brushes are exits (pits, porticullis/gates, stairwells)
  // some brushes causes you to exit ON CONTACT
  // some brushes want you to cross a particular boundary to exit
  //
   
  void setBackground(String col) {
    background=AtariColor.translate(col);
    }
 
  public Scene () {
    brushList=new Vector<Brush>(256);
    itemList=new Vector<Item>(256);
	monsterList=new Vector<Monster>(16);
	wide=160;
	high=192;
	exitList=new Scene[exitListSize];
    }

  
	
  // collision should be rewritten: I can be in contact with multiple brushes at one time.
  // sometimes this is acceptable (climbing upwards)
  // sometimes I will need to be further out onto a brush (halfway?) before I count myself as
  //    having climbed or fallen
  // you can nearly always fall off a higher brush
  // again, i'm wondering if I shouldn't just go 3D for this :P
  // how should I let a brush be a stepping stone to a higher z-level?

  // right now i'm setting this so it only checks that the z-level is the same
  Force collision(BoundBox check) { 
   Brush current; 
   Force value=new Force();
   value.inside=false;
   value.scene=null;
   value.dx=0;
   value.dy=0;
   value.brush=null;
   for (int i=0; i<brushList.size(); i++) {
     current=brushList.get(i);
     if (check.z!=current.z) continue;
     if (check.x+check.width<=current.x || current.x+current.width<=check.x) continue;
     if (current.type==Brush.GATE) {if (check.y+check.height<=current.y || current.y+(current.unlocked?current.height/8:current.height)<=check.y) continue;}
		else if (check.y+check.height<=current.y || current.y+current.height<=check.y) continue;     
	 if (!current.passable) {value.inside=true; value.brush=current; }
     value.scene=current.exit(check);
	 if (current.force_type==0) { value.dx+=1; }
	 if (current.force_type==1) { value.dy-=1; }
	 if (current.force_type==2) { value.dx-=1; }
	 if (current.force_type==3) { value.dy+=1; }
     break;
     } 
   return value;  
   } 

Item itemCheck(BoundBox check) {
	Item current;
	for (int i=0; i<itemList.size(); i++) {
		current=itemList.get(i);
		if (check.z!=current.z) continue;
		if (check.x+check.width<=current.x || current.x+current.width<=check.x) continue;
		if (check.y+check.height<=current.y || current.y+current.height<=check.y) continue;
		return current;
	}
	return null;  
  }
   
   
   
  void addBrush(Brush b) {
    brushList.add(b);
    }

  // yes, this is quadratic time :P
  void zsort() {
    Brush select; 
    Brush current; 
    for (int i=0; i<brushList.size(); i++) 
      for (int j=i+1; j<brushList.size(); j++) {
        current=brushList.get(i);
        select=brushList.get(j);
        if (select.z<current.z) {
          brushList.remove(j);
          brushList.add(j,current);
          brushList.remove(i);
          brushList.add(i,select);
          }
        }
    }

  void draw(Graphics2D g, BoundBox player, boolean lamplight) {
   g.setClip(0,0,160,192);
   g.setColor(background);
   g.fillRect(0,0,160,192);
   Brush current; 
   for (int i=0; i<brushList.size(); i++) {
      current=brushList.get(i);
    if (current.drawn) {
		if (current.type==Brush.BLOCK) {
			g.setColor(current.color);
			g.setClip(current.x,current.y,current.width,current.height);
			g.fillRect(current.x,current.y,current.width,current.height);
			if (current.decal!=null) {
				g.setClip(current.x,current.y,current.width,current.height);
				g.drawImage(current.decal,current.x,current.y,null);
				}
		} else if (current.type==Brush.GATE) {
			if (current.unlocked) 
				g.setClip(current.x,current.y,current.width,current.height/8);
				else
				g.setClip(current.x,current.y,current.width,current.height);		
			g.drawImage(current.decal,current.x,current.y,null);
            	  
	  
	  }
        }
    }
    g.setClip(0,0,160,192);
	
	Item currdraw;
	for (int i=0; i<itemList.size(); i++) {
		currdraw=itemList.get(i);
		currdraw.draw(g);
	}
	
/*
 Item curritem;
 for (int i=0; i<itemList.size(); i++) {
	  curritem=itemList.get(i);	 
	  g.setClip(curritem.x,curritem.y,curritem.width,curritem.height);
	  g.drawImage(curritem.sprite, curritem.x, curritem.y, null);
          g.setClip(0,0,160,192);
          // I assume that if this is not drawn as a block, it will use a supplied graphic	  
	} */


  if (darkness) {
		if (lamplight) {
    // draw as black everything except a square around the player
    // we're going to black out most of our work :P
    // circle radius will be 3 players wide :P
    // TOP, BOTTOM, LEFT SLICE, RIGHT SLICE
		g.setColor(AtariColor.translate("DKGY"));
		g.fillRect(0,0,160,player.y-3*8);
		g.fillRect(0,player.y+4*8,160,192-player.y+4*8);
		g.fillRect(0,player.y-3*8,player.x-3*4,7*8);
		g.fillRect(player.x+4*4,player.y-3*8,160-player.x-4*4,7*8);
		int flicker=(int)(40.0*Math.random()); // really needs to be bounded Brownian motion
		g.setColor(new Color(255,140,0,120+flicker));
		g.fillRect(player.x-3*4,player.y-3*8,7*4,7*8);
		// the visible area needs to change size in a flickering fashion
		// shade edges, flicker effects?
		} else {
		g.setClip(0,0,160,192);
		g.setColor(AtariColor.translate("DKGY"));
		g.fillRect(0,0,160,192);
			
		}
    } 
	
	Monster monsterdraw;
	for (int i=0; i<monsterList.size(); i++) {
		monsterdraw=monsterList.get(i);
		monsterdraw.draw(g);
	}
	

  }

 

}
