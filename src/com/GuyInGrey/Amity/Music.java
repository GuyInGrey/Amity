package com.GuyInGrey.Amity;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.xxmicloxx.NoteBlockAPI.model.Playlist;
import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

public class Music implements Listener
{
	PositionSongPlayer player;
	
	public Music()
	{
		Playlist playlist = null;
		
		ConsoleCommandSender console = Amity.instance.getServer().getConsoleSender();
		console.sendMessage(Amity.instance.getDataFolder().getAbsolutePath() + File.separator + "arcadeMusic");
		
		File[] files = new File(Amity.instance.getDataFolder().getAbsolutePath() + File.separator + "arcadeMusic").listFiles();
		List<File> fileList = Arrays.asList(files);
		Collections.shuffle(fileList);
		fileList.toArray(files);
		
		for (final File f : files)
		{
			
			Song song = NBSDecoder.parse(f);
			if (playlist == null)
			{
				playlist = new Playlist(song);
			}
			else
			{
				playlist.add(song);
			}
		}
		
		player = new PositionSongPlayer(playlist);
		player.setTargetLocation(new Location(Bukkit.getServer().getWorlds().get(0), 0.5, 80, 0.5));
		for (Player p : Bukkit.getServer().getOnlinePlayers())
		{
			player.addPlayer(p);
		}
		player.setRandom(true);
		player.setRepeatMode(RepeatMode.ALL);
		player.setPlaying(true);
	}
	
	@EventHandler
	public void PlayerJoins(PlayerJoinEvent e)
	{
		player.addPlayer(e.getPlayer());
	}
	
	@EventHandler
	public void PlayerQuits(PlayerQuitEvent e)
	{
		player.removePlayer(e.getPlayer());
	}
}