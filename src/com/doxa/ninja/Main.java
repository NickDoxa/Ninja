package com.doxa.ninja;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.doxa.ninja.bar.Bar;
import com.doxa.ninja.files.FileClass;
import com.doxa.ninja.levels.SQL;
import com.doxa.ninja.moves.Agility;
import com.doxa.ninja.moves.Chidori;
import com.doxa.ninja.moves.Kunai;
import com.doxa.ninja.moves.Meditate;
import com.doxa.ninja.moves.Rasengan;
import com.doxa.ninja.moves.ShadowClone;
import com.doxa.ninja.moves.Substitution;
import com.doxa.ninja.moves.Taijutsu;
import com.doxa.ninja.moves.particles.Quit;

import net.raidstone.wgevents.events.RegionEnteredEvent;
import net.raidstone.wgevents.events.RegionLeftEvent;

public class Main extends JavaPlugin implements Listener {
	
	private double version = 1.4;
	public double getPluginVersion() {
		return version;
	}
	
	//MOVES CLASSES
	public Kunai kunai;
	public Substitution sub;
	public Agility agility;
	public Chidori chi;
	public Rasengan ras;
	public ShadowClone sc;
	public Meditate med;
	public Taijutsu tai;

	//SCOREBOARD
	public ScoreBoard scoreboard;
	
	private boolean useScoreboard;
	public boolean useBoard() {
		return useScoreboard;
	}
	
	//COOLDOWN VARIABLES
	private int kunai_cooldown_config;
	private int agility_cooldown_config;
	private int sub_cooldown_config;
	private int chidori_cooldown_config;
	private int ras_cooldown_config;
	private int clone_cooldown_config;
	private int med_cooldown_config;
	private int tai_cooldown_config;
	
	//DAMAGE VARIABLES
	private double chidori_damage_config;
	private double ras_damage_config;
	private double chakra_damage_config;
	private double kunai_damage_config;
	private double tai_damage_config;
	
	//OTHER VARIABLES
	private int clone_amt_config;
	private boolean clone_bool_config;
	private String wg_region_config;
	
	//OTHER CLASSES
	public FileClass file;
	public void writeReport(String error, String reason) {
		file.writeReport(error, reason);
	}
	public Bar bar;
	public SQL sql;

	@Override
	public void onEnable() {
		System.out.println("[Ninja] Plugin Engaging!");
		this.file = new FileClass(this);
		this.saveDefaultConfig();
		file.createFile();
		useScoreboard = getConfig().getBoolean("use-scoreboard");
		this.scoreboard = new ScoreBoard(this);
		
		this.kunai = new Kunai(this);
		this.sub = new Substitution(this);
		this.agility = new Agility(this);
		this.chi = new Chidori(this);
		this.ras = new Rasengan(this);
		this.sc = new ShadowClone(this);
		this.med = new Meditate(this);
		this.tai = new Taijutsu(this);
		
		this.bar = new Bar(this);
		this.sql = new SQL(this);
		sql.createTable();
		this.getCommand("ninja").setTabCompleter(new TabClass());
		this.getCommand("n").setTabCompleter(new TabClass());
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(kunai, this);
		this.getServer().getPluginManager().registerEvents(sub, this);
		this.getServer().getPluginManager().registerEvents(agility, this);
		this.getServer().getPluginManager().registerEvents(chi, this);
		this.getServer().getPluginManager().registerEvents(ras, this);
		this.getServer().getPluginManager().registerEvents(sc, this);
		this.getServer().getPluginManager().registerEvents(med, this);
		this.getServer().getPluginManager().registerEvents(tai, this);
		this.getServer().getPluginManager().registerEvents(new Quit(), this);
		
		this.getServer().getPluginManager().registerEvents(scoreboard, this);
		if (!Bukkit.getOnlinePlayers().isEmpty())
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (useBoard()) {
					scoreboard.createBoard(online);
				}
				chi.setActiveMap(online, false);
				ras.setActiveMap(online, false);
				med.setActiveMap(online, false);
				bar.getBar(online).removeAll();
				bar.createBar(online);
				sub.setCD(online, false);
				sql.createPlayer(online);
		}
		
