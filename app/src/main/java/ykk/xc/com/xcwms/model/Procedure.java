package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 工序
 *
 * @author qxp 2018-11-09
 *
 */
public class Procedure implements Serializable {

	private Integer id;
	/* 计价类型id */
	private int valuationTypeId;
	/* 工序编号 */
	private String procedureNumber;
	/* 工序名称 */
	private String procedureName;
	/* 工序状态（0禁用、1启用） */
	private Integer procedureState;

	private String createDate;
	/* 修改日期 */
	private String fModifyDate;

	/* 创建者id */
	private Integer createrId;
	/* 创建这名称 */
	private String createrName;

	/* 用于标志勾选状态 */
	private int checkFlag;

	/* 用于设置顺序号 */
	private String standard;
	/* 用于设置单价 */
	private double price;
	private ValuationType valuationType;
	private ProcessflowEntry pfEntry;

	// 临时字段，不存表
	private int materialId;
	private String barCode; // 工序条码
	private Collective collective;

	public Procedure() {
		super();
	}

	public Procedure(Integer id, int valuationTypeId, String procedureNumber, String procedureName,
					 Integer procedureState, String createDate, String fModifyDate, Integer createrId, String createrName,
					 int checkFlag, String standard, Double price, ProcessflowEntry pfEntry, int materialId, String barCode) {
		super();
		this.id = id;
		this.valuationTypeId = valuationTypeId;
		this.procedureNumber = procedureNumber;
		this.procedureName = procedureName;
		this.procedureState = procedureState;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
		this.checkFlag = checkFlag;
		this.standard = standard;
		this.price = price;
		this.pfEntry = pfEntry;
		this.materialId = materialId;
		this.barCode = barCode;
	}

	public ValuationType getValuationType() {
		return valuationType;
	}

	public void setValuationType(ValuationType valuationType) {
		this.valuationType = valuationType;
	}

	public ProcessflowEntry getPfEntry() {
		return pfEntry;
	}

	public void setPfEntry(ProcessflowEntry pfEntry) {
		this.pfEntry = pfEntry;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getValuationTypeId() {
		return valuationTypeId;
	}

	public void setValuationTypeId(int valuationTypeId) {
		this.valuationTypeId = valuationTypeId;
	}

	public String getProcedureNumber() {
		return procedureNumber;
	}

	public void setProcedureNumber(String procedureNumber) {
		this.procedureNumber = procedureNumber;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Integer getProcedureState() {
		return procedureState;
	}

	public void setProcedureState(Integer procedureState) {
		this.procedureState = procedureState;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getfModifyDate() {
		return fModifyDate;
	}

	public void setfModifyDate(String fModifyDate) {
		this.fModifyDate = fModifyDate;
	}

	public Integer getCreaterId() {
		return createrId;
	}

	public void setCreaterId(Integer createrId) {
		this.createrId = createrId;
	}

	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public int getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(int checkFlag) {
		this.checkFlag = checkFlag;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public Collective getCollective() {
		return collective;
	}

	public void setCollective(Collective collective) {
		this.collective = collective;
	}

}
