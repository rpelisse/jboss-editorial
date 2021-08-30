package org.jboss.editorial;

public class Author {
	private String trigram;
	private String name;

	public Author(String trigram, String name) {
		this.trigram = trigram;
		this.name = name;
	}

	public String getTrigram() {
		return trigram;
	}

    public void setTrigram(String trigram) {
		this.trigram = trigram;
	}

    public String getName() {
		return name;
	}

    public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Author [trigram=" + trigram + ", name=" + name + "]";
	}
}
