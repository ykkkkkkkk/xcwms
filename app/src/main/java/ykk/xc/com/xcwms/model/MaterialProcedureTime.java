package ykk.xc.com.xcwms.model;


import java.io.Serializable;

public class MaterialProcedureTime implements Serializable {

	private Integer id;
	/* 物料id */
	private Integer fMaterialId;
	/* 工艺工序关联id */
	private Integer processflowId;

	/* 状态：0禁用，1未禁用 */
	private Integer priceState;
	/* 创建时间 */
	private String createDate;
	/* 修改时间 */
	private String fModifyDate;

	/* 创建者id */
	private Integer createrId;
	/* 创建这名称 */
	private String createrName;
	/* 物料实体 */
	private Material material;
	/* 工艺路线实体 */
	private Processflow processflow;
	/* 工序实体 */
	private Procedure procedure;

	/* 小计金额 ，暂不存数据库，仅用前端显示 */
	private double subtotalAmount;
	/* 工时单价，暂不存数据库，仅用前端显示 */
	private double unitPrice;

	public MaterialProcedureTime() {
		super();
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Processflow getProcessflow() {
		return processflow;
	}

	public void setProcessflow(Processflow processflow) {
		this.processflow = processflow;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getfMaterialId() {
		return fMaterialId;
	}

	public void setfMaterialId(Integer fMaterialId) {
		this.fMaterialId = fMaterialId;
	}

	public Integer getProcessflowId() {
		return processflowId;
	}

	public void setProcessflowId(Integer processflowId) {
		this.processflowId = processflowId;
	}

	public Integer getPriceState() {
		return priceState;
	}

	public void setPriceState(Integer priceState) {
		this.priceState = priceState;
	}

	public double getSubtotalAmount() {
		return subtotalAmount;
	}

	public void setSubtotalAmount(double subtotalAmount) {
		this.subtotalAmount = subtotalAmount;
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

	@Override
	public String toString() {
		return "MaterialPrice [id=" + id + ", fMaterialId=" + fMaterialId + ", processflowId=" + processflowId
				+ ", priceState=" + priceState + ", createDate=" + createDate + ", fModifyDate=" + fModifyDate
				+ ", createrId=" + createrId + ", createrName=" + createrName + ", material=" + material
				+ ", processflow=" + processflow + ", procedure=" + procedure + ", subtotalAmount=" + subtotalAmount
				+ ", unitPrice=" + unitPrice + "]";
	}
}
