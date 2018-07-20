package ykk.xc.com.xcwms.model;


import java.io.Serializable;

/**
 * 防伪二维码类
 * @author Administrator
 *
 */
public class SecurityCode implements Serializable {
	/*id*/
	private Integer id;
	/*防伪二维码号*/
	private String securityQrCode;
	/*防伪二维码组号*/
	private String securityQrCodeGruopNumber;
	/*每组数量*/
	private Integer groupCount;
	/*状态 0代表未绑定，1代表绑定*/
	private Integer status;
	/*绑定类型 0代表物料，1代表包装*/
	private Integer bindingType;
	/*创建时间*/
	private String createTime;
	/*物料id*/
	private Integer materialId;
	/*物料*/
	private Material material;
	/*箱子id*/
	private Integer boxId;
	/*箱子*/
	private Box box;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSecurityQrCode() {
		return securityQrCode;
	}
	public void setSecurityQrCode(String securityQrCode) {
		this.securityQrCode = securityQrCode;
	}
	public String getSecurityQrCodeGruopNumber() {
		return securityQrCodeGruopNumber;
	}
	public void setSecurityQrCodeGruopNumber(String securityQrCodeGruopNumber) {
		this.securityQrCodeGruopNumber = securityQrCodeGruopNumber;
	}
	public Integer getGroupCount() {
		return groupCount;
	}
	public void setGroupCount(Integer groupCount) {
		this.groupCount = groupCount;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getBindingType() {
		return bindingType;
	}
	public void setBindingType(Integer bindingType) {
		this.bindingType = bindingType;
	}

	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Integer getMaterialId() {
		return materialId;
	}
	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Integer getBoxId() {
		return boxId;
	}
	public void setBoxId(Integer boxId) {
		this.boxId = boxId;
	}
	public Box getBox() {
		return box;
	}
	public void setBox(Box box) {
		this.box = box;
	}

	public SecurityCode() {
		super();
	}

	@Override
	public String toString() {
		return "SecurityCode [id=" + id + ", securityQrCode=" + securityQrCode + ", securityQrCodeGruopNumber="
				+ securityQrCodeGruopNumber + ", groupCount=" + groupCount + ", status=" + status + ", bindingType="
				+ bindingType + ", createTime=" + createTime + ", materialId=" + materialId + ", material=" + material
				+ ", boxId=" + boxId + ", box=" + box + "]";
	}

}
