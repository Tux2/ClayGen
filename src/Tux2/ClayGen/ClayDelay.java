package Tux2.ClayGen;

import java.io.Serializable;

import org.bukkit.block.Block;

public class ClayDelay implements Serializable {
	Block theblock;
	int delayvalue = 0;
	long intime = System.currentTimeMillis();
	
	ClayDelay(Block yourblock) {
		theblock = yourblock;
	}
	ClayDelay(Block yourblock, int indelay, long timeelapsed) {
		theblock = yourblock;
		delayvalue = indelay;
		intime = System.currentTimeMillis() - timeelapsed;
	}
	public void upDelay() {
		delayvalue++;
	}
	public void upDelay(int amount) {
		delayvalue += amount;
	}
	public int getDelay() {
		return delayvalue;
	}
	public void setDelay(int delay) {
		delayvalue = delay;
	}
	public Block getBlock() {
		return theblock;
	}
	public long getInTime() {
		return intime;
	}

}
