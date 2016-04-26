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

import java.util.Stack;
import java.util.Vector;
import java.util.regex.*;
import java.io.*;

class Tag {
	String tagName;
	Vector attribute;
	Vector value;
	int numberPairs;
	private boolean opening;
	private boolean closing;
	
	Vector subTree;
	Vector<Boolean> subType;
	// subTree is going to contain Strings and tags alternatingly
	// whitespace-only strings will be dumped
	
	Tag() { // the empty tag for the document root
		attribute=new Vector(16,16);
		value=new Vector(16,16);
		numberPairs=0;
		tagName=new String("");
		subTree=new Vector(16,16);
		subType=new Vector<Boolean>(16,16);
	}
	
	Tag(String tagText) {
		// just regex it here
		// match the name
		// then match the individual pairs in order
		// make sure to recognize the type
		subTree=new Vector(16,16);
		subType=new Vector<Boolean>(16,16);
		attribute=new Vector(16,16);
		value=new Vector(16,16);
		numberPairs=0;
		boolean front;
		boolean rear;
		Pattern namePattern=Pattern.compile("<\\s*\\/?\\s*(\\w+)\\s*");
		Pattern frontslashPattern=Pattern.compile("<\\s*\\/\\s*\\w");
		Pattern rearslashPattern=Pattern.compile("\\/\\s*>");
		Pattern attributePattern=Pattern.compile("\\s(\\w+)=\"([^\"]*)\"");
		Matcher nomen = namePattern.matcher(tagText);
		if (nomen.find()) tagName=nomen.group(1);
		Matcher ante = frontslashPattern.matcher(tagText);
		if (ante.find()) front=true; else front=false;
		Matcher post = rearslashPattern.matcher(tagText);
		if (post.find()) rear=true; else rear=false;
		opening=!front;
		closing=front || rear;
		if (!closing) subTree=new Vector(16,16);
		Matcher par = attributePattern.matcher(tagText);
		while (par.find()) {
			attribute.add(par.group(1));
			value.add(par.group(2));
			numberPairs++;
			}
		}
		
	boolean start() { return opening; }
	boolean end() { return closing; }
	String name() { return tagName; }
	
	boolean hasAttribute(String key) {
		for (int i=0; i<numberPairs; i++) {
			String checkKey = (String)attribute.get(i);
			if (checkKey.equalsIgnoreCase(key)) return true;
			}
		return false;
		}
		
	String getValue(String key) {
		for (int i=0; i<numberPairs; i++) {
			String checkKey = (String)attribute.get(i);
			if (checkKey.equalsIgnoreCase(key)) return (String)value.get(i);
			}
		return new String("");
		}
		
	int numberOfPairs() {
		return numberPairs;
		}
		
	String getAttribute(int i) {
		if (0<=i && i<numberPairs) return (String)attribute.get(i); else return new String("");
		}
		
	String getValue(int i) {
		if (0<=i && i<numberPairs) return (String)value.get(i); else return new String("");		
		}
	
	int numberOfElements() {
		return subTree.size();
		}
	
	Object getElement(int i) {
		if (subTree!=null && 0<=i && i<numberOfElements()) // or I could just let it throw an exception...
			return subTree.get(i);
			else return null;
		}
		
	void addElement(Tag t) {
		if (subTree!=null) subTree.add(t);
		if (subType!=null) subType.add(new Boolean(true));
		}

	void addElement(String s) {
		if (subTree!=null) subTree.add(s);
		if (subType!=null) subType.add(new Boolean(false));
		}
		
	void tagPrint() {
		if (!tagName.equalsIgnoreCase("")) {
			System.out.print("<"+tagName+" ");
			for (int i=0; i<attribute.size(); i++) {
				System.out.print((String)attribute.get(i)+"=\""+(String)value.get(i)+"\" ");
				}
			if (end()) System.out.println(" />"); else System.out.println(" >");
				
			}
		for (int i=0; i<subTree.size(); i++) {
			System.out.println("++++ "+subTree.size()+"|"+subType.size()+" ++++");
			Boolean w=subType.get(i);
			boolean which=w.booleanValue();
			if (which) {
				Tag item=(Tag)subTree.get(i);
				item.tagPrint();
				} else {
				System.out.print((String)subTree.get(i));
				}
			}
		}
		
	}
	
	
	


class TagParser {
	
	// TagParser should receive a stream
	// from there it begins parse, accepting text until a tag occurs... 
	// once tag occurs, crap out the text, then handle the tag event. first handler parses the tag,
	// then kicks it to one of three routines according to type.
	// it might handle broken tag conditions, not sure. I don't need this functionality right now, but perhaps
	// in the future (unmatched tag, broken tag syntax)
	// 
	
	static Tag fileRead(String filename) {
	Tag rootTag=new Tag();
		try
			{
            FileInputStream fstream = new FileInputStream(filename);
			BufferedReader in = new BufferedReader(new InputStreamReader(fstream));
			StringBuffer current=new StringBuffer(8192);
// < {whitespace block}?{tagname} {{whitespace block}{identifier}\=\"{string value}\"}* {whitespace block}> 
			Pattern tagPattern=Pattern.compile("<\\s*\\/?\\s*\\w+(\\s+\\w+=\"[^\"]*\")*\\s*\\/?\\s*>");
			
			// SOME SORT OF STACK STRUCTURE FOR TAGS SHOULD BE CREATED HERE. PROBABLY A VECTOR
			Stack<Tag> tagStack=new Stack<Tag>();
			// should I track the location of a tag in its parent tag?
			Tag topTag=rootTag;
			tagStack.push(topTag);
			
			// this project doesn't need entity translation, but I probably ought to make it available
            while (in.ready()) {
				String fetch=in.readLine();
				current.append(fetch);
				System.out.println(fetch);
				String target=current.toString();
				Matcher m = tagPattern.matcher(target);
				int lastend=0;
				boolean tagFound=false;
				while (m.find()) {
					System.out.println("start" + m.start() + " " +m.end() );
					// I should catch and emit pre-tag text here
					// emit lastend to m.start()-1 for text
					
					System.out.println("\""+target.substring(lastend,m.start())+"\"");
					String caughtTag=target.substring(m.start(),m.end());
					System.out.println("|"+caughtTag);
					lastend=m.end();
					
					// should emit/parse tag here
					Tag t=new Tag(caughtTag);
					int pairs=t.numberOfPairs();
					for (int i=0; i<pairs; i++) {
						System.out.print(t.getAttribute(i)+"="+t.getValue(i)+"|");
						}
					if (t.start() && !t.end()) {
						// PLACE T INTO TOPTAG'S SUBTREE
						topTag.addElement(t);
						topTag=t;
						tagStack.push(t);
						} else if (t.end()) {
						// I have no idea what the best strategy for a mismatch is
						if (topTag.name().equalsIgnoreCase(t.name())) {
							tagStack.pop();
							topTag=tagStack.peek();
							}
						} else {
						// PlACE ThE SINglETON TAG INTO SUBTREE
						topTag.addElement(t);
						}
					System.out.print("\n");
					tagFound=true;
					}
				if (tagFound) current.delete(0,lastend);  
				}
				// emit remaining text before closing file
				in.close();
			} catch (Exception e) {
			System.err.println(e.toString());
			}
	return rootTag;
	}
	
	
	public static void main(String [] args) {
		Tag root=new Tag();
		if (args.length>=1) root=fileRead(args[0]); else
			System.err.println("Invalid parameters");
		root.tagPrint();
	}

	

}