package Tux2.ClayGen;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class ClayFromTo implements Listener {
	
	ClayGen plugin;
	
	public ClayFromTo(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockFromTo(BlockFromToEvent event) {
		Block thewaterblock2 = event.getToBlock();
		plugin.convertBlocks(thewaterblock2);
	}
}
