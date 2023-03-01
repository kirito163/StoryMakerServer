/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storymakerserver.control;

import com.mycompany.storymakerserver.model.Level;
import com.mycompany.storymakerserver.model.Room;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import com.mycompany.storymakerserver.model.Files;
import com.mycompany.storymakerserver.model.StoryList;
import static com.mycompany.storymakerserver.model.Files.dirStoryConfermed;
import static com.mycompany.storymakerserver.model.Files.dirStoryReceved;
import static com.mycompany.storymakerserver.model.Files.extension;
import com.mycompany.storymakerserver.model.Story;

/**
 *
 * this class starts the server. you can see the stories received, read them,
 * approve them or delete them
 */
public class RestServer {

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        String command = "";
        File dirConfermed = new File(dirStoryConfermed);
        File dirReceved = new File(dirStoryReceved);
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(4321).build();
        ResourceConfig config = new ResourceConfig(ServerService.class);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
        try {
            server.start();
            System.out.println(String.format("Jersey app started with WADL available at "
                    + "%sapplication.wadl\nHit enter to stop it...", "http://localhost:4321/"));

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                command = scanner.nextLine();
                List<String> tokens = new ArrayList<>();
                tokens = tokenizer(command);

                if (tokens.get(0).equalsIgnoreCase("end") || tokens.get(0).equalsIgnoreCase("quit")) {
                    break;

                } else if (tokens.get(0).equalsIgnoreCase("story") && tokens.size() == 1) { // this prints title and lenght of stories receved
                    File dir = new File(dirStoryReceved);
                    File[] listFiles = dir.listFiles();
                    if (listFiles.length == 0) {
                        System.out.println("there are no stories received");
                    } else {
                        for (File file : listFiles) {
                            System.out.println("name: " + file.getName() + "\tlenght: " + file.length());
                        }
                    }
                } else if (tokens.get(0).equalsIgnoreCase("story") && tokens.size() > 1) { // this print all the information of the story
                    if (Files.isFileNameReceved(tokens.get(1))) {
                        File file = new File(dirStoryReceved + tokens.get(1) + extension);
                        Story story = Files.fileToStory(file);
                        System.out.println("TITLE\n" + story.getTitle() + "\nDESCRIPTION\n" + story.getDescription());
                        int countLevel = 1;

                        for (Level l : story.getStory()) {

                            System.out.println("\nLEVEL " + countLevel);
                            int countRoom = 1;
                            for (Room r : l.getRoom()) {
                                System.out.println("\nROOM " + countRoom);
                                System.out.println("ROOM NAME\n" + r.getName() + "\nROOM DESCRIPTION\n" + r.getDescription());

                                // lambda expression
                                l.getFigure().stream().filter(f -> f.getId() == r.getId()).forEach(f -> System.out.println("\nFIGURE NAME\n" + f.getName() + "\nFIGURE DESCRIPTION\n" + f.getDescription()));
                                l.getObjActive().stream().filter(o -> o.getId() == r.getId()).forEach(f -> System.out.println("OBJ ACTIVE NAME\n" + f.getName() + "\nOBJ ACTIVE DESCRIPTION\n" + f.getDescription()));

                                if (l.getObjPassive().getId() == r.getId()) {
                                    System.out.println("\nOBJ PASSIVE NAME\n" + l.getObjPassive().getName() + "\nOBJ PASSIVE DESCRIPTION\n" + l.getObjPassive().getDescription());
                                }
                                countRoom += 1;
                            }
                            countLevel += 1;
                        }
                    } else {
                        System.out.println("story not found");
                    }

                } else if (tokens.get(0).equalsIgnoreCase("commands") || tokens.get(0).equalsIgnoreCase("command") || tokens.get(0).equalsIgnoreCase("menu")) { // menu
                    System.out.println("STORY: returns a list of received stories");
                    System.out.println("STORY + story name: returns the information of a story");
                    System.out.println("ADD + story name: confirm the story selected");
                    System.out.println("DELETE + story name: delete the story selected");

                } else if (tokens.get(0).equalsIgnoreCase("add") && tokens.size() > 1) { // send the story to directory of story confermed and put information on database
                    if (Files.isFileNameReceved(tokens.get(1))) {
                        File fileReceved = new File(dirStoryReceved + tokens.get(1) + extension);
                        Story story = Files.fileToStory(fileReceved);
                        File fileConfermed = new File(dirStoryConfermed + tokens.get(1) + extension);
                        Database database = new Database();
                        Files.storyToFile(fileConfermed, story); // put the story to file
                        database.addStory(story.getTitle(), story.getDescription(), story.getScore(), story.getnScore(), fileConfermed.getPath(), story.getUserId());
                        fileReceved.delete();
                        System.out.println("story added");

                    } else {
                        System.out.println("story not found");
                    }

                } else if (tokens.get(0).equalsIgnoreCase("delete") && tokens.size() > 1) {
                    if (Files.isFileNameReceved(tokens.get(1))) {
                        File file = new File(dirStoryReceved + tokens.get(1) + extension);
                        file.delete();
                    } else if (Files.isFileNameConfermed(tokens.get(1))) {
                        File file = new File(dirStoryConfermed + tokens.get(1) + extension);
                        Database d = new Database();
                        d.deleteStoryByTitle(tokens.get(1));
                        file.delete();
                    }
                    System.out.println("story deleted");
                } else {
                    System.out.println("command not recognized");
                }
            }
            server.shutdown();

        } catch (IOException ex) {
            Logger.getLogger(RestServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public static List<String> tokenizer(String string) {
        String[] split = string.toLowerCase().split("\\s+");
        List<String> tokens = new ArrayList<>();
        tokens.add(split[0]);
        if (split.length == 2) {
            tokens.add(split[1]);
        } else if (split.length > 2) { // if the second word contain over two words
            StringBuffer stringBuf = new StringBuffer();
            stringBuf.append(split[1]);
            for (int i = 2; i < split.length; i++) {
                stringBuf.append(" " + split[i]);
            }
            tokens.add(stringBuf.toString());
        }
        return tokens;
    }

}
