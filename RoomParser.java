
import java.awt.image.BufferedImage;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.*;
import java.io.*;

		
class ExitTuple {
	boolean brushexit;
	Brush brush;
	String brushname;
	String direction;
	String from;
	Scene fromroom;
	String to;
	int x,y;
	int brushType;
	boolean unlocked;
	}	


class RoomParser {

	// TagParser should receive a stream
	// from there it begins parse, accepting text until a tag occurs... 
	// once tag occurs, crap out the text, then handle the tag event. first handler parses the tag,
	// then kicks it to one of three routines according to type.
	// it might handle broken tag conditions, not sure. I don't need this functionality right now, but perhaps
	// in the future (unmatched tag, broken tag syntax)
	// 
	
	static Vector<Scene> fileRead(String filename, SpriteLibrary SL) {   // image library should be passed in here
		Vector<Scene> data=new Vector<Scene>(16,16);
		Vector<ExitTuple> exitdata=new Vector<ExitTuple>(256,16);
		Vector<Brush> triggerdata=new Vector<Brush>(64,16);
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
					if (t.name().equalsIgnoreCase("room") && t.start()) {
						//System.out.println("START ROOM");
						currentroom=new Scene();
						currentroom.setName(t.getValue("name"));
						currentroom.setGroup(t.getValue("group"));
						currentroom.setBackground(t.getValue("bgcolor"));
						if (t.hasAttribute("darkness")) currentroom.darkness=Boolean.valueOf(t.getValue("darkness"));
						}

					if (t.name().equalsIgnoreCase("room") && t.end()) {
						//System.out.println("END ROOM");
						data.add(currentroom);
						currentroom=null;
						}
					if (t.name().equalsIgnoreCase("exit") && t.start()) { // modify to handle the case of an exit that targets a brush
						//System.out.println("GOT EXIT");
						ExitTuple e=new ExitTuple();
						if (t.hasAttribute("direction")) {
							e.brushexit=false;
							e.direction=t.getValue("direction");
							e.from=currentroom.getName();
							e.fromroom=currentroom;
							e.to=t.getValue("room");
							} 
						if (t.hasAttribute("brush")) {
							e.brushexit=true;
							e.brushname=t.getValue("brush");
							e.from=currentroom.getName();
							e.fromroom=currentroom;
							e.to=t.getValue("room");
							e.x=Integer.valueOf(t.getValue("x"));
							e.y=Integer.valueOf(t.getValue("y"));
							//e.unlocked=Boolean.valueOf(t.getValue("unlocked"));
							//String btype=t.getValue("type");
							//if (btype.equalsIgnoreCase("BLOCK")) e.brushType=Brush.BLOCK;
							//if (btype.equalsIgnoreCase("GATE")) e.brushType=Brush.GATE;
							} 
							
						exitdata.add(e);
						}
					if (t.name().equalsIgnoreCase("brush") && t.start()) { // needs to check for named brushes. will need a register for that 
						//System.out.println("GOT BRUSH");
						String bcol=t.getValue("color");
						int x=Integer.valueOf(t.getValue("x"));
						int y=Integer.valueOf(t.getValue("y"));
						int z=Integer.valueOf(t.getValue("z"));
						int width=Integer.valueOf(t.getValue("width"));
						int height=Integer.valueOf(t.getValue("height"));
						Brush b=new Brush(bcol,x,y,z,width,height);
						if (t.hasAttribute("name")) b.name=t.getValue("name");
						if (t.hasAttribute("keyname")) b.keyname=t.getValue("keyname");
						if (t.hasAttribute("type")) {
							String brushtype=t.getValue("type");
							if (brushtype.equalsIgnoreCase("GATE")) {
								b.type=Brush.GATE;
								b.decal=SL.nameSearch("porticullis").pix[0];
								//System.out.println("GATE LOADED");
								// set decal to porticullis; nothing else needs done, I think
								}
							if (brushtype.equalsIgnoreCase("BLOCK")) b.type=Brush.BLOCK;
							}							
						if (t.hasAttribute("image")) { b.imagename=t.getValue("image"); b.decal=SL.nameSearch(b.imagename).pix[0];}
						if (t.hasAttribute("drawn")) { b.drawn=Boolean.valueOf(t.getValue("drawn")); }
						if (t.hasAttribute("passable")) { b.passable=Boolean.valueOf(t.getValue("passable")); }
						if (t.hasAttribute("unlocked")) { b.unlocked=Boolean.valueOf(t.getValue("unlocked")); }
						if (t.hasAttribute("force")) { 
							String forcevalue=t.getValue("force");
							if (forcevalue.equalsIgnoreCase("NONE")) b.force_type=-1;
							if (forcevalue.equalsIgnoreCase("EAST")) b.force_type=0;
							if (forcevalue.equalsIgnoreCase("NORTH")) b.force_type=1;
							if (forcevalue.equalsIgnoreCase("WEST")) b.force_type=2;
							if (forcevalue.equalsIgnoreCase("SOUTH")) b.force_type=3;
							}
						if (t.hasAttribute("trigger")) {
							String triggervalue=t.getValue("trigger");
							if (t.hasAttribute("target")) {
								b.targetstring=t.getValue("target");
								if (triggervalue.equalsIgnoreCase("NONE")) b.trigger=Brush.TRIGGER_NONE;
								if (triggervalue.equalsIgnoreCase("ON")) b.trigger=Brush.TRIGGER_ON;
								if (triggervalue.equalsIgnoreCase("OFF")) b.trigger=Brush.TRIGGER_OFF;
								if (triggervalue.equalsIgnoreCase("TOGGLE")) b.trigger=Brush.TRIGGER_TOGGLE;
								triggerdata.add(b);
							    }
							}
						currentroom.addBrush(b);
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
	
	
	try {
	// NEXT STEP: BIND CARDINAL EXITS
	//System.out.println("TOTAL ROOMS:"+exitdata.size());
	for (int i=0; i<exitdata.size(); i++) {
		ExitTuple e=exitdata.get(i);
		// for each exit, search for target room
		String targetname=e.to;
		Scene targetroom=null;
		//System.out.println(" ||| "+targetname);
		for (int j=0; j<data.size(); j++) {
			if (targetname.equalsIgnoreCase(data.get(j).getName())) { targetroom=data.get(j); break; }
			}
		//if (targetroom==null) System.out.println("FAILED TO RESOLVE TARGETROOM"); else System.out.println(targetname+" is the room");
		if (!e.brushexit) {
			int index=4;
			if (e.direction.equalsIgnoreCase("north")) index=1;
			if (e.direction.equalsIgnoreCase("south")) index=7;
			if (e.direction.equalsIgnoreCase("east")) index=5;
			if (e.direction.equalsIgnoreCase("west")) index=3;
			e.fromroom.exitList[index]=targetroom;
			} else {
			// search fromroom for the brush
			//System.out.println(e.fromroom.getName()+" to "+targetroom.getName());
			//System.out.println(e.brushname);
			if (e.fromroom.brushList==null) System.out.println("NULL FROMROOM brushlist"); 
			for (int j=0; j<e.fromroom.brushList.size(); j++) {
				//System.out.println("j is "+j+"  "+e.fromroom.brushList.get(j).name);
				if (e.fromroom.brushList.get(j).name.equalsIgnoreCase(e.brushname)) { e.brush=e.fromroom.brushList.get(j); e.brush.teleport=true; break; }
				}  // DOES NOT SIGNAL FAILURE TO FIND.. will give out of bounds index exception
			
			// set exitScene, exitX, exitY
			e.brush.exitScene=targetroom;
			e.brush.exitX=e.x;
			e.brush.exitY=e.y;
			//e.brush.unlocked=e.unlocked; e.brush.type=e.brushType;
			// set type
			
			}
		}

	// bind trigger brushes to their targets
	for (int i=0; i<triggerdata.size(); i++) {
		Brush source=triggerdata.get(i);
		//split string
		int slash=source.targetstring.lastIndexOf('/');
		String targetroom=source.targetstring.substring(0,slash);
		String targetbrush=source.targetstring.substring(slash+1,source.targetstring.length());
		//	System.out.println("room/brush="+targetroom+"/"+targetbrush);
		// search for room then brush
		for (int j=0; j<data.size(); j++) {
			if (data.get(j).getName().equalsIgnoreCase(targetroom)) {
			//System.out.println(data.get(j).getName());
			source.target=data.get(j).nameSearch(targetbrush);
			if (source.target==null) System.out.println("source.target is null");
			}
			//System.out.print(" "+j);
		  }
		}
		
	} catch (Exception e) {
	System.err.println(e.toString());
	}	


	return data;
	}
	
	
	public static void main(String [] args) {
		Vector<Scene> rooms=null;
		if (args.length>=1) rooms=fileRead(args[0],null); else
			System.err.println("Invalid parameters");
		System.out.println("NUMBER OF ROOMS: "+rooms.size());
		
	}

	

}
