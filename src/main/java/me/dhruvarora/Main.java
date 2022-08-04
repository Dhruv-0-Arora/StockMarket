package me.dhruvarora;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;

import me.dhruvarora.UI.Frame;

/* 
 * Main class extends JFrame which is built into java
 */
public class Main {
    // global variable for the frame
    public static Frame frame;

    /*
     * main method for program
     * 
     * calls upon the main frame of the program
     * - mainframe is the only frame in this project
     */
    public static void main(String[] args)
            throws FileNotFoundException, IOException, ParseException {
        frame = new Frame();

    }

}