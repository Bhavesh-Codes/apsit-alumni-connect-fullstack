package com.apsit.alumni.connect.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class Education {

    private String institution;
    private String degree;
    private String field;
    private String startYear;
    private String endYear;
}
