package gramtarang.mint.utils;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*this COnnectionClass is for the setting, credentials for sql database*/
public class ConnectionClass {
    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://13.233.151.160/mint", "mint","Mint@14082020@!" );
        } catch (SQLException se) {
            Log.e("ERROor", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROor", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROor", e.getMessage());
        }
        return conn;
    }
}