import java.util.ArrayList;

public class Char extends Entity {

	int maxHP = 100;
	int health = maxHP;
	int hpInc = 20;
	double regen = 0;
	double dmgMult = 0;

	int cash = 0;
	private ArrayList<Item> items;

	int shootSpeed = 11;

	int[] levels = new int[] { 1, 1, 1, 1, 1 };

	int moveSpeedInc = 1;

	public static final int HP = 0;
	public static final int SHOOT = 1;
	public static final int REGEN = 2;
	public static final int DMG = 3;
	public static final int MOVE = 4;

	public Char(int x, int y, int speed, String type, int tileSize, boolean hasFourImages, MBController c) {
		super(speed, type, tileSize, hasFourImages, c);
		setPos(x, y);
	}

	public void upgradeStat(int stat) {
		switch (stat) {
		case HP:
			levels[stat]++;
			maxHP += hpInc;
			health = maxHP;
			break;
		case SHOOT:
			shootSpeed--;
			levels[stat]++;
			break;
		case REGEN:
			regen += 2;
			levels[stat]++;
			break;
		case DMG:
			levels[stat]++;
			dmgMult += 10;
			break;
		case MOVE:
			levels[stat]++;
			setSpeed(getSpeed() + moveSpeedInc);
			break;

		}

	}

	public int getLevel(int stat) {
		if (stat < 0 || stat >= levels.length) {
			System.out.println("error");
			return -1;
		} else {
			return levels[stat];
		}

	}

	public boolean validShootUp() {
		return shootSpeed > 1;
	}

	public boolean validRegenUp() {
		return regen < 50;
	}

	public void regen() {
		health += maxHP * (regen / 100);
		health = Math.min(health, maxHP);
	}


	public boolean isMoveable(int x, int y) {
		updateTileBounds(x, y);
		int c_base = (int) Math.floor((tile_bounds.getX()) / tileSize);
		int c_top = (int) Math.floor((tile_bounds.getX() + tile_bounds.getWidth()) / tileSize);
		int r_base = (int) Math.floor((tile_bounds.getY()) / tileSize);
		int r_top = (int) Math.floor((tile_bounds.getY() + tile_bounds.getHeight()) / tileSize);

		if (c_top < control.getCols() && c_base >= 0 && r_top < control.getRows() && r_base >= 0) {
		} else {
			return false;
		}
		boolean checkDoor = type.equals("player");

		return control.isWalk(r_base, c_base, checkDoor) && control.isWalk(r_base, c_top, checkDoor)
				&& control.isWalk(r_top, c_base, checkDoor) && control.isWalk(r_top, c_top, checkDoor);
	}


	@Override
	public void setPos(int x, int y) {
		super.setPos(x, y);
		updateCollisionBox(x, y);
		updateTileBounds(x, y);

	}

	public void updateCollisionBox(int x, int y) {
		double xStart = 0.25;
		double xEnd = 0.75;
		double yStart = 0;
		double yEnd = 1;
		double nx = ((double) x) + (double) (tileSize) * xStart;
		double ny = ((double) y) + (double) (tileSize) * yStart;
		double dx = (double) (tileSize) * (xEnd - xStart);
		double dy = (double) (tileSize) * (yEnd - yStart);
		collision.setRect(nx, ny, dx, dy);
	}

	public void updateTileBounds(int x, int y) {
		double xStart = .3;
		double xEnd = 0.7;
		double yStart = .5;
		double yEnd = .8;
		double nx = ((double) x) + (double) (tileSize) * xStart;
		double ny = ((double) y) + (double) (tileSize) * yStart;
		double dx = (double) (tileSize) * (xEnd - xStart);
		double dy = (double) (tileSize) * (yEnd - yStart);
		tile_bounds.setRect(nx, ny, dx, dy);
	}

	public int getShootRate() {
		return shootSpeed;
	}

	public int getMaxHP() {
		return maxHP;
	}

	public void setMaxHP(int maxHP) {
		this.maxHP = maxHP;
	}

	public int getCash() {
		return cash;
	}

	public void changeCash(int cash) {
		this.cash += cash;
	}

	public ArrayList<Item> getItems() {
		return items;
	}

	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
		if (health < 0)
			health = 0;
	}

	public void changeHealth(int c) {
		health += c;
		if (health < 0)
			health = 0;
	}


}
