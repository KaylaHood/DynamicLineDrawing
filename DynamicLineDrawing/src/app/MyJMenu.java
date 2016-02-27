package app;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JFrame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.imageio.ImageIO;

import lines.MultiPointCurve;

public class MyJMenu extends JMenuBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5171854710499593429L;
	
	JFrame container;
	DynamicLineDrawing content;
	JMenuBar menuBar;
	JMenu fileMenu;
	JMenu editMenu;
	JMenuItem save;
	JMenuItem clear;
	
	public class SaveAction extends AbstractAction {

		private static final long serialVersionUID = -440873695393032172L;

		JFileChooser fc = new JFileChooser();
		int iMult = 5;
		BufferedImage bi = new BufferedImage((content.width)*iMult,(content.height)*iMult,BufferedImage.TYPE_INT_ARGB);
		
		public SaveAction(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		
		public ArrayList<Tuple<Color,MultiPointCurve>> MultiplyCurve(Tuple<Color,MultiPointCurve> c,Tuple<Color,MultiPointCurve> n) {
			//generate intermediary lines, to fill in empty space due to stretching
			ArrayList<Tuple<Color,MultiPointCurve>> res = new ArrayList<Tuple<Color,MultiPointCurve>>();
			MultiPointCurve copyC = new MultiPointCurve(c.y); //make copy of original line
			copyC.mult((double) iMult); //multiply original line by iMult
			res.add(new Tuple<Color,MultiPointCurve>(c.x,copyC)); //add multiplied original line to res
			//store a copy of next line, multiply by iMult
			MultiPointCurve copyN = new MultiPointCurve(n.y);
			copyN.mult((double) iMult);
			
			if(iMult > 1) {
				int diffColorR = (n.x.getRed())-(c.x.getRed());
				int diffColorG = (n.x.getGreen())-(c.x.getGreen());
				int diffColorB = (n.x.getBlue())-(c.x.getBlue());
				int diffColorA = (n.x.getAlpha())-(c.x.getAlpha());
				ArrayList<Number> diffXs = new ArrayList<Number>();
				ArrayList<Number> diffYs = new ArrayList<Number>();
				for(int m=0;m<(copyC.xs.size());m++) {
					diffXs.add(((copyN).xs).get(m).doubleValue() - ((copyC).xs).get(m).doubleValue());
					diffYs.add(((copyN).ys).get(m).doubleValue() - ((copyC).ys).get(m).doubleValue());
				}
				for(int j=0;j<iMult;j++) {
					Color newColor = new Color((c.x.getRed())+(diffColorR*(j/iMult)),(c.x.getGreen())+(diffColorG*(j/iMult)),(c.x.getBlue())+(diffColorB*(j/iMult)),(c.x.getAlpha())+(diffColorA*(j/iMult)));
					ArrayList<Number> newXs = new ArrayList<Number>();
					for(int m=0;m<diffXs.size();m++) {
						newXs.add(copyC.xs.get(m).doubleValue() + (diffXs.get(m).doubleValue()*(j/iMult)));
					}
					ArrayList<Number> newYs = new ArrayList<Number>();
					for(int m=0;m<diffYs.size();m++) {
						newYs.add(copyC.ys.get(m).doubleValue() + (diffYs.get(m).doubleValue()*(j/iMult)));
					}
					MultiPointCurve curve = new MultiPointCurve(newXs,newYs);
					res.add(new Tuple<Color,MultiPointCurve>(newColor,curve));
					//System.out.println(newXs);
					//System.out.println(newYs);
				}	
			}
			
			return res;
		}
		
		public void actionPerformed(ActionEvent arg0) {
			// TODO Save action
			int returnVal = fc.showSaveDialog(MyJMenu.this.getTopLevelAncestor());
			if(returnVal == JFileChooser.APPROVE_OPTION){
				ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>> BIcache = content.cache;
				ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>> MultBIcache = new ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>>();
				//build multiplied cache curves (to enlarge final image)
				for(int i=0;i<(BIcache.size() - 1);i++) {
					Tuple<Color,MultiPointCurve> cur = new Tuple<Color,MultiPointCurve>(BIcache.get(i).x,new MultiPointCurve(BIcache.get(i).y.x));
					Tuple<Color,MultiPointCurve> next = new Tuple<Color,MultiPointCurve>(BIcache.get(i+1).x,new MultiPointCurve(BIcache.get(i+1).y.x));
					Tuple<Color,MultiPointCurve> curFlip = new Tuple<Color,MultiPointCurve>(BIcache.get(i).x,new MultiPointCurve(BIcache.get(i).y.y));
					Tuple<Color,MultiPointCurve> nextFlip = new Tuple<Color,MultiPointCurve>(BIcache.get(i+1).x,new MultiPointCurve(BIcache.get(i+1).y.y));
					ArrayList<Tuple<Color,MultiPointCurve>> newCurves1 = MultiplyCurve(cur, next);
					ArrayList<Tuple<Color,MultiPointCurve>> newCurves2 = MultiplyCurve(curFlip, nextFlip);
					ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>> combinedCurves = new ArrayList<Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>>();
					for(int j=0;j<(newCurves1.size());j++) {
						Tuple<MultiPointCurve,MultiPointCurve> curveTup = new Tuple<MultiPointCurve,MultiPointCurve>(newCurves1.get(j).y,newCurves2.get(j).y);
						combinedCurves.add(new Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>(newCurves1.get(j).x,curveTup));
					}
					MultBIcache.addAll(combinedCurves);
				}
				//add the last curve in, because it was missed in the building for loop above
				MultiPointCurve newEnd = new MultiPointCurve(BIcache.get(BIcache.size() - 1).y.x);
				newEnd.mult(iMult);
				MultiPointCurve newEndFlip = new MultiPointCurve(BIcache.get(BIcache.size() - 1).y.y);
				newEndFlip.mult(iMult);
				Tuple<MultiPointCurve,MultiPointCurve> endTup = new Tuple<MultiPointCurve,MultiPointCurve>(newEnd,newEndFlip);
				Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>> endBigTup = new Tuple<Color,Tuple<MultiPointCurve,MultiPointCurve>>((BIcache.get(BIcache.size() - 1).x),endTup);
				MultBIcache.add(endBigTup);
				//paint multiplied curves onto buffered image
				Graphics2D graphics = bi.createGraphics();
				//System.out.println(bi.getWidth() + " " + bi.getHeight());
				graphics.setPaint(Color.BLACK);
				graphics.fillRect(0,0,bi.getWidth(),bi.getHeight());
				for(int i=0;i<(MultBIcache.size());i++) {
					graphics.setPaint(MultBIcache.get(i).x);
					//System.out.println(graphics.getColor());
					//System.out.println(graphics.getColor().getAlpha());
					((MultBIcache.get(i)).y).x.paintComponent(graphics,1);
					((MultBIcache.get(i)).y).y.paintComponent(graphics,1);
				}
				graphics.drawString("It is painting", 5, 10);;
				
				File file = fc.getSelectedFile();
				if(!(file.getPath().endsWith(".png"))) {
					file = new File(((file).getPath()) + (".png"));
				}
				try {
					ImageIO.write(bi, "png", file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				graphics.dispose();
			}
			
		}
		
	}
	
	public class ClearAction extends AbstractAction {

		private static final long serialVersionUID = 1562408842167582203L;

		public ClearAction(String name, Integer mnemonic) {
			super(name);
			putValue(MNEMONIC_KEY, mnemonic);
		}
		public void actionPerformed(ActionEvent arg0) {
			content.first = true;
			content.repaint();
		}
		
	}
	
	public MyJMenu(JFrame c,DynamicLineDrawing d) {	
		
		container = c;
		content = d;
		
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		fileMenu.getAccessibleContext().setAccessibleDescription("Save operations");
		this.add(fileMenu);
		
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic(KeyEvent.VK_E);
		editMenu.getAccessibleContext().setAccessibleDescription("Make changes to canvas");
		this.add(editMenu);
		
		save = new JMenuItem(new SaveAction("Save As...",KeyEvent.VK_S));
		save.getAccessibleContext().setAccessibleDescription("");
		fileMenu.add(save);
		
		clear = new JMenuItem(new ClearAction("Clear Canvas",KeyEvent.VK_C));
		clear.getAccessibleContext().setAccessibleDescription("");
		editMenu.add(clear);
	
	}
}

