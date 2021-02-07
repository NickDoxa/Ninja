package com.doxa.ninja.moves;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Substitution extends MoveBase implements Listener {

	Main plugin;
	public Substitution(Main main) {
		plugin = main;
	}
	
	public void createItemSubstitution() {
		setName("Substitution", ChatColor.AQUA + "" + ChatColor.BOLD + "Substitution");
		setItem(Material.OAK_LOG);
		setMoveType(MoveType.SUBSTITUTION);
		setDescription("Substitution is a keen tool for a ninjas arsenal."
				+ " By replacing one's self with a log or tree branch, a ninja can evade an enemy attack and"
				+ " disappear. To use: hold substitution while an enemy hits you. You will have a small amount"
				+ " of invisibility and you will not take damage from the attack used as long as you are not"
				+ " on cooldown!");
	}
	
	public void createSubItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<Material, Location> block = new HashMap<Material, Location>();
	Map<String, Long> sub_cd = new HashMap<String, Long>();
	Map<Player, Boolean> msg_cd = new HashMap<Player, Boolean>();
	public void setCD(Player p, boolean b) {
		msg_cd.put(p, b);
	}
	
	public void createLog(Player player, int amt, int cd_amt) {
		if (sub_cd.containsKey(player.getName())) {
			if (sub_cd.get(player.getName()) > System.currentTimeMillis()) {
				long timeleft = (sub_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "Cannot use for " + timeleft + " seconds"));
				return;
			}
		}
		sub_cd.put(player.getName(), System.currentTimeMillis() + (cd_amt * 1000));
		msg_cd.put(player, true);
		boolean cd = msg_cd.get(player) ? true : false;
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        //CHECK FOR ONE TIME RUN
		if (cd) {
	        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	msg_cd.put(player, false);
	            }
	        }, (cd_amt * 20));
		}
		final Location location = player.getLocation();
		final Location location2 = new Location(player.getWorld(), location.getX(), (location.getY() + 1), location.getZ());
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 3 * 20, 1, true, false, false));
		player.setVelocity(player.getLocation().getDirection().multiply(amt).setY(2));
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			@Override
			public void run() {
			player.getWorld().getBlockAt(location).setType(Material.OAK_LOG);
			player.getWorld().getBlockAt(location2).setType(Material.OAK_LOG);
			block.put(Material.OAK_LOG, location);
			block.put(Material.OAK_LOG, location2);
			player.getWorld().spawnParticle(Particle.CLOUD, location.getX(), location.getY(), location.getZ(),
	    			10);
			player.getWorld().spawnParticle(Particle.CLOUD, location2.getX(), location2.getY(), location2.getZ(),
	    			10);
	        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	player.getWorld().getBlockAt(location).setType(Material.AIR);
	            	player.getWorld().spawnParticle(Particle.SPIT, location.getX(), location.getY(), location.getZ(),
	            			10);
	            	player.getWorld().getBlockAt(location2).setType(Material.AIR);
	            	player.getWorld().spawnParticle(Particle.SPIT, location2.getX(), location2.getY(), location2.getZ(),
	            			10);
	            }
	        }, 100);
		  }
	   }, 10);
	}
	
	@EventHandler
	public void onBreak(PlayerItemBreakEvent event) {
		try {
			if (block.get(event.getBrokenItem().getType()).equals(event.getPlayer().getEyeLocation())) {
				event.getPlayer().getInventory().remove(Material.OAK_LOG);
			}
		} catch(NullPointerException e) {
			plugin.writeReport(e.toString(), "log breaking");
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			boolean cd = msg_cd.get(player) ? true : false;
			try {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName())) {
					if (!cd) {
						event.setCancelled(true);
						createLog(player, 10, plugin.getCooldown(MoveType.SUBSTITUTION));
					}
				}	
			} catch (NullPointerException e) {
				plugin.writeReport(e.toString(), "log creation");
			}
		}
	}

	@EventHandler
	public void onItemHold(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		try {
			if (player.getInventory().getItem(event.getNewSlot()).getItemMeta().getDisplayName().equals(getColorName())) {
				if (msg_cd.get(player)) {
					long timeleft = (sub_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "Cooldown - " + timeleft));
				}
			} else {
				return;
			}
		} catch (NullPointerException e) {
			plugin.writeReport(e.toString(), "log creation");
		}
	}
}
