package main;
import java.awt.Color;
import java.awt.Font;
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
    
    int numAvailablePositions = 760;
    ArrayList<ArrayList<Integer>> avaliable_positions = new ArrayList<ArrayList<Integer>>();
    
    int NUM_QUADRANTS = 32;
    int CELLS_PER_QUADRANT = 5;
    
    // initialize player
    int playerX = 120, playerY = 120;
    int lives = 5;
    
    // initialize enemies
    int active_enemies = 7;
    int NUM_ENEMIES = 10;
    int[] enemyX = new int[NUM_ENEMIES];
    int[] enemyY = new int[NUM_ENEMIES];
    
    // initialize blocks
    int NUM_BLOCKS = 233; // determined by count from youtube video
    int[] blockX = new int[NUM_BLOCKS];
    int[] blockY = new int[NUM_BLOCKS];
    
    // initialize concretes
    int active_concretes = 10;
    int NUM_CONCRETES = 20;
    int[] concreteX = new int[NUM_CONCRETES];
    int[] concreteY = new int[NUM_CONCRETES];
    int[] concrete_quadrant = new int[NUM_CONCRETES];

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
        
        // populate available_positions
        int index = 0;
        for (int i = 0; i < boardWidth; ++i) {
        	for (int j = 0; j < boardHeight - 1; ++j) {
        		
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

    	ArrayList<Integer> pos_ = get_available_position();
        playerX = pos_.get(0);
    	playerY = pos_.get(1);
        
        // position enemies
        for (int i = 0; i < NUM_ENEMIES; ++i) {
        	ArrayList<Integer> pos = get_available_position();
            enemyX[i] = pos.get(0);
        	enemyY[i] = pos.get(1);
        }
        
        // position blocks
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	ArrayList<Integer> pos = get_available_position();
            blockX[i] = pos.get(0);
        	blockY[i] = pos.get(1);
        }
        
        // position concretes
        for (int i = 0; i < active_concretes; ++i) {
        	ArrayList<Integer> pos = get_available_position();
            concreteX[i] = pos.get(0);
        	concreteY[i] = pos.get(1);
        }
    }
    
    private ArrayList<Integer> get_available_position() {
    	int randomIndex = rand.nextInt(numAvailablePositions);
    	ArrayList<Integer> pos = new ArrayList<Integer>();
    	pos.add(avaliable_positions.get(randomIndex).get(0));
    	pos.add(avaliable_positions.get(randomIndex).get(1));
    	avaliable_positions.remove(randomIndex);
    	numAvailablePositions--;
    	return pos;
    }
    
    private boolean check_availablity(int x, int y) {
    	if (x < CELLSIZE || y < CELLSIZE || x > CELLSIZE * boardWidth || y > CELLSIZE * (boardHeight - 1)) {
    		return false;
    	}
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == x && blockY[i] == y) {
    			return false;
    		}
    	}
    	for (int i = 0; i < active_concretes; ++i) {
    		if (concreteX[i] == x && concreteY[i] == y) {
    			return false;
    		}
    	}
    	for (int i = 0; i < active_enemies; ++i) {
    		if (enemyX[i] == x && enemyY[i] == y) {
    			return false;
    		}
    	}
    	return true;
    }
    
    private void add_if_available(int x, int y, ArrayList<Integer> potential_movesX, ArrayList<Integer> potential_movesY) {
    	if (check_availablity(x, y)) {
    		potential_movesX.add(x);
    		potential_movesY.add(y);
    	}
    }
    
    public void update() {
    	// move enemy
    	if (time + 1000 < System.currentTimeMillis()) {
        	time = System.currentTimeMillis();
        	
        	for (int i = 0; i < active_enemies; ++i) {
        	    ArrayList<Integer> potential_movesX = new ArrayList<Integer>();
        	    ArrayList<Integer> potential_movesY = new ArrayList<Integer>();
        	    add_if_available(enemyX[i] - CELLSIZE, enemyY[i] - CELLSIZE, potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i] - CELLSIZE, enemyY[i]           , potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i] - CELLSIZE, enemyY[i] + CELLSIZE, potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i]           , enemyY[i] - CELLSIZE, potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i]           , enemyY[i]           , potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i]           , enemyY[i] + CELLSIZE, potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i] + CELLSIZE, enemyY[i] - CELLSIZE, potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i] + CELLSIZE, enemyY[i]           , potential_movesX, potential_movesY);
        	    add_if_available(enemyX[i] + CELLSIZE, enemyY[i] + CELLSIZE, potential_movesX, potential_movesY);
        	    if (potential_movesX.size() == 0)
        	    	continue;
        	    int chosen_index = rand.nextInt(potential_movesX.size());    	    
        	    int chosenX = potential_movesX.get(chosen_index);
        	    int chosenY = potential_movesY.get(chosen_index);
        		enemyX[i] = chosenX;
        		enemyY[i] = chosenY;
        	}
        	repaint();
    	}
    	
    	// check if player collides with enemy
    	for (int i = 0; i < active_enemies; ++i) {
	    	if (playerX == enemyX[i] && playerY == enemyY[i]) {
	    		lives--;
	    		playerX = 30 * 38; // 1140
	    		playerY = 30 * 20; // 600
	    	}
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
        
        // draw enemies       
        graphics.setColor(Color.decode("#FF0000"));
        for (int i = 0; i < active_enemies; ++i) {
        	graphics.fillRect(enemyX[i], enemyY[i], CELLSIZE, CELLSIZE);
        }        
        
        /*graphics.setColor(Color.decode("#009900"));
        for (int i = 0; i < numAvailablePositions; ++i) {
        	//graphics.fillRect(avaliable_positions[i][0], avaliable_positions[i][1], CELLSIZE, CELLSIZE);
        }*/
        
        // draw text
        graphics.setColor(Color.GRAY);
        graphics.setFont(new Font("Arial Black", Font.PLAIN, 20));
        graphics.drawString("Beasts: " + active_enemies, 450, windowHeight - 12);
        graphics.drawString("Level: " + "1K", 600, windowHeight - 12);
        graphics.drawString("Time: " + "0", 750, windowHeight - 12);
        graphics.drawString("Lives: " + lives, 900, windowHeight - 12);
        graphics.drawString("Score:  " + "0", 1050, windowHeight - 12);
    }

    public void actionPerformed(ActionEvent e) 
    {
        // Redraw the square when something happens
        repaint();
    }

    public void keyPressed(KeyEvent e) 
    {
        int keyCode = e.getKeyCode();
        
        boolean playerLegalMove = true;
        int player_potentialX = -1, player_potentialY = -1;
        
        if (keyCode == KeyEvent.VK_UP) 
        {
            direction = Direction.UP;
            player_potentialX = playerX;
            player_potentialY = playerY - CELLSIZE;
            if (player_potentialY < CELLSIZE) {
            	playerLegalMove = false;
            }
        }

        if (keyCode == KeyEvent.VK_DOWN)
        {
            direction = Direction.DOWN;
            player_potentialX = playerX;
            player_potentialY = playerY + CELLSIZE;
            if (player_potentialY  > CELLSIZE * (boardHeight - 1)) {
            	playerLegalMove = false;
            }
        }

        if (keyCode == KeyEvent.VK_RIGHT)
        {
            direction = Direction.RIGHT;
            player_potentialX = playerX + CELLSIZE;
            player_potentialY = playerY;
        	if (playerX + CELLSIZE > CELLSIZE * boardWidth) {
        		playerLegalMove = false;
        	}
        }

        if (keyCode == KeyEvent.VK_LEFT) 
        {
            direction = Direction.LEFT;
            player_potentialX = playerX - CELLSIZE;
            player_potentialY = playerY;
        	if (player_potentialX < CELLSIZE) {
        		playerLegalMove = false;
        	}
        }
        
    	for (int i = 0; i < NUM_CONCRETES; ++i) {
    		if (concreteX[i] == player_potentialX && concreteY[i] == player_potentialY) {
        		playerLegalMove = false;
    		}
    	}
        
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == player_potentialX && blockY[i] == player_potentialY) {
    			if (direction == Direction.UP) {
    				Integer y = moveToNextEmptyPositionY(i, blockX[i], blockY[i] - CELLSIZE);
    				if (y != null) { blockY[i] = y; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.DOWN) { 
    				Integer y = moveToNextEmptyPositionY(i, blockX[i], blockY[i] + CELLSIZE);
    				if (y != null) { blockY[i] = y; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.LEFT) { 
    				Integer x = moveToNextEmptyPositionX(i, blockX[i] - CELLSIZE, blockY[i]);
    				if (x != null) { blockX[i] = x; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.RIGHT) { 
    				Integer x = moveToNextEmptyPositionX(i, blockX[i] + CELLSIZE, blockY[i]);
    				if (x != null) { blockX[i] = x; } else { playerLegalMove = false; }
    			}
    		}
    	}
    	if (playerLegalMove) {
			playerX = player_potentialX;
			playerY = player_potentialY;
    	}
    	
        //checkForCollisions(playerX, playerY, -1);
    }
    
    private Integer moveToNextEmptyPositionY(int index, int x, int y) {
    	if (y < CELLSIZE || y > CELLSIZE * (boardHeight - 1)) {
    		return null;
    	}
    	for (int i = 0; i < NUM_CONCRETES; ++i) {
    		if (concreteX[i] == x && concreteY[i] == y) {
    			return null;
    		}
    	}
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == x && blockY[i] == y && i != index) {
    			if (direction == Direction.UP)  { y -= CELLSIZE; }
    			if (direction == Direction.DOWN) { y += CELLSIZE; }
    			return moveToNextEmptyPositionY(index, x, y);
    		}
    	}
    	return y;
    }
    
    private Integer moveToNextEmptyPositionX(int index, int x, int y) {
    	if (x < CELLSIZE || x > CELLSIZE * boardWidth) {
    		return null;
    	}
    	for (int i = 0; i < NUM_CONCRETES; ++i) {
    		if (concreteX[i] == x && concreteY[i] == y) {
    			return null;
    		}
    	}
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == x && blockY[i] == y && i != index) {
    			if (direction == Direction.LEFT)  { x -= CELLSIZE; }
    			if (direction == Direction.RIGHT) { x += CELLSIZE; }
    			return moveToNextEmptyPositionX(index, x, y);
    		}
    	}
    	return x;
    }
    
    /*private void checkForCollisions(int x, int y, int id) {
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
    }*/

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