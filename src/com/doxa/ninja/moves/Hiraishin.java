package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Hiraishin extends MoveBase implements Listener {

	Main plugin;
	public Hiraishin(Main main) {
		this.plugin = main;
	}
	
	public void createItemHir() {
		setName("Flying Raijin", ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Flying Raijin");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.HIRAISHIN);
		setDescription("The Flying Raijin is a projectile Kunai that you can throw and teleport to. The key difference between the Flying Raijin and an"
				+ " Ender Pearl is you can teleport to the Flying Raijin whenever you left click. To use: right click to throw the kunai, and then left click to"
				+ " teleport. After " + plugin.getCooldown(MoveType.HIRAISHIN) + " minutes your kunai returns to you if you haven't teleported. If you hit someone with your arrow"
				+ " you are instantly teleported to them.");
	}
	
	public void createHiraishin(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (plugin.isInProtectedRegion(player))
				return;
			launchProjectile(player);
		}
	}
	
	private Map<Player, Boolean> use_bool = new HashMap<Player, Boolean>();
	
	private Map<Player, Location> arrow_loc = new HashMap<Player, Location>();
	private Map<Arrow, Player> arrow_shooter = new HashMap<Arrow, Player>();
	private Map<Arrow, Integer> taskID = new HashMap<Arrow, Integer>();
	private Map<String, Long> hir_cd = new HashMap<String, Long>();
	private List<Arrow> shooter = new ArrayList<Arrow>();
	private Map<Player, Boolean> one_time_use = new HashMap<Player, Boolean>();
	public void launchProjectile(Player player) {
		if (use_bool.containsKey(player)) {
			if (use_bool.get(player).equals(false)) {
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "You already have a Flying Raijin active. Left Click!"));
				return;
			}
		}
		if (hir_cd.containsKey(player.getName())) {
			if (hir_cd.get(player.getName()) > System.currentTimeMillis()) {
				long timeleft = (hir_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
						new TextComponent(ChatColor.RED + "Cannot throw Flying Raijin for " + (timeleft+1) + " seconds"));
				return;
			}
		}
		hir_cd.put(player.getName(), System.currentTimeMillis() + ((plugin.getCooldown(MoveType.KUNAI) * 1000)*3));
		use_bool.put(player, false);
		one_time_use.put(player, true);
		Arrow arrow = player.launchProjectile(Arrow.class);
		shooter.add(arrow);
		arrow_shooter.put(arrow, player);
		int task;
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				arrow_loc.put(player, arrow.getLocation());
			}
			
		},0, 1);
		taskID.put(arrow, task);
	}
	
	@EventHandler
	public void arrowHit(ProjectileHitEvent event) {
		Projectile arrow = event.getEntity();
		if (arrow.getShooter() instanceof Player) {
			if (arrow instanceof Arrow && shooter.contains(arrow)) {
				//GET RID OF ARROW
				Bukkit.getScheduler().cancelTask(taskID.get(arrow));
				arrow_loc.remove(arrow_shooter.get(arrow));
				arrow_loc.put(arrow_shooter.get(arrow), arrow.getLocation());
				arrow.remove();
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity damager = event.getDamager();
		if (damager instanceof Arrow && shooter.contains(damager)) {
			Arrow arrow = (Arrow) damager;
			//TELEPORT TO ENTITY LOCATION TODO
			Player player = arrow_shooter.get(arrow);
			player.teleport(entity.getLocation());
			use_bool.put(player, true);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLeftClick(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			Player player = event.getPlayer();
			if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
				return;
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			if (plugin.isInProtectedRegion(player))
				return;
			if (one_time_use.containsKey(player)) {
				if (one_time_use.get(player).equals(false)) {
					return;
				}
			}
			player.teleport(arrow_loc.get(player));
			use_bool.put(player, true);
			one_time_use.put(player, false);
		}
	}
	
}
