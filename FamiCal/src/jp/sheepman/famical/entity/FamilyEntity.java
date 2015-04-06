package jp.sheepman.famical.entity;

import jp.sheepman.common.entity.BaseEntity;

public class FamilyEntity extends BaseEntity {
	private int family_id;
	private String family_name;
	private String birth_date;
	private int image_id;
	/**
	 * @return family_id
	 */
	@IgnoreDBAccess
	public int getFamily_id() {
		return family_id;
	}
	/**
	 * @param family_id セットする family_id
	 */
	public void setFamily_id(int family_id) {
		this.family_id = family_id;
	}
	/**
	 * @return family_name
	 */
	public String getFamily_name() {
		return family_name;
	}
	/**
	 * @param family_name セットする family_name
	 */
	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}
	/**
	 * @return birth_date
	 */
	public String getBirth_date() {
		return birth_date;
	}
	/**
	 * @param birth_date セットする birth_date
	 */
	public void setBirth_date(String birth_date) {
		this.birth_date = birth_date;
	}
	/**
	 * @return image_id
	 */
	public int getImage_id() {
		return image_id;
	}
	/**
	 * @param image_id セットする image_id
	 */
	public void setImage_id(int image_id) {
		this.image_id = image_id;
	}
}
