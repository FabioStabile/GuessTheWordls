import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class ProgressBar extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JProgressBar bar;
	private JPanel pnlSouth, pnlCenter, pnlNorth;
	private JLabel lbl;
	private JButton btnExit;
	private String file;
	private int rows, columns;
	private Vector<String> originalFileContent;
	
	public ProgressBar(String file, int rows, int columns, Vector<String> originalFileContent) { 
		this.file = file;
		this.rows = rows;
		this.columns = columns;
		this.originalFileContent = originalFileContent;
		
		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.setBackground(new Color(224,176,255));
		
		btnExit = new JButton("X");
		btnExit.setBackground(new Color(224,176,255));
		btnExit.setForeground(Color.BLACK);
		btnExit.setBorderPainted(false);
		btnExit.setFont(new Font("Times New Roman", Font.BOLD, 15));
		btnExit.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
		btnExit.setFocusPainted(false);
		btnExit.addActionListener(e ->{
			// revert the content of numbers to the default one
        	try {
    			BufferedWriter writer = new BufferedWriter(new FileWriter("Numbers.txt"));
    			
    			for(int i = 0; i < originalFileContent.size(); i++) {
    				writer.write(originalFileContent.get(i));
    				writer.newLine();
    			}
    			
    			writer.close();
    		}catch(IOException e1) {
    			e1.printStackTrace();
    		}
        	// empty the file UsedFiles
        	try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("UsedFiles.txt"));
				
				writer.write("");
				
				writer.close();
			}catch(IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		});
		
		// When the mouse cursor pass on the button ì, the background color get changed to red
		btnExit.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent e) {
				btnExit.setBackground(Color.RED);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnExit.setBackground(new Color(224,176,255));
			}
			
		});
		
		buttonKey(btnExit, KeyEvent.VK_ESCAPE);
		
		pnlNorth.add(btnExit, BorderLayout.EAST);
		
		pnlCenter = new JPanel();
		pnlCenter.setBackground(new Color(224,176,255));
		
		pnlSouth = new JPanel(new BorderLayout());
		
		lbl = new JLabel("downloading resources...");
		lbl.setOpaque(true);
		lbl.setBackground(new Color(224,176,255));
		lbl.setForeground(new Color(49,0,98));
		lbl.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lbl.setHorizontalAlignment(JLabel.CENTER);
		
		
		bar = new JProgressBar(0, 100);
		bar.setStringPainted(true);
		bar.setBackground(new Color(224,176,255));
		bar.setForeground(new Color(102, 0, 102));
		bar.setFont(new Font("Times New Roman", Font.BOLD, 15));
		progress();
		
		pnlSouth.add(lbl, BorderLayout.CENTER);
		pnlSouth.add(bar, BorderLayout.SOUTH);

		add(pnlNorth, BorderLayout.NORTH);
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(400, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setUndecorated(true);
	}
	
	public void progress() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				int currentValue = 0;
				
				while(currentValue <= 100) {
					bar.setValue(currentValue);
					
					try {
						Thread.sleep(new Random().nextInt(500) + 100);
					}catch(InterruptedException e1) {
						e1.printStackTrace();
					}
					
					currentValue += new Random().nextInt(10) + 1;		
				}
				
				bar.setValue(100);
				lbl.setText("Opening the game...");
				bar.setString("Completed!");
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				dispose();
				GUI gui = new GUI(file, rows, columns, originalFileContent);
				gui.setVisible(true);
			}
			
		});
		thread.start();
	}
	
	public static void buttonKey(JButton btn, int key) {
	    Action action = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	         public void actionPerformed(ActionEvent e) {
	            btn.doClick();
	         }
	    };
	    
	    // Set the btn actionCommand to the key to identify which action should be performed 
	    btn.setActionCommand("buttonAction" + key);
	       
	    // Associate the action object to the keyboard key
	    btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), "buttonAction" + key);
	    btn.getActionMap().put("buttonAction" + key, action);
	}
}
