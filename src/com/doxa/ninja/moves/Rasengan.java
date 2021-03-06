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
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import com.doxa.ninja.Main;
import com.doxa.ninja.moves.particles.ParticleData;
import com.doxa.ninja.moves.particles.SpellEffect;
import com.doxa.ninja.Main.MoveType;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Rasengan extends MoveBase implements Listener {

	Main plugin;
	public Rasengan(Main main) {
		plugin = main;
	}
	
	public void createItemRas() {
		setName("Rasengan", ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Rasengan");
		setItem(Material.NETHERITE_INGOT);
		List<String> lore = new ArrayList<String>();
		lore.add("");
		setLore(lore);
		setMoveType(MoveType.RASENGAN);
		setDescription("Rasengan, a rare move that many ninja cannot master, is a small ball of chakra"
				+ " bound together tightly. To use the rasengan: right click to begin loading your chakra"
				+ ", then left click your opponent. After 5 seconds your chakra will return to normal"
				+ " Using Chidori and Rasengan at the same time will overload"
				+ " your chakra and damage you badly. This move deals heavy knockback aswell.");
	}
	
	public void createRasItem(Player player, String prefix) {
		createItem(player, prefix);
	}
	
	
	private Map<Player, Boolean> Active_Map = new HashMap<Player, Boolean>();
	public void setActiveMap(Player p, Boolean b) {
		Active_Map.put(p, b);
	}
	public boolean isActive(Player p) {
		return Active_Map.get(p);
	}
	
	public void createRasengan(Player player) {
		SpellEffect effect = new SpellEffect(player);
		effect.startSpellEffect();
	}
	
	Map<String, Long> ras_cd = new HashMap<String, Long>();
	Map<Player, ParticleData> particles = new HashMap<Player, ParticleData>();
	public ParticleData getParticles(Player player) {
		return particles.get(player);
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
			particles.put(player, new ParticleData(player.getUniqueId()));
			if (ras_cd.containsKey(player.getName())) {
				if (ras_cd.get(player.getName()) > System.currentTimeMillis()) {
					long timeleft = (ras_cd.get(player.getName()) - System.currentTimeMillis()) / 1000;
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, 
							new TextComponent(ChatColor.RED + "Cannot use for " + (timeleft+1) + " seconds"));
					return;
				}
			}
			ras_cd.put(player.getName(), System.currentTimeMillis() + (plugin.getCooldown(MoveType.RASENGAN) * 1000));
			Active_Map.put(player, true);
			plugin.useChakra(0.2, player);
			plugin.chakraOverloadMain(player);
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 5 * 20, 3, true, false, false));
			player.getWorld().playSound(player.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
			chakraBuild(player);
			createRasengan(player);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			scheduler.scheduleSyncDelayedTask(plugin, new Runnable() {
	            @Override
	            public void run() {
	            	if (isActive(player)) {
	            		try {
		            	removeParticles(player);
		            	Active_Map.put(player, false);
	            		} catch (NullPointerException e) {
	            			return;
	            		}
	            	}
	            }
			}, (5 * 20));
		}
	}
	
	Map<Player, Double> chakraDamage = new HashMap<Player, Double>();
	private void chakraBuild(Player player) {
		chakraDamage.put(player, 0D);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    @Override
		    public void run() {
		    	if (Active_Map.get(player)) {
		    		double old_val = chakraDamage.get(player);
		    		chakraDamage.put(player, old_val+=1.0);
		    	} else {
		    		chakraDamage.put(player, 0D);
		    	}
		    }
		}, 0, 2 * 20); //0 Tick initial delay, 20 Tick (1 Second) between repeats
	}
	
	public void removeParticles(Player player) {
    	getParticles(player).endTask();
    	getParticles(player).removeID();
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSmack(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {	
			Player player = (Player) event.getDamager();
			//CANCEL OUT IF TAIJUTSU
			Entity damaged = event.getEntity();
			if (damaged instanceof Player) {
				Player d = (Player) damaged;
				try {
				if (player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "" + ChatColor.BOLD + "Taijutsu")
						&& isActive(d)) {
					removeParticles(d);
					Active_Map.put(d, false);
					return;
				}
				} catch (NullPointerException e) {
					return;
				}
			}
			try {
			if (!player.getItemInHand().getItemMeta().getDisplayName().equals(getColorName()))
				return;
			} catch(NullPointerException e) {
				return;
			}
			if (!Active_Map.get(player))
				return;
			Entity entity = event.getEntity();
			double damage = plugin.getDamage(MoveType.RASENGAN, player) + (chakraDamage.get(player)/2);
			if (entity instanceof Player) {
				try {
				if (((HumanEntity) damaged).getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.AQUA + "" + ChatColor.BOLD + "Substitution")) {
					return;
				}
				} catch (NullPointerException e) {
					return;
				}
				final double health = ((Player) entity).getHealth();
				double new_health = (health - damage);
				if (new_health > 0.1) {
					((Player) entity).setHealth(new_health);
					event.setDamage(0.1);
				} else {
					((Player) entity).setHealth(0.1);
					event.setDamage(0.1);
				}
				((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1), true);
				((Player) entity).getWorld().playSound(player.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1, 1);
			}
			final Location loc = entity.getLocation();
			entity.getWorld().spawnParticle(Particle.SPIT, loc.getX(), loc.getY(), loc.getZ(),
        			10);
			Vector vec = player.getEyeLocation().getDirection();
			entity.setVelocity(new Vector(vec.getX() * 5, vec.getY() * 1.5, vec.getZ() * 5));
	    	removeParticles(player);
			Active_Map.put(player, false);
		}
	}
	
	public void chakraOverload(Player player) {
		try {
			if (isActive(player)) {
				removeParticles(player);
		    	player.damage(plugin.getDamage(MoveType.CHAKRA_OVERLOAD, player));
		    	Active_Map.put(player, false);
			}
		} catch (NullPointerException e) {
			plugin.writeReport("Chakra Overload", "particles");
		}
	}
}
