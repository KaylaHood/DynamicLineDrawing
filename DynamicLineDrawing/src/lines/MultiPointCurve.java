package lines;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;

public class MultiPointCurve {
	/**
	 * multiple inflection points on this line, as many as are present in
	 * passed-in ArrayLists
	 */
	public ArrayList<Number> xs;
	public ArrayList<Number> ys;
	
	public MultiPointCurve(ArrayList<Number> xsI,ArrayList<Number> ysI) {
		xs = xsI;
		ys = ysI;
		while(xs.size() < ys.size()) {
			ys.remove(ys.size() - 1);
		}
		while(ys.size() < xs.size()) {
			xs.remove(xs.size() - 1);
		}
	}
	
	public MultiPointCurve(MultiPointCurve c) {
		xs = new ArrayList<Number>(c.xs);
		ys = new ArrayList<Number>(c.ys);
	}
	
	public void add(double n) {
		for(int i=0;i<xs.size();i++) {
			xs.set(i, (xs.get(i).doubleValue() + n));
			ys.set(i, (ys.get(i).doubleValue() + n));
		}
	}
	
	public void sub(double n) {
		add(-n);
	}
	
	public void mult(double n) {
		for(int i=0;i<xs.size();i++){
			System.out.println("Original xs and ys:");
			System.out.println("xs: " + xs.get(i));
			System.out.println("ys: " + ys.get(i));
			xs.set(i, ((xs.get(i).doubleValue()) * n));
			ys.set(i, ((ys.get(i).doubleValue()) * n));
			System.out.println("New xs and ys:");
			System.out.println("xs: " + xs.get(i));
			System.out.println("ys: " + ys.get(i));
		}
		
	}
	
	public void div(double n) {
		mult((1/n));
	}
	
	public void paintComponent(Graphics g,int multiplier) {
		Graphics2D g2g = (Graphics2D) g;
		for(int i=0;i<(xs.size() / 4);i+=4) {
			//System.out.println("xs: " + xs.get(i) + " " + xs.get(i+1) + " " + xs.get(i+2) + " " + xs.get(i+3));
			//System.out.println("ys: " + ys.get(i) + " " + ys.get(i+1) + " " + ys.get(i+2) + " " + ys.get(i+3));
			CubicCurve2D.Double CC = new CubicCurve2D.Double((xs.get(i).doubleValue())*multiplier,(ys.get(i).doubleValue())*multiplier,(xs.get(i+1).doubleValue())*multiplier,(ys.get(i+1).doubleValue())*multiplier,(xs.get(i+2).doubleValue())*multiplier,(ys.get(i+2).doubleValue())*multiplier,(xs.get(i+3).doubleValue())*multiplier,(ys.get(i+3).doubleValue())*multiplier);
			//System.out.println("curve being painted");
			//System.out.println(g.getColor());
			g2g.draw(CC);
		}
	}



}

