package Tux2.ClayGen;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Material;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * ClayGen block listener
 * @author Tux2
 */
public class ClayGenBlockListener extends BlockListener {
    public static final int CLAY = 82;
	private final ClayGen plugin;
    public static final int GRAVEL = 13;
    boolean debug = false;

    public ClayGenBlockListener(final ClayGen plugin) {
        this.plugin = plugin;
    }
    
    public void onBlockFromTo(BlockFromToEvent event) {
    	Block thewaterblock = event.getBlock();
    	plugin.convertBlocks(thewaterblock);
    }
    
    public void onBlockPlace (BlockPlaceEvent event) {
    	Block thegravelblock = event.getBlock();
    	if(thegravelblock.getTypeId() == GRAVEL) {
    		plugin.gravelPlaced(thegravelblock);
    	}
    }

    //put all Block related code here
}
