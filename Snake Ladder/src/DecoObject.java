import java.awt.*;
import java.util.Random;

class DecoObject {
    float x, y, v, sz; Color c; int type;
    public DecoObject(GameTheme theme) { reset(1350, 920, theme); }
    public void reset(int w, int h, GameTheme theme) {
        Random rnd = new Random(); x = rnd.nextInt(w); y = rnd.nextInt(h);
        sz = rnd.nextFloat() * 18 + 10; v = rnd.nextFloat() * 1.3f + 0.5f;
        type = rnd.nextInt(3);
        c = (theme == GameTheme.OUTERSPACE) ? Color.WHITE : new Color(180, 245, 255, 140);
    }
    public void update(int w, int h) { y += v; if (y > h) { y = -30; x = new Random().nextInt(w); } }
    public void render(Graphics2D g2, GameTheme theme) {
        g2.setColor(c);
        if(theme == GameTheme.OUTERSPACE) {
            g2.fillOval((int)x, (int)y, (int)sz/5, (int)sz/5);
        } else {
            if(type == 0) {
                int ix = (int)x, iy = (int)y, isz = (int)sz;
                g2.fillOval(ix, iy, isz, isz/2);
                int[] tx = {ix, ix-isz/2, ix-isz/2}; int[] ty = {iy+isz/4, iy, iy+isz/2};
                g2.fillPolygon(tx, ty, 3);
            } else if(type == 1) {
                g2.drawOval((int)x, (int)y, (int)sz/2, (int)sz/2);
            } else {
                drawStarfish(g2, (int)x, (int)y, (int)sz/2);
            }
        }
    }
    private void drawStarfish(Graphics2D g2, int x, int y, int r) {
        int[] xp = new int[10]; int[] yp = new int[10];
        for (int i = 0; i < 10; i++) {
            double ang = i * Math.PI / 5 - Math.PI / 2;
            double rad = (i % 2 == 0) ? r : r * 0.5;
            xp[i] = (int)(x + rad * Math.cos(ang)); yp[i] = (int)(y + rad * Math.sin(ang));
        }
        g2.fillPolygon(xp, yp, 10);
    }
}