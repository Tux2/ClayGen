package Tux2.ClayGen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.Listener;

public class ClaygenWorldListener implements Listener {
	
	ClayGen plugin;
	
	public ClaygenWorldListener(ClayGen plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onChunkUnload(ChunkUnloadEvent event) {
		if(!plugin.canUnloadChunk(event.getChunk())) {
			event.setCancelled(true);
		}
	}

}
