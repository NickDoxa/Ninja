package com.doxa.ninja.moves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitScheduler;
import com.doxa.ninja.Main;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class FireballJutsu extends MoveBase implements Listener {
	
	Main plugin;
	public FireballJutsu(Main main) {
		this.plugin = main;
	}

	public void createItemFire() {
		setName("Fireball", ChatColor.DARK_RED + "" + ChatColor.BOLD + "Fireball");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.FIREBALL);
		setDescription("Fireball Jutsu, known best by the Uchiha Clan, is a powerful Jutsu that emits a powerful fireball that on"
				+ " impact causes massive damage and fire tick. To Use: Hold shift and then right click!");
	}
	
	public void createFireItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	Map<Player, Boolean> use = new HashMap<Player, Boolean>();
	Map<Player, Boolean> cooldown = new HashMap<Player, Boolean>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onShift(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().getType() == null || player.getItemInHand().getType() == Material.AIR)
			return;
		if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
			return;
		if (plugin.isInProtectedRegion(player))
			return;
		
		if (player.isSneaking()) {
			if (cooldown.containsKey(player)) {
				if (cooldown.get(player)) {
					long timeleft = (fire_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use Fireball for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			use.put(player, true);
		}
	}
	
	Map<String, Long> fire_cd = new HashMap<String, Long>();
	
	List<Fireball> fbs = new ArrayList<Fireball>();
	
	//PARTICLES
	Map<Fireball, Integer> taskID = new HashMap<Fireball, Integer>();
	
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
			if (use.containsKey(player)) {
				if (use.get(player)) {
					if (fire_cd.containsKey(player.getName())) {
						if (fire_cd.get(player.getName()) > System.currentTimeMillis()) {
							long timeleft = (fire_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
							player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
									new TextComponent(ChatColor.RED + "Cannot use Fireball for " + (timeleft+1) + " seconds"));
							return;
						}
					}
					fire_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.FIREBALL) * 1000));
					launchFireball(player);
					plugin.useChakra(0.2, player);
					use.put(player, false);
					cooldown.put(player, true);
					BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			        scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
			            @Override
			            public void run() {
			            	cooldown.put(player, false);
			            }
			        }, (plugin.getCooldown(MoveType.FIREBALL)*20));
				}
			}
		}
	}

	private void launchFireball(Player player) {
		if (player.isSneaking()) {
			Fireball fb = player.launchProjectile(LargeFireball.class);
			fbs.add(fb);
			fb.setIsIncendiary(true);
			fb.setShooter(player);
			fb.setVelocity(fb.getDirection().multiply(24));
			int task;
			task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					Location fl = fb.getLocation();
					fb.getWorld().spawnParticle(Particle.FLAME, fb.getLocation(), 1);
					fb.getWorld().spawnParticle(Particle.FLAME, new Location(fl.getWorld(), fl.getX() + 0.5, fl.getY(), fl.getZ()), 0);
					fb.getWorld().spawnParticle(Particle.FLAME, new Location(fl.getWorld(), fl.getX(), fl.getY() + 0.5, fl.getZ()), 0);
					fb.getWorld().spawnParticle(Particle.FLAME, new Location(fl.getWorld(), fl.getX(), fl.getY(), fl.getZ() + 0.5), 0);
				}
				
			},0, 1);
			taskID.put(fb, task);
			use.put(player, false);
		}
	}
	
	@EventHandler
	public void onHit(ProjectileHitEvent event) {
		Projectile fb = event.getEntity();
		if (fb.getShooter() instanceof Player) {
			if (fb instanceof Fireball && fbs.contains(fb)) {
				Bukkit.getScheduler().cancelTask(taskID.get(fb));
				
			}
		}
	}
	
	@EventHandler
	public void arrowDamage(EntityDamageByEntityEvent event) {
		if (!event.getCause().equals(DamageCause.PROJECTILE))
			return;
		if (event.getDamager() instanceof Fireball) {
			Fireball fb = (Fireball) event.getDamager();
			if (fbs.contains(fb)) {
				Entity entity = event.getEntity();
				entity.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, entity.getLocation(), 1);
				entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
				entity.setFireTicks(5*20);
				if (entity instanceof Player) {
					final double health = ((Player) entity).getHealth();
					double damage = 0;
					try {
					damage = plugin.getDamage(MoveType.FIREBALL, ((Player)event.getDamager()));
					} catch (ClassCastException e) {
						plugin.writeReport("Class Cast On Fireball", e.toString());
					}
					double new_health = (health - damage);
					if (new_health > 0.1) {
						((Player) entity).setHealth(new_health);
						event.setDamage(0.1);
					} else {
						((Player) entity).setHealth(0.1);
						event.setDamage(0.1);
					}
				}
				
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
	    if(event.getEntity() instanceof Fireball && event.getDamager() instanceof Player) {
	    	Fireball fb = (Fireball) event.getEntity();
	    	if (fbs.contains(fb)) {	
	    		event.setCancelled(true);
	    	}
	    }
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		Entity e = event.getEntity();
		if (e instanceof Fireball && fbs.contains(e)) {
			event.setCancelled(true);
		}
	}
	
}
