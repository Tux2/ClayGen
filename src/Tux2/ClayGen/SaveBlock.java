package Tux2.ClayGen;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class SaveBlock implements Serializable {
	
	double x = 0;
	double y = 0;
	double z = 0;
	int delayvalue = 0;
	long intime = 0;
	String world = "world";
	
	SaveBlock(ClayDelay yourblock) {
		this(yourblock.getBlock().getLocation(), yourblock.getDelay(), 
				System.currentTimeMillis() - yourblock.getInTime());
	}
		
	SaveBlock(Block yourblock) {
		this(yourblock.getLocation(), 0, 0);
	}
	
	SaveBlock(Block yourblock, int delay) {
		this(yourblock.getLocation(), delay, 0);
	}
	
	SaveBlock(Block yourblock, int delay, long timeelapsed) {
		this(yourblock.getLocation(), delay, timeelapsed);
	}
	
	SaveBlock(Location yourblock) {
		this(yourblock, 0, 0);
	}
	
	SaveBlock(Location yourblock, int delay) {
		this(yourblock, delay, 0);
	}
	
	SaveBlock(Location yourblock, int delay, long timeelapsed) {
		x = yourblock.getX();
		y = yourblock.getY();
		z = yourblock.getZ();
		delayvalue = delay;
		world = yourblock.getWorld().getName();
	}
	
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}

	/**
	 * @return the delayvalue
	 */
	public int getDelayvalue() {
		return delayvalue;
	}

	/**
	 * @return the intime
	 */
	public long getIntime() {
		return intime;
	}

	/**
	 * @return the world
	 */
	public String getWorld() {
		return world;
	}

}
