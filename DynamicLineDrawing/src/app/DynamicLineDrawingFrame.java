package app;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class DynamicLineDrawingFrame {
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
	
	public static void createAndShowGUI() {
		JFrame window = new JFrame("Dynamic Line Painting");
		DynamicLineDrawing content = new DynamicLineDrawing();
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.setJMenuBar(new MyJMenu(window,content));
		window.setLayout(new BorderLayout());
		window.add(content, BorderLayout.CENTER);
		window.pack();
		window.revalidate();
		window.setVisible(true);
	}
	
}
