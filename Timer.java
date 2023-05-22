import java.awt.Font;
import javax.swing.*;


public class Timer extends JLabel implements Runnable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean stop = false;
	private GUI gui;
	private static final int maxMinutes = 10, maxSeconds = 60;
	private int count = maxMinutes * maxSeconds, seconds, minutes;
	private String time = "";
	public Timer(GUI gui) {
		super("10:00");
		//setFont(new Font("Arial", Font.BOLD, 20));
		new Thread(this).start();
		this.gui = gui;
	}

	@Override
	public void run() {
		synchronized(this) {
			while(!stop) {
				if(count >= 0) {
					minutes = count / maxSeconds;
					seconds = count % maxSeconds;
					
					time = String.format("%02d:%02d", minutes, seconds);
					
					setText(time);
					count--;
				}else
					gui.blockProgram(gui, 2);
				try {
					wait(1000);
				}catch(InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
		
	public synchronized void stop() {
		stop = true;
	}

	public synchronized int getCount() {
		return count;
	}
}
