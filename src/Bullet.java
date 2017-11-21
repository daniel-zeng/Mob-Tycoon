public class Bullet extends Entity {
	int damage;
	double angle = 0;

	public Bullet(int x, int y, double angle, int damage, int tileSize, MBController c) {
		super(20, "bullet", tileSize, true, c);
		setPos(x, y);
		this.damage = damage;
		this.angle = angle;
		String[] strs = { "bullet_up.png", "bullet_left.png", "bullet_down.png", "bullet_right.png" };
		setFileNameMult(strs);
		constructImage();
	}

	public boolean isMoveable(int x, int y) {
		updateCollisionBox(x, y);
		int c_base = (int) Math.floor((collision.getX()) / tileSize);
		int c_top = (int) Math.floor((collision.getX() + collision.getWidth()) / tileSize);
		int r_base = (int) Math.floor((collision.getY()) / tileSize);
		int r_top = (int) Math.floor((collision.getY() + collision.getHeight()) / tileSize);

		if (c_top < control.getCols() && c_base >= 0 && r_top < control.getRows() && r_base >= 0) {
		} else {
			return false;
		}

		return control.isWalk(r_base, c_base, false) && control.isWalk(r_base, c_top, false) && control.isWalk(r_top, c_base, false) && control.isWalk(r_top, c_top, false);

	}

	@Override
	public void setPos(int x, int y) {
		super.setPos(x, y);
		updateCollisionBox(x, y);
	}
	public void updateCollisionBox(int x, int y) {
		double xStart = 0;
		double xEnd = 0.4;
		double yStart = 0;
		double yEnd = 0.4;
		double nx = ((double) x) + (double) (tileSize) * xStart;
		double ny = ((double) y) + (double) (tileSize) * yStart;
		double dx = (double) (tileSize) * (xEnd - xStart);
		double dy = (double) (tileSize) * (yEnd - yStart);
		collision.setRect(nx, ny, dx, dy);
	}

	public boolean move() {
		int newY = getY();
		int newX = getX();

		int dX = (int) (Math.cos(angle) * speed);
		int dY = (int) (Math.sin(angle) * speed);
		double deg = Math.toDegrees(angle);
		newX += dX;
		newY += dY;
		double tet = deg - 45;
		if (tet < 0)
			tet += 360;
		if (tet == 360)
			tet = 0;
		int image_dir = (int) (tet) / 90;
		if (image_dir == 0) {
			image_dir = 2;
		} else if (image_dir == 2) {
			image_dir = 0;
		}
		setDir(image_dir);
		if (isMoveable(newX, newY)) {
			setPos(newX, newY);
			return false;
		} else {
			control.getBullets().remove(this);
			return true;
		}

	}

	public int getDamage() {
		return damage;
	}

	public double getAngle() {
		return angle;
	}

}
