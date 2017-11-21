import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MBController {
	MBModel model = null;
	MBView view = null;
	String mapDir = "maps/";
	Random ran = new Random();

	String map1 = "laundromat_interior";
	String map2 = "laundromat_exterior";
	String currMap = map1;

	int enemiesKilled = 0;

	boolean toRegen = false;

	public MBController(MBModel m, MBView v) {
		boolean usingMapWidth = false;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int winL = (int) screenSize.getWidth();
		int winH = (int) screenSize.getHeight();
		int largeSize = 0;
		if (winL > winH) {
			largeSize = winL;
			usingMapWidth = true;
		} else {
			largeSize = winH;
			usingMapWidth = false;
		}

		File file = new File(mapDir + currMap);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
		} catch (ClassNotFoundException e) {
		} catch (InstantiationException e) {
		} catch (IllegalAccessException e) {
		}
		boolean selectMap = false;
		if (selectMap) {
			JFileChooser fileP = new JFileChooser();
			int val = fileP.showOpenDialog(null);
			if (val == JFileChooser.APPROVE_OPTION) {
				file = fileP.getSelectedFile();
			}
		}

		BackgroundTile[][] map = readMap(file);
		m = new MBModel(this, largeSize, usingMapWidth, map);
		this.model = m;

		v = new MBView(this, largeSize, winL, winH);

		this.view = v;

	}

	private BackgroundTile[][] readMap(File f) {
		BackgroundTile[][] map = null;
		String fileName = "";
		String walkableString = "";
		boolean front = true;
		boolean walkable = false;

		try {
			@SuppressWarnings("resource")
			Scanner in = new Scanner(f);
			int row = 0, col = 0;

			int rows = in.nextInt();
			int cols = in.nextInt();
			map = new BackgroundTile[rows][cols];

			while (in.hasNext()) {
				if (front) {
					fileName = in.next();
					fileName = fileName.substring(1, fileName.length()); 
					front = !front;
				} else {
					walkableString = in.next();
					walkableString = walkableString.substring(0, walkableString.length() - 1);
					front = !front;
					walkable = walkableString.equals("true");

					BackgroundTile nextBGTile = new BackgroundTile(fileName, walkable);
					map[row][col] = nextBGTile;

					col++;
					if (col >= cols) {
						col = 0;
						row++;
					}
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return map;
	}

	//moves both player and AI
	public void moveAll(boolean[] b) {
		ArrayList<Char> cs = model.getChars();
		for (int j = 0; j < cs.size(); j++) {
			Char c = cs.get(j);
			if (toRegen) {
				c.regen();
				toRegen = false;
			}
			if (c.getType().equals("npc")) {
				((NPC) (c)).move();
			} else if (c.getType().equals("player")) {
				((Player) (c)).move(b);

				//hit short period invul.
				((Player) (c)).decTimeOut();

			}
		}
		ArrayList<Bullet> bs = model.getBullets();
		for (int i = 0; i < bs.size(); i++) {
			boolean dec = bs.get(i).move();
			if (dec)
				i--;
		}
	}

	static final int HP = 0;
	static final int SHOOT = 1;
	static final int REGEN = 2;
	static final int DMG = 3;
	static final int MOVE = 4;

	public void setToRegen(boolean a) {
		toRegen = a;
	}

	int[] costs = new int[] { 30, 40, 60, 40, 30 };

	public int getCosts(int stat) {
		return costs[stat];
	}

	public boolean[] getAvailButtons() {
		Player p = getPlayer();
		int cash = p.getCash();
		boolean[] avail = new boolean[5];
		for (int i = 0; i < 5; i++) {
			if (i == 1)
				avail[i] = cash >= costs[i] && p.validShootUp();
			if (i == 2)
				avail[i] = cash >= costs[i] && p.validRegenUp();
			else
				avail[i] = cash >= costs[i];
		}
		return avail;
	}

	public boolean upgradePlayer(int stat) {
		Player p = getPlayer();
		int cash = p.getCash();
		switch (stat) {
		case HP:
			if (cash >= costs[0]) {
				p.changeCash(-1 * costs[0]);
				costs[0] += 10;
				p.upgradeStat(stat);
				return true;
			}
			break;

		case SHOOT:
			if (cash >= costs[1] && p.validShootUp()) {
				p.changeCash(-1 * costs[1]);
				costs[1] += 10;
				p.upgradeStat(stat);
				return true;
			}
			break;
		case REGEN:
			if (cash >= costs[2] && p.validRegenUp()) {
				p.changeCash(-1 * costs[2]);
				costs[2] += 10;
				p.upgradeStat(stat);
				return true;
			}
			break;
		case DMG:
			if (cash >= costs[3]) {
				p.changeCash(-1 * costs[3]);
				costs[3] += 10;
				p.upgradeStat(stat);
				return true;
			}
			break;
		case MOVE:
			if (cash >= costs[4]) {
				p.changeCash(-1 * costs[4]);
				costs[4] += 10;
				p.upgradeStat(stat);
				return true;
			}
			break;

		}
		return false;
	}

	public void checkCollisions() {
		ArrayList<Bullet> bs = model.getBullets();
		ArrayList<Char> cs = model.getChars();

		for (int i = 0; i < bs.size(); i++) {
			if (i < 0) {
				break;
			}
			Bullet b = bs.get(i);
			for (int j = 0; j < cs.size(); j++) {
				Char c = cs.get(j);
				if (c.getType().equals("npc")) {
					Rectangle2D.Double bulRect = b.getCollision();
					Rectangle2D.Double charRect = c.getCollision();
					if (bulRect.intersects(charRect)) {
						c.changeHealth((int) (-1 * b.getDamage() * getPlayer().dmgMult()));


						if (c.getHealth() < 1) {
							cs.remove(c);
							j--;

							killedAEnemy();

						}
						bs.remove(b);
						i--;
					}
				}
			}
		}

		Player p = getPlayer();
		Rectangle2D.Double pRect = p.getCollision();
		for (int j = 0; j < cs.size(); j++) {
			Char c = cs.get(j);
			if (c.getType().equals("npc")) {
				Rectangle2D.Double charRect = c.getCollision();
				if (pRect.intersects(charRect)) {
					int dmg = ((NPC) (c)).getHitDmg();
					p.takeDamage(-1 * dmg);
					if (p.getHealth() < 1) {
						boolean stopGame = true;
						if (stopGame)
							view.stopGame();
					}
				}
			}
		}
	}

	int killCash = 20;

	public void killedAEnemy() {
		enemiesKilled++;
		getPlayer().changeCash(killCash);
	}

	public int getEnemiesKilled() {
		return enemiesKilled;
	}

	public void setEnemiesKilled(int enemiesKilled) {
		this.enemiesKilled = enemiesKilled;
	}

	int startNPCs = 3;

	public void startGame() {
		view.startGame();
		for (int i = 0; i < startNPCs; i++) {
			int diff = ran.nextInt(2);
			newNPC(diff);
		}
	}

	public void changeMap() {
		int r = 0, c = 10;
		if (currMap.equals(map1)) {
			currMap = map2;
			r = 5;
			c = 5;
		} else if (currMap.equals(map2)) {
			currMap = map1;
			r = 8;
			c = 8;
		}
		model.getPlayer().setPos(c * getTileSize(), r * getTileSize());
		model.getPlayer().setJustTeleported(true);
		File file = new File(mapDir + currMap);
		BackgroundTile[][] map = readMap(file);
		model.setMap(map);
		int npcs = model.getChars().size() - 1;
		clearNPCs();
		for (int i = 0; i < npcs; i++) {
			int diff = ran.nextInt(2);
			newNPC(diff);
		}
	}

	public void shoot(int x, int y) {
		//x y is the pos of the mouse
		int px = model.getPlayer().getX() + getTileSize() / 2;
		int py = model.getPlayer().getY() + getTileSize() / 2;
		x -= getTileSize() * .3;
		y -= getTileSize() * .3;
		double dx = x - px;
		double dy = y - py;
		double angle = Math.atan(dy / dx);
		if (dx < 0) {
			angle += Math.PI;
		}
		model.shoot(px, py, angle);
	}

	public Image getBTImage(int r, int c) {
		return model.getBTImage(r, c);
	}

	public int getRows() {
		return model.getRows();
	}

	public int getCols() {
		return model.getCols();
	}

	public int getTileSize() {
		return model.getTileSize();
	}

	public int getMode() {
		return model.getMode();
	}

	public void updateView() {
		view.update();
	}

	public void setMode(int mode) {
		model.setMode(mode);
	}

	public Player getPlayer() {
		return model.getPlayer();
	}

	public void newNPC() {
		int diff = ran.nextInt(2);
		model.newNPC(diff);
	}

	public void newNPC(int diff) {
		model.newNPC(diff);
	}

	public boolean inBounds(int ro, int co) {
		return model.inBounds(ro, co);
	}

	public boolean isWalk(int ro, int co, boolean a) {
		return model.isWalk(ro, co, a);
	}

	public ArrayList<Char> getChars() {
		return model.getChars();
	}

	public ArrayList<Bullet> getBullets() {
		return model.getBullets();
	}

	public void clearNPCs() {
		model.clearNPCs();
	}
}