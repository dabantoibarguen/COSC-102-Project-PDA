****************Pistola de Agua****************

----Theme----
Pistola de agua means Water Gun in spanish.

This creative version of the game consists in the story of a young kid who lives in the
town of Malafortunado. His home is being threatened by a ferocious beast that makes whoever
makes contact with it combust into flames. Equipped only with his water gun, the kid wants
to help out his hometown by dousing the flames of those who have been affected by the beast
Salamander. However, as many kids, he wants to be appreciated for his act of heroism, and
personally shake the hands of every person he saves. 

His water gun is very effective, however it runs on ammunition, and must be recharged in
order to keep it working. Since he is going on the road to save the people escaping from
the Salamander, the irony of finding water bottles on the ground (litter) and using it to
recharge his water gun is pretty funny if I do say so myself. The monster is included in
the game, if the kid runs into it, he must fight it with all his might (in slow motion,
because that makes it cooler).


************IMPORTANT************
To run the game please use my uploaded version of GameGrid.java, since I edited the 
setGridBackground() function to make my background displayable. 

The CreativeGame class extends AbstractGame as it tweaks some of the fundamental functions 
(including handleCollision(), scrollLeft(), handleKeyPress(), among others).


----Cuztomizations----
Mouse Clicks: Unlike the demo where the only important actions the user can take are
moving through the grid to avoid or get items, I included the possibility of clicking
the normally harmful ones. This turns AVOIDs into GETs in order to increase the score.
Uses a helper function "handleClick()" called by handleMouseClick().

Water gun (Ammunition): Complementing the clicking functionality, I added a limit to
how many times the player can use the "water gun" to make the burning citizens into
saved ones. This is controlled by an "ammunition" counter, which works similar to the
score since it increases by touching the water bottle item with the player character,
and decreases each time a shot is attempted, whether it hits or misses.

Shoot ahead: Another small complement of the Mouse Clicks is the fact that the player
is not allowed to shoot at items to its left or directly above/below it. This is to
simulate a "facing" feeling, since they can only shoot ahead with their gun as they
advance through the road (no turning around allowed to increase challenge).

*These three are the ones I'm the most proud of since they are part of a single 
functionality.

Boss Fight: Added a special type of "AVOID" item to the game, a boss. This particular
enemy appears with a very low chance (1 in 225), and will only appear once (if at all) 
in a single sitting of the game. When it is generated the timerDelay is set to a fixed
high value to slow down the game, for a dramatic moment and to allow the player a 
fair fight (it takes 5 hits to beat it after all). It provides 70 points rather
than the usual 10 for saving and shaking someone's hand (running into them). 

WASD keys: Changed the movement from arrow keys to the classic WASD to facilitate
click-move coordination. Moved special buttons (specified in instruction transcript).


----Instruction transcript (as seen in second screen)----

-Move the hero pistola de agua using the WASD keys
W – Up / A – Left / S – Down / D  - Right

-You have 5 lives, touching someone on fire will reduce your total by 1, 
if you hit 0 you lose.

-Use your gun by clicking on the people on fire to extinguish it 
(use your mouse or touch pad). 

-Touch the saved people to shake their hand and get acknowledged for your feat
(+10 points out of 150).

-You cannot use your gun on anyone you cannot see (they must be to the right of
 the character on the screen at the moment you click them).

-You need ammunition (water) to save people, each click spends 1 but you can 
collect water bottles to earn more. 

-There is a small chance the boss      appears, best of luck if it does, 
shoot it until it goes away. If you lose if it gets past you.

-Additional commands: . – speed up / , - slow down / t – take a screenshot
g – display grid lines / p – pause the game / l – quit the game. 


----Losing/Winning conditions----
Win: Score at least 150 points (10 from shooting then touching the people, 70 from 
defeating the boss if it appears).

Lose 1: Lose your 5 lives after running into people on fire.

Lose 2: Be touched by the boss

Lose 3: Let the boss reach the leftmost space (since it makes it 
impossible to shoot it from there)


Image credits:
http://pixelartmaker.com/art/3935c5c3d3cbb07
https://giphy.com/explore/self-immolation
http://pixelartmaker.com/art/1ae4a8cfe9d503c
https://gfycat.com/stickers/search/water+gun
http://pixelartmaker.com/art/6f35b7c2bb212a5
https://www.pixilart.com/search?term=dragon