package com.example.domain;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Asset implements Serializable {
    private static final long serialVersionUID = 1L;

    private String assetID;

    private String color;

    private int size;

    private String owner;

    private int appraisedValue;


}
