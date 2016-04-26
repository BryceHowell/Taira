import java.awt.Color;


class AtariColor {
 
    private Color [] colorCycle;
    private int cycleLength;

    int cycleType;
    
    static int BROWN = 0x69690F;           // a
    static int DARK_BROWN = 0x484800;      // b
    static int YELLOW = 0xFCFC54;          // c
    static int LIGHT_ORANGE = 0xECC860;    // d
    static int ORANGE = 0xFCBC74;          // e
    static int RED = 0xA71A1A;             // f
    static int YELLOW_GREEN = 0x86861D;    // g
    static int PINK = 0xE46F6F;            // h
    static int GREEN = 0x6E9C42;           // i
    static int BLUE = 0x2D6D98;            // j
    static int BLACK = 0x000000;           // k
    static int DARK_GRAY = 0x8E8E8E;       // l
    static int WHITE = 0xECECEC;           // m
    static int GRAY = 0xD6D6D6;            // n
    static int DARK_GREEN = 0x355F18;      // o
    static int DARK_YELLOW = 0xBBBB35;     // p
    static int DARKEST_GRAY = 0x6F6F6F;    // q
    static int DARKEST_GREEN = 0x143C00;   // r
    static int LIGHT_GREEN = 0x5CBA5C;     // s
    static int PURPLE = 0x480078;          // t
    static int DARK_ORANGE = 0xAC5030;     // u
    static int LIGHT_GRAY = 0xD3D3D3;      // L
	static int CYAN = 0x008080;            // C


    static int[] COLORS = {
       BROWN,                // 0
       DARK_BROWN,           // 1
       YELLOW,               // 2
       LIGHT_ORANGE,         // 3
       ORANGE,               // 4
       RED,                  // 5
       YELLOW_GREEN,         // 6
       PINK,                 // 7
       GREEN,                // 8
       BLUE,                 // 9
       BLACK,                // 10
       DARK_GRAY,            // 11
       WHITE,                // 12
       GRAY,                 // 13
       DARK_GREEN,           // 14
       DARK_YELLOW,          // 15
       DARKEST_GRAY,         // 16
       DARKEST_GREEN,        // 17
       LIGHT_GREEN,          // 18
       PURPLE,               // 19
       DARK_ORANGE,          // 20
	   CYAN                  // 21
    };
    
  public static Color translate(String code) {
    if (code.equalsIgnoreCase("BRN")) { return new Color(BROWN);}
    if (code.equalsIgnoreCase("DBR")) { return new Color(DARK_BROWN);}
    if (code.equalsIgnoreCase("YEL")) { return new Color(YELLOW);}
    if (code.equalsIgnoreCase("LOR")) { return new Color(LIGHT_ORANGE);}
    if (code.equalsIgnoreCase("ORN")) { return new Color(ORANGE);}
    if (code.equalsIgnoreCase("RED")) { return new Color(RED);}
    if (code.equalsIgnoreCase("YGR")) { return new Color(YELLOW_GREEN);}
    if (code.equalsIgnoreCase("PNK")) { return new Color(PINK);}
    if (code.equalsIgnoreCase("GRN")) { return new Color(GREEN);}
    if (code.equalsIgnoreCase("BLU")) { return new Color(BLUE);}
    if (code.equalsIgnoreCase("BLK")) { return new Color(BLACK);}
    if (code.equalsIgnoreCase("DGY")) { return new Color(DARK_GRAY);}
    if (code.equalsIgnoreCase("WHI")) { return new Color(WHITE);}
    if (code.equalsIgnoreCase("GRY")) { return new Color(GRAY);}
    if (code.equalsIgnoreCase("DGR")) { return new Color(DARK_GREEN);}
    if (code.equalsIgnoreCase("DYL")) { return new Color(DARK_YELLOW);}
    if (code.equalsIgnoreCase("DKGY")) { return new Color(DARKEST_GRAY);}
    if (code.equalsIgnoreCase("DKGR")) { return new Color(DARKEST_GREEN);}
    if (code.equalsIgnoreCase("LGR")) { return new Color(LIGHT_GREEN);}
    if (code.equalsIgnoreCase("PUR")) { return new Color(PURPLE);}   
    if (code.equalsIgnoreCase("DOR")) { return new Color(DARK_ORANGE);}   
    if (code.equalsIgnoreCase("LGY")) { return new Color(LIGHT_GRAY);}   
    if (code.equalsIgnoreCase("CYN")) { return new Color(CYAN);}   
    return new Color(BLACK);
    }
  public static int hexstring(String code) {
		int len=code.length();
		int i=0;
		int value=0;
		do {
			value<<=4;
			char c=code.charAt(i);
			int cval;
			switch (c) {
				case '0': cval=0; break;
				case '1': cval=1; break;
				case '2': cval=2; break;
				case '3': cval=3; break;
				case '4': cval=4; break;
				case '5': cval=5; break;
				case '6': cval=6; break;
				case '7': cval=7; break;
				case '8': cval=8; break;
				case '9': cval=9; break;
				case 'A': cval=10; break;
				case 'B': cval=11; break;
				case 'C': cval=12; break;
				case 'D': cval=13; break;
				case 'E': cval=14; break;
				case 'F': cval=15; break;
				case 'a': cval=10; break;
				case 'b': cval=11; break;
				case 'c': cval=12; break;
				case 'd': cval=13; break;
				case 'e': cval=14; break;
				case 'f': cval=15; break;
				default: cval=-1; break;
			}
			if (cval!=-1) { value+=cval;}
			i++;
		} while (i<len);
		return value;
  }
}
