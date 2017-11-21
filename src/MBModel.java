import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class MBModel {
	MBController control = null;
	BackgroundTile[][] map;
	Player p;
	NPC ai_1;

	int rows = 10;
	int cols = 10;
	int tileSize;

	public static final int UP = 0;
	public static final int LEFT = 1;
	public static final int DOWN = 2;
	public static final int RIGHT = 3;

	int mode = 0;
	public static final int TITLE = 0;
	public static final int GAME = 1;
	public static final int MENU = 2;
	public static final int LOADING = 3;

	boolean useWidth;
	int size = 0;

	ArrayList<Char> chars = new ArrayList<Char>();
	ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	ArrayList<Point> walkables = new ArrayList<Point>();

	int bulletBaseDamage = 10;

	Random ran = new Random();

	public MBModel(MBController c, int size, boolean useWidth, BackgroundTile[][] map) {
		this.useWidth = useWidth;
		this.size = size;
		setMap(map);

		this.control = c;
		mode = TITLE; //starting game

		Point initPoint = walkables.get(ran.nextInt(5));
		p = new Player((int) initPoint.getX(), (int) initPoint.getY(), tileSize, c);
		chars.add(p);


	}

	public BackgroundTile[][] getMap() {
		return map;
	}

	public void newNPC(int diff) {
		Point p = walkables.get(ran.nextInt(walkables.size()));
		chars.add(new NPC(diff, (int) p.getX(), (int) p.getY(), tileSize, control));
	}
	
	public void setMap(BackgroundTile[][] map) {
		this.map = map;
		cols = map[0].length;
		rows = map.length;
		if (useWidth) {
			tileSize = size / cols;
		} else {
			tileSize = size / rows;
		}

		setWalkables();
	}

	private void setWalkables() {
		walkables.clear();
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				if (isWalk(r, c, false)) {
					walkables.add(new Point(c * tileSize, r * tileSize));
				}
			}
		}
	}

	public boolean isWalk(int r, int c, boolean isPlayer) {
		BackgroundTile bt = map[r][c];
		String f = bt.getFileName();
		if (isPlayer && mode == GAME && f.equals("BottomDoor.png")) {
			mode = LOADING;
			control.changeMap();
			mode = GAME;
		}
		return bt.getPropertyWalkable();

	}

	public void shoot(int x, int y, double angle) {
		Bullet bullet = new Bullet(x, y, angle, bulletBaseDamage, tileSize, control);
		bullets.add(bullet);
	}

	public boolean inBounds(int ro, int co) {
		if (ro < rows && ro >= 0) {
			if (co < cols && co >= 0) {
				return true;
			}
		}
		return false;
	}

	public int getMode() {
		return mode;
	}

	public int getRows() {
		return rows;
	}

	public int getCols() {
		return cols;
	}

	public int getTileSize() {
		return tileSize;
	}

	public BackgroundTile getBT(int r, int c) {
		return map[r][c];
	}

	public Image getBTImage(int r, int c) {
		return map[r][c].getI();
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public Player getPlayer() {
		return p;
	}

	public ArrayList<Char> getChars() {
		return chars;
	}

	public void clearNPCs() {
		chars.subList(1, chars.size()).clear();
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}
}
