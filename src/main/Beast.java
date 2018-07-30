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
import java.util.concurrent.TimeUnit;

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
    
    Direction direction = Direction.UP;
    
    int numAvailablePositions = -1;
    ArrayList<ArrayList<Integer>> avaliable_positions = new ArrayList<ArrayList<Integer>>();
    
    int NUM_QUADRANTS = 32;
    int CELLS_PER_QUADRANT = 5;
    
    int level = 0;
    int score = 0;
    
    long setupTime = 0;
    boolean settingUpLevel = true;
    
    int stopwatch = 0;
    long time;
    
    // initialize player
    int playerX = -1, playerY = -1;
    int lives = 5;
    
    // initialize enemies
    int active_enemies = -1; // will be initialized later
    int alive_enemies = -1; // will be initialized later
    int NUM_ENEMIES = 105;
    int[] enemyX = new int[NUM_ENEMIES];
    int[] enemyY = new int[NUM_ENEMIES];
    boolean[] enemy_alive = new boolean[NUM_ENEMIES];
    int[] enemy_self_destruct = new int[NUM_ENEMIES];
    ArrayList<Integer> available_cells_in_ = new ArrayList<Integer>();
    //ArrayList<ArrayList<Integer>> cells_seen_recently = new ArrayList<ArrayList<Integer>>();
    
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
        
        // number of cells in board (width: 38, height: 20)
        CELLSIZE = CELLSIZE_;
        boardWidth = boardWidth_;
        boardHeight = boardHeight_;
        windowWidth = windowWidth_;
        windowHeight = windowHeight_;        
        
        setup_level();
    }
    
    private void setup_level() {
    	long startTime = System.currentTimeMillis();
    	
    	if (level > 0)
    		score += 9 + 4 * level;
    	
    	++level;
    	
    	stopwatch = 0;
    	
    	int index = 0;
    	avaliable_positions.clear();
        for (int i = 0; i < boardWidth; ++i) {
        	for (int j = 0; j < boardHeight - 1; ++j) {
        		
        		ArrayList<Integer> pos = new ArrayList<Integer>();
        		pos.add(i * CELLSIZE + CELLSIZE);
        		pos.add(j * CELLSIZE + CELLSIZE);
        		avaliable_positions.add(pos);
        		
        		index++;
        	}
        }
        numAvailablePositions = 760;
        if (index != numAvailablePositions) {
        	System.out.println("WARNING: assignment index did not match numAvailablePositions");
        	System.out.println("index: "+index);
        	System.out.println("numAvailablePositions: "+numAvailablePositions);
        }
        
    	ArrayList<Integer> pos_ = get_available_position();
        playerX = pos_.get(0);
    	playerY = pos_.get(1);
        
        // position enemies
    	active_enemies = 5 + level * 2;
    	alive_enemies = 5 + level * 2;
        for (int i = 0; i < active_enemies; ++i) {
        	ArrayList<Integer> pos = get_available_position();
            enemyX[i] = pos.get(0);
        	enemyY[i] = pos.get(1);
        	enemy_alive[i] = true;
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
    	int randomIndex = rand.nextInt(numAvailablePositions); // bug here relating to WARNING: assignment index did not match numAvailablePositions
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
    
    private void find_reachable_cells(int x, int y, ArrayList<Integer> reachable_cells) {
		if (reachable_cells.size() > 9) {
			return;
		}
    	int x_check = -1, y_check = -1;
    	for (int i = 0; i < 8; ++i) {
    		if (i == 0) { x_check = x - CELLSIZE; y_check = y - CELLSIZE; }
    		if (i == 1) { x_check = x           ; y_check = y - CELLSIZE; }
    		if (i == 2) { x_check = x + CELLSIZE; y_check = y - CELLSIZE; }
    		if (i == 3) { x_check = x + CELLSIZE; y_check = y           ; }
    		if (i == 4) { x_check = x + CELLSIZE; y_check = y + CELLSIZE; }
    		if (i == 5) { x_check = x           ; y_check = y + CELLSIZE; }
    		if (i == 6) { x_check = x - CELLSIZE; y_check = y + CELLSIZE; }
    		if (i == 7) { x_check = x - CELLSIZE; y_check = y           ; }
    		//System.out.println("checking:" + (y_check * 38 / CELLSIZE - CELLSIZE) + (x_check / CELLSIZE - CELLSIZE));
    		if (check_availablity(x_check, y_check)) {
    			boolean never_seen = true;
    			for (Integer j : reachable_cells) {
        			//System.out.print(j + "|");     
    				if (calc_cell_id(x_check, y_check) == j) {
    					never_seen = false;
    				}
    			}
    			if (never_seen == true) {
	    			reachable_cells.add(calc_cell_id(x_check, y_check));
	    			find_reachable_cells(x_check, y_check, reachable_cells);
    			}
    		}
    	}
    }
    
    private int calc_cell_id(int x, int y) {
    	return (y - CELLSIZE) / CELLSIZE * 38 + (x - CELLSIZE) / CELLSIZE;
    }
    
    public void update() {
    	if (settingUpLevel == true) {
    		settingUpLevel = false;
    		setupTime = System.currentTimeMillis();
    		//System.out.println("capturing:" + setupTime);
    	}
    	
    	// move enemy
    	if (time + 1000 < System.currentTimeMillis()) {
        	time = System.currentTimeMillis();
        	
        	stopwatch++;
        	
        	for (int i = 0; i < active_enemies; ++i) {
        		ArrayList<Integer> reachable_cells = new ArrayList<Integer>();
        		find_reachable_cells(enemyX[i], enemyY[i], reachable_cells);
            	if (reachable_cells.size() > 9) {
        			enemy_self_destruct[i] = 10;
            	} else {
            		enemy_self_destruct[i]--;
            		if (enemy_self_destruct[i] == 0) {
            			enemy_killed(i);
            		}
        		}
            	//for (Integer x : reachable_cells) {
        			//System.out.print(x + "|");            		
            	//}
            	//System.out.println("");
        		//System.out.println(reachable_cells.size() + " - " + enemy_self_destruct[i]);
        		
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
			if (!enemy_alive[i])
				continue;
	    	if (playerX == enemyX[i] && playerY == enemyY[i]) {
	    		lives--;
	    		if (lives == 0) { playerX = 2000; }
    			while (true) {
    				playerX = rand.nextInt(boardWidth) * CELLSIZE + CELLSIZE;
    				playerY = rand.nextInt(boardHeight - 1) * CELLSIZE + CELLSIZE;
    				if (lives > 0) {
    					if (check_availablity(playerX, playerY))
    						break;
    				}
    			}
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
        graphics.setColor(Color.decode("#DDDD00"));
        graphics.fillRect(0, 0, windowWidth, CELLSIZE); // top
        graphics.fillRect(0, 0, CELLSIZE, windowHeight - CELLSIZE); // left
        graphics.fillRect(windowWidth - CELLSIZE, 0, windowWidth, windowHeight - CELLSIZE); // right
        graphics.fillRect(0, windowHeight - CELLSIZE * 2, windowWidth, CELLSIZE); // bottom
        
        // draw player
        graphics.setColor(Color.decode("#00FFFF"));
        //graphics.fillRect(playerX, playerY, CELLSIZE, CELLSIZE);
        graphics.fillRect(playerX + 13, playerY + 2, 1, 26);
        graphics.fillRect(playerX + 12, playerY + 3, 1, 24);
        graphics.fillRect(playerX + 11, playerY + 4, 1, 22);
        graphics.fillRect(playerX + 10, playerY + 5, 1, 20);
        graphics.fillRect(playerX + 9,  playerY + 6, 1, 18);
        graphics.fillRect(playerX + 8,  playerY + 7, 1, 16);
        graphics.fillRect(playerX + 7,  playerY + 8, 1, 14);
        graphics.fillRect(playerX + 6,  playerY + 9, 1, 12);
        graphics.fillRect(playerX + 5,  playerY + 10, 1, 10);
        graphics.fillRect(playerX + 4,  playerY + 11, 1, 8);
        graphics.fillRect(playerX + 3,  playerY + 12, 1, 6);
        graphics.fillRect(playerX + 2,  playerY + 13, 1, 4);
        graphics.fillRect(playerX + 1,  playerY + 14, 1, 2);
        
        graphics.fillRect(playerX + 17, playerY + 2, 1, 26);
        graphics.fillRect(playerX + 18, playerY + 3, 1, 24);
        graphics.fillRect(playerX + 19, playerY + 4, 1, 22);
        graphics.fillRect(playerX + 20, playerY + 5, 1, 20);
        graphics.fillRect(playerX + 21,  playerY + 6, 1, 18);
        graphics.fillRect(playerX + 22,  playerY + 7, 1, 16);
        graphics.fillRect(playerX + 23,  playerY + 8, 1, 14);
        graphics.fillRect(playerX + 24,  playerY + 9, 1, 12);
        graphics.fillRect(playerX + 25,  playerY + 10, 1, 10);
        graphics.fillRect(playerX + 26,  playerY + 11, 1, 8);
        graphics.fillRect(playerX + 27,  playerY + 12, 1, 6);
        graphics.fillRect(playerX + 28,  playerY + 13, 1, 4);
        graphics.fillRect(playerX + 29,  playerY + 14, 1, 2);
        
        
        // draw concretes
    	graphics.setColor(Color.decode("#DDDD00"));
        for (int i = 0; i < active_concretes; ++i) {
        	graphics.fillRect(concreteX[i], concreteY[i], CELLSIZE, CELLSIZE);
        }
        
        // draw blocks
        graphics.setColor(Color.decode("#005500"));
        for (int i = 0; i < NUM_BLOCKS; ++i) {
        	graphics.fillRect(blockX[i], blockY[i], CELLSIZE, CELLSIZE);
        }
        
        // draw enemies       
        graphics.setColor(Color.decode("#FF0000"));
        for (int i = 0; i < active_enemies; ++i) {
        	if (enemy_alive[i]) {
        		graphics.fillRect(enemyX[i] + 8, enemyY[i], 2, CELLSIZE);
        		graphics.fillRect(enemyX[i] + 22, enemyY[i], 2, CELLSIZE);
        		graphics.fillRect(enemyX[i] + 8, enemyY[i] + 14, 14, 2);
        	}
        }
        
        // draw text
        graphics.setColor(Color.GRAY);
        graphics.setFont(new Font("Arial Black", Font.PLAIN, 20));
        graphics.drawString("Beasts: " + alive_enemies, 450, windowHeight - 12);
        graphics.drawString("Level: " + level + "K", 600, windowHeight - 12);
        graphics.drawString("Time: " + String.format("%02d", stopwatch / 60) + ":" + String.format("%02d", stopwatch % 60), 750, windowHeight - 12);
        graphics.drawString("Lives: " + lives, 900, windowHeight - 12);
        graphics.drawString("Score:  " + score, 1050, windowHeight - 12);
        
        // setup screen
        graphics.setColor(Color.BLACK);      
        graphics.fillRect(0, 0, windowWidth, (int)setupTime);
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
        int player_potentialX = playerX, player_potentialY = playerY;
        
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
    				Integer y = findNextEmptyPositionY(i, blockX[i], blockY[i] - CELLSIZE);
    				if (y != null) { blockY[i] = y; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.DOWN) { 
    				Integer y = findNextEmptyPositionY(i, blockX[i], blockY[i] + CELLSIZE);
    				if (y != null) { blockY[i] = y; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.LEFT) { 
    				Integer x = findNextEmptyPositionX(i, blockX[i] - CELLSIZE, blockY[i]);
    				if (x != null) { blockX[i] = x; } else { playerLegalMove = false; }
    			}
    			if (direction == Direction.RIGHT) { 
    				Integer x = findNextEmptyPositionX(i, blockX[i] + CELLSIZE, blockY[i]);
    				if (x != null) { blockX[i] = x; } else { playerLegalMove = false; }
    			}
    		}
    	}
    	if (playerLegalMove) {
			playerX = player_potentialX;
			playerY = player_potentialY;
			//System.out.println("moving to:" + playerX + "," + playerY + ", id:" + calc_cell_id(playerX, playerY));
    	}
    }
    
    private void enemy_killed(int index) {
		enemy_alive[index] = false;
		alive_enemies--;
		score += 2;
		if (alive_enemies == 0)
			setup_level();    	
    }
    
    private Integer findNextEmptyPositionY(int index, int x, int y) {
    	if (y < CELLSIZE || y > CELLSIZE * (boardHeight - 1)) {
    		return null;
    	}
    	for (int i = 0; i < active_enemies; ++i) {
    		if (enemyX[i] == x && enemyY[i] == y) {
    			if (!enemy_alive[i])
    				continue;
    			int y_check = 0;
    			if (direction == Direction.UP) { y_check = y - CELLSIZE; }
    			if (direction == Direction.DOWN) { y_check = y + CELLSIZE; }
    	    	if (y_check < CELLSIZE || y_check > CELLSIZE * (boardHeight - 1)) {
	    			enemy_killed(i);
	    			return y;    	    		
    	    	}
    	    	for (int j = 0; j < active_concretes; ++j) {
    	    		if (concreteX[j] == x && concreteY[j] == y_check) {
    	    			enemy_killed(i);
    	    			return y;
    	    		}
    	    	}
    	    	for (int j = 0; j < NUM_BLOCKS; ++j) {
    	    		if (blockX[j] == x && blockY[j] == y_check) {
    	    			enemy_killed(i);
    	    			return y;
    	    		}
    	    	}
    			return null;
    		}
    	}
    	for (int i = 0; i < active_concretes; ++i) {
    		if (concreteX[i] == x && concreteY[i] == y) {
    			return null;
    		}
    	}
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == x && blockY[i] == y && i != index) {
    			if (direction == Direction.UP)  { y -= CELLSIZE; }
    			if (direction == Direction.DOWN) { y += CELLSIZE; }
    			return findNextEmptyPositionY(index, x, y);
    		}
    	}
    	return y;
    }
    
    private Integer findNextEmptyPositionX(int index, int x, int y) {
    	if (x < CELLSIZE || x > CELLSIZE * boardWidth) {
    		return null;
    	}
    	for (int i = 0; i < active_enemies; ++i) {
    		if (enemyX[i] == x && enemyY[i] == y) {
    			if (!enemy_alive[i])
    				continue;
    			int x_check = 0;
    			if (direction == Direction.LEFT) { x_check = x - CELLSIZE; }
    			if (direction == Direction.RIGHT) { x_check = x + CELLSIZE; }
    	    	if (x_check < CELLSIZE || x_check > CELLSIZE * boardWidth) {
	    			enemy_killed(i);
	    			return x;    	    		
    	    	}
    	    	for (int j = 0; j < active_concretes; ++j) {
    	    		if (concreteX[j] == x_check && concreteY[j] == y) {
    	    			enemy_killed(i);
    	    			return x;
    	    		}
    	    	}
    	    	for (int j = 0; j < NUM_BLOCKS; ++j) {
    	    		if (blockX[j] == x_check && blockY[j] == y) {
    	    			enemy_killed(i);
    	    			return x;
    	    		}
    	    	}
    			return null;
    		}
    	}
    	for (int i = 0; i < active_concretes; ++i) {
    		if (concreteX[i] == x && concreteY[i] == y) {
    			return null;
    		}
    	}
    	for (int i = 0; i < NUM_BLOCKS; ++i) {
    		if (blockX[i] == x && blockY[i] == y && i != index) {
    			if (direction == Direction.LEFT)  { x -= CELLSIZE; }
    			if (direction == Direction.RIGHT) { x += CELLSIZE; }
    			return findNextEmptyPositionX(index, x, y);
    		}
    	}
    	return x;
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