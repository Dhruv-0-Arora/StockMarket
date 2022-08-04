package me.dhruvarora.Utility.ChangeStockPage;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;

import org.json.simple.parser.ParseException;

import me.dhruvarora.UI.Frame;

public class BackButton extends JButton {
    private final Frame frame;
    private Shape shape;
    private final Color borderColor = Color.BLACK;
    private final Color backgroundColor = Color.WHITE;

    public BackButton(Frame frame) {
        this.frame = frame;
        setOpaque(false);
        setText("< Back");
        setFont(new Font("Arial", Font.PLAIN, 20));
        setPreferredSize(new Dimension(125, 30));
        setSize(getPreferredSize());
        setLocation(1, 1);
        setBackground(backgroundColor);
        setBorderPainted(false);
        setFocusPainted(false);

        addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    BackButton.this.frame.swapScreens();
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
