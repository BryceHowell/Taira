/*
 * Taira
 * Copyright (C) 2012 meatfighter.com
 *
 * This file is part of Pitfall 4K.
 *
 * Pitfall 4K is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pitfall 4K is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

import java.applet.Applet;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;

public class strip extends Applet implements Runnable {

  // keys
  private boolean[] a = new boolean[32768];

  @Override
  public void start() {
    enableEvents(8);
    new Thread(this).start();
  }

  public void run() {

    final float GRAVITY = 0.05f;
    final int JUMP_SPEED = -1;
    final int UPPER_FLOOR_Y = 97;
    final int LOWER_FLOOR_Y = 152;
    final int UPPER_FLOOR_LOWER_Y = 142;
    final int LADDER_TOP_Y = 112;
    final int LADDER_BOTTOM_Y = 152;

    final int RIGHT = 0;
    final int LEFT = 1;

    final int VK_LEFT = 0x25;
    final int VK_RIGHT = 0x27;
    final int VK_UP = 0x26;
    final int VK_DOWN = 0x28;
    final int VK_JUMP = 0x42;
    final int VK_PAUSE = 0x50;
    final int VK_HINTS = 0x38;



    final int BROWN = 0x69690F;           // a
    final int DARK_BROWN = 0x484800;      // b
    final int YELLOW = 0xFCFC54;          // c
    final int LIGHT_ORANGE = 0xECC860;    // d
    final int ORANGE = 0xFCBC74;          // e
    final int RED = 0xA71A1A;             // f
    final int YELLOW_GREEN = 0x86861D;    // g
    final int PINK = 0xE46F6F;            // h
    final int GREEN = 0x6E9C42;           // i
    final int BLUE = 0x2D6D98;            // j
    final int BLACK = 0x000000;           // k
    final int DARK_GRAY = 0x8E8E8E;       // l
    final int WHITE = 0xECECEC;           // m
    final int GRAY = 0xD6D6D6;            // n
    final int DARK_GREEN = 0x355F18;      // o
    final int DARK_YELLOW = 0xBBBB35;     // p
    final int DARKEST_GRAY = 0x6F6F6F;    // q
    final int DARKEST_GREEN = 0x143C00;   // r
    final int LIGHT_GREEN = 0x5CBA5C;     // s

    final int[] COLORS = {
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
    };

    final Color COLOR_DARK_BROWN = new Color(DARK_BROWN);
    final Color COLOR_BLACK = new Color(0);
    final Color COLOR_DARK_GREEN = new Color(DARK_GREEN);
    final Color COLOR_YELLOW_GREEN = new Color(YELLOW_GREEN);
    final Color COLOR_GREEN = new Color(GREEN);
    final Color COLOR_GRAY = new Color(GRAY);
    final Color COLOR_DARK_YELLOW = new Color(DARK_YELLOW);

    ArrayList<int[]> queue = new ArrayList<int[]>();
    int[] object;

    object = new int[OBJECT_ARRAY_SIZE];
    queue.add(object);
    object[OBJECT_X] = 116;
    object[OBJECT_Y] = 111;
    object[OBJECT_SPRITE_INDEX] = SPRITE_LOG;
    object[OBJECT_SPRITE_DIRECTION] = LEFT;

    BufferedImage[][] sprites = new BufferedImage[50][2];
    BufferedImage image = new BufferedImage(
        152, 192, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = (Graphics2D)image.getGraphics();
    Graphics2D g2 = null;
    
    int i;
    int j;
    int k = 0;
    int x;
    int y;
    int z;

    int restartDelay = 0;
    int timer = 0;
    boolean attractMode = true;
    boolean jumpReleased = true;
    boolean hintsKeyReleased = true;
    boolean pauseKeyReleased = true;
    boolean resetScreen = false;
    boolean paused = false;
 
   int ball_x=75;
   int ball_y=80; 
   int ball_vx=2;
   int ball_vy=1; 


    long nextFrameStartTime = System.nanoTime();
    while(true) {

      do {
        nextFrameStartTime += 16666667;

        // -- update starts ----------------------------------------------------

        if (a[VK_PAUSE] && pauseKeyReleased) {
          pauseKeyReleased = false;
          paused = !paused;
        }
        if (paused) {
          continue;
        }
        
        if (attractMode) {


          } else if (a[VK_JUMP] || a[VK_UP] || a[VK_DOWN]
              || a[VK_LEFT] || a[VK_RIGHT]) {  // press any key to start








          // update timer
          timer++;







              // lame non-collision detection
              // (image.getRGB(harryX, 119) & 0xFFFFFF) != DARK_YELLOW









      } while(nextFrameStartTime < System.nanoTime());

      // -- render starts ------------------------------------------------------

      // clear frame
      g.setColor(COLOR_BLACK);
      g.fillRect(0, 0, 152, 210);

      // draw forest
      //g.setColor(COLOR_GREEN);
      //g.fillRect(0, 46, 152, 65);

      //  g.drawImage(sprites[SPRITE_TREE_BRANCHES][0], j - 2, 51, null);
      //  g.drawImage(sprites[SPRITE_TREE_BRANCHES][0], 138 - j, 51, null);






      //if (pit) {
        // draw top of pit behind Harry
      //  g.setClip(40, 111, 64, 8);
      //  g.drawImage(sprites[pitSprite][1], 40, 111 + pitOffset, 32, 8, null);
       // g.drawImage(sprites[pitSprite][0], 72, 111 + pitOffset, 32, 8, null);


      //  g.setClip(null);

      




      // -- render ends --------------------------------------------------------

      // show the hidden buffer
      if (g2 == null) {
        g2 = (Graphics2D)getGraphics();
        requestFocus();
      } else {
        g2.drawImage(image, 0, 0, 608, 384, null);
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
    final int VK_JUMP = 0x42;
    final int VK_W = 0x57;
    final int VK_S = 0x53;
    final int VK_A = 0x41;
    final int VK_D = 0x44;
    final int VK_PAUSE = 0x50; // press p for pause
    final int VK_HINTS = 0x38; // press 8 for hint map

    int k = keyEvent.getKeyCode();
    if (k > 0) { 
      k = k == VK_W ? VK_UP : k == VK_D ? VK_RIGHT : k == VK_A ? VK_LEFT
          : k == VK_S ? VK_DOWN : k;
      a[(k >= VK_LEFT && k <= VK_DOWN) || k == VK_HINTS 
          || k == VK_PAUSE ? k : VK_JUMP] = keyEvent.getID() != 402;
    }
  }

  // to run in window, uncomment below
  public static void main(String[] args) throws Throwable {
    javax.swing.JFrame frame = new javax.swing.JFrame("Pitfall 4K");
    frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
    strip applet = new strip();
    applet.setPreferredSize(new java.awt.Dimension(608, 384));
    frame.add(applet, java.awt.BorderLayout.CENTER);
    frame.setResizable(false);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
    Thread.sleep(250);
    applet.start();
  }
}

