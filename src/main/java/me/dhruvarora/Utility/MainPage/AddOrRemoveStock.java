package me.dhruvarora.Utility.MainPage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.awt.Shape;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import org.json.simple.parser.ParseException;

import me.dhruvarora.UI.Frame;

public class AddOrRemoveStock extends JButton {
    private final Color textColor = Color.white;
    private final Color backgroundColor = new Color(67, 129, 255);
    private final Color borderColor = Color.BLACK;
    private final Frame frame;
    private Shape shape;

    public AddOrRemoveStock(Frame frame) {
        this.frame = frame;

        setVerticalTextPosition(AbstractButton.CENTER);
        setHorizontalTextPosition(AbstractButton.CENTER);
        setText("Add/Remove Stock");
        setOpaque(false);
        setForeground(textColor);
        setBackground(backgroundColor);
        setPreferredSize(new Dimension(150, 75));
        setFont(new Font("Arial", Font.PLAIN, 20));
        setAlignmentX(CENTER_ALIGNMENT);
        setFocusPainted(false);
        setBorderPainted(false);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    AddOrRemoveStock.this.frame.swapScreens();
                } catch (IOException | ParseException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        super.paintComponent(g);
    }

    protected void paintBorder(Graphics g) {
        g.setColor(borderColor);
        g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
    }

    public boolean contains(int x, int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            shape = new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
        }
        return shape.contains(x, y);
    }

}