		//MYSQL
		try {
			sql.connect();
		} catch (SQLException e) {
			// Login info is incorrect
			// they are not using a database
			Bukkit.getLogger().info("Database not connected");
		}
		
		
		if (sql.isConnected()) {
			Bukkit.getLogger().info("Database is connected!");
			sql.createTable();
		}
		
	
		//BASE MOVES CREATION
		kunai.createItemKunai();
		sub.createItemSubstitution();
		agility.createItemAgility();
		chi.createItemChidori();
		ras.createItemRas();
		sc.createItemSC();
		med.createItemMed();
		tai.createItemTai();
		
		//GET CONFIG COOLDOWN
		kunai_cooldown_config = getConfig().getInt("cooldowns.kunai");
		agility_cooldown_config = getConfig().getInt("cooldowns.agility");
		sub_cooldown_config = getConfig().getInt("cooldowns.substitution");
		chidori_cooldown_config = getConfig().getInt("cooldowns.chidori");
		ras_cooldown_config = getConfig().getInt("cooldowns.rasengan");
		clone_cooldown_config = getConfig().getInt("cooldowns.shadow-clone");
		med_cooldown_config = getConfig().getInt("cooldowns.meditate");
		tai_cooldown_config = getConfig().getInt("cooldowns.taijutsu");
		
		//GET CONFIG DAMAGE
		chidori_damage_config = getConfig().getDouble("damage.chidori");
		ras_damage_config = getConfig().getDouble("damage.rasengan");
		chakra_damage_config = getConfig().getDouble("damage.chakra-overload");
		tai_damage_config = getConfig().getDouble("damage.taijutsu");
		kunai_damage_config = getConfig().getDouble("damage.kunai");
		
