package common.content;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import common.models.Entity;

/**
 * Class to load and retrieve cached images.
 * @author Andrew O'Hara
 */
public class ImageLoader {
	
	public static final String IMG_PATH = "img/";
	private static final Cache<BufferedImage> IMG_CACHE = new Cache<>();
	private static final Cache<Dimension> DIM_CACHE = new Cache<>();
	
	public static BufferedImage getImage(Entity entity){
		return getImage(entity.getClass().getSimpleName() + ".png");
	}

	public static BufferedImage getImage(String imageName){
		String path = IMG_PATH + imageName;
		
		// Check cache
		BufferedImage image = IMG_CACHE.get(path);
		
		// if not cached, compute and cache image
		if (image == null){
			URL url = ImageLoader.class.getClassLoader().getResource(path);
			if (url != null){
				try {
					image = ImageIO.read(url);
				} catch (IOException e) {}
			}
			if (image != null){
				IMG_CACHE.put(path, image);
			} else {
				throw new IllegalArgumentException("Unable to load image: " + path);
			}
		}
		
		// return image
		return image;
	}
	
	public static Dimension scaleToFit(BufferedImage image, Dimension toFit) {
	    // Check input
	    if (image == null || toFit == null) {
	    	throw new IllegalArgumentException("invalid arguments");
	    }
	
    	// Check cache
    	Dimension result = DIM_CACHE.get(image, toFit);
    	
    	// If not cached, compute the Dimension and cache it
    	if (result == null){
    		double dScale = 1d;
    		double dScaleWidth = getScaleFactor(image.getWidth(), toFit.width);
	        double dScaleHeight = getScaleFactor(image.getHeight(), toFit.height);
	        dScale = Math.min(dScaleHeight, dScaleWidth);
	        result =  new Dimension(
		    	(int) Math.round(image.getWidth() * dScale),
		    	(int) Math.round(image.getHeight() * dScale)
		    );
	        DIM_CACHE.put(image, toFit, result);
    	}
    	
    	// Return dimension
        return result;	    
	}

	private static double getScaleFactor(int iMasterSize, int iTargetSize) {
	    if (iMasterSize > iTargetSize) {
	        return (double) iTargetSize / (double) iMasterSize;
	    } else {
	        return (double) iTargetSize / (double) iMasterSize;
	    }
	}

}
