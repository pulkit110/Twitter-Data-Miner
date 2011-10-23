package twitter.dataanalyzer;

import processing.core.PApplet;
import processing.core.PImage;

public class ConnectionVisualizer extends PApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	PImage world_map_img;
	boolean recording = false;
	float angle = 0;
	int fps = 30;

	public void setup() {
		// world_map_img = loadImage("worldmap.png");
		// String url =
		// "http://upload.wikimedia.org/wikipedia/commons/1/17/BlankMap-World-noborders.png";
		String url = "http://upload.wikimedia.org/wikipedia/commons/thumb/0/03/BlankMap-World6.svg/2000px-BlankMap-World6.svg.png";
		world_map_img = loadImage(url, "png");

		size(200, 200);
		background(0);
	}

	public void keyPressed() {
		if (key == 's') {
			save("screen_shots/" + year() + "_" + month() + "_" + day() + "_"
					+ hour() + "_" + minute() + "_" + second() + ".png");
		}
	}

	public void draw() {

		background(0);
		image(world_map_img, 0, 0, width, height);

		double latitude = 22.725313;
		double longitude = 75.865555;

		latitude = Math.PI * latitude / 180;
		longitude = Math.PI * longitude / 180;

		// adjust position by radians
		latitude -= 1.570795765134; // subtract 90 degrees (in radians)

		double temp1 = Math.sin(latitude);
		double temp2 = Math.cos(latitude);
		double temp3 = Math.sin(longitude);
		double temp4 = Math.cos(longitude);
		double xPos = (width/2) * Math.cos(longitude) * Math.cos(latitude);
		double yPos = (height/2) * Math.sin(longitude) * Math.cos(latitude);
		double zPos = (width/2) * Math.sin(latitude);

		// and switch z and y
		// double xPos = ((width) * Math.sin(latitude) * Math.cos(longitude));
		// double zPos = ((width) * Math.sin(latitude) * Math.sin(longitude));
		// double yPos = ((width) * Math.cos(latitude));
//		int focalLength = 1;
//		float x = (float) (xPos * focalLength / (focalLength + zPos));
//		float y = (float) (yPos * focalLength / (focalLength + zPos));

		float x = (float) ((float) xPos + width/6.7);
		float y = (float) yPos - height/15;
		
		float centerX = width / 2 - width/40;
		float centerY = height / 2;
		x += centerX;
		y = centerY - y;

		System.out.println(x + ", " + y);

		ellipse(x, y, (float) 5.0, (float) 5.0);
		ellipse(centerX, centerY, (float) 5.0, (float) 5.0);

//		var earthRadius = 6367; // radius in kmfunction
								 

		angle += 0.5;
		if (angle > 360)
			angle = 0;

		tint(0, 153, 204, 255);

		float pos_x, pos_y;
		// for(int ii = 0; ii < city_marks.length; ii++) {
		// city_marks[ii].draw();
		// }

		float center_x = width / 2;
		float center_y = height / 2;
		float center_z = (float) 0.0;

		float eye_x = 400 * cos(angle * PI / 180) + center_x;
		float eye_y = 400 * sin(angle * PI / 180) + center_y;
		float eye_z = (float) 200.0;
		camera(eye_x, eye_y, eye_z, center_x, center_y, center_z, (float) 0.0,
				(float) 0.0, (float) -1.0); // upX, upY, upZ

		// if(recording) {
		// loadPixels();
		// mm.addFrame(pixels);
		// }

		// println(frameRate);

	}
}
