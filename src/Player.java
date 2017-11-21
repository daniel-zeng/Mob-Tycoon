public class Player extends Char {
	boolean justTeleported = false;
	public Player(int x, int y, int tileSize, MBController c){
		super(x, y, 0, "player", tileSize, true, c);
		int baseSpeed = 10;
		setSpeed(baseSpeed);
		
		String[] strs = {"player_back.png", "player_left.png", "player_front.png", "player_right.png"};
		setFileNameMult(strs);
		constructImage();
	}
	
	int timeOut = 0;
	
	public void move(boolean[] b) {
		int newY = getY();
		int newX = getX();
		int oldY = newY;
		int oldX = newX;

		if (b[UP]) {
			setDir(UP);
			newY -= getSpeed();
		}
		if (b[LEFT]) {
			setDir(LEFT);
			newX -= getSpeed();
		}
		if (b[DOWN]) {
			setDir(DOWN);
			newY += getSpeed();
		}
		if (b[RIGHT]) {
			setDir(RIGHT);
			newX += getSpeed();
		}

		if(control.getMode() == 1){
			if (isMoveable(newX, newY) && !justTeleported) {
				setPos(newX, newY);
			} else if (isMoveable(newX, oldY) && !justTeleported) {
				setPos(newX, oldY);
			} else if (isMoveable(oldX, newY) && !justTeleported) {
				setPos(oldX, newY);
			}
			justTeleported = false;
		}

	}
	public void takeDamage(int dmg){
		if(timeOut == 0){
			changeHealth(dmg);
			timeOut = 30;
		}
	}

	public double hpPercent(){
		return ((double)health)/maxHP;
	}
	public double dmgMult() {
		return dmgMult/100 + 1;
	}
	public void setJustTeleported(boolean justTeleported) {
		this.justTeleported = justTeleported;
	}
	public void decTimeOut() {
		if(timeOut > 0){
			timeOut--;
		}
	}

}
