package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 箱子类
 * @author Administrator
 *
 */
public class Box implements Serializable {

	/*id*/
	private Integer id;
	/*箱子名称*/
	private String boxName;
	/*箱子规格*/
	private String boxSize;
	/*长*/
	private double length;
	/*宽*/
	private double width;
	/*高*/
	private double altitude;
	/*体积*/
	private double volume;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBoxName() {
		return boxName;
	}
	public void setBoxName(String boxName) {
		this.boxName = boxName;
	}
	public String getBoxSize() {
		return boxSize;
	}
	public void setBoxSize(String boxSize) {
		this.boxSize = boxSize;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}

	public Box() {
		super();
	}
	
}
