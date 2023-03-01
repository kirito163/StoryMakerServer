/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storymakerserver.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sauru
 */
public class Files {

    public static final String dirStoryReceved = "./src/main/java/com/mycompany/storymakerserver/storyReceved/";
    public static final String dirStoryConfermed = "./src/main/java/com/mycompany/storymakerserver/storyConfermed/";
    public static final String extension = ".dat";

    public static boolean isFileNameReceved(String nomeFile) { // look if there is a file with the string parameter in the directory
        File dir = new File(dirStoryReceved);
        File[] listFiles = dir.listFiles();
        for (File f : listFiles) {      
            if (f.getName().equalsIgnoreCase(nomeFile + extension)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFileNameConfermed(String nomeFile) { 
        File dir = new File(dirStoryConfermed);
        File[] listFiles = dir.listFiles();
        for (File f : listFiles) {
            if (f.getName().equalsIgnoreCase(nomeFile + extension)) {
                return true;
            }
        }
        return false;
    }

    public static Story fileToStory(File file) { // put the file in story
        try {
            ObjectInputStream in;
            in = new ObjectInputStream(new FileInputStream(file));
            Story story = (Story) in.readObject();
            in.close();
            return story;

        } catch (IOException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void storyToFile(File file, Story story) { // put the story in file
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(story);
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Files.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
