package Server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class DatabaseOfSessions {
    private static String ReadFile() {
        String content = null;
        try {
            String fileName = "./QuizBot/DataBase/Sessions.json";
            content = Files.lines(Paths.get(fileName)).reduce("", String::concat);
        } catch (Exception e) {
            System.out.println(e);
        }
        return content;
    }

    private static void WriteFile(String textToWrite) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("./QuizBot/DataBase/Sessions.json", "UTF-8");
            writer.println(textToWrite);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, User> getDatabaseOfUsers()
    {
        var rawJson = ReadFile();
        var gson = new Gson();
//        var der = new JSONDeserializer<HashMap<String, User>>();
        var type = new TypeToken<HashMap<String, User>>() {
        }.getType();
//        System.out.println(der.deserialize(rawJson).values());
//        return der.deserialize(rawJson);
        return gson.fromJson(rawJson, type);
    }
    public static User GetUserByUsername(String username)
    {
        var userDatabase = getDatabaseOfUsers();
        return userDatabase.get(username);
    }

    public static boolean Contains(String username)
    {
        var userDatabase = getDatabaseOfUsers();
        return userDatabase.containsKey(username);
    }

    public static void AddNewUserInDatabase(User user) {
//        var gson = new Gson();
        var ser = new JSONSerializer();
        var userDatabase = getDatabaseOfUsers();
        userDatabase.put(user.handle, user);
        WriteFile(ser.deepSerialize(userDatabase));
    }
    public static void UpdateUserInDatabase(User user) {
//        var gson = new Gson();
        var userDatabase = getDatabaseOfUsers();
        var ser = new JSONSerializer();
        userDatabase.remove(user.handle);
        userDatabase.put(user.handle, user);
        WriteFile(ser.deepSerialize(userDatabase));
    }

    public static void RemoveUserFromDatabase(String userHandle) {
        var ser = new JSONSerializer();
//        Gson gson = new Gson();
        HashMap<String, User> userDatabase = getDatabaseOfUsers();
        userDatabase.remove(userHandle);
        WriteFile(ser.deepSerialize(userDatabase));
    }
}
