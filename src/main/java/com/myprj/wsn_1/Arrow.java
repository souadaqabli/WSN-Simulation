package com.myprj.wsn_1;

import java.awt.Graphics;
import java.awt.Graphics2D;

public class Arrow {
    private int x1, y1, x2, y2;

    public Arrow(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = 15; // ajustez cette valeur pour la longueur de la flèche
        g2d.translate(x1, y1);
        g2d.rotate(angle);
        g2d.drawLine(0, 0, (int) Math.sqrt(dx * dx + dy * dy) - len, 0);
        g2d.fillPolygon(new int[]{(int) Math.sqrt(dx * dx + dy * dy) - len, (int) Math.sqrt(dx * dx + dy * dy) - len - 5, (int) Math.sqrt(dx * dx + dy * dy) - len - 5}, new int[]{0, -5, 5}, 3);
        g2d.dispose();
    }
}

