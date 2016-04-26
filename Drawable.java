import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Drawable { 
	int x,y,z;
	BufferedImage [] pix;
	int frame;
	int width,height;
	public Drawable () {
		pix=new BufferedImage[16]; frame=0;
	}
	// draw method to be passed a graphic2D
	void draw(Graphics2D g) {
		g.setClip(x,y,width,height);
		g.drawImage(pix[frame],x,y,null);
	}
	void setFrame(int f) { frame=f;}
	// needs to be included in collision detection
	// collision method to be overriden by subclasses
	
}

