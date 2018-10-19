package ykk.xc.com.xcwms.model.sal;

import java.io.Serializable;

/**
 * 拣货单子表
 * @author Administrator
 *
 */
public class PickingListEntry implements Serializable{

	/*id*/
	private int id;
	private int pickingListId;
	private String barcode; // 条码号
	private String createDate;
	/* 创建人id  */
	private int	createUserId;
	/* 创建人名称  */
	private String createUserName;

	public PickingListEntry() {
		super();
	}

	public int getId() {
		return id;
	}

	public int getPickingListId() {
		return pickingListId;
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

	public void setPickingListId(int pickingListId) {
		this.pickingListId = pickingListId;
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