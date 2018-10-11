package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 物料包装类
 * @author Administrator
 *
 */
public class MaterialPack implements Serializable {

	/*物料包装id*/
	private int id;
	/*箱子id*/
	private int boxId;
	/*箱子*/
	private Box box;
	/*物料id*/
	private int materialId;
	/*物料*/
	private Material material;
	/*物料包装名称*/
	private String materialPackName;
	/*物料包装规格*/
	private String materialPackSize;
	/*条形码*/
	private String barcode;
	/*包装物装物料的数量*/
	private double number;
	/*包装物装入物料后的总重量*/
	private double materialPackWeight;
	/*是否基本包装 0不是，1是*/
	private int isBasicPack;
	/*是否默认包装 0不是，1是*/
	private int isFixPack;
	/*是否最小包装数量 0不是，1是*/
	private int isMinNumberPack;
	/*wms非物理删除标识*/
	private String isDelete;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBoxId() {
		return boxId;
	}

	public void setBoxId(int boxId) {
		this.boxId = boxId;
	}

	public Box getBox() {
		return box;
	}

	public void setBox(Box box) {
		this.box = box;
	}

	public int getMaterialId() {
		return materialId;
	}

	public void setMaterialId(int materialId) {
		this.materialId = materialId;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public String getMaterialPackName() {
		return materialPackName;
	}

	public void setMaterialPackName(String materialPackName) {
		this.materialPackName = materialPackName;
	}

	public String getMaterialPackSize() {
		return materialPackSize;
	}

	public void setMaterialPackSize(String materialPackSize) {
		this.materialPackSize = materialPackSize;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public double getMaterialPackWeight() {
		return materialPackWeight;
	}

	public void setMaterialPackWeight(double materialPackWeight) {
		this.materialPackWeight = materialPackWeight;
	}

	public int getIsBasicPack() {
		return isBasicPack;
	}

	public void setIsBasicPack(int isBasicPack) {
		this.isBasicPack = isBasicPack;
	}

	public int getIsFixPack() {
		return isFixPack;
	}

	public void setIsFixPack(int isFixPack) {
		this.isFixPack = isFixPack;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	public int getIsMinNumberPack() {
		return isMinNumberPack;
	}

	public void setIsMinNumberPack(int isMinNumberPack) {
		this.isMinNumberPack = isMinNumberPack;
	}

	public MaterialPack() {
		super();
	}
	
}
