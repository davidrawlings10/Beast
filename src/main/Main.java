// http://stackoverflow.com/questions/28531137/java-moving-square-on-a-frame-how-do-i-stop-it-at-the-edge-of-the-frame

package main;
import java.util.ArrayList;

import javax.swing.JFrame;

public class Main 
{

	public static void main(String args[]) 
	{
		int cellSize = 30;
		int boardWidth = 38; // not including border
		int boardHeight = 21; // not including border
		int windowWidth = boardWidth * cellSize + cellSize * 2;
		int windowHeight = boardHeight * cellSize + cellSize * 2;
	
		int[] blockY = new int[100];
		
	    JFrame frmMain = new JFrame();
	    frmMain.setSize(windowWidth + 20, windowHeight + 50); // had to add a 20 and 50 pixel buffers for some reason to get the window to be how many pixel I expect
	
	    // Create a moving square and add to the frame
	    Beast beast = new Beast(cellSize, boardWidth, boardHeight, windowWidth, windowHeight);      
	    frmMain.add(beast);
	
	    // Final configuration settings for frame.
	    frmMain.setVisible(true);
	    frmMain.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frmMain.setTitle("Beast");	   
	    
	    while (true) {
	    	beast.update();
	    }
	}

}