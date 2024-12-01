package com.cheng.youthapartment.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class RoomBean implements Serializable {
    /**
     * id : 2
     * rent : 2500
     * graphVoList : [
     * {"name":"公寓 (12).jpg","url":"xxx-公寓 (12).jpg"},
     * {"name":"公寓 (17).jpg","url":"xxx-公寓 (17).jpg"},
     * {"name":"公寓 (7).jpg","url":"xxx-公寓 (7).jpg"}
     * ]
     * labelInfoList : []
     * apartmentInfo : {
     * "id":9,"name":"温都水城社区","introduction":"这是一座现代化公寓，位于城市中心，拥有多种户型，从舒适的一居室到宽敞的三居室。设施齐全，配备现代厨房、设施完备的健身房和社交区。公寓内部设计时尚精致，大窗户带来充足自然光线，俯瞰城市美景。24小时安保、智能门禁系统和停车位，确保居民安全与便利。步行可至购物中心、餐厅和公共交通站点，提供舒适便捷的城市生活体验。",
     * "districtId":110114,
     * "districtName":"昌平区",
     * "cityId":1101,
     * "cityName":"市辖区",
     * "provinceId":11,
     * "provinceName":"北京市",
     * "addressDetail":"北京市昌平区温都水城北七家镇王府街55号",
     * "latitude":"40.103976",
     * "longitude":"116.370825",
     * "phone":"1234567788",
     * "isRelease":1
     * }
     */
    @SerializedName("id")
    private Long id;
    @SerializedName("rent")
    private BigDecimal rent;
    private String roomNumber;
    private List<GraphVoBean> graphVoList;
    private List<LabelInfoBean> labelInfoList;
    private ApartmentInfoBean apartmentInfo;

    public RoomBean() {

    }

    public RoomBean(Long id, BigDecimal rent, String roomNumber, ApartmentInfoBean apartmentInfo, List<GraphVoBean> graphVoList, List<LabelInfoBean> labelInfoList) {
        this.id = id;
        this.rent = rent;
        this.roomNumber = roomNumber;
        this.apartmentInfo = apartmentInfo;
        this.graphVoList = graphVoList;
        this.labelInfoList = labelInfoList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getRent() {
        return rent;
    }

    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public ApartmentInfoBean getApartmentInfo() {
        return apartmentInfo;
    }

    public void setApartmentInfo(ApartmentInfoBean apartmentInfo) {
        this.apartmentInfo = apartmentInfo;
    }

    public List<GraphVoBean> getGraphVoList() {
        return graphVoList;
    }

    public void setGraphVoList(List<GraphVoBean> graphVoList) {
        this.graphVoList = graphVoList;
    }

    public List<LabelInfoBean> getLabelInfoList() {
        return labelInfoList;
    }

    public void setLabelInfoList(List<LabelInfoBean> labelInfoList) {
        this.labelInfoList = labelInfoList;
    }

    @Override
    public String toString() {
        return "RoomBean{" +
                "id=" + id +
                ", rent=" + rent +
                ", roomNumber='" + roomNumber + '\'' +
                ", graphVoList=" + graphVoList +
                ", labelInfoList=" + labelInfoList +
                ", apartmentInfo=" + apartmentInfo +
                "}\n";
    }
}
