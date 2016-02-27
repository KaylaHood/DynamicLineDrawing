package app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

import lines.MultiPointCurve;

public class DynamicLineDrawing extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3182864139827368021L;

	Random rnd = new Random();
	Color MorphColor = new Color(rnd.nextInt(255),rnd.nextInt(255),rnd.nextInt(255),75);
	boolean first = true;
	Timer timer = new Timer(8, this);
	int width = 1000;
	int height = 800;
	double mouseX = 0.0;
	double mouseY = 0.0;
	double mouseXFlip = 0.0;
	double mouseYFlip = 0.0;
	ArrayList<Number> xs = new ArrayList<Number>();
	ArrayList<Number> ys = new ArrayList<Number>();
	ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>> cache = new ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>>();
	BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	
	public void fillArrays() {
		xs.add(0.0);
		xs.add(0.0);
		xs.add(0.0);
		xs.add(0.0);
		ys.add(0.0);
		ys.add(0.0);
		ys.add(0.0);
		ys.add(0.0);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(width,height);
	}
	
	public DynamicLineDrawing() {
		rnd.setSeed(System.currentTimeMillis());
		
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				if(e.getID() == MouseEvent.MOUSE_RELEASED){ 
					System.out.println("MouseEvent received");
					for(double i = 0.0;i < (double)(xs.size());i+=1.0) {
						xs.set((int)i, i);
						ys.set((int)i,i);
					}
					timer.stop();
					repaint();
				}
			}
			public void mousePressed(MouseEvent e) {
				if(e.getID() == MouseEvent.MOUSE_PRESSED){ 
					System.out.println("MouseEvent received");
					first = false;
					mouseX = e.getX();
					mouseY = e.getY();
					mouseXFlip = Math.abs(mouseX - 2*(mouseX-(width/2)));
					mouseYFlip = mouseY;
					timer.start();
				}
			}
		});
		
		fillArrays();
		repaint();
	}
	
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		mouseX = (MouseInfo.getPointerInfo().getLocation()).getX() - (this.getLocationOnScreen()).getX();
		mouseY = (MouseInfo.getPointerInfo().getLocation()).getY() - (this.getLocationOnScreen()).getY();
		mouseXFlip = Math.abs(mouseX - 2*(mouseX-(width/2)));
		mouseYFlip = mouseY;
		for(int i=0;i<(xs.size());i++) {
			double newX = ((xs.get(i).doubleValue()+(Math.abs(rnd.nextInt(20)))-10));
			double newY = ((ys.get(i).doubleValue()+(Math.abs(rnd.nextInt(20)))-10));
			if(newX > (i*20)) {
				newX = i*20;
			} else if(newX < (i*(-20))) {
				newX = -(i*20);
			}
			if(newY > (i*20)) {
				newY = i*20;
			} else if(newY < (i*(-20))) {
				newY = -(i*20);
			}
			System.out.println("newX:" + newX + " newY:" + newY);
			xs.set(i,newX);
			ys.set(i,newY);
		}

		int newRed = Math.abs((MorphColor.getRed() + (rnd.nextInt(20) - 20)) % 255);
		int newGreen = Math.abs((MorphColor.getGreen() + (rnd.nextInt(20) - 20)) % 255);
		int newBlue = Math.abs((MorphColor.getBlue() + (rnd.nextInt(20) - 20)) % 255);
		//System.out.println("newRed:" + newRed + " newGreen:" + newGreen + " newBlue:" + newBlue);
		while(newRed+newBlue+newGreen < 150) {
			newRed += 10;
			newGreen += 10;
			newBlue += 10;
		}
		MorphColor = new Color(newRed,newGreen,newBlue,75);
		repaint();
	}
	
	public void paintComponent(Graphics g) {
		//super.paintComponent(g);
		Graphics2D buffImage = image.createGraphics();
		if(first) {
			buffImage.setColor(Color.BLACK);
			buffImage.fillRect(0,0,this.getWidth(),this.getHeight());
		}
		ArrayList<Number> xsMod = new ArrayList<Number>(xs);
		ArrayList<Number> ysMod = new ArrayList<Number>(ys);
		ArrayList<Number> xsModFlip = new ArrayList<Number>(xs);
		ArrayList<Number> ysModFlip = new ArrayList<Number>(ys);
		for(int i=0;i<(xsMod.size());i++) {
			xsMod.set(i, mouseX + xs.get(i).doubleValue());
			ysMod.set(i, mouseY + ys.get(i).doubleValue());
			xsModFlip.set(i, mouseXFlip - xs.get(i).doubleValue());
			ysModFlip.set(i, mouseYFlip + ys.get(i).doubleValue());
		}
		buffImage.setColor(MorphColor);
		MultiPointCurve genCurve = new MultiPointCurve(xsMod, ysMod);
		MultiPointCurve genCurveFlip = new MultiPointCurve(xsModFlip, ysModFlip);
		genCurve.paintComponent(buffImage,1);
		genCurveFlip.paintComponent(buffImage,1);
		cache.add(new Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>(MorphColor,new Tuple<MultiPointCurve,MultiPointCurve>(genCurve,genCurveFlip)));
		g.drawImage(image,0,0,null);
	}

}
