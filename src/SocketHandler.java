import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.*;

public class SocketHandler extends Thread {

    private Socket income;
    private String[] msg;
    private Connection con;


    public SocketHandler(Socket income) {
        this.income = income;
    }

    public void run() {

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/lockedapp?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC", "root", "1234");
            System.out.println("SQL connected");

            BufferedReader breader = new BufferedReader(new InputStreamReader(income.getInputStream()));

            String str = breader.readLine();
            msg = str.split(",");
            if (msg[0].equals("l"))
                login();
            if (msg[0].equals("2"))
                getHouseList();
            income.close();

//            breader.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("-= " + e.getMessage() + " =-");
        }
    }

    private void login() {
        int res = -1;
        Statement stmt = null;
        System.out.println("user: " + msg[1] + ", pass: " + msg[2]);
        String query = "SELECT * FROM users WHERE users.name = '" + msg[1] + "' AND users.password = '" + msg[2] + "'";
        DataOutputStream out_msg = null;
        try {

            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                res = rs.getInt("users.id");
            }

            System.out.println(msg[1] + " , pass: " + msg[2]);
            if (res != -1) System.out.println(" good");
            else System.out.println(" bad");

            System.out.println("res = " + res);
            out_msg = new DataOutputStream(income.getOutputStream());
            out_msg.writeBytes(res + "\n");
            out_msg.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getHouseList() {
        String query = "select house.name, address, lat, lot from house where house.id in (select user_lock.lock_id from user_lock where user_lock.user_id = (select users.id from users where name = '" + msg[1] + "')) ";
        Statement stm = null;
        DataOutputStream out_msg = null;
        try {
            stm = con.createStatement();
            ResultSet rs = stm.executeQuery(query);
            String result = "";
            while (rs.next()) {
                for (int i = 1; i < 5; i++) {
                    result += rs.getString(i);
                    if (i < 4)
                        result+=",";
                }
                result+="!";
            }
            System.out.println("2 --> " + result);
            out_msg= new DataOutputStream(income.getOutputStream());
         //   out_msg.writeBytes(result + "\n");
            out_msg.writeUTF(result);
            out_msg.close();

        }
            catch(SQLException e){
                e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
