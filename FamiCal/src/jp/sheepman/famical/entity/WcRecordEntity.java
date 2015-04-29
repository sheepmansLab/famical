/**
 * 
 */
package jp.sheepman.famical.entity;

import jp.sheepman.common.entity.BaseEntity;

/**
 * @author sheepman
 *
 */
public class WcRecordEntity extends BaseEntity {
	private int family_id;
	private String wc_record_date;
	private int pe_count;
	private int po_count;
	private String comment;
	/**
	 * @return family_id
	 */
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
	 * @return wc_record_date
	 */
	public String getWc_record_date() {
		return wc_record_date;
	}
	/**
	 * @param wc_record_date セットする wc_record_date
	 */
	public void setWc_record_date(String wc_record_date) {
		this.wc_record_date = wc_record_date;
	}
	/**
	 * @return pe_count
	 */
	public int getPe_count() {
		return pe_count;
	}
	/**
	 * @param pe_count セットする pe_count
	 */
	public void setPe_count(int pe_count) {
		this.pe_count = pe_count;
	}
	/**
	 * @return po_count
	 */
	public int getPo_count() {
		return po_count;
	}
	/**
	 * @param po_count セットする po_count
	 */
	public void setPo_count(int po_count) {
		this.po_count = po_count;
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
