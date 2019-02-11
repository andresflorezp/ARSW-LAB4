package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;

	private int health;

	private int defaultDamageValue;

	private final List<Immortal> immortalsPopulation;

	private final String name;

	public boolean espere;
	private final Random r = new Random(System.currentTimeMillis());

	public Immortal(String name, List<Immortal> immortalsPopulation, int health, int defaultDamageValue,
			ImmortalUpdateReportCallback ucb) {
		super(name);
		this.updateCallback = ucb;
		this.name = name;
		this.immortalsPopulation = immortalsPopulation;
		this.health = health;
		this.defaultDamageValue = defaultDamageValue;
		this.espere = false;
	}

	public void run() {

		while (true) {
			Immortal im;

			int myIndex = immortalsPopulation.indexOf(this);

			int nextFighterIndex = r.nextInt(immortalsPopulation.size());

			// avoid self-fight
			if (nextFighterIndex == myIndex) {
				nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
			}

			im = immortalsPopulation.get(nextFighterIndex);

			this.fight(im);

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void fight(Immortal i2) {
		for (;;) {
			if (this.espere) {
				synchronized (this) {
					try {
						System.out.println("Esta esperando");
						wait(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if (i2.getHealth() > 0) {
				i2.changeHealth(i2.getHealth() - defaultDamageValue);
				this.health += defaultDamageValue;
				updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
			} else {
				updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
			}
		}

	}

	public void changeHealth(int v) {
		health = v;
	}

	public synchronized int getHealth() {
		return health;
	}

	public void detener() {

		espere = true;

	}

	public void seguir() {

		espere = false;
	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

}
