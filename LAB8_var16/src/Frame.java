

import java.awt.*;
import javax.swing.JFrame;

public class Frame extends JFrame
{
    private Information inf = null;
    private final int diam = 900;

    @Override
    public void paint(Graphics g)
    {
        inf.paint(g);
    }

    public Frame()
    {
        super("Information");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        inf = new Information(new Color(255, 255, 255), new Color(0, 0, 255), diam);
        setSize(diam+80, diam+80);
    }

    public static void main(String[] args)
    {
        Frame field = new Frame();
        field.setVisible(true);
    }
}
