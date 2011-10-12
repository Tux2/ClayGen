package Tux2.ClayGen;

import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldListener;

public class ClaygenWorldListener extends WorldListener {
	
	ClayGen plugin;
	
	public ClaygenWorldListener(ClayGen plugin) {
		this.plugin = plugin;
	}
	
	public void onChunkUnload(ChunkUnloadEvent event) {
		if(!plugin.canUnloadChunk(event.getChunk())) {
			event.setCancelled(true);
		}
	}

}
