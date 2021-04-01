package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class SageMode extends MoveBase implements Listener {
	
	Main plugin;
	public SageMode(Main main) {
		this.plugin = main;
	}

	public void createItemSage() {
		setName("Sage Mode", ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Sage Mode");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.SAGEMODE);
		setDescription("Sage Mode is a technique that gives the user a strength buff and increases the power of there jutus!"
				+ " To use: Right click with sage mode and then use your jutsu and weapons!");
	}
	
	public void createSageItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<String, Long> sgm_cd = new HashMap<String, Long>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (plugin.isInProtectedRegion(player))
				return;
			
			if (sgm_cd.containsKey(player.getName())) {
				if (sgm_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (sgm_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use SageMode for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			sgm_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.SAGEMODE) * 1000));
			activateMode(player);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	deactivateMode(player);
	            }
			}, (20*20));
		}
	}
	
	public Map<Player, Boolean> sgm_map = new HashMap<Player, Boolean>();
	
	public void activateMode(Player player) {
		sgm_map.put(player, true);
		player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100000000, 2));
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
				new TextComponent(ChatColor.AQUA + "SAGE MODE ACTIVE!"));
	}
	
	public void deactivateMode(Player player) {
		sgm_map.put(player, false);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.ABSORPTION);
		player.setGlowing(false);
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
				new TextComponent(ChatColor.RED + "SAGE MODE DEACTIVATED!"));
	}
	
	public boolean isActive(Player player) {
		if (sgm_map.containsKey(player)) {
			return sgm_map.get(player);
		} else {
			return false;
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (this.isActive(player)) {
			player.setGlowing(true);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100000000, 3));
		}
	}
	
}
