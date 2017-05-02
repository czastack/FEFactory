package cza.hack;

public class HackLog implements Comparable<HackLog> {
	
	public String title;
	public int addr, size, value;

	@Override
	public int compareTo(HackLog log) {
		return addr - log.addr;
	}
}
