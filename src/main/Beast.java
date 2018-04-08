package main;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Random;

// youtube video
// https://www.youtube.com/watch?v=gffS40Djmxc
@SuppressWarnings("serial")
public class Beast extends JPanel implements ActionListener, KeyListener
{
    Timer shapeTimer = new Timer(5, this);

    int cellSize;
    int boardWidth;
    int boardHeight;
    int windowWidth;
    int windowHeight;
    
    long time;
    
    Direction direction = Direction.UP;

    int playerX = 120, playerY = 120;
    int enemyX = 240, enemyY = 240;
    
    Random rand = new Random();
    int NUM_BLOCKS = 25;
//    int[] blockX = {rand.nextInt(38) * cellSize, 60, 90, 150, 180};
//    int[] blockY = {rand.nextInt(20) * cellSize, 360, 90, 150, 180};
    int[] blockX = new int[NUM_BLOCKS];
    int[] blockY = new int[NUM_BLOCKS];
    //blockX[0] = 8;
    
    //int[] blockY = new int[100];
    
    //for (int i = 0; i < NUM_BLOCKS; ++i) {
    //	blockY[i] = rand.nextInt(38) * cellSize;
    //}
   
    
//    for (int i = 0; i < numBlocks; ++i) {
//    	blockY[i] = rand.nextInt(38) * cellSize;
//    }
    
    public enum Direction {
    	UP, DOWN, LEFT, RIGHT
    }

    public Beast(int cellSize_, int boardWidth_, int boardHeight_, int windowWidth_, int windowHeight_)   // Constructor is passed the size of the parent frame
    {
        shapeTimer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        cellSize = cellSize_;
        boardWidth = boardWidth_;
        boardHeight = boardHeight_;
        windowWidth = windowWidth_;
        windowHeight = windowHeight_;
        //System.out.println("cellSize:"+cellSize+"boardWidth:"+boardWidth+"boardHeight:"+boardHeight+"windowWidth:"+windowWidth+"windowHeight:"+windowHeight);
        
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	blockX[i] = rand.nextInt(boardWidth_ - 2) * cellSize + cellSize;
        }
        
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	//blockX[i] = rand.nextInt() * cellSize;
        	blockY[i] = rand.nextInt(boardHeight_ - 2) * cellSize + cellSize;
        }
    }
    
    public void update() {
    	if (time + 1000 < System.currentTimeMillis()) {
        	time = System.currentTimeMillis();
        	enemyX += cellSize;
        	repaint();
    	}
    	if (playerX == enemyX && playerY == enemyY) {
//    		System.out.println("collision");
//    		System.out.println(playerX + '-' + enemyX);
//    		System.out.println(playerY + '-' + enemyY);
    	}
    }

    public void paintComponent(Graphics g) 
    {	
        super.paintComponent(g);
        Graphics2D graphics = (Graphics2D) g;
        
        // draw backdrop
        graphics.setColor(Color.BLACK);      
        graphics.fillRect(0, 0, windowWidth, windowHeight);
        
        // draw border
        graphics.setColor(Color.YELLOW);
        graphics.fillRect(0, 0, windowWidth, cellSize); // top
        graphics.fillRect(0, 0, cellSize, windowHeight - cellSize); // left
        graphics.fillRect(windowWidth - cellSize, 0, windowWidth, windowHeight - cellSize); // right
        graphics.fillRect(0, windowHeight - cellSize * 2, windowWidth, cellSize); // bottom
        
        // draw player
        graphics.setColor(Color.decode("#00FFFF"));
        graphics.fillRect(playerX, playerY, cellSize, cellSize);
        
        // draw enemies
        graphics.setColor(Color.decode("#FF0000"));
        graphics.fillRect(enemyX, enemyY, cellSize, cellSize);
        
        // draw concretes
        graphics.setColor(Color.decode("#FFFF00"));
        graphics.fillRect(210, 210, cellSize, cellSize);
        
        // draw blocks
        graphics.setColor(Color.decode("#009900"));
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	graphics.fillRect(blockX[i], blockY[i], cellSize, cellSize);
        }
        
        // draw text
        graphics.setColor(Color.WHITE);
        graphics.drawString("Beasts: ", 200, windowHeight - 15);
    }

    public void actionPerformed(ActionEvent e) 
    {
        // Redraw the square when something happens
        repaint();
    }

    public void keyPressed(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();

        if (keyCode == KeyEvent.VK_UP) 
        {
            playerY += -cellSize;
            direction = Direction.UP;
        }

        if (keyCode == KeyEvent.VK_DOWN)
        {
            playerY += cellSize;
            direction = Direction.DOWN;
        }

        if (keyCode == KeyEvent.VK_RIGHT)
        {
            playerX += cellSize;
            direction = Direction.RIGHT;
        }

        if (keyCode == KeyEvent.VK_LEFT) 
        {
            playerX -= cellSize;
            direction = Direction.LEFT;
        }
        checkForCollisions(playerX, playerY, -1);
    }
    
    private void checkForCollisions(int x, int y, int id) {
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		//System.out.println(i);
    		if (blockX[i] == x && blockY[i] == y && i != id) {
    			if (direction == Direction.UP) {
    				blockY[i] -= cellSize;
    			}
    			if (direction == Direction.DOWN) {
    				blockY[i] += cellSize;
    			}
    			if (direction == Direction.LEFT) {
    				blockX[i] -= cellSize;
    			}
    			if (direction == Direction.RIGHT) {
    				blockX[i] += cellSize;
    			}
    			checkForCollisions(blockX[i], blockY[i], i);
    		}
    	}
    }

    public void keyTyped(KeyEvent e) {}
    
    public void keyReleased(KeyEvent e)
    {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP) {}
        if (keyCode == KeyEvent.VK_DOWN) {}
        if (keyCode == KeyEvent.VK_RIGHT) {}
        if (keyCode == KeyEvent.VK_LEFT) {}               
    }
}