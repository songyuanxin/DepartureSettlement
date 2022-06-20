package com.syx.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@ApiModel(value="com-jzj-domain-StoreHead")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "SAPStoreHead")
public class SAPStoreHead {
    @TableId(value = "storeId", type = IdType.INPUT)
    @ApiModelProperty(value="门店编码")
    private String storeId;

    @TableField(value = "storeName")
    @ApiModelProperty(value="门店名称")
    private String storeName;

    @TableField(value = "oldStoreId")
    @ApiModelProperty(value="旧POS编码")
    private String oldStoreId;

    @TableField(value = "address")
    @ApiModelProperty(value="地址")
    private String address;

    @TableField(value = "levelParcel")
    @ApiModelProperty(value="省级分部")
    private String levelParcel;

    @TableField(value = "levelParcelName")
    @ApiModelProperty(value="省级分部描述")
    private String levelParcelName;

    @TableField(value = "storeType")
    @ApiModelProperty(value="门店类型")
    private String storeType;

    @TableField(value = "storeTypeTitle")
    @ApiModelProperty(value="门店类型描述")
    private String storeTypeTitle;

    @TableField(value = "phone")
    @ApiModelProperty(value="电话")
    private String phone;

    @TableField(value = "person")
    @ApiModelProperty(value="负责人")
    private String person;

    @TableField(value = "personPhone")
    @ApiModelProperty(value="负责人电话")
    private String personPhone;

    @TableField(value = "isRun")
    @ApiModelProperty(value="是否经营")
    private String isRun;

    @TableField(value = "isRunTitle")
    @ApiModelProperty(value="是否经营描述")
    private String isRunTitle;

    @TableField(value = "goodsDay")
    @ApiModelProperty(value="送货日程")
    private String goodsDay;

    @TableField(value = "storeTitle")
    @ApiModelProperty(value="门店名全称")
    private String storeTitle;

    @TableField(value = "storeStatus")
    @ApiModelProperty(value="门店状态")
    private String storeStatus;

    @TableField(value = "market")
    @ApiModelProperty(value="市级分部")
    private String market;

    @TableField(value = "marketTitle")
    @ApiModelProperty(value="市级分部描述")
    private String marketTitle;

    @TableField(value = "city")
    @ApiModelProperty(value="城市")
    private String city;

    @TableField(value = "cityTitle")
    @ApiModelProperty(value="城市描述")
    private String cityTitle;

    @TableField(value = "manageArea")
    @ApiModelProperty(value="管理地区")
    private String manageArea;

    @TableField(value = "area")
    @ApiModelProperty(value="片区")
    private String area;

    @TableField(value = "updateTime")
    @ApiModelProperty(value="更新时间")
    private Date updateTime;
}