		//GET CONFIG OTHERS
		clone_amt_config = getConfig().getInt("shadow-clone.amt");
		clone_bool_config = getConfig().getBoolean("shadow-clone.use-invisibility");
		wg_region_config = getConfig().getString("protected-region");
		
	}
	
	@Override
	public void onDisable() {
		System.out.println("[Ninja] Plugin Disengaging!");
		sql.disconnect();
	}
	
    public enum RANK{GENIN, CHUNIN, JONIN, SHINOBI, HOKAGE}
	
	public enum MoveType{KUNAI, SUBSTITUTION, AGILITY, CHIDORI, RASENGAN, CLONE, MEDITATE, TAIJUTSU, CHAKRA_OVERLOAD};
	//COOLDOWN RETURN
	public int getCooldown(MoveType mt) {
		if (mt.equals(MoveType.KUNAI)) {
			return kunai_cooldown_config;
		} else if (mt.equals(MoveType.AGILITY)) {
			return agility_cooldown_config;
		} else if (mt.equals(MoveType.SUBSTITUTION)) {
			return sub_cooldown_config;
		} else if (mt.equals(MoveType.CHIDORI)) {
			return chidori_cooldown_config;
		} else if (mt.equals(MoveType.RASENGAN)) {
			return ras_cooldown_config;
		} else if (mt.equals(MoveType.CLONE)) {
			return clone_cooldown_config;
		} else if (mt.equals(MoveType.MEDITATE)) {
			return med_cooldown_config;
		} else if (mt.equals(MoveType.TAIJUTSU)) {
			return tai_cooldown_config;
		} else {
			return -1;
		}
	}
	//DAMAGE RETURN
	public double getDamage(MoveType mt) {
		if (mt.equals(MoveType.CHIDORI)) {
			return chidori_damage_config;
		} else if (mt.equals(MoveType.RASENGAN)) {
			return ras_damage_config;
		} else if (mt.equals(MoveType.CHAKRA_OVERLOAD)) {
			return chakra_damage_config;
		} else if (mt.equals(MoveType.TAIJUTSU)) {
			return tai_damage_config;
		} else if (mt.equals(MoveType.KUNAI)) {
			return kunai_damage_config;
		} else {
			return -1;
		}
	}
	//OTHER RETURNS
	public boolean useInvisibility() {
		return clone_bool_config;
	}
	public int getCloneAmount() {
		return clone_amt_config;
	}
	
	public String getNinjaLevel(Player player) {
		return sql.getRank(player.getName());
	}
	
	//PREFIX
	public String prefix = 
			ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("prefix")) + " ";
	
	//COMMANDS
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!label.equalsIgnoreCase("ninja") && !label.equalsIgnoreCase("n"))
			return true;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (args.length < 1) {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help or /ninja bind <move>");
				return true;
			}
			if (args.length < 2 && !args[0].equalsIgnoreCase("help") && !args[0].equalsIgnoreCase("clear") 
					&& !args[0].equalsIgnoreCase("errors") && !args[0].equalsIgnoreCase("version") && !args[0].equalsIgnoreCase("level")) {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help or /ninja bind <move>");
				return true;
			}
			
			//ARGS SECTION
			if (args[0].equalsIgnoreCase("bind")) {
				switch (args[1].toLowerCase()) { 
					//KUNAI BIND
					case "kunai":
						kunai.createKunai(player, prefix);
						break;
					//SUBSTITION BIND
					case "substitution":
						sub.createSubItem(player, prefix);
						break;
					//CHIDORI BIND
					case "chidori":
						chi.createChiItem(player, prefix);
						break;
					//SHADOW CLONE BIND
					case "clone":
						sc.createSCItem(player, prefix);
						break;
					//AGILITY BINDS
					case "agility":
						agility.createAgItem(player, prefix);
						break;
					//RASENGAN BINDS
					case "rasengan":
						ras.createRasItem(player, prefix);
						break;
					//MEDITATE BINDS
					case "meditate":
						med.createMedItem(player, prefix);
						break;
					//TAIJUTSU
					case "taijutsu":
						tai.createItem(player, prefix);
						break;
					//CLEAR BINDS
					case "clear":
						kunai.clear(player);
						sub.clear(player);
						agility.clear(player);
						chi.clear(player);
						ras.clear(player);
						sc.clear(player);
						med.clear(player);
						tai.clear(player);
						player.sendMessage(prefix + ChatColor.RED + "Binds Cleared!");
						break;
					//SHOW ALL BINDS
					case "list":
						player.sendMessage("");
						player.sendMessage(prefix);
						player.sendMessage("");
						player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Moves:");
						player.sendMessage(ChatColor.AQUA + "Kunai");
						player.sendMessage(ChatColor.AQUA + "Agility");
						player.sendMessage(ChatColor.AQUA + "Substitution");
						player.sendMessage(ChatColor.AQUA + "Clone");
						player.sendMessage(ChatColor.AQUA + "Rasengan");
						player.sendMessage(ChatColor.AQUA + "Chidori");
						player.sendMessage(ChatColor.AQUA + "Meditate");
						player.sendMessage(ChatColor.AQUA + "Taijutsu");
						player.sendMessage("");
						break;
					default:
						player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja bind <move> or /ninja bind list - to see all moves!");
						break;
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				if (args.length < 2) {
					player.sendMessage("");
					player.sendMessage(prefix + ChatColor.YELLOW + "Version: " + getPluginVersion() + " - Created By Nick Doxa");
					player.sendMessage("");
					player.sendMessage(ChatColor.DARK_AQUA  + "" + ChatColor.BOLD + "Commands:");
					player.sendMessage(ChatColor.AQUA + "/Ninja help - brings up help menu!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind <move> - binds move to current slot!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind clear - clears binds!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind list - show all moves!");
					player.sendMessage(ChatColor.AQUA + "/Ninja errors - show error log!");
					player.sendMessage(ChatColor.AQUA + "/Ninja errors clear - clear error log!");
					player.sendMessage(ChatColor.AQUA + "/Ninja level <player> <rank> - set player Ninja Level!");
					player.sendMessage("");
				} else {
					switch(args[1].toLowerCase()) {
						//TAIJUTSU
						case "taijutsu":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + tai.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + tai.getDescription());
							break;
						case "kunai":
							player.sendMessage(ChatColor.DARK_AQUA + kunai.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + kunai.getDescription());
							break;
						//SUBSTITION BIND
						case "substitution":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sub.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sub.getDescription());
							break;
						//CHIDORI BIND
						case "chidori":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + chi.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + chi.getDescription());
							break;
						//SHADOW CLONE BIND
						case "clone":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sc.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sc.getDescription());
							break;
						//AGILITY BINDS
						case "agility":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + agility.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + agility.getDescription());
							break;
						//RASENGAN BINDS
						case "rasengan":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + ras.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + ras.getDescription());
							break;
						//MEDITATE BINDS
						case "meditate":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + ras.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + ras.getDescription());
							break;
						default:
							player.sendMessage(prefix + ChatColor.RED + "Invalid move name. Try /ninja bind list");
							break;
					}
				}
			} else if (args[0].equalsIgnoreCase("clear")) {
				kunai.clear(player);
				sub.clear(player);
				agility.clear(player);
				chi.clear(player);
				ras.clear(player);
				sc.clear(player);
				med.clear(player);
				tai.clear(player);
				player.sendMessage(prefix + ChatColor.RED + "Binds Cleared!");
			} else if (args[0].equalsIgnoreCase("errors") && args.length > 1) {
				switch (args[1].toLowerCase()) {
					case "clear":
						file.clearFile(player, prefix);
						break;
					default:
						player.sendMessage(prefix + ChatColor.RED + "Incorrect arguments: try /ninja errors clear or /ninja errors");
				}
			} else if (args[0].equalsIgnoreCase("errors") && args.length < 2) {
				try {
					file.scanFile(player);
				} catch (IOException e) {
					file.writeReport(e.getCause().toString(), "IOException Reader!");
				}
			} else if (args[0].equalsIgnoreCase("version")) {
				player.sendTitle(ChatColor.AQUA + "Version: " + version, 
						ChatColor.GOLD + "Created by Nick Doxa", 1, 60, 1);
			} else if (args[0].equalsIgnoreCase("level")) {
				if (args.length == 3) {
				try {
				Player p2 = Bukkit.getPlayer(args[1]);
					switch (args[2].toLowerCase()) {
						case "genin":
							sql.setRank(p2.getName(), RANK.GENIN);
							player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
							p2.sendMessage(prefix + ChatColor.GREEN +  "Your Ninja Level is now: Genin!");
							scoreboard.updateBoard(p2);
							break;
						case "chunin":
							sql.setRank(p2.getName(), RANK.CHUNIN);
							player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
							p2.sendMessage(prefix + ChatColor.GREEN +   "Your Ninja Level is now: Chunin!");
							scoreboard.updateBoard(p2);
							break;
						case "jonin":
							sql.setRank(p2.getName(), RANK.JONIN);
							player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
							p2.sendMessage(prefix + ChatColor.GREEN +  "Your Ninja Level is now: Jonin!");
							scoreboard.updateBoard(p2);
							break;
						case "shinobi":
							sql.setRank(p2.getName(), RANK.SHINOBI);
							player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
							p2.sendMessage(prefix + ChatColor.GREEN +   "Your Ninja Level is now: Shinobi!");
							scoreboard.updateBoard(p2);
							break;
						case "hokage":
							sql.setRank(p2.getName(), RANK.HOKAGE);
							player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
							p2.sendMessage(prefix + ChatColor.GREEN +   "Your Ninja Level is now: Hokage!");
							scoreboard.updateBoard(p2);
							break;
						default:
							player.sendMessage(prefix + ChatColor.RED + "Invalid Rank, Try: Genin, Chunin, Jonin, Shinobi, or Hokage!");
							break;
					}
				} catch (NullPointerException e) {
					player.sendMessage(prefix +  ChatColor.RED + args[1] + " is not online or does not exist!");
				}
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help");
				}
			} else {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help");
			}
		} else {
			System.out.println("Consoles can't be ninja im sorry :(");
		}
		return false;
	}
	
	public void endParticles(Player player) {
		ras.removeParticles(player);
		chi.removeParticles(player);
		med.removeParticles(player);
		chi.setActiveMap(player, false);
		ras.setActiveMap(player, false);
		med.setActiveMap(player, false);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		sql.createPlayer(player);
		chi.setActiveMap(player, false);
		ras.setActiveMap(player, false);
		med.setActiveMap(player, false);
		sub.setCD(player, false);
		bar.createBar(player);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (ras.isActive(player)) {
			ras.removeParticles(player);
			ras.setActiveMap(player, false);
		}
		if (chi.isActive(player)) {
			chi.removeParticles(player);
			chi.setActiveMap(player, false);
		}
		if (med.isActive(player)) {
			med.removeParticles(player);
			med.setActiveMap(player, false);
		}
		try {
			if (bar.getBar(player).getPlayers().contains(player)) {
				removeBar(player);
			} else {
				return;
			}
		} catch (NullPointerException e) {
			return;
		}
	}
	
	@EventHandler
	public void onDeathRemoveParticles(PlayerDeathEvent event) {
		Player player = (Player) event.getEntity();
		if (ras.isActive(player)) {
			ras.removeParticles(player);
			ras.setActiveMap(player, false);
		}
		if (chi.isActive(player)) {
			chi.removeParticles(player);
			chi.setActiveMap(player, false);
		}
		if (med.isActive(player)) {
			med.removeParticles(player);
			med.setActiveMap(player, false);
		}
	}
	
	@EventHandler
	public void onKill(PlayerDeathEvent event) {
		if (event.getEntity() instanceof Player && event.getEntity().getKiller() instanceof Player) {
			Player player = (Player) event.getEntity().getKiller();
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			sql.addKills(player.getName(), player.getStatistic(Statistic.PLAYER_KILLS));
			scoreboard.createBoard(player);
		}
	}
	
	public void chakraOverloadMain(Player player) {
		if (ras.isActive(player) || chi.isActive(player)) {
			ras.chakraOverload(player);
			chi.chakraOverload(player);
		}
	}
	
	public void removeBar(Player player) {
		bar.getBar(player).removeAll();
	}
	
	public void useChakra(double d, Player player) {
		bar.useChakra(d, player);
	}
	
	/*
	 * WORLD GUARD
	 * VERSION 6.1
	 */
	
	Map<Player, Boolean> guardMap = new HashMap<Player, Boolean>();
	
	public boolean isPlayerInGuardedRegion(Player player) {
		return guardMap.get(player);
	}
	
	@EventHandler
	public void onRegionEnter(RegionEnteredEvent regionEvent)	{
		Player player = regionEvent.getPlayer();
		if (regionEvent.getRegion().getId().equalsIgnoreCase(wg_region_config)) {
			guardMap.put(player, false);
		}
	}
	
	@EventHandler
	public void onRegionLeave(RegionLeftEvent regionEvent) {
		Player player = regionEvent.getPlayer();
		if (regionEvent.getRegion().getId().equalsIgnoreCase(wg_region_config)) {
			guardMap.put(player, true);
		}
	}
	
	@EventHandler
	public void onGuardedJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		guardMap.put(player, true);
	}
	
	@EventHandler
	public void onGuardedQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		guardMap.remove(player);
	}
	
}