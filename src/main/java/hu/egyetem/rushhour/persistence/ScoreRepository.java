package hu.egyetem.rushhour.persistence;

import java.sql.*;
import java.nio.file.*;
import java.util.*;

public final class ScoreRepository {
    private final String url;
    public ScoreRepository(){this("jdbc:sqlite:rushhour.db");}
    public ScoreRepository(String url){this.url=url;initialize();}
    private Connection connect() throws SQLException{return DriverManager.getConnection(url);}
    private void initialize(){try(Connection c=connect();Statement s=c.createStatement()){
        s.executeUpdate("CREATE TABLE IF NOT EXISTS results (id INTEGER PRIMARY KEY AUTOINCREMENT, player_name TEXT NOT NULL, moves INTEGER NOT NULL, won INTEGER NOT NULL, played_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP)");
    }catch(SQLException e){throw new IllegalStateException("Nem sikerült létrehozni az eredmény-adatbázist.",e);}}
    public void save(String name,int moves,boolean won){try(Connection c=connect();PreparedStatement p=c.prepareStatement("INSERT INTO results(player_name,moves,won) VALUES(?,?,?)")){p.setString(1,name);p.setInt(2,moves);p.setInt(3,won?1:0);p.executeUpdate();}catch(SQLException e){throw new IllegalStateException("Az eredmény mentése sikertelen.",e);}}
    public List<String> highScores(){List<String> out=new ArrayList<>();String sql="SELECT player_name, COUNT(*) AS wins, MIN(moves) AS best FROM results WHERE won=1 GROUP BY player_name ORDER BY wins DESC, best ASC, player_name ASC LIMIT 20";
        try(Connection c=connect();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){int rank=1;while(r.next())out.add(String.format("%2d. %-20s %d győzelem, legjobb: %d lépés",rank++,r.getString("player_name"),r.getInt("wins"),r.getInt("best")));}
        catch(SQLException e){throw new IllegalStateException("A ranglista lekérése sikertelen.",e);}return out;}
}
