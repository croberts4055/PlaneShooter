import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.LinkedList;

public class Player extends Sprite{

	static String[] action = {"lt", "rt", "up", "dn", "shoot"};

	static LinkedList<Bullet> bullets;
	boolean isAlive;
	int ammo = 5000; 		// Number of ammo the player has.

	public Player(int x, int y) {
		super(x, y, "img/Fly_", action, 5, 1);

		bullets = new LinkedList<Bullet>();
		moving = true;
		pose = LEFT;	
		isAlive = true;		// Player starts alive when first created.
	}


	
	public static LinkedList<Bullet> getBullets() {
		return bullets;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public Rectangle getBounds() {

		return new Rectangle((int)x,(int)y,111,76);
	}


	public void shoot() {

		if (isAlive == true) {

			if (ammo > 0) 
			{

				moving = true;
				pose = SHOOT;		// Shooting pose is the sprite with a muzzle flash.
				ammo--; 				// Decrease the number bullets the player has by one for every shot. 


				
				Bullet b = new Bullet((int)x + 78, (int)y + 43); // When shoot is called, create a bullet 
				// at the coordinates entered (right where the player's gun is).

				bullets.add(b);									// add that bullet to the LinkedList.
			} 
		}
	}


	public void draw(Graphics g)
	{
		// Only draw the player of they are alive.
		if (isAlive == true) {
			if(moving)
				g.drawImage(animation[pose].nextImage(), (int)x, (int)y, null);
			else
				g.drawImage(animation[pose].stillImage(), (int)x, (int)y, null);

		}


	}



}
