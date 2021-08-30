package org.jboss.editorial;

public class Editorial {
	private String trigram;
	private int weekNo;

	public Editorial(String trigram, int weekNo) {
		this.trigram = trigram;
		this.weekNo = weekNo;
	}

    public String getTrigram() {
		return trigram;
	}

    public void setTrigram(String trigram) {
		this.trigram = trigram;
	}

	public int getWeekNo() {
		return weekNo;
	}

    public void setWeekNo(int weekNo) {
		this.weekNo = weekNo;
	}

    @Override
	public String toString() {
		return "Editorial [trigram=" + trigram + ", weekNo=" + weekNo + "]";
	}
}
