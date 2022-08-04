package me.dhruvarora.Utility.MainPage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

public class GraphTitle extends JLabel {

    public GraphTitle(String title) {
        setText(title);
        setAlignmentX(CENTER_ALIGNMENT);
        setPreferredSize(new Dimension(50, 25));
        setBackground(Color.darkGray);
        setForeground(Color.white);
        setFont(new Font("Arial", Font.PLAIN, 20));
    }

}
