package ykk.xc.com.xcwms.model;

/**
 * 枚举字典
 * @author Administrator
 *
 */
public enum EnumDict {

	/**
	 * 系统设置项，仓库及库位默认值来源，
	 * 值为1代表默认值来源于物料设置的默认仓库仓位，
	 * 值为2代表默认值来源于用户或者岗位设置的默认仓库仓位。
	 */
	STOCKANDPOSTIONTDEFAULTSOURCEOFVALUE,

	/**
	 * 系统设置项：是否启用拣货导航
	 * 值为1代表启用
	 * 值为2代表不启用
	 */
	ISPICKINGNAVIGATION,
}
