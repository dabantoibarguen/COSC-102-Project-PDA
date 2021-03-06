import java.awt.event.KeyEvent;
import java.util.*;
import java.awt.Color;

public class CreativeGame extends AbstractGame {
    
    private static final int INTRO = 0; 
    private static final int INSTRUCTIONS = 1;

    private String PLAYER_IMG = "USER.gif";    // specify user image file
    private String SPLASH_IMG = "intro.jpg";
    private String GET_IMG = "AMMO.png";
    private String AVOID_IMG = "ON_FIRE.gif";
    private String SAVED = "SAVED.png";
    private String BOSS = "BOSS.gif";

    // ADD others for Avoid/Get items 
    
    
    // default number of vertical/horizontal cells: height/width of grid
    private static final int DEFAULT_GRID_H = 5;
    private static final int DEFAULT_GRID_W = 10;
    
    private static final int DEFAULT_TIMER_DELAY = 115; //Made the game start off slow, since it's harder now that there are clicks 
    
    // default location of user at start
    private static final int DEFAULT_PLAYER_ROW = 0;
    
    protected static final int STARTING_FACTOR = 3;
    
    protected int factor = STARTING_FACTOR;
    
    protected Location player;
    protected String[][] assets; //Faster than creating filling the 2d array with locations.
    
    protected int score;
    protected int lives = 5; //Looks better than counting "hits" because the player will understand they can only take 5 hits. The number of targets on fire is 
    //significantly less than on the DEMO so 5 lives should be enough.
    private int ammo = 5; //Starting with 5 seems appropriate to get some points until the player can get more ammo
    private int hp = 5; //For the Boss
    private boolean appeared = false;
    
    private boolean started = false;    
    
    protected int screen = INTRO;
    
    protected GameGrid grid;
    
    protected boolean paused = false;
    
    protected int gridH = DEFAULT_GRID_H;
	protected int gridW = DEFAULT_GRID_W;    
    
    public CreativeGame() {
        this(DEFAULT_GRID_H, DEFAULT_GRID_W);
    }
    
    public CreativeGame(int grid_h, int grid_w){
         this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }

    
    
