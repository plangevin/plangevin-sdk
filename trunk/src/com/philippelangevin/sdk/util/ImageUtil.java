package com.philippelangevin.sdk.util;

import java.awt.Image;

import javax.swing.ImageIcon;

/**
 * <p> Title: {@link ImageUtil} <p>
 * <p> Description: Image utility class. </p>
 * <p> Company : C-Tec <p>
 *
 * @author plefebvre
 * Copyright: (c) 2011, C-Tec Inc. - All rights reserved
 */

/*
 * History
 * ------------------------------------------------
 * Date			Name		BT		Description
 * 2011-01-28	plefebvre
 */
public class ImageUtil {
	/**
	 * Loads an image using new ImageIcon(imagePath), throws an exception if it fails.
	 * @param imagePath The path to the image.
	 * @return The loaded image.
	 * @throws IllegalStateException Thrown if the image is not found.
	 */
	public static ImageIcon loadImage(String imagePath) throws IllegalStateException {
		ImageIcon imageIcon = new ImageIcon(imagePath);
		if (imageIcon.getIconHeight() == -1 || imageIcon.getIconWidth() == -1) {
			throw new IllegalStateException("Error loading this icon: " + imagePath);
		}
		return imageIcon;
	}
	
	/**
	 * Loads an image using new ImageIcon(imagePath) and scales it.
	 * Throws an exception if it fails to load the image.
	 * @param imagePath The path to the image.
	 * @param width The image width.
	 * @param height The image height.
	 * @return The loaded image.
	 * @throws IllegalStateException Thrown if the image is not found.
	 */
	public static ImageIcon loadScaledImage(String imagePath, int width, int height) throws IllegalStateException {
		return new ImageIcon( loadImage(imagePath).getImage().getScaledInstance( width, height, Image.SCALE_SMOOTH ) );
	}
}
