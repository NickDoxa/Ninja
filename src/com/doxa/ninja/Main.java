package com.doxa.ninja;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;

import com.doxa.ninja.bar.Bar;
import com.doxa.ninja.files.FileClass;
import com.doxa.ninja.levels.SQL;
import com.doxa.ninja.moves.Agility;
import com.doxa.ninja.moves.Chidori;
import com.doxa.ninja.moves.Dome;
import com.doxa.ninja.moves.FireballJutsu;
import com.doxa.ninja.moves.Hiraishin;
import com.doxa.ninja.moves.Kunai;
import com.doxa.ninja.moves.Meditate;
import com.doxa.ninja.moves.Rasengan;
import com.doxa.ninja.moves.SageMode;
import com.doxa.ninja.moves.ShadowClone;
import com.doxa.ninja.moves.SmokeBomb;
import com.doxa.ninja.moves.Substitution;
import com.doxa.ninja.moves.Taijutsu;
import com.doxa.ninja.moves.particles.Quit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

public class Main extends JavaPlugin implements Listener {
	
	private double version = 1.4;
	public double getPluginVersion() {
		return version;
	}
	
	//PERMISSIONS
	public String default_perm = "ninja.player";
	public String admin_perm = "ninja.admin";
	public boolean isPlayerAdmin(Player player) {
		if (player.hasPermission(admin_perm)) {
			return true;
		} else {
			return false;
		}
	}
	
	//MOVE PERMISSIONS
	public String kunai_perm = "ninja.moves.kunai";
	public String exp_kunai_perm = "ninja.moves.expkunai";
	public String agility_perm = "ninja.moves.agility";
	public String sub_perm = "ninja.moves.substitution";
	public String chidori_perm = "ninja.moves.chidori";
	public String ras_perm = "ninja.moves.rasengan";
	public String med_perm = "ninja.moves.meditate";
	public String clone_perm = "ninja.moves.clone";
	public String tai_perm = "ninja.moves.taijutsu";
	public String hir_perm = "ninja.moves.hiraishin";
	public String fb_perm = "ninja.moves.fireball";
	public String sage_perm = "ninja.moves.sagemode";
	public String sb_perm = "ninja.moves.smokebomb";
	public String dome_perm = "ninja.moves.dome";
	
	//MOVES CLASSES
	public Kunai kunai;
	public Substitution sub;
	public Agility agility;
	public Chidori chi;
	public Rasengan ras;
	public ShadowClone sc;
	public Meditate med;
	public Taijutsu tai;
	public Hiraishin hir;
	public FireballJutsu fb;
	public SageMode sgm;
	public SmokeBomb sb;
	public Dome dome;

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
	private int hir_cooldown_config;
	private int fb_cooldown_config;
	private int sgm_cooldown_config;
	private int sb_cooldown_config;
	private int dome_cooldown_config;
	
	//DAMAGE VARIABLES
	private double chidori_damage_config;
	private double ras_damage_config;
	private double chakra_damage_config;
	private double kunai_damage_config;
	private double tai_damage_config;
	private double fb_damage_config;
	
	//OTHER VARIABLES
	private int clone_amt_config;
	private boolean clone_bool_config;
	private List<String> wg_region_config;
	
	//OTHER CLASSES
	public FileClass file;
	public void writeReport(String error, String reason) {
		file.writeReport(error, reason);
	}
	public Bar bar;
	public SQL sql;

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		System.out.println("[Ninja] Plugin Engaging!");
		this.file = new FileClass(this);
		this.saveDefaultConfig();
		file.createFile();
		useScoreboard = getConfig().getBoolean("use-scoreboard");
		this.scoreboard = new ScoreBoard(this);
		//ABILITIES
		this.kunai = new Kunai(this);
		this.sub = new Substitution(this);
		this.agility = new Agility(this);
		this.chi = new Chidori(this);
		this.ras = new Rasengan(this);
		this.sc = new ShadowClone(this);
		this.med = new Meditate(this);
		this.tai = new Taijutsu(this);
		this.hir = new Hiraishin(this);
		this.fb = new FireballJutsu(this);
		this.sgm = new SageMode(this);
		this.sb = new SmokeBomb(this);
		this.dome = new Dome(this);
		
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
		this.getServer().getPluginManager().registerEvents(hir, this);
		this.getServer().getPluginManager().registerEvents(fb, this);
		this.getServer().getPluginManager().registerEvents(sgm, this);
		this.getServer().getPluginManager().registerEvents(sb, this);
		this.getServer().getPluginManager().registerEvents(dome, this);
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
		hir.createItemHir();
		fb.createItemFire();
		sgm.createItemSage();
		sb.createItemSB();
		dome.createItemDome();
		
