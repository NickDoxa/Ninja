package com.doxa.ninja.moves;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;
import com.doxa.ninja.moves.particles.HealEffect;
import com.doxa.ninja.moves.particles.ParticleData;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Meditate extends MoveBase implements Listener {
	
	Main plugin;
	public Meditate(Main main) {
		plugin = main;
	}
	
	public void createItemMed() {
		setName("Meditate", ChatColor.GREEN + "" + ChatColor.BOLD + "Meditate");
		setItem(Material.SUNFLOWER);
		setMoveType(MoveType.MEDITATE);
		setDescription("Meditation is a cleansing event for a ninja. Used by many to heal the mind but also to"
				+ " heal the body. To use: shift while holding meditate. The longer you shift the more you heal.");
	}
	
	public void createMedItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	public void createMeditation(Player player) {
		HealEffect effect = new HealEffect(player);
		effect.startSpellEffect();
	}
	
	private Map<Player, Boolean> Active_Map = new HashMap<Player, Boolean>();
	public void setActiveMap(Player p, Boolean b) {
		Active_Map.put(p, b);
	}
	public boolean isActive(Player p) {
		return Active_Map.get(p);
	}
	
	public void removeParticles(Player player) {
    	getParticles(player).endTask();
    	getParticles(player).removeID();
	}
	
	Map<String, Long> med_cd = new HashMap<String, Long>();
	Map<Player, ParticleData> particles = new HashMap<Player, ParticleData>();
	public ParticleData getParticles(Player player) {
		return particles.get(player);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.isSneaking()) {
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			particles.put(player, new ParticleData(player.getUniqueId()));
			if (med_cd.containsKey(player.getName())) {
				if (med_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (med_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use for " + timeleft + " seconds"));
					return;
				}
			}
			med_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.MEDITATE) * 1000));
			Active_Map.put(player, true);
			plugin.useChakra(0.2, player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 2, true, false, false));
			createMeditation(player);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	if (isActive(player)) {
	            		try {
		            	Active_Map.put(player, false);
		            	removeParticles(player);
	            		} catch (NullPointerException e) {
	            			plugin.writeReport(e.toString(), "particles");
	            		}
	            	}
	            }
			}, (5 * 20));
		} else {
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			player.removePotionEffect(PotionEffectType.REGENERATION);
		}
	}
	
}
