package jp.sheepman.famical.entity;

import jp.sheepman.common.entity.BaseEntity;

public class ImagesEntity extends BaseEntity {
	private int image_id;
	private String image_name;
	private byte[] image;
	private String comment;
	/**
	 * @return image_id
	 */
	@IgnoreDBAccess
	public int getImage_id() {
		return image_id;
	}
	/**
	 * @param image_id セットする image_id
	 */
	public void setImage_id(int image_id) {
		this.image_id = image_id;
	}
	/**
	 * @return image_name
	 */
	public String getImage_name() {
		return image_name;
	}
	/**
	 * @param image_name セットする image_name
	 */
	public void setImage_name(String image_name) {
		this.image_name = image_name;
	}
	/**
	 * @return image
	 */
	public byte[] getImage() {
		return image;
	}
	/**
	 * @param image セットする image
	 */
	public void setImage(byte[] image) {
		this.image = image;
	}
	/**
	 * @return comment
	 */
	public String getComment() {
		return comment;
	}
	/**
	 * @param comment セットする comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}
}
