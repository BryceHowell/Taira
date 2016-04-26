import java.awt.image.BufferedImage;
import java.awt.Graphics2D;


public class Item extends Drawable {
	BufferedImage iconview;
	String name;
	String type;
	public Item() {
		pix=new BufferedImage[16]; frame=0;
	}
	public boolean onButton(int x, int y, Graphics2D G) {
	return false;
	}
	
}