		//GET CONFIG COOLDOWN
		kunai_cooldown_config = getConfig().getInt("cooldowns.kunai");
		agility_cooldown_config = getConfig().getInt("cooldowns.agility");
		sub_cooldown_config = getConfig().getInt("cooldowns.substitution");
		chidori_cooldown_config = getConfig().getInt("cooldowns.chidori");
		ras_cooldown_config = getConfig().getInt("cooldowns.rasengan");
		clone_cooldown_config = getConfig().getInt("cooldowns.shadow-clone");
		med_cooldown_config = getConfig().getInt("cooldowns.meditate");
		tai_cooldown_config = getConfig().getInt("cooldowns.taijutsu");
		hir_cooldown_config = getConfig().getInt("cooldowns.raijin");
		fb_cooldown_config = getConfig().getInt("cooldowns.fireball");
		sgm_cooldown_config = getConfig().getInt("cooldowns.sagemode");
		sb_cooldown_config = getConfig().getInt("cooldowns.smokebomb");
		dome_cooldown_config = getConfig().getInt("cooldowns.dome");
		
		//GET CONFIG DAMAGE
		chidori_damage_config = getConfig().getDouble("damage.chidori");
		ras_damage_config = getConfig().getDouble("damage.rasengan");
		chakra_damage_config = getConfig().getDouble("damage.chakra-overload");
		tai_damage_config = getConfig().getDouble("damage.taijutsu");
		kunai_damage_config = getConfig().getDouble("damage.kunai");
		fb_damage_config = getConfig().getDouble("damage.fireball");
		
