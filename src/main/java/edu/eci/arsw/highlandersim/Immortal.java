package edu.eci.arsw.highlandersim;

import java.util.List;
import java.util.Random;

public class Immortal extends Thread {

	private ImmortalUpdateReportCallback updateCallback = null;

	private int health;

	private int defaultDamageValue;

	private final List<Immortal> immortalsPopulation;

	private final String name;

	private Boolean vivo;

	public boolean espere;

	public boolean fin;

	public static final Object lock = new Object();

	public static Object monitor = ControlFrame.monitor;
	public boolean isStop = false;

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
		this.vivo = true;
		this.fin = false;
	}

	public void run() {
		
		while (true) {
			System.out.println(isStop);
			while(isStop) {
				System.out.println("Stop");
			}
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

				if (espere) {
					synchronized (monitor) {
						monitor.wait();
						seguir();
					}
				}

				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	public void fight(Immortal i2) {
			
			if (i2.getHealth() > 0) {
				
				i2.changeHealth(i2.getHealth() - defaultDamageValue);
				this.health += defaultDamageValue;
				updateCallback.processReport("Fight: " + this + " vs " + i2 + "\n");
				if(i2.getHealth() > 0)ControlFrame.immortalsLives.add(i2);
			} else {
				muerto();
				updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
			}
		}

	

	public void sync(Immortal i) {
		int i1 = System.identityHashCode(this);
		int i2 = System.identityHashCode(i);

		if (i1 < i2) {
			synchronized (this) {
				synchronized (i) {
					this.fight(i);
				}
			}
		} else if (i1 > i2) {
			synchronized (i) {
				synchronized (this) {
					this.fight(i);
				}
			}
		} else {
			synchronized (lock) {
				synchronized (this) {
					synchronized (i) {
						this.fight(i);
					}
				}
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

	public void muerto() {
		this.vivo = false;
	}

	public void parar() {

		isStop = true;

	}

	public void avanzar() {

		isStop = false;

	}

	@Override
	public String toString() {

		return name + "[" + health + "]";
	}

	public void fin() {
		this.fin = true;

	}

}
