package javaGame;

public class GoldFade {
	int amount;
	int x;
	int y;
	int fade;
	int fadeAmount;
	int type;
	
	
	public GoldFade(int amount, int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.amount = amount;
		this.fade = 300;
		this.fadeAmount = 6;
		this.type = type;
	}
	
	public void fadeOut() {
		fade -= fadeAmount;
	}

}
