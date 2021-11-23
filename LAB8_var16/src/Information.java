

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

public class Information extends Canvas
{
    private float diam;
    private Color iColor = null;
    private Color bColor = null;


    public Color getIColor()
    {
        return this.iColor;
    }

    public Color getBColor()
    {
        return this.bColor;
    }

    public float getDiam()
    {
        return this.diam;
    }

    public void setIColor(Color tmp)
    {
        this.iColor = tmp;
    }

    public void setBColor(Color tmp)
    {
        this.bColor = tmp;
    }

    public void setDiam(float tmp)
    {
        this.diam = tmp;
    }

    public Information(Color iColor, Color bColor, float diam)
    {
        this.diam = diam;
        this.iColor = iColor;
        this.bColor = bColor;
    }

    public Information()
    {

    }


    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        Shape circle = new Ellipse2D.Float(40, 40, diam, diam);
        g2d.draw(circle);
        g2d.setColor(bColor);
        g2d.fill(circle);

        g2d.setColor(iColor);

        Shape small = new Rectangle2D.Float(40 + diam / 2 - diam / 12, 40 + diam / 16, diam / 16 + diam / 8, diam / 8);
        Shape rec = new Rectangle2D.Float(40 + diam / 2 - diam / 12, 40 + diam / 16 + diam / 6, diam / 16 + diam / 8, 11 * diam / 16);
        g2d.draw(small);
        g2d.draw(rec);
        g2d.fill(small);
        g2d.fill(rec);
    }
}