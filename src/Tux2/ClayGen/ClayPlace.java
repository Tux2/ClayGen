package Tux2.ClayGen;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ClayPlace implements Listener {
	
	ClayGen plugin;
	
	public ClayPlace(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace (BlockPlaceEvent event) {
		Block thegravelblock = event.getBlock();
		if(thegravelblock.getType() == Material.GRAVEL) {
			plugin.gravelPlaced(thegravelblock);
		}
	}
	
	

}
