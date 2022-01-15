import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.Color;

public class BaseGame extends AbstractGame {
    
    private static final int INTRO = 0; 

    private String PLAYER_IMG = "user.gif";    // specify user image file
    private String SPLASH_IMG = "ink.png";
    private String GET_IMG = "get.gif";
    private String AVOID_IMG = "avoid.gif";

    // ADD others for Avoid/Get items 
    
    
    // default number of vertical/horizontal cells: height/width of grid
    private static final int DEFAULT_GRID_H = 5;
    private static final int DEFAULT_GRID_W = 10;
    
    private static final int DEFAULT_TIMER_DELAY = 100;
    
    // default location of user at start
    private static final int DEFAULT_PLAYER_ROW = 0;
    
    protected static final int STARTING_FACTOR = 3;      // you might change that this when working on timing (Actually decided to change timerDelay instead)
    
    protected int factor = STARTING_FACTOR;
    
    protected Location player;
    protected String[][] assets; //Faster than creating filling the 2d array with locations.
    
    protected int score;
    protected int lives = 10; //Looks better than counting "hits" because the player will understand they can only take 10 hits.
    
    protected int screen = INTRO;
    
    protected GameGrid grid;
    
    protected boolean paused = false;
    
    protected int gridH = DEFAULT_GRID_H;
	protected int gridW = DEFAULT_GRID_W;    
    
    public BaseGame() {
        this(DEFAULT_GRID_H, DEFAULT_GRID_W);
    }
    
    public BaseGame(int grid_h, int grid_w){
         this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }

    
    
    public BaseGame(int hdim, int wdim, int init_delay_ms) {
        super(init_delay_ms);
        //set up our "board" (i.e., game grid) 
      	grid = new GameGrid(hdim, wdim); 
        gridH = hdim;
        gridW = wdim;
        assets = new String[gridH][gridW];
    }
    
    /******************** Methods **********************/
    
    protected void initGame(){
         
         // store and initialize user position
         player = new Location(DEFAULT_PLAYER_ROW, 0);
         
         grid.setCellImage(player, PLAYER_IMG);
         
         updateTitle();                           
    }
    
        
    // Display the intro screen: not too interesting at the moment
    // Notice the similarity with the while structure in play()
    // sleep is required to not consume all the CPU; going too fast freezes app 
    protected void displayIntro(){
     
       grid.setGridBackground(SPLASH_IMG);
       while (screen == INTRO) {
          super.sleep(timerDelay);
          // Listen to keep press to break out of intro 
          // in particular here --> space bar necessary
          handleKeyPress();
       }
       grid.setGridBackground(null);
    }
  
    protected void updateGameLoop() {
    	
        handleKeyPress();        // update state based on user key press
        handleMouseClick();      // when the game is running: 
        // click & read the console output 
        
		if(paused) //Used to pause/resume game
    		return;
        
        if (turnsElapsed % factor == 0) {  // if it's the FACTOR timer tick
            // constant 3 initially
            scrollLeft();           
            populateRightEdge();
        }     
        updateTitle();
        
    }
    
    
    
    // update game state to reflect adding in new cells in the right-most column
    private void populateRightEdge() {
    	int randInt = rand.nextInt(5); //Gets random integer (using Random from abstract game) from 0-4, 0 = avoid, 1 = get, 2-4 = empty.
    	int currentIndex = 0; //Current index of assets
    	int avoidCounter = 0; //Avoid counter resets each time the function is called, so no need to reset it at end of loop
    	for(int i = 0; i<gridH;i++){
			if(randInt == 0 && avoidCounter<gridH){ //Making sure there is at least one space that is not 'avoid'
				avoidCounter += 1;
				assets[i][gridW-1] = AVOID_IMG;
				if(!player.equals(new Location(i, gridW-1)))
					grid.setCellImage(new Location(i, gridW-1), AVOID_IMG); //Using name of img directly rather than accessing the array like in scrollLeft
			}
			else if(randInt == 1){
				assets[i][gridW-1] = GET_IMG;
				if(!player.equals(new Location(i, gridW-1)))
					grid.setCellImage(new Location(i, gridW-1), GET_IMG);
			}
			else{
				assets[i][gridW-1] = null;
				if(!player.equals(new Location(i, gridW-1)))
					grid.setCellImage(new Location(i, gridW-1), null);
			}
			randInt = rand.nextInt(5); //Updates random int
		}
    }
    

    
    // updates the game state to reflect scrolling left by one column
    private void scrollLeft() {
        for(int i = 0; i<gridW-1; i++){
        	for(int j = 0; j<gridH;j++){
        		if(!player.equals(new Location(j, i))&&!player.equals(new Location(j, i+1))){ //To not override the cell where the player is currently at.
					if(assets[j][i+1] != null){
					 //Takes value from the next row and moves it to the left
						assets[j][i] = assets[j][i+1];
						assets[j][i+1] = null; //Cleaning the next column to avoid duplicates
						grid.setCellImage(new Location(j, i), assets[j][i]);
					}
					else{
						assets[j][i] = null;
						grid.setCellImage(new Location(j, i), assets[j][i]);
					}
				}
				else{
					handleCollision(); //This type of collition consists in an asset moving into the players space while scrolling left
					if(player.getCol()>0){
						Location adjacent = new Location(player.getRow(), player.getCol() - 1);
						grid.setCellImage(adjacent,null); //Making sure the image to the left of the player is erased after collition is handled
					}
				}
			}
			
        
        }
    }
    
    
    /* handleCollision()
     * handle a collision between the user and an object in the game
     */    
    private void handleCollision() {
		int row = player.getRow();
    	int col = player.getCol();
    	if(assets[row][col+1] != null){
			if(assets[row][col+1].equals(GET_IMG)){
				score += 10;
				assets[row][col+1] = null;	
			}
			else if(assets[row][col+1].equals(AVOID_IMG)){
				lives -= 1;
				assets[row][col+1] = null;	
			}
    	}
    	//The collisions are evaluated at the time of scrolling left, and the evaluation is by looking at the object at the
    	//right of the player at the moment of the scrolling, and at the current position of the player.
    	/*if(assets[row][col] != null){
			if(assets[row][col].equals(GET_IMG)){
				score += 10;
				assets[row][col] = null;
			}
			else if(assets[row][col].equals(AVOID_IMG)){
				lives -= 1;
				assets[row][col] = null;	
			}
    	}*/
		

    }
    
