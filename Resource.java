package javaGame;

import java.util.HashMap;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Resource {
	 private static HashMap<String, Image> imageMap = new HashMap<String, Image>();
	 
	 public static void loadImage(String key, Image img) throws SlickException {
	      Image ret;
	      imageMap.put(key, img);
	  }

	  public static Image loadImage(String key, String path) throws SlickException {
	      Image ret;
	      imageMap.put(key, ret = new Image(path));
	      return ret;
	  }

	  public static Image getImage(String key) {
	      return imageMap.get(key);
	  }

	  public static void unloadImage(String key) {
	      Image image = imageMap.get(key);
	      if (image!=null) {
	          imageMap.remove(image);
	          //image.release();
	      }
	  }
	
	public Resource() {}

}
