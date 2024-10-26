package main.server;

import java.io.*;

public class UserDB {

    public static void loadUserDB(String database, String username, UserWriter userDB) throws IOException{
        File userFile = new File(database, username + ".db");
        if(!userFile.exists()){
            userFile.createNewFile();
            System.out.println(username + " , your account has been created with 0 dollars");
        } else {
            FileReader fr = new FileReader(userFile);
            BufferedReader br = new BufferedReader(fr);

            if (userFile.length() == 0){
                System.out.println(username + " , your account has 0 dollars");
            } else{
                System.out.println(username + ", your account contains: ");
                String line ="x";
                while ((line = br.readLine()) != null){
                    System.out.println(line);
                }
            }

            br.close();
            fr.close();
        }
    }

    public static void saveUserDB(String database, String username, UserWriter userDB) throws IOException{
        File userFile = new File(database, username + ".db");
        FileWriter fw = new FileWriter(userFile);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(userDB.getBalance());
        bw.flush();
        bw.close();
        fw.close();

        System.out.println(username + "'s balance has been updated");
    }
    
}
