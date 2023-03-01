/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.storymakerserver.control;

import com.mycompany.storymakerserver.model.Story;
import com.mycompany.storymakerserver.model.StoryList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sauru
 */
public class Database {

    public static final String LOGIN = "CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY AUTO_INCREMENT, username VARCHAR(1024) NOT NULL, password VARCHAR(1024) NOT NULL, email VARCHAR(1024) NOT NULL)";
    public static final String STORY = "CREATE TABLE IF NOT EXISTS story (id INTEGER PRIMARY KEY AUTO_INCREMENT, title VARCHAR(1024) NOT NULL, description VARCHAR(1024) NOT NULL, score FLOAT NOT NULL, nScore INTEGER NOT NULL, date DATE NOT NULL, path VARCHAR(1024) NOT NULL, idAuthor INTEGER, FOREIGN KEY (idAuthor) REFERENCES user(id))";

    Properties dbprops;
    Connection conn;
    Statement stm;

    public Database() {
        try {
            dbprops = new Properties();
            dbprops.setProperty("user", "user");
            dbprops.setProperty("password", "1234");
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            stm = conn.createStatement();
            stm.executeUpdate(LOGIN);
            stm.close();
            conn.close();
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            stm = conn.createStatement();
            stm.executeUpdate(STORY);
            stm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean isUsername(String username) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT username FROM user WHERE username = ?");
            pstm.setString(1, username);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                if (rs.wasNull()) {
                    rs.close();
                    conn.close();
                    return false;
                } else {
                    rs.close();
                    conn.close();
                    return true;
                }
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isEmail(String email) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT email FROM user WHERE email = ?");
            pstm.setString(1, email);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                if (rs.wasNull()) {
                    rs.close();
                    conn.close();
                    return false;
                } else {
                    rs.close();
                    conn.close();
                    return true;
                }
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean isPassword(String password) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT password FROM user WHERE password = ?");
            pstm.setString(1, password);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                if (rs.wasNull()) {
                    rs.close();
                    conn.close();
                    return false;
                } else {
                    rs.close();
                    conn.close();
                    return true;
                }
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int signIn(String username, String password) {
        int result = 0;
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT id FROM user WHERE username = ? and password = ?");
            pstm.setString(1, username);
            pstm.setString(2, password);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                result = rs.getInt(1);
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public void addUser(String username, String password, String email) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES(?, ?, ?)");
            pstm.setString(1, username);
            pstm.setString(2, password);
            pstm.setString(3, email);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void addStory(String title, String description, double score, int nScore, String path, int userId) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("INSERT INTO story (title, description, score, nScore, date, path, idAuthor) VALUES(?, ?, ?, ?, ?, ?, ?)");
            pstm.setString(1, title);
            pstm.setString(2, description);
            pstm.setDouble(3, score);
            pstm.setInt(4, nScore);
            pstm.setDate(5, java.sql.Date.valueOf(java.time.LocalDate.now()));
            pstm.setString(6, path);
            pstm.setInt(7, userId);

            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addScore(int storyId, double score) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("UPDATE story SET score = score + ?, nScore = nScore + 1 WHERE id = ?");
            pstm.setDouble(1, score);
            pstm.setInt(2, storyId);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public StoryList getStoryList() {
        StoryList storyList = new StoryList();
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT story.id, title, description, score, nScore, date, username FROM story INNER JOIN user ON story.idAuthor = user.id");
            while (rs.next()) {                
                storyList.getStory().add(new Story(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5), rs.getDate(6), rs.getString(7)));
            }
            rs.close();
            stm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return storyList;
    }

    public String getEmail(int id) {
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT email FROM user WHERE id = ?");
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String[] getStoryInfo(int id) {
        String[] storyInfo = new String[2];
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("SELECT title, description FROM story WHERE id = ?");
            pstm.setInt(1, id);
            ResultSet rs = pstm.executeQuery();
            
            if (rs.next()) {
                storyInfo[0] = rs.getString(1);
                storyInfo[1] = rs.getString(2);

            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        return storyInfo;
    }
    
    public void deleteStoryByTitle(String title){
        StoryList storyList = new StoryList();
        try {
            conn = DriverManager.getConnection("jdbc:h2:./resources/db/user", dbprops);
            PreparedStatement pstm = conn.prepareStatement("DELETE FROM story WHERE title = ?");            
            pstm.setString(1, title);
            pstm.executeUpdate();
            pstm.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
      

}