    public CreativeGame(int hdim, int wdim, int init_delay_ms) {
        super(init_delay_ms);
        //set up our "board" (i.e., game grid) 
      	grid = new GameGrid(hdim, wdim, SPLASH_IMG, "background.png");
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
       grid.setGridBackground("inst.jpg");
       while (screen == INSTRUCTIONS) {
          super.sleep(timerDelay);
          handleKeyPress();
       }
	   grid.setGridBackground(null);
	   started = true;
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
    	int randInt = rand.nextInt(16); //Gets random integer (using Random from abstract game) from 0-15, 0 = avoid, 1 = ammo, 2-15 = empty.
    	int prevInt = -1;
    	int currentIndex = 0; //Current index of assets
    	int avoidCounter = 0; //Avoid counter resets each time the function is called, so no need to reset it at end of loop
    	for(int i = 0; i<gridH;i++){
			if(randInt == 0 && prevInt == 13 && !appeared){
				assets[i][gridW-1] = BOSS;
				appeared = true;
				timerDelay = 200; //Dramatic slow down
				if(!player.equals(new Location(i, gridW-1)))
					grid.setCellImage(new Location(i, gridW-1), BOSS);	
			}
    		else if((randInt == 0) && avoidCounter<gridH){ //Making sure there is at least one space that is not 'avoid'
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
			prevInt = randInt;
			randInt = rand.nextInt(16); //Updates random int
		}
    }
    	
    
    // updates the game state to reflect scrolling left by one column
    private void scrollLeft() {
        for(int i = 0; i<gridW-1; i++){
        	for(int j = 0; j<gridH;j++){
        		if(appeared){
        			if(assets[j][0] != null){
        				if(assets[j][0].equals(BOSS))
        					score = -9999;
        			}
        		}
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
    	if(col == gridW - 1){
    		handleCollision2();
    		return;
    	}
    	if(assets[row][col+1] != null){
			if(assets[row][col+1].equals(GET_IMG)){
				ammo += 1; //Increases ammunition rather than score
				assets[row][col+1] = null;	
			}
			else if(assets[row][col+1].equals(AVOID_IMG)){
				lives -= 1;
				assets[row][col+1] = null;	
			}
			else if(assets[row][col+1].equals(SAVED)){
				score += 10;
				assets[row][col+1] = null;	
			}
			else if(assets[row][col+1].equals(BOSS)){
				lives = -9999;
				grid.setCellImage(player, "dead.png");
				assets[row][col+1] = null;	
			}
    	}
    	//The collisions are evaluated at the time of scrolling left, and the evaluation is by looking at the object at the
    	//right of the player at the moment of the scrolling, and at the current position of the player.
    	
    	
		

    }
    
    private void handleCollision2() { //To not mix up the collisions that result in a asset moving into the space vs the player moving into the space of an asset
		int row = player.getRow();
    	int col = player.getCol(); 
    	if(assets[row][col] != null){
			if(assets[row][col].equals(GET_IMG)){
				ammo += 1;
				assets[row][col] = null;
			}
			else if(assets[row][col].equals(AVOID_IMG)){
				lives -= 1;
				assets[row][col] = null;	
			}
			else if(assets[row][col].equals(SAVED)){
				score += 10;
				assets[row][col] = null;	
			}
			else if(assets[row][col].equals(BOSS)){
				lives = -9999;
				grid.setCellImage(player, "dead.png");
				assets[row][col] = null;	
			}
		}
    	
    }
    
    private	void handleClick(Location loc){ //Unique to this version (not in base game)
    	if(paused || !started ) 
    		return;
    	int row = loc.getRow();
    	int col = loc.getCol();
    	int pCol = player.getCol();
    	if(pCol == gridW - 1)
    		return;
    	else if(col > pCol && ammo>0){
    		ammo -= 1;
    	}
    	else
    		return;
		if(assets[row][col] != null){
			if(assets[row][col].equals(AVOID_IMG)){ //Making sure the target is a person on fire and there is ammo left
				grid.setCellImage(new Location(row, col), "splash.png"); //Not the type of splash that is used in the grid
				grid.repaint();
				assets[row][col] = SAVED;	
			}
			else if(assets[row][col].equals(BOSS)){
				hp -= 1;
				if(hp == 0){
					grid.setCellImage(new Location(row, col), "splash.png");
					grid.repaint();
					assets[row][col] = null;
					score += 70; //Boss is dead!
					timerDelay = DEFAULT_TIMER_DELAY;
				}
			}
    	}
    	
    	
    }
    
    //---------------------------------------------------//
    
    // handles actions upon mouse click in game
    private void handleMouseClick() {
        
        Location loc = grid.checkLastLocationClicked();
        
        if (loc != null) 
            handleClick(loc);
                      
    }
    
    // handles actions upon key press in game
    protected void handleKeyPress() {
        
        int key = grid.checkLastKeyPressed();
        
        //use Java constant names for key presses
        //http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
        
        // Q for quit
        if (key == KeyEvent.VK_L)
            System.exit(0);
        
        if (key == KeyEvent.VK_P)
            paused = !paused;
        
        if(paused)
        	return; //really funny result if this is not added here, cheat modes: on
        
        else if (key == KeyEvent.VK_T)
            grid.save("screenshot.jpg");
        
        else if (key == KeyEvent.VK_G){
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
        
        
       else if (key == KeyEvent.VK_W){
       	   if(player.getRow() > 0){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow() - 1, player.getCol());	   
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
	  else if (key == KeyEvent.VK_S){
       	   if(player.getRow() < gridH-1){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow() + 1, player.getCol());	 
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
	  else if (key == KeyEvent.VK_A){
       	   if(player.getCol() > 0){
       	   	   grid.setCellImage(player, null);
       	   	   player = new Location(player.getRow(), player.getCol() - 1);
       	   	   grid.setCellImage(player, PLAYER_IMG);
       	   	   handleCollision2();
       	   }
       }
   	  else if (key == KeyEvent.VK_D){
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
        return "Score: " + score + " Lives: " + lives + " Ammunition: " + ammo;    //dummy for now
    }
    
    // update the title bar of the game window 
    private void updateTitle() {
    	if(started)
    		grid.setTitle("Scrolling Game:  " + getScore());
    	else
    		grid.setTitle("Pistola de Agua");
    }
    
    // return true if the game is finished, false otherwise
    //      used by play() to terminate the main game loop 
    protected boolean isGameOver() {
    	if(score>=150||lives<=0||score==-9999){
    		displayOutcome(); //Just changes the title for the base game. (for now).
    		return true;
    	}
        return false;
    }

    public static void main(String[] args){
}
    
    // display the game over screen, blank for now
    protected void displayOutcome() {
		 if(score >= 150){
        	grid.setTitle("You Win! You've achieved a score of: " + score + "!");
        	if(hp == 0)
        		grid.setTitle("You Win! You've achieved a score of " + score + " and have slain the beast!");
        }
        else if(lives <= 0){
        	grid.setTitle("You lose! Your score was: " + score + "!");
        }
        else if(score == -9999){
        	grid.setTitle("The beast has escaped... no one can stop it now. You lose.");	
        }
        
    }
}