		//GET CONFIG OTHERS
		clone_amt_config = getConfig().getInt("shadow-clone.amt");
		clone_bool_config = getConfig().getBoolean("shadow-clone.use-invisibility");
		wg_region_config = (List<String>) getConfig().getList("protected-region");
		
	}
	
	@Override
	public void onDisable() {
		System.out.println("[Ninja] Plugin Disengaging!");
		sql.disconnect();
	}
	
    public enum RANK{GENIN, CHUNIN, JONIN, HOKAGE}
	
	public enum MoveType{KUNAI, SUBSTITUTION, AGILITY, CHIDORI, RASENGAN, CLONE, MEDITATE, TAIJUTSU, CHAKRA_OVERLOAD, EXPLOSIVE_KUNAI, HIRAISHIN, FIREBALL, SAGEMODE, SMOKEBOMB,
		DOME};
	
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
		} else if (mt.equals(MoveType.HIRAISHIN)) {
			return hir_cooldown_config;
		} else if (mt.equals(MoveType.FIREBALL)) {
			return fb_cooldown_config;
		} else if (mt.equals(MoveType.SAGEMODE)) {
			return sgm_cooldown_config;
		} else if (mt.equals(MoveType.SMOKEBOMB)) {
			return sb_cooldown_config;
		} else if (mt.equals(MoveType.DOME)) {
			return dome_cooldown_config;
		} else {
			return -1;
		}
	}
	//DAMAGE RETURN
	public double getDamage(MoveType mt, Player player) {
		double i=0;
			if (mt.equals(MoveType.CHIDORI)) {
				i = chidori_damage_config;
			} else if (mt.equals(MoveType.RASENGAN)) {
				i = ras_damage_config;
			} else if (mt.equals(MoveType.CHAKRA_OVERLOAD)) {
				i = chakra_damage_config;
			} else if (mt.equals(MoveType.TAIJUTSU)) {
				i = tai_damage_config;
			} else if (mt.equals(MoveType.KUNAI)) {
				i = kunai_damage_config;
			} else if (mt.equals(MoveType.FIREBALL)) {
				i = fb_damage_config;
			} else {
				i = 0;
			}
			if (sgm.isActive(player)) {
				return (i*1.5);
			} else {
				return i;
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
					&& !args[0].equalsIgnoreCase("errors") && !args[0].equalsIgnoreCase("version") && !args[0].equalsIgnoreCase("level")
					&& !args[0].equalsIgnoreCase("mission")) {
				player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help or /ninja bind <move>");
				return true;
			}
			
			//ARGS SECTION
			if (args[0].equalsIgnoreCase("bind")) {
				switch (args[1].toLowerCase()) { 
					//KUNAI BIND
					case "kunai":
						if (player.hasPermission(kunai_perm)) {
							kunai.createKunai(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Kunai!");
						}
						break;
					//SUBSTITION BIND
					case "substitution":
						if (player.hasPermission(sub_perm)) {
							sub.createSubItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Substitution!");
						}
						break;
					//CHIDORI BIND
					case "chidori":
						if (player.hasPermission(chidori_perm)) {
							chi.createChiItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Chidori!");
						}
						break;
					//SHADOW CLONE BIND
					case "clone":
						if (player.hasPermission(clone_perm)) {
							sc.createSCItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Shadow Clones!");
						}
						break;
					//AGILITY BINDS
					case "agility":
						if (player.hasPermission(agility_perm)) {
							agility.createAgItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Agility!");
						}
						break;
					//RASENGAN BINDS
					case "rasengan":
						if (player.hasPermission(ras_perm)) {
							ras.createRasItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Rasengan!");
						}
						break;
					//MEDITATE BINDS
					case "meditate":
						if (player.hasPermission(med_perm)) {
							med.createMedItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Meditate!");
						}
						break;
					//TAIJUTSU
					case "taijutsu":
						if (player.hasPermission(tai_perm)) {
							tai.createItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Taijutsu!");
						}
						break;
					//FLYING RAIJIN
					case "raijin":
						if (player.hasPermission(hir_perm)) {
							hir.createHiraishin(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Flying Raijin!");
						}
						break;
					//FIREBALL JUTSU
					case "fireball":
						if (player.hasPermission(fb_perm)) {
							fb.createFireItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Fireball Jutsu!");
						}
						break;
					//SAGEMODE BINDS
					case "sagemode":
						if (player.hasPermission(sage_perm)) {
							sgm.createItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Sage Mode!");
						}
						break;
					//SMOKEBOMB BINDS
					case "smokebomb":
						if (player.hasPermission(sb_perm)) {
							sb.createItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Smoke Bomb!");
						}
						break;		
					//DOME BINDS
					case "dome":
						if (player.hasPermission(dome_perm)) {
							dome.createItem(player, prefix);
						} else {
							player.sendMessage(prefix + ChatColor.RED + "You do not have access to Dome!");
						}
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
						sgm.clear(player);
						sb.clear(player);
						dome.clear(player);
						player.sendMessage(prefix + ChatColor.RED + "Binds Cleared!");
						break;
					//SHOW ALL BINDS
					case "list":
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
						player.sendMessage(ChatColor.AQUA + "Raijin");
						player.sendMessage(ChatColor.AQUA + "Fireball");
						player.sendMessage(ChatColor.AQUA + "SageMode");
						player.sendMessage(ChatColor.AQUA + "SmokeBomb");
						player.sendMessage(ChatColor.AQUA + "Dome");
						player.sendMessage("");
						break;
					default:
						player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja bind <move> or /ninja bind list - to see all moves!");
						break;
				}
			} else if (args[0].equalsIgnoreCase("help")) {
				if (args.length < 2) {
					//PLAYER HELP
					player.sendMessage("");
					player.sendMessage(prefix + ChatColor.YELLOW + "Version: " + getPluginVersion() + " - Created By Nick Doxa");
					player.sendMessage("");
					player.sendMessage(ChatColor.DARK_AQUA  + "" + ChatColor.BOLD + "Commands:");
					player.sendMessage(ChatColor.AQUA + "/Ninja help - brings up help menu!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind <move> - binds move to current slot!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind clear - clears binds!");
					player.sendMessage(ChatColor.AQUA + "/Ninja bind list - show all moves!");
					//ADMIN HELP
					if (this.isPlayerAdmin(player)) {
						player.sendMessage(ChatColor.AQUA + "/Ninja errors - show error log!");
						player.sendMessage(ChatColor.AQUA + "/Ninja errors clear - clear error log!");
						player.sendMessage(ChatColor.AQUA + "/Ninja level <player> <rank> - set player Ninja Level!");
					}
					player.sendMessage("");
				} else {
					switch(args[1].toLowerCase()) {
						//TAIJUTSU HELP
						case "taijutsu":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + tai.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + tai.getDescription());
							break;
						//KUNAI HELP
						case "kunai":
							player.sendMessage(ChatColor.DARK_AQUA + kunai.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + kunai.getDescription());
							break;
						//SUBSTITION HELP
						case "substitution":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sub.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sub.getDescription());
							break;
						//CHIDORI HELP
						case "chidori":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + chi.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + chi.getDescription());
							break;
						//SHADOW CLONE HELP
						case "clone":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sc.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sc.getDescription());
							break;
						//AGILITY HELP
						case "agility":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + agility.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + agility.getDescription());
							break;
						//RASENGAN HELP
						case "rasengan":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + ras.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + ras.getDescription());
							break;
						//MEDITATE HELP
						case "meditate":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + ras.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + ras.getDescription());
							break;
						//HIRAISHIN HELP
						case "raijin":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + hir.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + hir.getDescription());
							break;
						//FIREBALL HELP
						case "fireball":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + fb.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + fb.getDescription());
						//SAGEMODE HELP
						case "sagemode":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sgm.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sgm.getDescription());
							break;
						//SMOKEBOMB HELP
						case "smokebomb":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + sb.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + sb.getDescription());
							break;
						//DOME HELP
						case "dome":
							player.sendMessage(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + dome.getName());
							player.sendMessage("");
							player.sendMessage(ChatColor.AQUA + dome.getDescription());
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
				hir.clear(player);
				fb.clear(player);
				sgm.clear(player);
				sb.clear(player);
				dome.clear(player);
				player.sendMessage(prefix + ChatColor.RED + "Binds Cleared!");
			} else if (args[0].equalsIgnoreCase("errors") && args.length > 1) {
				if (this.isPlayerAdmin(player)) {
					switch (args[1].toLowerCase()) {
						case "clear":
							file.clearFile(player, prefix);
							break;
						default:
							player.sendMessage(prefix + ChatColor.RED + "Incorrect arguments: try /ninja errors clear or /ninja errors");
					}
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Insufficient Permissions!");
				}
			} else if (args[0].equalsIgnoreCase("errors") && args.length < 2) {
				if (this.isPlayerAdmin(player)) {
					try {
						file.scanFile(player);
					} catch (IOException e) {
						file.writeReport(e.getCause().toString(), "IOException Reader!");
					}
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Insufficient Permissions!");
				}
			} else if (args[0].equalsIgnoreCase("version")) {
				player.sendTitle(ChatColor.AQUA + "Version: " + version, 
						ChatColor.GOLD + "Created by Nick Doxa", 1, 60, 1);
			} else if (args[0].equalsIgnoreCase("level")) {
				if (this.isPlayerAdmin(player)) {
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
							case "hokage":
								sql.setRank(p2.getName(), RANK.HOKAGE);
								player.sendMessage(prefix + ChatColor.GREEN + "Ninja Level Updated!");
								p2.sendMessage(prefix + ChatColor.GREEN +   "Your Ninja Level is now: Hokage!");
								scoreboard.updateBoard(p2);
								break;
							default:
								player.sendMessage(prefix + ChatColor.RED + "Invalid Rank, Try: Genin, Chunin, Jonin, or Hokage!");
								break;
						}
					} catch (NullPointerException e) {
						player.sendMessage(prefix +  ChatColor.RED + args[1] + " is not online or does not exist!");
					}
					} else {
						player.sendMessage(prefix + ChatColor.RED + "Incorrect Usage: /ninja help");
					}
				} else {
					player.sendMessage(prefix + ChatColor.RED + "Insufficient Permissions!");
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
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.ABSORPTION);
		player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
		player.setGlowing(false);
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
	 * VERSION 1.16
	 */
	
	Map<Player, Boolean> guardMap = new HashMap<Player, Boolean>();
	
    public boolean isInProtectedRegion(Player player) {
    	return guardMap.get(player);
    }
    
    @EventHandler
    public void onMoveInRegion(PlayerMoveEvent event) {
    	Player player = event.getPlayer();
    	Location loc = BukkitAdapter.adapt(player.getLocation());
    	RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    	RegionQuery query = container.createQuery();
    	ApplicableRegionSet set = query.getApplicableRegions(loc);
    	try {
    	if (set.size() < 1) {
    		guardMap.put(player, false);
    	}
    	for (ProtectedRegion r : set.getRegions()) {
    		if (wg_region_config.contains(r.getId())) {
    			guardMap.put(player, true);
    		} else {
    			guardMap.put(player, false);
    		}
    	}
    	} catch (NullPointerException e) {
    		guardMap.put(player, false);
    	}
    }
    
    @EventHandler
    public void onJoinGuard(PlayerJoinEvent event) {
    	Player player = event.getPlayer();
    	guardMap.put(player, true);
    }
}