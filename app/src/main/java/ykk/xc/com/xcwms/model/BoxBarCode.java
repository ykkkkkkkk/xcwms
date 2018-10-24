package ykk.xc.com.xcwms.model;

import java.io.Serializable;
import java.util.List;

/**
 * 包装物条码类，用于记录对每个包装物使用流水号生成唯一条码
 * @author Administrator
 *
 */
public class BoxBarCode implements Serializable {
	/*id*/
	private Integer id;
	/*包装物id*/
	private Integer boxId;
	/*包装物生成的唯一码*/
	private String barCode;
	/**箱子的状态
	 * 0代表创建
	 * 1代表开箱
	 * 2代表封箱
	 * */
	private Integer status;
	/*箱子净重*/
	private double roughWeight;
	/*包装物*/
	private Box box;
	/* pda扫描箱码查询箱子里的物料列表  */
	public List<MaterialBinningRecord> mtlBinningRecord;
	/*生码日期*/
	private String createDateTime;
	/*打印次数*/
	private Integer printNumber;

	public BoxBarCode() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public Integer getBoxId() {
		return boxId;
	}

	public String getBarCode() {
		return barCode;
	}

	public Integer getStatus() {
		return status;
	}

	public double getRoughWeight() {
		return roughWeight;
	}

	public Box getBox() {
		return box;
	}

	public List<MaterialBinningRecord> getMtlBinningRecord() {
		return mtlBinningRecord;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setBoxId(Integer boxId) {
		this.boxId = boxId;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setRoughWeight(double roughWeight) {
		this.roughWeight = roughWeight;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public void setMtlBinningRecord(List<MaterialBinningRecord> mtlBinningRecord) {
		this.mtlBinningRecord = mtlBinningRecord;
	}

	public String getCreateDateTime() {
		return createDateTime;
	}

	public void setCreateDateTime(String createDateTime) {
		this.createDateTime = createDateTime;
	}

	public Integer getPrintNumber() {
		return printNumber;
	}

	public void setPrintNumber(Integer printNumber) {
		this.printNumber = printNumber;
	}
	
}
