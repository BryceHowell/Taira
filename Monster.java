import java.awt.image.BufferedImage;
import java.awt.Graphics2D;


public class Monster extends Drawable {
	final static int M_WANDER=0;
	final static int M_GUARD=1;
	final static int M_CHASE=2;
	final static int M_HOVER=3;
	final static int M_AVOID=4;
	
	int speed; // 0 to 255 (over 256) - fraction of maximum speed
	int behavior;
	int destx,desty;
	String name;
	String type;
	Item guard;
	boolean seenplayer;
	boolean dead;
	int dx,dy; // direction
	Scene where; // maintain current room for movement routines
	public Monster() {
		behavior=M_HOVER;
		pix=new BufferedImage[16]; frame=0;
		dx=dy=0;
		seenplayer=false;
		dead=false;
	}
	
	

	
// needs a method for movement offscreen and onscreen.... nvm
// 
	public void move(int px,int py, Scene whereplayer) {
		if (dead) { frame=2; return; }
		// behaviors-
		//  wandering, seeking player, guarding item (chases player away?), hovering, avoiding player
		
		// different monsters switch behaviors under different conditions
		// different dragons guard different things like in Adventure?
		// on the same screen 
		frame++; frame%=2;
		// different idea: monsters need to have a "seen player" state
		// certain monsters will give chase up to a screen away
		// some will chase on the same screen as a "treasure" only
		// others will ignore the player at that time
		// aggressive and follows/aggressive and chases away/wandering/hovering
		
 		if (whereplayer==where) {
			seenplayer=true;
			if (behavior==M_CHASE || behavior==M_GUARD) {
				if ((int)Math.floor(256*Math.random()) <= speed) {
					if (px<x) x--; else if (px>x) x++;
					if (py<y) y--; else if (py>y) y++; }
				}
			if (behavior==M_AVOID) {
				if ((int)Math.floor(256*Math.random()) <= speed) {
					if (px<x) x++; else if (px>x) x--;
					if (py<y) y++; else if (py>y) y--; }
				}	
			}
		if (behavior==M_WANDER) {
			if (dx==0 && dy==0) {
				//dx=(int)Math.floor(3*Math.random())-1;
				//dy=(int)Math.floor(3*Math.random())-1;
				dy=-1;
			} else {
			if (Math.random()<0.005) {
				dx=(int)Math.floor(3*Math.random())-1;
				dy=(int)Math.floor(3*Math.random())-1;				
				}	
			}
			x+=dx; y+=dy;
			}
		// detect player nearby
		if (behavior==M_CHASE && seenplayer) {
			if (whereplayer!=where) {
				boolean continuechase=false;
				if (where.exitList[1]!=null && where.exitList[1]==whereplayer) { y--; continuechase=true;}
				if (where.exitList[7]!=null && where.exitList[7]==whereplayer) { y++; continuechase=true;}
				if (where.exitList[5]!=null && where.exitList[5]==whereplayer) { x++; continuechase=true;}
				if (where.exitList[3]!=null && where.exitList[3]==whereplayer) { x--; continuechase=true;}
				if (!continuechase) seenplayer=false;
				} 
			}
		if (behavior==M_HOVER) {
			if ((int)Math.floor(256*Math.random()) <= speed) {
				if (frame==0) y-=2;
				if (frame==1) y+=2;
				}
			}	
		BoundBox b=new BoundBox();
		b.x=x; b.y=y; b.z=z; b.width=width; b.height=height;
		Scene nextwhere=where.boundaryCheck(b); // reflect velocity over corrected coords
		if (nextwhere==where) {
			if (b.arx!=0) dx=-dx;
			if (b.ary!=0) dy=-dy; }
		x=b.x; y=b.y; z=b.z;
		if (nextwhere!=where) {
			where.removeMonster(this);
			nextwhere.addMonster(this);
			}
		where=nextwhere;
			
		} // end move
	
}
