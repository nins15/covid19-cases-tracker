import java.util.*;

public class Contact {
	private String individual;
	private int date;
	private int duration;
	Contact(String individual,int date,int duration){
		this.individual=individual;
		this.date=date;
		this.duration=duration;
	}
	String getIndividual() {
		return this.individual;
	}
	int getDate() {
		return this.date;
	}
	String getDuration() {
		return Integer.toString(this.duration);
	}

}
