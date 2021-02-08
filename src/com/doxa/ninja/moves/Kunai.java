package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
		setDescription("The Kunai is a key tool in a ninjas arsenal. Used to throw or for hand to hand combat,"
				+ " the kunai is very versatile. To use: right click to throw, "
				+ "or left click an opponent to strike, or shift right click to throw an explosive arrow!");
	}
	
	public void createKunai(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	
	//COOLDOWN MAP
	Map<String, Long> kunai_cd = new HashMap<String, Long>();
	Map<String, Long> kunai_exp_cd = new HashMap<String, Long>();

	//Regular kunai list
	List<Arrow> shooter = new ArrayList<Arrow>();
	//Explosive kunai list
	List<Arrow> explosive = new ArrayList<Arrow>();
	
	//PARTICLES
	Map<Arrow, Integer> taskID = new HashMap<Arrow, Integer>();
	
	public void throwKunai(Player player) {
		if (player.isSneaking()) {
			if (kunai_exp_cd.containsKey(player.getName())) {
				if (kunai_exp_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (kunai_exp_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot throw explosive kunai for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			kunai_exp_cd.put(player.getName(), System.currentTimeMillis() + ((plugin.getCooldown(MoveType.KUNAI) * 1000)*3));
			Arrow arrow = player.launchProjectile(Arrow.class);
			arrow.setDamage(plugin.getDamage(MoveType.KUNAI));
			explosive.add(arrow);
			int task;
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					arrow.getWorld().spawnParticle(Particle.DRIP_LAVA, arrow.getLocation(), 1);
				}
				
			},0, 1);
			taskID.put(arrow, task);
		} else {
			if (kunai_cd.containsKey(player.getName())) {
				if (kunai_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (kunai_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot throw for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			kunai_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.KUNAI) * 1000));
			Arrow arrow = player.launchProjectile(Arrow.class);
			arrow.setDamage(plugin.getDamage(MoveType.KUNAI));
			shooter.add(arrow);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Arrow arrow = player.launchProjectile(Arrow.class);
					arrow.setDamage(plugin.getDamage(MoveType.KUNAI));
					shooter.add(arrow);
				}
			}, 4);
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					Arrow arrow = player.launchProjectile(Arrow.class);
					arrow.setDamage(plugin.getDamage(MoveType.KUNAI));
					shooter.add(arrow);
				}
			}, 8);
		}
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
			} else if (arrow instanceof Arrow && explosive.contains(arrow)) {
				Bukkit.getScheduler().cancelTask(taskID.get(arrow));
				arrow.remove();
			}
		}
	}
	
	@EventHandler
	public void arrowDamage(EntityDamageByEntityEvent event) {
		if (!event.getCause().equals(DamageCause.PROJECTILE))
			return;
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (explosive.contains(arrow)) {
				Entity entity = event.getEntity();
				entity.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, entity.getLocation(), 1);
				entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				entity.setFireTicks(3*20);
			}
		}
	}
	
}