    private void handleCollision2() { //To not mix up the collisions that result in a asset moving into the space vs the player moving into the space of an asset
		int row = player.getRow();
    	int col = player.getCol(); 
    	if(assets[row][col] != null){
			if(assets[row][col].equals(GET_IMG)){
				score += 10;
				assets[row][col] = null;
			}
			else if(assets[row][col].equals(AVOID_IMG)){
				lives -= 1;
				assets[row][col] = null;	
			}
    	}
    	
    }
    //---------------------------------------------------//
    
    // handles actions upon mouse click in game
    private void handleMouseClick() {
        
        Location loc = grid.checkLastLocationClicked();
        
        if (loc != null) 
            System.out.println("You clicked on a square " + loc);
        
    }
    
    // handles actions upon key press in game
    protected void handleKeyPress() {
        
        int key = grid.checkLastKeyPressed();
        
        //use Java constant names for key presses
        //http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
        
        // Q for quit
        if (key == KeyEvent.VK_Q)
            System.exit(0);
        
        if (key == KeyEvent.VK_P)
            paused = !paused;
        
        if(paused)
        	return; //really funny result if this is not added here, cheat modes: on
        
        else if (key == KeyEvent.VK_S)
            grid.save("screenshot.jpg");
        
        else if (key == KeyEvent.VK_D){
        	if(grid.getLineColor() == null)
        		grid.setLineColor(Color.GREEN);
        	else
        		grid.setLineColor(null);
        }
        
         else if (key == KeyEvent.VK_COMMA) //Slow down
            timerDelay += 10;
      
         else if (key == KeyEvent.VK_PERIOD && timerDelay > 10) //Speed up, not allowing it to set the delay to 0 for instant game over.
         	 timerDelay -= 10; //Considered adding a limit, but not required so left it out for now (until creative part)
        
        else if (key == KeyEvent.VK_SPACE)
           screen += 1;
        
       else if (key == KeyEvent.VK_UP){
       	   if(player.getRow() > 0){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow() - 1, player.getCol());	   
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
	  else if (key == KeyEvent.VK_DOWN){
       	   if(player.getRow() < gridH-1){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow() + 1, player.getCol());	 
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
	  else if (key == KeyEvent.VK_LEFT){
       	   if(player.getCol() > 0){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow(), player.getCol() - 1);
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
   	  else if (key == KeyEvent.VK_RIGHT){
       	   if(player.getCol() < gridW-1){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow(), player.getCol() + 1);
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
       	   	
       	

        /* To help you with step 9: 
         use the 'T' key to help you with implementing speed up/slow down/pause
         this prints out a debugging message */
        else if (key == KeyEvent.VK_T)  {
            boolean interval =  (turnsElapsed % factor == 0);
            System.out.println("timerDelay " + timerDelay + " msElapsed reset " + 
                               msElapsed + " interval " + interval);
        } 
    }
    
    // return the "score" of the game 
    private String getScore() {
        return "Score: " + score + " Lives: " + lives;    //dummy for now
    }
    
    // update the title bar of the game window 
    private void updateTitle() {
        grid.setTitle("Scrolling Game:  " + getScore());
    }
    
    // return true if the game is finished, false otherwise
    //      used by play() to terminate the main game loop 
    protected boolean isGameOver() {
    	if(score>=100||lives<=0){ //If you go quickly to the right and get two GETs the score can go over 100
    		displayOutcome(); //Just changes the title for the base game. (for now).
    		return true;
    	}
        return false;
    }

    
    // display the game over screen, blank for now
    protected void displayOutcome() {
		 if(score >= 100){
        	grid.setTitle("You Win! You've achieved a score of: " + score + "!");
        }
        else if(lives <= 0){
        	grid.setTitle("You lose! Your score was: " + score + "!");
        }
        
    }
}
