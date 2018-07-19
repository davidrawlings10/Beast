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
    
    int numAvailablePositions = 760; // = boardWidth * (boardHeight - 1); (these vars were not populated yet of course)
    ArrayList<ArrayList<Integer>> avaliable_positions = new ArrayList<ArrayList<Integer>>();
    
    int NUM_QUADRANTS = 32;
    int CELLS_PER_QUADRANT = 5;
    
    // initialize player
    int playerX = 120, playerY = 120;
    //int playerX, playerY;
    //int enemyX = 240, enemyY = 240;
    
    // initialize enemies
    int active_enemies = 5;
    int NUM_ENEMIES = 10;
    int[] enemyX = new int[NUM_ENEMIES];
    int[] enemyY = new int[NUM_ENEMIES];
    
    // initialize blocks
    int NUM_BLOCKS = 233; // determined by count from youtube video
    int[] blockX = new int[NUM_BLOCKS];
    int[] blockY = new int[NUM_BLOCKS];
    //int[] block_quadrant = new int[NUM_BLOCKS];
    
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
            	
    	/*int randomIndex = rand.nextInt(numAvailablePositions);        	
    	playerX = avaliable_positions.get(randomIndex).get(0);
    	playerY = avaliable_positions.get(randomIndex).get(1);
    	avaliable_positions.remove(randomIndex);
    	numAvailablePositions--;*/
        
        //assign_position(playerX, playerY);

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
            
        	//blockX[i] = rand.nextInt(boardWidth_) * CELLSIZE + CELLSIZE;
        	//blockY[i] = rand.nextInt(boardHeight_ - 1) * CELLSIZE + CELLSIZE;        	
        	//blockX[i] = avaliable_positions_o[randomIndex][0];
        	//blockY[i] = avaliable_positions_o[randomIndex][1];
        	
        	/*int randomIndex = rand.nextInt(numAvailablePositions);        	
        	blockX[i] = avaliable_positions.get(randomIndex).get(0);
        	blockY[i] = avaliable_positions.get(randomIndex).get(1);
        	avaliable_positions.remove(randomIndex);
        	numAvailablePositions--;*/
        	
        	///System.out.println("numAvailablePositions:" + numAvailablePositions + ", randomIndex:" + randomIndex + 
        	//		", x:" + avaliable_positions.get(randomIndex).get(0) + ", y:" + avaliable_positions.get(randomIndex).get(1));
        }
        
        // position concretes
        for (int i = 0; i < active_concretes; ++i) {
        	ArrayList<Integer> pos = get_available_position();
            concreteX[i] = pos.get(0);
        	concreteY[i] = pos.get(1);
            
        	//concreteX[i] = rand.nextInt(boardWidth_) * CELLSIZE + CELLSIZE;
        	//concreteY[i] = rand.nextInt(boardHeight_ - 1) * CELLSIZE + CELLSIZE;
        	
        	//concreteX[i] = avaliable_positions_o[rand.nextInt(numAvailablePositions)][0];
        	//concreteY[i] = avaliable_positions_o[rand.nextInt(numAvailablePositions)][1];
            
        	/*int randomIndex = rand.nextInt(numAvailablePositions);
        	concreteX[i] = avaliable_positions.get(randomIndex).get(0);
        	concreteY[i] = avaliable_positions.get(randomIndex).get(1);
        	avaliable_positions.remove(randomIndex);
        	numAvailablePositions--;*/
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
    
    /*public int determine_quadrant(int x, int y) {   	
		if (x < (CELLSIZE * CELLS_PER_QUADRANT) * 1) {
			return determine_quadrantY(y, 0);
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 1 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 2) {
			return determine_quadrantY(y, 1);
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 2 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 3) {
			return determine_quadrantY(y, 2);    			
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 3 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 4) {
			return determine_quadrantY(y, 3);    			
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 4 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 5) {
			return determine_quadrantY(y, 4);    			
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 5 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 6) {
			return determine_quadrantY(y, 5);    			
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 6 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 7) {
			return determine_quadrantY(y, 6);    			
		} else if (x >= (CELLSIZE * CELLS_PER_QUADRANT) * 7 && x < (CELLSIZE * CELLS_PER_QUADRANT) * 8) {
			return determine_quadrantY(y, 7);
		}
		return -1;
    }
    
    public int determine_quadrantY(int y, int x_quadrant) {
		if (y < (CELLSIZE * CELLS_PER_QUADRANT) * 1) {
			return x_quadrant;
			//System.out.println("assigned block to: " + x_quadrant);
		} else if (y >= (CELLSIZE * CELLS_PER_QUADRANT) * 1 && y < (CELLSIZE * CELLS_PER_QUADRANT) * 2) {
			return 8 + x_quadrant;
			//System.out.println("assigned block to: " + (8 + x_quadrant));
		} else if (y >= (CELLSIZE * CELLS_PER_QUADRANT) * 2 && y < (CELLSIZE * CELLS_PER_QUADRANT) * 3) {
			return 16 + x_quadrant;
			//System.out.println("assigned block to: " + (16 + x_quadrant));
		} else if (y >= (CELLSIZE * CELLS_PER_QUADRANT) * 3 && y < (CELLSIZE * CELLS_PER_QUADRANT) * 4) {
			return 24 + x_quadrant;
			//System.out.println("assigned block to: " + (24 + x_quadrant));
		}
		return -1;
    }*/
    
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
    	if (time + 1000 < System.currentTimeMillis()) {
        	time = System.currentTimeMillis();
        	
        	/*for (int i = 0; i < NUM_BLOCKS; ++i) {
        		block_quadrant[i] = determine_quadrant(blockX[i], blockY[i]);
        	}
        	
        	for (int i = 0; i < NUM_CONCRETES; ++i) {
        		concrete_quadrant[i] = determine_quadrant(concreteX[i], concreteY[i]);
        	}*/
        	
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
        		//System.out.print(i + ", ");
        	}
        	//System.out.println(" - " + boardWidth + " - " + windowWidth + " - " );
        	repaint();
    	}
    	for (int i = 0; i < active_enemies; ++i) {
	    	if (playerX == enemyX[i] && playerY == enemyY[i]) {
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
        
        graphics.setColor(Color.decode("#009900"));
        for (int i = 0; i < numAvailablePositions; ++i) {
        	//graphics.fillRect(avaliable_positions[i][0], avaliable_positions[i][1], CELLSIZE, CELLSIZE);
        }
        
        // draw text
        graphics.setColor(Color.GRAY);
        graphics.setFont(new Font("Arial Black", Font.PLAIN, 20));
        graphics.drawString("Beasts: " + active_enemies, 450, windowHeight - 12);
        graphics.drawString("Level: " + "1K", 600, windowHeight - 12);
        graphics.drawString("Time: " + "0", 750, windowHeight - 12);
        graphics.drawString("Lives: " + "2", 900, windowHeight - 12);
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