package com.qbit.p2p.credit.user.resource;

import com.qbit.commons.auth.AuthFilter;
import com.qbit.commons.crypto.util.EncryptionUtil;
import com.qbit.p2p.credit.env.Env;
import com.qbit.commons.model.Point2;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.commons.codec.binary.Base64;

/**
 * @author Alexander_Sergeev
 */
@Path("photos")
@Singleton
public class UserPhotosResource {

	@Inject
	private Env env;

	@Context
	private HttpServletRequest request;
	
	@GET
	@Path("{id}")
	@Produces("image/jpeg")
	public byte[] getUserPhoto(@PathParam("id") String userId) {
		String imageName = EncryptionUtil.getMD5(userId);
		BufferedImage bufferedImage;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(2048);
		File imageFile = createPhotoPath(imageName);
		if (!imageFile.exists()) {
			imageFile = createPhotoPath("NO_IMAGE");
		}
		try {
			bufferedImage = ImageIO.read(imageFile);
			ImageIO.write(bufferedImage, "jpeg", outputStream);
		} catch (IOException ex) {
			throw new WebApplicationException(ex);
		}
		return outputStream.toByteArray();
	}

	@PUT
	@Path("current")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public void setUserPhoto(UserPhotoRequest userPhoto) {
		if (!userPhoto.isValid()) {
			throw new WebApplicationException();
		}
		
		String userId = AuthFilter.getUserId(request);
		String imageName = EncryptionUtil.getMD5(userId);
		File userPhotoFile = createPhotoPath(imageName);

		if (userPhoto.getImageString().isEmpty() && userPhotoFile.exists()) {
			userPhotoFile.delete();
			return;
		}

		try {
			byte[] imageBytes = Base64.decodeBase64(userPhoto.getImageString());
			BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageBytes));
			Point2 start = userPhoto.getStartPoint();
			Point2 size = userPhoto.getSize();
			BufferedImage result = bufferedImage.getSubimage(
					start.getX(), start.getY(), size.getX(), size.getY());
			ImageIO.write(result, "jpeg", userPhotoFile);
		} catch (IOException e) {
			throw new WebApplicationException(e);
		}
	}
	
	private File createPhotoPath(String photoName) {
		return new File(env.getUserPhotoPathFolder() + photoName + ".jpg");
	}
}
