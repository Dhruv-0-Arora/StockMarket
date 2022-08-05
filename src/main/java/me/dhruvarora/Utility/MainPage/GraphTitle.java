package me.dhruvarora.Utility.MainPage;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;

/*
 * class GraphTitle is a JLable for each graph
 * 
 * used to label the company of each graph
 */
public class GraphTitle extends JLabel {
    private final Color backgroundColor = Color.darkGray;
    private final Color textColor = Color.white;
    private final int fontSize = 20;

    /*
     * CONSTRUCTOR
     * 
     * take in the name of the company as title and sets defaults
     */
    public GraphTitle(String title) {
        setText(title);
        setAlignmentX(CENTER_ALIGNMENT);
        setPreferredSize(new Dimension(50, 25));

        setBackground(backgroundColor);
        setForeground(textColor);
        setFont(new Font("Arial", Font.PLAIN, fontSize));
    }

}
