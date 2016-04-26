// learn to regex in Java

// my cheap ass XML parser should accept a stream as input
// should it buffer????
// it needs to read and recognize tags then act on them
// unmatched and bad tags/data should be recognized and perhaps signaled in STDERR(?)
// good tags and data can either
   // a) be reloaded into a tree data structure to be parsed yet AGAIN
   // b) be loaded directly into world classes (this is not reusable since it becomes a parser only for this proj. )
   // c) be handled via a class instance which provides the function members which handle data appropriately
   // d) not be handled directly in this class (function members are do-nothings) but allow things to be done in a subclass
   
// remember, minimize the amount of work. you might actually want a real XML parser for this
// however it might be nice to have a pseudo-XML parser that handles badly formed XML since I doubt 
// good parsers are set up to half-accept badly formed XML tagged data without bailing out on the parse
// and i'm sure many vendors still fuck up their data

// fuck this. I say, build an event-driven XML parser.
// events:
// text string, with current tag stack included. should I handle entity codes as an additional step?
// (optional) entity code handler or autoconvert table?
// open tag event: adds to stack, allows handling by client. key value hash of tag values avail
// node tag event: stack available, not modified. k-v hash of tag values avail
// close tag event: closed tag, plus stack. obvious pop of tag from stack

import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.*;
import java.io.*;

		


class ItemParser {

	// TagParser should receive a stream
	// from there it begins parse, accepting text until a tag occurs... 
	// once tag occurs, crap out the text, then handle the tag event. first handler parses the tag,
	// then kicks it to one of three routines according to type.
	// it might handle broken tag conditions, not sure. I don't need this functionality right now, but perhaps
	// in the future (unmatched tag, broken tag syntax)
	// 
	
	static void fileRead(String filename, SpriteLibrary SL, Vector<Scene> roomList, Vector<Monster> ML, Vector<Location> LL) {   // image library should be passed in here
		try
			{
            FileInputStream fstream = new FileInputStream(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
			StringBuffer current=new StringBuffer(8192);
			Pattern tagPattern=Pattern.compile("<\\s*\\/?\\s*\\w+(\\s+\\w+=\"[^\"]*\")*\\s*\\/?\\s*>");
			
			Scene currentroom=new Scene();
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
					if (t.name().equalsIgnoreCase("item") && t.start() && t.end()) { // modify to handle the case of an exit that targets a brush
						//System.out.println("GOT ITEM");
						Item I=new Item();
						if (t.hasAttribute("name")) {
							I.name=t.getValue("name");
							} 
						if (t.hasAttribute("type")) {
							I.type=t.getValue("type");
							} 
						if (t.hasAttribute("x")) {
							I.x=Integer.valueOf(t.getValue("x"));
							}
						if (t.hasAttribute("y")) {
							I.y=Integer.valueOf(t.getValue("y"));
							}
						if (t.hasAttribute("z")) {
							I.z=Integer.valueOf(t.getValue("z"));
							}
							
							
						if (t.hasAttribute("sprite")) {
							
							Sprite temp=SL.nameSearch(t.getValue("sprite"));
							//if (temp==null) System.out.println("null"); else System.out.println("good");
							I.pix[0]=temp.pix[0];
							I.iconview=temp.pix[1];
							I.width=temp.width;
							I.height=temp.height;
							}
							
						if (t.hasAttribute("room")) {
							String place=t.getValue("room");
							for (int i=0; i<roomList.size(); i++ ) {
								if (roomList.get(i).getName().equals(place)) {
									Scene locus=roomList.get(i);
									locus.addItem(I);
									}
								}
							
							} 
						}
						
					if (t.name().equalsIgnoreCase("monster") && t.start() && t.end()) { 
						//System.out.println("GOT ITEM");
						Monster M=new Monster();
						if (t.hasAttribute("name")) {
							M.name=t.getValue("name");
							} 
						if (t.hasAttribute("type")) {
							M.type=t.getValue("type");
							} 
						if (t.hasAttribute("x")) {
							M.x=Integer.valueOf(t.getValue("x"));
							}
						if (t.hasAttribute("y")) {
							M.y=Integer.valueOf(t.getValue("y"));
							}
						if (t.hasAttribute("z")) {
							M.z=Integer.valueOf(t.getValue("z"));
							}
							
							
						if (t.hasAttribute("sprite")) {
							
							Sprite temp=SL.nameSearch(t.getValue("sprite"));
							//if (temp==null) System.out.println("null"); else System.out.println("good");
							M.pix=temp.pix;
							M.width=temp.width;
							M.height=temp.height;
							}
							
						if (t.hasAttribute("behavior")) {
							String activity=t.getValue("behavior");
							if (activity.equalsIgnoreCase("HOVER")) M.behavior=Monster.M_HOVER;
							if (activity.equalsIgnoreCase("GUARD")) M.behavior=Monster.M_GUARD;
							if (activity.equalsIgnoreCase("CHASE")) M.behavior=Monster.M_CHASE;
							if (activity.equalsIgnoreCase("AVOID")) M.behavior=Monster.M_AVOID;
							if (activity.equalsIgnoreCase("WANDER")) M.behavior=Monster.M_WANDER;
							}
						if (t.hasAttribute("speed")) {
							String activity=t.getValue("speed");
							M.speed=Integer.valueOf(activity);
							}
							
						
							
						if (t.hasAttribute("room")) {
							String place=t.getValue("room");
							for (int i=0; i<roomList.size(); i++ ) {
								if (roomList.get(i).getName().equals(place)) {
									Scene locus=roomList.get(i);
									locus.addMonster(M);
									M.where=locus;
									ML.add(M);
									//System.out.println(ML.size());
									}
								}
							
							} 
						}
						
					if (t.name().equalsIgnoreCase("location") && t.start() && t.end()) { 
						//System.out.println("GOT LOCATION");
						Location L=new Location();
						if (t.hasAttribute("type")) {
							L.type=t.getValue("type");
							} 
						if (t.hasAttribute("x")) {
							L.x=Integer.valueOf(t.getValue("x"));
							}
						if (t.hasAttribute("y")) {
							L.y=Integer.valueOf(t.getValue("y"));
							}
						if (t.hasAttribute("z")) {
							L.z=Integer.valueOf(t.getValue("z"));
							}
						if (t.hasAttribute("monster")) {
							L.monster=Boolean.valueOf(t.getValue("monster"));
							}	
														
						if (t.hasAttribute("room")) {
							L.roomname=t.getValue("room");
							for (int i=0; i<roomList.size(); i++ ) {
								if (roomList.get(i).getName().equals(L.roomname)) {
									L.room=roomList.get(i); break;
									}
								}							
							}
							
						LL.add(L);
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
	
	
	public static void main(String [] args) {
		Vector<Scene> rooms=null;
		if (args.length>=1) fileRead(args[0],null,null,null,null); else
			System.err.println("Invalid parameters");
		System.out.println("NUMBER OF ITEMS: "+rooms.size());
		
	}

	

}
