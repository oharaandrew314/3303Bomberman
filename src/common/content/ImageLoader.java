package common.content;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

import common.models.Entity;

/**
 * Class to load and retrieve cached images.
 * @author Andrew O'Hara
 */
public class ImageLoader {
	
	public static final String IMG_PATH = "img/";
	private static final HashMap<String, BufferedImage> CACHE = new HashMap<>();
	
	/**
	 * Load the image assigned to the given Entity.
	 * The image must be stored in the img dir.
	 * The image filename must conform to <classname>.png.
	 * 
	 * @param entity the entity to find the image for
	 * @return the image for the given entity.
	 */
	public static BufferedImage getImage(Entity entity){
		return getImage(entity.getClass().getSimpleName() + ".png");
	}

	public static BufferedImage getImage(String imageName){
		String path = IMG_PATH + imageName;
		
		// Check cache
		BufferedImage image = CACHE.get(path);
		
		// if not cached, compute and cache image
		if (image == null){
			URL url = ImageLoader.class.getClassLoader().getResource(path);
			if (url != null){
				try {
					image = ImageIO.read(url);
				} catch (IOException e) {}
			}
			if (image != null){
				CACHE.put(path, image);
			} else {
				throw new IllegalArgumentException("Unable to load image: " + path);
			}
		}
		
		// return image
		return image;
	}
}
