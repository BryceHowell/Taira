import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.io.*;
import java.util.regex.*;

class Sprite {
        String name;
        
	// Sprite should be an array of images 
	// the image array should be a cartesian product of
        BufferedImage [] pix;
        int numpix;
        int width,height;
public  Sprite() {
	pix=new BufferedImage[16];
	numpix=0;
        }
public  Sprite(String nm,int w,int h, BufferedImage [] initpix, int number) {
        name=nm; width=w; height=h; pix=initpix; numpix=number;
        
        }

}

class SpriteLibrary {
        Vector<Sprite> library;
public SpriteLibrary (){ library=new Vector<Sprite>(64,16);}
public Sprite nameSearch(String nomen) {
        int i;
        for (i=0; i<library.size(); i++) {
                if (nomen.equals(library.get(i).name)) { return library.get(i); }
                }
        return null;
        }
public void add(Sprite spx) {
        library.add(spx);
        }
public void readFile(String filename) {
	int [] colormap=new int[256];
	try {
        	FileInputStream fstream = new FileInputStream(filename);
		BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
		StringBuffer current=new StringBuffer(8192);
		Pattern tagPattern=Pattern.compile("<\\s*\\/?\\s*\\w+(\\s+\\w+=\"[^\"]*\")*\\s*\\/?\\s*>");
		Sprite spx=null; int picno=-1;
		//System.out.println("opened Files");
			boolean doublescan=false;
			while (in.ready()) {
				String fetch=in.readLine();
				current.append(fetch);
				//System.out.println(fetch);
				String target=current.toString();
				Matcher m = tagPattern.matcher(target);
				int lastend=0;
				boolean tagFound=false;
				while (m.find()) {
					//System.out.println("start" + m.start() + " " +m.end() );
					// I should catch and emit pre-tag text here
					// emit lastend to m.start()-1 for text
					
					//System.out.println("\""+target.substring(lastend,m.start())+"\"");
					String caughtTag=target.substring(m.start(),m.end());
					//System.out.println("|"+caughtTag);
					lastend=m.end();
					
					// should emit/parse tag here
					Tag t=new Tag(caughtTag);
					int pairs=t.numberOfPairs();
					//for (int i=0; i<pairs; i++) {
					//	System.out.print(t.getAttribute(i)+"="+t.getValue(i)+"|");
					//	}
					//System.out.println("TAGNAME ="+t.name());
					//if (t==null) System.out.println("no tag");
					if (t.name().equalsIgnoreCase("sprite") && t.start()) {
						//System.out.println("START sprite");
						spx=new Sprite();
						picno=0;
						spx.name=t.getValue("name");
						spx.width=Integer.valueOf(t.getValue("width"));
						spx.height=Integer.valueOf(t.getValue("height"));
						if (t.hasAttribute("doublescan")) doublescan=Boolean.valueOf(t.getValue("doublescan")); else doublescan=false;
						if (doublescan) spx.height*=2;
						}

					if (t.name().equalsIgnoreCase("sprite") && t.end()) {
						add(spx);
						// place SPrite in library
						//System.out.println("END ROOM");
						doublescan=false;
						spx=null;
						}
					if (t.name().equalsIgnoreCase("colormap") && t.start() && t.end() ) { // modify to handle the case of an exit that targets a brush
						//System.out.println("GOT Colormap");
						
						if (t.hasAttribute("char") && t.hasAttribute("color")) {	
							colormap[t.getValue("char").charAt(0)]=
 								AtariColor.hexstring(t.getValue("color"));
							} 
							
						}
				

					if (t.name().equalsIgnoreCase("image") && t.start() && t.end()) { // needs to check for named brushes. will need a register for that 
						//System.out.println("GOT IMAGE");
						BufferedImage drawing=new BufferedImage(spx.width,spx.height, BufferedImage.TYPE_INT_ARGB_PRE);
						//System.out.println("("+spx.width+","+spx.height+")");
						int yinc=1;
						if (doublescan) yinc=2; 
						for(int y=0; y<spx.height; y+=yinc) {
							String data=in.readLine();
							//System.out.println(data+"a"+y);
							for (int x=0; x<spx.width; x++) {
								char c=data.charAt(x);
								drawing.setRGB(x,y,colormap[c]);
								if (doublescan) drawing.setRGB(x,y+1,colormap[c]);
								}
							}
						spx.pix[spx.numpix]=drawing;
						spx.numpix++;
						}

					// Just convert tags to data here

					//System.out.print("\n");
					tagFound=true;
					}
				if (tagFound) current.delete(0,lastend);  
				}
			
				// emit remaining text before closing file
				in.close();

		
	} catch (Exception e) {
		System.err.println(e.toString());
		}

	}
	
	public static void main(String args[]) {
		SpriteLibrary SL=new SpriteLibrary();
		SL.readFile("sprites.txt");
		Sprite t=SL.nameSearch("porticullis");
		for (int y=0; y<t.height; y++) {
			for (int x=0; x<t.width; x++) if (t.pix[0].getRGB(x,y)!=0) System.out.print("x"); else System.out.print(" ");
			System.out.print("\n"); }
	}		
}
