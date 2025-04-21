package com.cheng.youthapartment.entity.apartment


import com.google.gson.annotations.SerializedName

/**
 * 公寓详略数据，一般用于租约、预约、房间信息的传递
 */
data class ApartmentBean(
    /**
     * "apartmentInfo": {
     *   "id": 9,
     *   "name": "温都水城社区",
     *   "introduction": "这是一座现代化公寓，位于城市中心，拥有多种户型，从舒适的一居室到宽敞的三居室。设施齐全，配备现代厨房、设施完备的健身房和社交区。公寓内部设计时尚精致，大窗户带来充足自然光线，俯瞰城市美景。24小时安保、智能门禁系统和停车位，确保居民安全与便利。步行可至购物中心、餐厅和公共交通站点，提供舒适便捷的城市生活体验。",
     *   "districtId": 110114,
     *   "districtName": "昌平区",
     *   "cityId": 1101,
     *   "cityName": "市辖区",
     *   "provinceId": 11,
     *   "provinceName": "北京市",
     *   "addressDetail": "北京市昌平区温都水城北七家镇王府街55号",
     *   "latitude": "40.103976",
     *   "longitude": "116.370825",
     *   "phone": "1234567788",
     *   "isRelease": 1
     * }
     */
    @SerializedName("addressDetail")
    val addressDetail: String,
    @SerializedName("cityId")
    val cityId: Int,
    @SerializedName("cityName")
    val cityName: String,
    @SerializedName("districtId")
    val districtId: Int,
    @SerializedName("districtName")
    val districtName: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("introduction")
    val introduction: String,
    @SerializedName("isRelease")
    val isRelease: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("provinceId")
    val provinceId: Int,
    @SerializedName("provinceName")
    val provinceName: String
)