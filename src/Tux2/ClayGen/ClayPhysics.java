package Tux2.ClayGen;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class ClayPhysics implements Listener {
	
	ClayGen plugin;
	public static final int CLAY = 82;
	
	public ClayPhysics(ClayGen plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPhysics (BlockPhysicsEvent event) {
		if(event.getBlock().getTypeId() == CLAY) {
			if(plugin.waterenabled && plugin.lavaenabled) {
	    		if(!plugin.hasBlockNextTo(event.getBlock(), 8, 9, 10, 11)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}else if(plugin.waterenabled) {
				if(!plugin.hasBlockNextTo(event.getBlock(), 8, 9)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}else if(plugin.lavaenabled) {
				if(!plugin.hasBlockNextTo(event.getBlock(), 10, 11)) {
	    			plugin.clayWaterRemoved(event.getBlock());
	    		}
			}
		}
	}

}
