/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storymakerserver.control;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.mycompany.storymakerserver.model.Files;
import com.mycompany.storymakerserver.model.Story;
import com.mycompany.storymakerserver.model.StoryList;

/**
 *
 * @author sauru
 */
@Path("storymaker")
public class ServerService {

    List<Story> story = new ArrayList<>();
    List<Story> storyReceved = new ArrayList<>();

    @PUT
    @Path("/signUp")
    @Consumes("application/json")
    public Response signUp(String json) {
        Gson gson = new Gson();
        User login = gson.fromJson(json, User.class);
        Database database = new Database();        
        boolean isUser = database.isUsername(login.getUsername());
        boolean isEmail = database.isEmail(login.getEmail());        
        if (!isUser && !isEmail) {
            database.addUser(login.getUsername(), login.getPassword(), login.getEmail());
            return Response.ok().build();
        }

        return Response.notAcceptable(null).build();
    }

    @PUT
    @Path("/signIn")
    @Consumes("application/json")
    public Response signIn(String json) {
        Gson gson = new Gson();
        User login = gson.fromJson(json, User.class);
        Database database = new Database();
        int id = database.signIn(login.getUsername(), login.getPassword());
        if (id > 0) {
            login.setId(id);
            login.setIsLogin(true);
            login.setPassword("");
            String jsonString = gson.toJson(login);
            return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
        }
        return Response.notAcceptable(null).build();
    }

    @PUT
    @Path("/send")  // takes the story from client, put the story on directory story receved, 
    @Consumes("application/json")
    public Response send(String json) throws FileNotFoundException, IOException {

        Gson gson = new Gson();
        Story story = gson.fromJson(json, Story.class);
        if (story != null) {
            if (Files.isFileNameConfermed(story.getTitle()) || Files.isFileNameReceved(story.getTitle())) {
                return Response.notAcceptable(null).build();
            }
            File file = new File(Files.dirStoryReceved + story.getTitle() + Files.extension);
            Database database = new Database();
            Email email = new Email(database.getEmail(story.getUserId()));
            email.sendStory();
            Files.storyToFile(file, story);
            return Response.ok().build();

        }
        return Response.notAcceptable(null).build();
    }

    @GET
    @Path("/getAll") // send a list of information of the stories  to client
    @Produces("application/json")
    public Response getStoryList() { 
        Database database = new Database();
        StoryList storyList = database.getStoryList();
        Gson gson = new Gson();
        String jsonString = gson.toJson(storyList);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/getStory") // send the selected story to client
    @Produces("application/json")
    public Response getStory(@QueryParam("storyId") int storyId) throws FileNotFoundException, IOException, ClassNotFoundException {
        Database database = new Database();
        String[] storyInfo = database.getStoryInfo((storyId));
        File file = new File(Files.dirStoryConfermed + storyInfo[0] + Files.extension);
        Story story = Files.fileToStory(file);
        story.setTitle(storyInfo[0]);
        story.setDescription(storyInfo[1]);
        Gson gson = new Gson();
        String jsonString = gson.toJson(story);
        return Response.ok(jsonString, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("/score") // put the score value to server
    @Consumes("application/json")
    public Response sendScore(String json) {
        Gson gson = new Gson();
        Story story = gson.fromJson(json, Story.class);
        Database database = new Database();
        database.addScore(story.getId(), story.getScore());
        return Response.ok().build();
    }

}
