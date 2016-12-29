package RL;

public class Action {

	String name;
	int ordinal;

	public Action(int ordinal, String name) {
		this.ordinal = ordinal;
		this.name = name;
	}

	public int ordinal() {
		return ordinal;
	}

	public void setOrdinal(int ordinal) {
		this.ordinal = ordinal;
	}

	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
