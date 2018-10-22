package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 出入库记录类子表
 * @author Administrator
 *
 */
public class ScanningRecordEntry implements Serializable {

	/*id*/
	private int id;
	private int scId;
	private String barcode; // 条码号
	private String createDate;
	/* 创建人id  */
	private int	createUserId;
	/* 创建人名称  */
	private String createUserName;

	public ScanningRecordEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public int getScId() {
		return scId;
	}

	public String getBarcode() {
		return barcode;
	}

	public String getCreateDate() {
		return createDate;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public String getCreateUserName() {
		return createUserName;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setScId(int scId) {
		this.scId = scId;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}

}
