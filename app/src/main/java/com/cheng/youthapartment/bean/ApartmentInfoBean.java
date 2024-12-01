package com.cheng.youthapartment.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ApartmentInfoBean implements Serializable {
    /**
     * id : 9
     * name : 温都水城社区
     * introduction : 这是一座现代化公寓，位于城市中心，拥有多种户型，从舒适的一居室到宽敞的三居室。设施齐全，配备现代厨房、设施完备的健身房和社交区。公寓内部设计时尚精致，大窗户带来充足自然光线，俯瞰城市美景。24小时安保、智能门禁系统和停车位，确保居民安全与便利。步行可至购物中心、餐厅和公共交通站点，提供舒适便捷的城市生活体验。
     * districtId : 110114
     * districtName : 昌平区
     * cityId : 1101
     * cityName : 市辖区
     * provinceId : 11
     * provinceName : 北京市
     * addressDetail : 北京市昌平区温都水城北七家镇王府街55号
     * latitude : 40.103976
     * longitude : 116.370825
     * phone : 1234567788
     * isRelease : 1
     */
    @SerializedName("id")
    private Long id;
    private String name;
    private String introduction;
    private Long districtId;
    private String districtName;
    private Long cityId;
    private String cityName;
    private Long provinceId;
    private String provinceName;
    private String addressDetail;
    private String latitude;
    private String longitude;
    private String phone;
    private String isRelease;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Long provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsRelease() {
        return isRelease;
    }

    public void setIsRelease(String isRelease) {
        this.isRelease = isRelease;
    }

    @Override
    public String toString() {
        return "ApartmentInfoBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", introduction='" + introduction + '\'' +
                ", districtId=" + districtId +
                ", districtName='" + districtName + '\'' +
                ", cityId=" + cityId +
                ", cityName='" + cityName + '\'' +
                ", provinceId=" + provinceId +
                ", provinceName='" + provinceName + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", phone='" + phone + '\'' +
                ", isRelease='" + isRelease + '\'' +
                "}\n";
    }
}