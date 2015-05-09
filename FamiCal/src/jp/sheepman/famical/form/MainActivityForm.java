package jp.sheepman.famical.form;

import java.util.Calendar;

import jp.sheepman.common.form.BaseForm;

public class MainActivityForm extends BaseForm {
	private int family_id;
	private Calendar wc_record_date;
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
	public Calendar getWc_record_date() {
		return wc_record_date;
	}
	/**
	 * @param wc_record_date セットする wc_record_date
	 */
	public void setWc_record_date(Calendar wc_record_date) {
		this.wc_record_date = wc_record_date;
	}
}
