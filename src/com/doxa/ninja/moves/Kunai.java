package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitScheduler;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Kunai extends MoveBase implements Listener {
	
	Main plugin;
	public Kunai(Main main) {
		this.plugin = main;
	}
	
	public void createItemKunai() {
		setName("Kunai", ChatColor.RED + "" + ChatColor.BOLD + "Kunai");
		setItem(Material.WOODEN_SWORD);
		setMoveType(MoveType.KUNAI);
	}
	
	public void createKunai(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	
	//COOLDOWN MAP
	Map<String, Long> kunai_cd = new HashMap<String, Long>();

	List<Arrow> shooter = new ArrayList<Arrow>();
	
	public void throwKunai(Player player) {
		if (kunai_cd.containsKey(player.getName())) {
			if (kunai_cd.get(player.getName()) > System.currentTimeMillis()) {
				long timeleft = (kunai_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "Cannot throw for " + timeleft + " seconds"));
				return;
			}
		}
		kunai_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.KUNAI) * 1000));
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setDamage(3.0);
		shooter.add(arrow);
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Arrow arrow = player.launchProjectile(Arrow.class);
				arrow.setDamage(3.0);
				shooter.add(arrow);
			}
		}, 4);
		scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				Arrow arrow = player.launchProjectile(Arrow.class);
				arrow.setDamage(3.0);
				shooter.add(arrow);
			}
		}, 8);
	}
	
	//Throw the knife
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
				throwKunai(player);
		}
	}
	
	@EventHandler
	public void arrowHit(ProjectileHitEvent event) {
		Projectile arrow = event.getEntity();
		if (arrow.getShooter() instanceof Player) {
			if (arrow instanceof Arrow && shooter.contains(arrow)) {
				//GET RID OF ARROW
				arrow.remove();
			}
		}
	}
	
}
