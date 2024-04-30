package jpabook.dashdine.domain.common;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Address {

    private String street;
    private String streetDetail;
    private String zipcode;

    protected Address() {
    }

    public Address(String street, String streetDetail, String zipcode) {
        this.street = street;
        this.streetDetail = streetDetail;
        this.zipcode = zipcode;
    }

    public void updateStreet(String street) {
        this.street = street;
    }

    public void updateDetail(String streetDetail) {
        this.streetDetail = streetDetail;
    }

    public void updateZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
