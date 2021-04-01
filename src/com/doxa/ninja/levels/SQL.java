package com.doxa.ninja.levels;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.entity.Player;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.RANK;

public class SQL {
	
	Main plugin;
	public SQL(Main main) {
		this.plugin = main;
	}

	//CONNECTION SHIT
    private String host, port, database, username, password;
    static Connection connection;

    public void connect() throws SQLException {
	    host = plugin.getConfig().getString("MySQL.host");
	    port = plugin.getConfig().getString("MySQL.port");
	    database = plugin.getConfig().getString("MySQL.database");
	    username = plugin.getConfig().getString("MySQL.username");
	    password = plugin.getConfig().getString("MySQL.password");
	    connection = DriverManager.getConnection("jdbc:mysql://" +
	    	     host + ":" + port + "/" + database + "?useSSL=false",
	    	     username, password);
	    System.out.print("Connected successfully to MySQL Database!\n");
    }
    
    public void disconnect() {
    	if (isConnected()) {
    		try {
    			connection.close();
    			System.out.print("Disconnected successfully from MySQL Database!\n");
    		} catch(SQLException e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    public static Connection getConnection() {
    	return connection;
    }
    
    public boolean isConnected() {
    	return (connection == null ? false : true);
    }
    
    //RANKS
    public String rankString(RANK r) {
    	if (r == RANK.GENIN) {
    		return "Genin";
    	} else if (r == RANK.CHUNIN) {
    		return "Chunin";
    	} else if (r == RANK.JONIN) {
    		return "Jonin";
    	} else if (r == RANK.HOKAGE) {
    		return "Hokage";
    	} else {
    		return "null";
    	}
    }
    
    //COMMANDS FOR DATBASE
    
    //TABLE NAME IS levels
    
    public void createTable() {
    	PreparedStatement ps;
    	try {
    		ps = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS levels"
    				+ " (NAME VARCHAR(100),RANK VARCHAR(100),MISSION VARCHAR(100),LAST VARCHAR(100),EXPLOSIVE INT(100),KILLS INT(100),PRIMARY KEY (NAME))");
    		ps.executeUpdate();
    	} catch (SQLException | NullPointerException e) {
    		System.out.println("[Ninja] Table either already exists, or an error was thrown! (Most likely not an issue)");
    	}
    }
    
    public void createPlayer(Player player) {
		try {
			String uuid = player.getName();
			if (!exists(uuid)) {
				PreparedStatement ps2 = getConnection().prepareStatement("INSERT IGNORE INTO levels"
						+ " (NAME,RANK) VALUES (?,?)");
				ps2.setString(1, player.getName());
				ps2.setString(2, rankString(RANK.GENIN));
				ps2.executeUpdate();
				
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists(String name) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM levels WHERE NAME=?");
			ps.setString(1, name);
			
			ResultSet results = ps.executeQuery();
			if (results.next()) {
				// player is found
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addKills(String name, int kills) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET KILLS=? WHERE NAME=?");
			ps.setInt(1, (getKills(name) + kills));
			ps.setString(2, name.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getKills(String name) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT KILLS FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			int points = 0;
			if (rs.next()) {
				points = rs.getInt("KILLS");
				return points;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void setKills(String name, int kills) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET KILLS=? WHERE NAME=?");
			ps.setInt(1, kills);
			ps.setString(2, name.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addExplosiveKills(String name, int kills) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET EXPLOSIVE=? WHERE NAME=?");
			ps.setInt(1, (getKills(name) + kills));
			ps.setString(2, name.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int getExplosiveKills(String name) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT EXPLOSIVE FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			int points = 0;
			if (rs.next()) {
				points = rs.getInt("KILLS");
				return points;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void setRank(String name, RANK r) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET RANK=? WHERE NAME=?");
			ps.setString(1, rankString(r));
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getRank(String name) {
		String rank = "";
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT RANK FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rank = rs.getString("RANK");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rank;
	}
	
	public void setMission(String name, String m) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET MISSION=? WHERE NAME=?");
			ps.setString(1, m);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getMission(String name) {
		String mission = "";
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT MISSION FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				mission = rs.getString("MISSION");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mission;
	}
	
	public void setLastMission(String name, String m) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("UPDATE levels SET LAST=? WHERE NAME=?");
			ps.setString(1, m);
			ps.setString(2, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getLastMission(String name) {
		String last_mission = "";
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT LAST FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				last_mission = rs.getString("LAST");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return last_mission;
	}
	
	public boolean hasMissionActive(Player p) {
		String mission = "";
		try {
			PreparedStatement ps = getConnection().prepareStatement("SELECT MISSION FROM levels WHERE NAME=?");
			ps.setString(1, p.getName());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				mission = rs.getString("MISSION");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (mission == null || mission == "" || mission == "None") {
			return false;
		} else {
			return true;
		}
	}
	
	
	// DELETE STUFF
	
	public void emptyTable() {
		try {
			PreparedStatement ps = getConnection().prepareStatement("TRUNCATE levels");
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void remove(String name) {
		try {
			PreparedStatement ps = getConnection().prepareStatement("DELETE FROM levels WHERE NAME=?");
			ps.setString(1, name);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}