package main;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

// youtube video
// https://www.youtube.com/watch?v=gffS40Djmxcavaliable_positionsX
@SuppressWarnings("serial")
public class Beast extends JPanel implements ActionListener, KeyListener
{
    public enum Direction {
    	UP, DOWN, LEFT, RIGHT
    }
	
    Timer shapeTimer = new Timer(5, this);
    Random rand = new Random();

    int CELLSIZE;
    int boardWidth;
    int boardHeight;
    int windowWidth;
    int windowHeight;
    long time;
    
    Direction direction = Direction.UP;
    int playerX = 120, playerY = 120;
    int enemyX = 240, enemyY = 240;
    
    int numAvailablePositions = 760; // = boardWidth * (boardHeight - 1); (these vars were not populated yet of course)
    int[][] avaliable_positions_o = new int[760][2];
    
    ArrayList<ArrayList<Integer>> avaliable_positions = new ArrayList<ArrayList<Integer>>();
    
    int NUM_BLOCKS = 233;
    int[] blockX = new int[NUM_BLOCKS];
    int[] blockY = new int[NUM_BLOCKS];
    
    int active_concretes = 10;
    int[] concreteX = new int[20];
    int[] concreteY = new int[20];

    public Beast(int CELLSIZE_, int boardWidth_, int boardHeight_, int windowWidth_, int windowHeight_)   // Constructor is passed the size of the parent frame
    {
        shapeTimer.start();

        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        
        CELLSIZE = CELLSIZE_;
        boardWidth = boardWidth_;
        boardHeight = boardHeight_;
        windowWidth = windowWidth_;
        windowHeight = windowHeight_;
                
        int index = 0;
        for (int i = 0; i < boardWidth; ++i) {
        	for (int j = 0; j < boardHeight - 1; ++j) {
        		//System.out.println("index: " + index + ", i: " + i + ", j: " + j + ", posx: " + (i * CELLSIZE + CELLSIZE) + ", poxy: " + (j * CELLSIZE + CELLSIZE));
        		//avaliable_positions_o[index][0] = i * CELLSIZE + CELLSIZE;
        		//avaliable_positions_o[index][1] = j * CELLSIZE + CELLSIZE;
        		
        		ArrayList<Integer> pos = new ArrayList<Integer>();
        		pos.add(i * CELLSIZE + CELLSIZE);
        		pos.add(j * CELLSIZE + CELLSIZE);
        		avaliable_positions.add(pos);
        		
        		index++;
        	}
        }
        if (index != numAvailablePositions) {
        	System.out.println("WARNING: assignment index did not match numAvailablePositions");
        	System.out.println("index: "+index);
        	System.out.println("numAvailablePositions: "+numAvailablePositions);
        }
        
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	//blockX[i] = rand.nextInt(boardWidth_) * CELLSIZE + CELLSIZE;
        	//blockY[i] = rand.nextInt(boardHeight_ - 1) * CELLSIZE + CELLSIZE;        	
        	//blockX[i] = avaliable_positions_o[randomIndex][0];
        	//blockY[i] = avaliable_positions_o[randomIndex][1];
        	
        	int randomIndex = rand.nextInt(numAvailablePositions);        	
        	blockX[i] = avaliable_positions.get(randomIndex).get(0);
        	blockY[i] = avaliable_positions.get(randomIndex).get(1);
        	avaliable_positions.remove(randomIndex);
        	numAvailablePositions--;
        	
        	///System.out.println("numAvailablePositions:" + numAvailablePositions + ", randomIndex:" + randomIndex + 
        	//		", x:" + avaliable_positions.get(randomIndex).get(0) + ", y:" + avaliable_positions.get(randomIndex).get(1));
        }
        
        for (int i = 0; i < active_concretes; ++i) {
        	//concreteX[i] = rand.nextInt(boardWidth_) * CELLSIZE + CELLSIZE;
        	//concreteY[i] = rand.nextInt(boardHeight_ - 1) * CELLSIZE + CELLSIZE;
        	
        	//concreteX[i] = avaliable_positions_o[rand.nextInt(numAvailablePositions)][0];
        	//concreteY[i] = avaliable_positions_o[rand.nextInt(numAvailablePositions)][1];

        	int randomIndex = rand.nextInt(numAvailablePositions);
        	concreteX[i] = avaliable_positions.get(randomIndex).get(0);
        	concreteY[i] = avaliable_positions.get(randomIndex).get(1);
        	avaliable_positions.remove(randomIndex);
        	numAvailablePositions--;
        }
    }
    
    public void update() {
    	if (time + 1000 < System.currentTimeMillis()) {
        	time = System.currentTimeMillis();
        	enemyX += CELLSIZE;
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
        graphics.fillRect(0, 0, windowWidth, CELLSIZE); // top
        graphics.fillRect(0, 0, CELLSIZE, windowHeight - CELLSIZE); // left
        graphics.fillRect(windowWidth - CELLSIZE, 0, windowWidth, windowHeight - CELLSIZE); // right
        graphics.fillRect(0, windowHeight - CELLSIZE * 2, windowWidth, CELLSIZE); // bottom
        
        // draw player
        graphics.setColor(Color.decode("#00FFFF"));
        graphics.fillRect(playerX, playerY, CELLSIZE, CELLSIZE);
        
        // draw enemies       
        graphics.setColor(Color.decode("#FF0000"));
        graphics.fillRect(enemyX, enemyY, CELLSIZE, CELLSIZE);
        
        // draw concretes
    	graphics.setColor(Color.decode("#FFFF00"));
        for (int i = 0; i < active_concretes; ++i) {
        	graphics.fillRect(concreteX[i], concreteY[i], CELLSIZE, CELLSIZE);
        }
        
        // draw blocks
        graphics.setColor(Color.decode("#009900"));
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	graphics.fillRect(blockX[i], blockY[i], CELLSIZE, CELLSIZE);
        }
        
        
        graphics.setColor(Color.decode("#009900"));
        for (int i = 0; i < numAvailablePositions; ++i) {
        	//graphics.fillRect(avaliable_positions[i][0], avaliable_positions[i][1], CELLSIZE, CELLSIZE);
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
            playerY += -CELLSIZE;
            direction = Direction.UP;
        }

        if (keyCode == KeyEvent.VK_DOWN)
        {
            playerY += CELLSIZE;
            direction = Direction.DOWN;
        }

        if (keyCode == KeyEvent.VK_RIGHT)
        {
            playerX += CELLSIZE;
            direction = Direction.RIGHT;
        }

        if (keyCode == KeyEvent.VK_LEFT) 
        {
            playerX -= CELLSIZE;
            direction = Direction.LEFT;
        }
        checkForCollisions(playerX, playerY, -1);
    }
    
    private void checkForCollisions(int x, int y, int id) {
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		//System.out.println(i);
    		if (blockX[i] == x && blockY[i] == y && i != id) {
    			if (direction == Direction.UP) {
    				blockY[i] -= CELLSIZE;
    			}
    			if (direction == Direction.DOWN) {
    				blockY[i] += CELLSIZE;
    			}
    			if (direction == Direction.LEFT) {
    				blockX[i] -= CELLSIZE;
    			}
    			if (direction == Direction.RIGHT) {
    				blockX[i] += CELLSIZE;
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