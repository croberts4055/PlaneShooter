import java.applet.Applet;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;

@SuppressWarnings({ "serial", "deprecation" })
public class Game extends Applet implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	// Name of game on top of Applet.
	public static final String TITLE = "Plane Shooter";

	// makes a 16:9 aspect ratio which is common on most screens. Since 2010 it has become the most common aspect ratio for 
	// televisions and computer monitors. All background images are scaled to 1024/576 ratio.

	public static final int WIDTH = 1024;
	public static final int HEIGHT = WIDTH / 16 * 9; 

	// Buffer for double buffering.
	Image    off_screen;
	Graphics off_g;

	// Instance of the Player.
	Player plane = new Player(100, HEIGHT/2);

	// Game scorea
	int score = 0;

	// Container for bullets. Player.getBullets returns bullets in the player class.
	LinkedList<?> bullets = Player.getBullets();

	// Containers for the Boxes that will appear on screen
	LinkedList<NumberedRect> boxes1 = new LinkedList<NumberedRect>();
	LinkedList<NumberedRect> boxes2 = new LinkedList<NumberedRect>();
	LinkedList<NumberedRect> boxes3 = new LinkedList<NumberedRect>();
	LinkedList<NumberedRect> boxes4 = new LinkedList<NumberedRect>();
	LinkedList<NumberedRect> boxes5 = new LinkedList<NumberedRect>();
	LinkedList<NumberedRect> boxes6 = new LinkedList<NumberedRect>();

	// Loading in images of the Sun and pause screen.
	Image sun = Toolkit.getDefaultToolkit().getImage("img/rotating_sun.gif");
	//	Image game_pause = Toolkit.getDefaultToolkit().getImage("img/game_paused.jpg"); Couldn't get this to work.

	// Background images for parallax scrolling.
	ImageLayer sky = new ImageLayer("img/sky.png", 0, 0, 100);
	ImageLayer rocks1 = new ImageLayer("img/rocks_1.png", 0, 0, 100);
	ImageLayer rocks2 = new ImageLayer("img/rocks_2.png", 0, 0, 100);
	ImageLayer rocks3 = new ImageLayer("img/rocks_3.png", 0, 0, 100);
	ImageLayer clouds1 = new ImageLayer("img/clouds_1.png", 0, 0, 100);
	ImageLayer clouds2 = new ImageLayer("img/clouds_2.png", 0, 0, 100);
	ImageLayer clouds3 = new ImageLayer("img/clouds_3.png", 0, 0, 100);
	ImageLayer pines = new ImageLayer("img/pines.png", 0, 0, 100);
	ImageLayer birds = new ImageLayer("img/birds.png", 0, 0, 100);

	// setting booleans for buttons
	boolean sp_pressed = false;
	boolean lt_Pressed = false;
	boolean rt_Pressed = false;
	boolean up_Pressed = false;
	boolean dn_Pressed = false;

	// Collisions for Game border. 
	Rect leftWall = new Rect(0,0, 0, HEIGHT);
	Rect rightWall = new Rect(WIDTH/2 + WIDTH/4, 0, WIDTH/2, HEIGHT);
	Rect topWall = new Rect(0, 0, WIDTH, 0);
	Rect bottomWall = new Rect(0,HEIGHT - 1,WIDTH, 0);

	// Booleans for Game running and Game Paused.
	private boolean GAME_ON = true;			
	private boolean isPaused = false;

	// Time between each bullet fired to prevent bullets looking like a laser when holding space button.
	// For some reason, I couldn't get the Timer class to work so this is a workaround. Courtesy of StackOverflow.
	private static int bulletDelay = 500;
	private static int time = 0;

	// Font for remaining ammo
	static Font bulletsLeft = new Font("Helvetica", Font.BOLD, 24);
	static Font scoreBoard = new Font("Arial", Font.BOLD, 24);
	static Font gameOverScreen = new Font("Arial", Font.BOLD, 90);


	// Game Sound
	Sound bulletSound = new Sound("sfx/bulletSound.wav");
	Sound boxHit = new Sound("sfx/boxHit.wav");
	Sound gameOver = new Sound("sfx/gameOver.wav");
	Sound playerCrash = new Sound("sfx/playerCrash.wav");

	@Override
	//Things in the init are called exactly once in an Applet's life, when the Applet is first loaded.
	public void init() {				
		setSize(WIDTH, HEIGHT);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

		// Allows me to change the name on top of the Applet since applet is not a JFrame. Courtesy of StackOverflow
		Frame frame = (Frame)this.getParent().getParent();		
		frame.setTitle(TITLE);

		// Each Linked list is filled with 600 boxes.
		for (int i = 0; i < 1000; i++)
			boxes1.add( new NumberedRect(WIDTH,0, 95, 95));
		for (int i = 0; i < 1000; i++)
			boxes2.add( new NumberedRect(WIDTH,96, 95, 95));
		for (int i = 0; i < 1000; i++)
			boxes3.add( new NumberedRect(WIDTH,192, 95, 95));
		for (int i = 0; i < 1000; i++)
			boxes4.add( new NumberedRect(WIDTH,288, 95, 95));
		for (int i = 0; i < 1000; i++)
			boxes5.add( new NumberedRect(WIDTH,384, 95, 95));
		for (int i = 0; i < 1000; i++)
			boxes6.add( new NumberedRect(WIDTH,480, 95, 95));

	}

	@Override
	// Things here are called at least once in an applet's life, when the applet is started or restarted.
	public void start() { 
		Thread thread = new Thread(this);
		thread.start();
	}

	// Where the double buffering offscreen magic takes place.
	public void update(Graphics g) { 		
		if(off_screen == null) {
			off_screen = createImage(WIDTH, HEIGHT); 
			off_g = off_screen.getGraphics();	
		}

		off_g.clearRect(0, 0, WIDTH, HEIGHT);
		paint (off_g);
		g.drawImage(off_screen, 0, 0, this);


	}

	// Function that checks Collisons using Java's built in Rectangle class. 
	// The Player, Bullet, and Numbered Rectangles each have a getBounds method that 
	// returns its (x,y,w,h) in the form of a Rectangle. Essentially the same as the overlaps function but much cleaner.

	public void checkCollisons() {

		//		Rectangle b1Bound = boxes1.getFirst().getBounds();
		//		Rectangle b2Bound = boxes2.getFirst().getBounds();
		//		Rectangle b3Bound = boxes3.getFirst().getBounds();
		//		Rectangle b4Bound = boxes4.getFirst().getBounds();
		//		Rectangle b5Bound = boxes5.getFirst().getBounds();
		//		Rectangle b6Bound = boxes6.getFirst().getBounds();
		Rectangle playerBound = plane.getBounds();


		// Collisions involving the bullets and the boxes.
		for (int i = 0; i < bullets.size(); i++)
		{
			Bullet round = (Bullet) bullets.get(i);			// The bullets.
			Rectangle bulletBound	 = round.getBounds();	// The boundary of the bullets.

			NumberedRect b1 =  boxes1.getFirst();			// The numbered rectangles.
			Rectangle r1Bound = b1.getBounds();				// The boundary of the box targets.

			NumberedRect b2 =  boxes2.getFirst();
			Rectangle r2Bound= b2.getBounds();

			NumberedRect b3 =  boxes3.getFirst();
			Rectangle r3Bound = b3.getBounds();

			NumberedRect b4 =  boxes4.getFirst();
			Rectangle r4Bound = b4.getBounds();

			NumberedRect b5 =  boxes5.getFirst();
			Rectangle r5Bound = b5.getBounds();

			NumberedRect b6 =  boxes6.getFirst();
			Rectangle r6Bound = b6.getBounds();

			if (r1Bound.intersects(bulletBound))			// If the Rectangle surrounding the box (r1) touches the rectangle surrounding the 
				// bullet (bulletBound) then:
			{
				if (b1.isAlive()) {						// If the box is alive 
					boxHit.play();						// Play the sound of the box being hit.
					b1.boxCount--;						// Lower the box's count by 1.
					score+=10;							// Increase the player's score by 10.
					round.visible = false;				// The bullet is set to invisible on impact with the box
				}
				else	{b1.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

			if (r2Bound.intersects(bulletBound))
			{
				if (b2.isAlive()) {
					boxHit.play();
					b2.boxCount--;
					score+=10;
					round.visible = false;
				}
				else	{b2.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

			if (r3Bound.intersects(bulletBound))
			{
				if (b3.isAlive()) {
					boxHit.play();
					b3.boxCount--;
					score+=10;
					round.visible = false;
				}
				else	{b3.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

			if (r4Bound.intersects(bulletBound))
			{
				if (b4.isAlive()) {
					boxHit.play();
					b4.boxCount--;
					score+=10;
					round.visible = false;
				}
				else	{b4.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

			if (r5Bound.intersects(bulletBound))
			{
				if (b5.isAlive()) {
					boxHit.play();
					b5.boxCount--;
					score+=10;
					round.visible = false;
				}
				else	{b5.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

			if (r6Bound.intersects(bulletBound))
			{
				if (b6.isAlive()) {
					boxHit.play();
					b6.boxCount--;
					score+=10;
					round.visible = false;
				}
				else	{b6.alive = false;
				System.out.println("Box is dead, turn invisible. Bullets can pass through invisible box.");}	// Box is dead.
			}

		}

		// Collisions involving the Player and the Boxes.
		for (int i = 0; i < boxes1.size(); i++)
		{
			NumberedRect b1 = boxes1.getFirst();
			Rectangle r1Bound = b1.getBounds();

			NumberedRect b2 = boxes2.getFirst();
			Rectangle r2Bound = b2.getBounds();

			NumberedRect b3 = boxes3.getFirst();
			Rectangle r3Bound = b3.getBounds();

			NumberedRect b4 = boxes4.getFirst();
			Rectangle r4Bound = b4.getBounds();

			NumberedRect b5 = boxes5.getFirst();
			Rectangle r5Bound = b5.getBounds();

			NumberedRect b6 = boxes6.getFirst();
			Rectangle r6Bound = b6.getBounds();

			// If the players boundary touches the boundary of any of the boxes AND If the player is not dead (visible):
			if ((playerBound.intersects(r1Bound) && b1.alive == true)|| 
					(playerBound.intersects(r2Bound) && b2.alive== true) ||
					(playerBound.intersects(r3Bound) && b3.alive == true) ||
					(playerBound.intersects(r4Bound) && b4.alive == true) ||
					(playerBound.intersects(r5Bound) && b5.alive == true) ||
					(playerBound.intersects(r6Bound) && b6.alive == true))
			{
				plane.isAlive = false;			// The player is dead so turn plane invisible.
				GAME_ON = false;					// Game is over.

			}

		}



	}

	@Override
	public void run() {

		while (true) {


			if (!GAME_ON)
			{
				playerCrash.play();
				gameOver.play();		// Play Game over sound.
				System.out.println("YOU ARE DEAD! GAME OVER!!!!!!!!");
				return;

			}

			// Destroying the boxes (removing them from the LinkedList) once they pass the left Game Border.

			// Boxes1
			for (int i = 0; i < boxes1.size(); i++)
			{

				if (boxes1.getFirst().x + 95 < leftWall.x) {
					boxes1.remove(boxes1.getFirst());		// Remove the first box in the list. The next box now becomes the first one.
					if (!boxes1.isEmpty()) {					// If the list is still not empty:
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes1.size() + " boxes left in the first Box's List");
						boxes1.get(i).x = WIDTH;				// Set the next box's x value in the list to the Width of the screen.
						boxes1.get(i).y = 0;					// // Set the next box's y value in the list to 0;
					}
				}
			}

			// Boxes2
			for (int i = 0; i < boxes2.size(); i++)
			{

				if (boxes2.getFirst().x + 95 < leftWall.x) {
					boxes2.remove(boxes2.getFirst());
					if (!boxes2.isEmpty()) {
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes2.size() + " boxes left in the second Box's List");

						boxes2.get(i).x = WIDTH;
						boxes2.get(i).y = 96;
					}
				}
			}
			// Boxes3
			for (int i = 0; i < boxes3.size(); i++)
			{

				if (boxes3.getFirst().x + 95 < leftWall.x) {
					boxes3.remove(boxes3.getFirst());
					if (!boxes3.isEmpty()) {
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes3.size() + " boxes left in the third Box's List");
						boxes3.get(i).x = WIDTH;
						boxes3.get(i).y = 192;
					}
				}
			}

			// Boxes4
			for (int i = 0; i < boxes4.size(); i++)
			{

				if (boxes4.getFirst().x + 95 < leftWall.x) {
					boxes4.remove(boxes4.getFirst());
					if (!boxes4.isEmpty()) {
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes4.size() + " boxes left in the fourth Box's List");


						boxes4.get(i).x = WIDTH;
						boxes4.get(i).y = 288;
					}

				}
			}

			// Boxes5
			for (int i = 0; i < boxes5.size(); i++)
			{

				if (boxes5.getFirst().x + 95 < leftWall.x) {
					boxes5.remove(boxes5.getFirst());
					if (!boxes5.isEmpty()) {
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes5.size() + " boxes left in the fifth Box's List");
						boxes5.get(i).x = WIDTH;
						boxes5.get(i).y = 384;
					}
				}
			}

			// Boxes6
			for (int i = 0; i < boxes6.size(); i++)
			{

				if (boxes6.getFirst().x + 95 < leftWall.x) {
					boxes6.remove(boxes6.getFirst());
					if (!boxes6.isEmpty()) {
						System.out.println("Box " + (i + 1) + " removed from List. There are " + boxes6.size() + " boxes left in the sixth Box's List");
						boxes6.get(i).x = WIDTH;
						boxes6.get(i).y = 480;
					}
				}
			}


			// Moving the boxes on screen to the left.
			for (NumberedRect nr : boxes1) {nr.moveLeftBy(2);	}
			for (NumberedRect nr : boxes2) {nr.moveLeftBy(2);	}
			for (NumberedRect nr : boxes3) {nr.moveLeftBy(2);	}
			for (NumberedRect nr : boxes4) {nr.moveLeftBy(2);	}
			for (NumberedRect nr : boxes5) {nr.moveLeftBy(2);	}
			for (NumberedRect nr : boxes6) {nr.moveLeftBy(2);	}



			// Background speed for each image in the Image Layer.
			rocks1.moveLeftBy(160 * 3);
			rocks2.moveLeftBy(40 * 3);
			rocks3.moveLeftBy(40 * 3);
			clouds1.moveLeftBy(160 * 3);
			clouds2.moveLeftBy(40 * 3);
			clouds3.moveLeftBy(40 * 3);	
			sky.moveLeftBy(20);
			pines.moveLeftBy(200 * 3);
			birds.moveLeftBy(90 * 3);

			// Calling the checkCollisons function while the game is running.
			checkCollisons();

			if(lt_Pressed)
			{
				// No longer needed because player only moves up and down.
				//				plane.moveLeftBy(7);
				//				if(plane.overlaps(leftWall)) {
				//					plane.x = 0;
				//				}
			}
			if(rt_Pressed)
			{
				// No longer needed because Player only moves up and down.
				//				plane.moveRightBy(7);
				//				if(plane.overlaps(rightWall)) {
				//					plane.x = WIDTH/2 + 145;
				//				}
			}
			if(up_Pressed)
			{
				plane.moveUpBy(10);
				if(plane.overlaps(topWall)) {	// If the Player touches the top wall border, set it's y value to 0.
					plane.y = 0;
				}
			}
			if(dn_Pressed)
			{
				plane.moveDownBy(10);
				if(plane.overlaps(bottomWall)) {	// If the Player touches the bottom wall border, set it's y value to HEIGHT -73.
					plane.y = HEIGHT - 73;
				}
			}

			if(sp_pressed)
			{ 

				time -= 100;
				if (time <= 0) {
					plane.shoot();			// Shoot a bullet.
					bulletSound.play();  // Play the sound of the bullet.
					time = bulletDelay;  // Reset the timer

					// Time between each bullet fired to prevent bullets looking like a laser when holding space button.
					// For some reason, I couldn't get the Timer class to work so this is a workaround. Courtesy of StackOverflow.
				}
			}


			// When the bullets are drawn in the paint function, this for loop moves them.
			for (int i = 0; i < bullets.size(); i++)
			{
				Bullet round = (Bullet) bullets.get(i);
				if(round.getVisible() == true) 		// If the bullet is visible 
					round.move();					// move the bullet.

				else {
					bullets.remove(i);				// If the bullet did hit something, remove it from the list.
					System.out.println("Bullet removed from list. Next Bullet is loaded");
				}
			}

			repaint();								// Repaint the screen.

			try
			{
				Thread.sleep(15);
			}

			catch(Exception e){
				e.printStackTrace();
			}

		}

	}

	public void paint (Graphics g) {

		// Draws Background images in an order that makes them appealing to the eye.
		sky.draw(g);
		g.drawImage(sun, 0, 0,null); 
		clouds3.draw(g);
		clouds2.draw(g);
		clouds1.draw(g);
		rocks3.draw(g);
		rocks2.draw(g);
		rocks1.draw(g);
		pines.draw(g);
		birds.draw(g);

		for (int i = 0; i < bullets.size(); i++)
		{
			Bullet round = (Bullet) bullets.get(i);
			g.drawImage(round.getImage(), round.getX(), round.getY(), null);
			// This draws the bullet at the barrel of the Player's gun.
		}

		// Keep drawing boxes as long as the LinkedLists have boxes in them.
		if (!boxes1.isEmpty()) {	boxes1.getFirst().draw(g);}
		if (!boxes2.isEmpty()) {	boxes2.getFirst().draw(g);}
		if (!boxes3.isEmpty()) {	boxes3.getFirst().draw(g);}
		if (!boxes4.isEmpty()) {	boxes4.getFirst().draw(g);}
		if (!boxes5.isEmpty()) {	boxes5.getFirst().draw(g);}
		if (!boxes6.isEmpty()) {	boxes6.getFirst().draw(g);}

		plane.draw(g);			// Draw Player.

		g.setColor(Color.RED);
		//				leftWall.draw(g);
		//				rightWall.draw(g);
		//				bottomWall.draw(g);
		//				topWall.draw(g);

		// Draws String that shows remaining ammo and score.
		g.setFont(bulletsLeft);
		g.setColor(Color.ORANGE);
		g.drawString("Ammo: " + plane.ammo + "/5000", WIDTH - 200, 20);
		g.setFont(scoreBoard);
		g.setColor(Color.BLUE);
		g.drawString(" Score: " + score, WIDTH - 200, 40);


		if (!GAME_ON)
		{
			g.setFont(gameOverScreen);
			g.setColor(Color.RED);
			g.drawString("GAME OVER", 288, HEIGHT/2);


		}


	}

	public void keyPressed(KeyEvent e)
	{
		int code = e.getKeyCode();

		if(code == KeyEvent.VK_LEFT)     {lt_Pressed = true;}
		if(code == KeyEvent.VK_RIGHT)    {rt_Pressed = true;}
		if(code == KeyEvent.VK_UP)       {up_Pressed = true;}
		if(code == KeyEvent.VK_DOWN)     {dn_Pressed = true;}
		if(code == KeyEvent.VK_SPACE)  	 {sp_pressed = true;}
	}

	public void keyReleased(KeyEvent e)
	{
		int code = e.getKeyCode();

		if(code == KeyEvent.VK_LEFT)     {lt_Pressed = false;}
		if(code == KeyEvent.VK_RIGHT)    {rt_Pressed = false;}
		if(code == KeyEvent.VK_UP)       {up_Pressed = false;}
		if(code == KeyEvent.VK_DOWN)     {dn_Pressed = false;}
		if(code == KeyEvent.VK_SPACE)  	 {sp_pressed = false;}
	}


	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	} 

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
