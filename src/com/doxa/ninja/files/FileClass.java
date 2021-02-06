package com.doxa.ninja.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.doxa.ninja.Main;


public class FileClass {

	Main plugin;
	public FileClass(Main main) {
		this.plugin = main;
	}
	
	public void createFile() {
		try {
			File file = new File("plugins/Ninja/ErrorLog.txt");
			//check if File exists
		    if (file.createNewFile()) {
		    	//Create new File
		    	System.out.println("File created: " + file.getName());
		    } else {
		    	//File exists so just scan it
		    	System.out.println("File scanned: ErrorLog.txt");
		    }
	    } catch (IOException e) {
	        System.out.println("An error occurred creating or reading ErrorLog.txt. contact Nick Doxa ASAP!");
	    }
	}
	
	public void writeReport(String error, String reason) {
		    try {
		    	File file = new File("plugins/Ninja/ErrorLog.txt");
		        FileWriter myWriter = new FileWriter(file.getPath(), true);
		        myWriter.write(error + ": " + reason + "\n");
		        myWriter.close();
		    } catch (IOException e) {
		    	System.out.println("Warning: Error, file exception.");
		    }
	}
	
	public void clearFile(Player player, String prefix) {
		player.sendMessage(prefix + ChatColor.RED + "File clearing...");
		File file = new File("plugins/Ninja/ErrorLog.txt");
		file.delete();
		createFile();
		player.sendMessage(prefix + ChatColor.GREEN + "File cleared!");
	}
	
	public void scanFile(Player player) throws IOException {
		File file = new File("plugins/Ninja/ErrorLog.txt");
		try {
			@SuppressWarnings("resource")
			Scanner scan = new Scanner(file);
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Errors Log");
			player.sendMessage("");
			int number = 0;
			BufferedReader br = new BufferedReader(new FileReader(file.getPath())); 
			if (br.readLine() == null) {
				player.sendMessage(ChatColor.RED + "No active errors logged!");
			} else {
				while(scan.hasNextLine()) {
					number++;
					player.sendMessage(ChatColor.YELLOW + "" + number + ". " + scan.nextLine());
				}
			}
		} catch (FileNotFoundException e) {
			createFile();
			player.sendMessage("File not found... Creating Now!");
		}
	}
	
}
